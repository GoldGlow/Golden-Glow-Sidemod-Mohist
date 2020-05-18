package com.goldenglowspigot.common.handlers.events;

import com.coloredcarrot.jsonapi.impl.JsonClickEvent;
import com.coloredcarrot.jsonapi.impl.JsonHoverEvent;
import com.coloredcarrot.jsonapi.impl.JsonMsg;
import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglowspigot.common.chatChannels.Channel;
import com.goldenglowspigot.common.chatChannels.ChannelsManager;
import com.goldenglowspigot.common.chatChannels.GlobalChannel;
import com.goldenglowspigot.common.chatChannels.PrivateChannel;
import com.goldenglowspigot.common.util.GGLogger;
import com.goldenglowspigot.common.util.Reference;
import com.google.gson.stream.JsonWriter;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.minecraft.entity.player.EntityPlayerMP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import red.mohist.api.ChatComponentAPI;
import red.mohist.api.PlayerAPI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

public class SpigotEvents implements Listener {
    @EventHandler
    public void onLogout(PlayerQuitEvent event){
        EntityPlayerMP player= PlayerAPI.getNMSPlayer(event.getPlayer());
        IPlayerData playerData = player.getCapability(OOPlayerProvider.OO_DATA, null);
        try {
            if (playerData != null) {
                User user = LuckPermsProvider.get().getUserManager().getUser(player.getName());
                File f = new File(Reference.statsDir, player.getUniqueID().toString() + ".json");
                if (!f.exists()) f.createNewFile();
                JsonWriter writer = new JsonWriter(new FileWriter(f));
                writer.setIndent("\t");

                writer.beginObject();

                int badgeCount = 0;
                for (Node n : user.getNodes()) {
                    if (n.getKey().startsWith("badge.") && n.getKey().endsWith("npc"))
                        badgeCount++;
                }

                if(playerData.getLoginTime()!=null) {
                    long sessionTime = Math.abs(Duration.between(Instant.now(), playerData.getLoginTime()).getSeconds());
                    long totalTime = player.getEntityData().getLong("playtime") + sessionTime;
                    player.getEntityData().setLong("playtime", totalTime);

                    writer.name("badges").value(Integer.toString(badgeCount));
                    writer.name("dex").value(Integer.toString(Pixelmon.storageManager.getParty(player.getUniqueID()).pokedex.countCaught()));
                    writer.name("time").value(String.format("%sh:%sm", totalTime / 3600, (totalTime % 3600) / 60));

                    writer.endObject();
                    writer.close();
                }
            }
        }
        catch (IOException e) {
            GGLogger.error("Error occurred saving player stats.");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getSlotType()== InventoryType.SlotType.ARMOR){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        String[] whisperCommand={"/msg", "/w", "/m", "/t", "/pm", "/emsg", "/epm", "/tell", "/etell", "/whisper", "/ewhisper"};
        String[] message=event.getMessage().split(" ");
        boolean isWhisper=false;
        for(String whisper: whisperCommand){
            if(message[0].equalsIgnoreCase(whisper)){
                isWhisper=true;
                break;
            }
        }
        if(isWhisper){
            Channel channel= com.goldenglowspigot.GoldenGlow.channelsManager.getPlayerChannel(event.getPlayer());
            Player otherPlayer= Bukkit.getPlayer(Bukkit.getPlayerUniqueId(message[1]));
            if(otherPlayer!=null&&otherPlayer instanceof Player){
                event.setCancelled(true);
                Player[] eligiblePlayers=new Player[]{event.getPlayer(), otherPlayer};
                com.goldenglowspigot.GoldenGlow.channelsManager.checkOrAddPrivateChannel(eligiblePlayers);
                com.goldenglowspigot.GoldenGlow.channelsManager.setPlayerChannel(event.getPlayer(), otherPlayer);
                JsonMsg firstMessage=((PrivateChannel)com.goldenglowspigot.GoldenGlow.channelsManager.getPlayerChannel(event.getPlayer())).getPrefix(eligiblePlayers[1]);
                JsonMsg secondMessage=((PrivateChannel)com.goldenglowspigot.GoldenGlow.channelsManager.getPlayerChannel(event.getPlayer())).getPrefix(eligiblePlayers[0]);
                if(event.getPlayer().getDisplayName().equals(eligiblePlayers[0].getDisplayName())){
                    firstMessage.append("to "+eligiblePlayers[1].getDisplayName()+": ");
                    secondMessage.append("from "+eligiblePlayers[0].getDisplayName()+": ");
                }
                else{
                    firstMessage.append("from "+eligiblePlayers[1].getDisplayName()+": ");
                    secondMessage.append("to "+eligiblePlayers[0].getDisplayName()+": ");
                }
                firstMessage.append(": ");
                secondMessage.append(": ");
                String toSend="";
                for(int i=2;i<message.length;i++){
                    if(i>2){
                        toSend+=" ";
                    }
                    toSend+=message[i];
                }
                firstMessage.append(toSend);
                secondMessage.append(toSend);
                firstMessage.send(eligiblePlayers[0]);
                secondMessage.send(eligiblePlayers[1]);
                if(channel instanceof GlobalChannel){
                    com.goldenglowspigot.GoldenGlow.channelsManager.setPlayerChannel(event.getPlayer(), ChannelsManager.EnumChannels.GLOBAL);
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        event.setCancelled(true);
        Channel channel= com.goldenglowspigot.GoldenGlow.channelsManager.getPlayerChannel(event.getPlayer());
        if(channel instanceof PrivateChannel){
            Player[] eligiblePlayers=((PrivateChannel) channel).getEligiblePlayers();
            JsonMsg firstMessage=((PrivateChannel) channel).getPrefix(eligiblePlayers[1]);
            JsonMsg secondMessage=((PrivateChannel) channel).getPrefix(eligiblePlayers[0]);
            if(event.getPlayer().getDisplayName().equals(eligiblePlayers[0].getDisplayName())){
                firstMessage.append("to "+eligiblePlayers[1].getDisplayName()+": ");
                secondMessage.append("from "+eligiblePlayers[0].getDisplayName()+": ");
            }
            else{
                firstMessage.append("from "+eligiblePlayers[1].getDisplayName()+": ");
                secondMessage.append("to "+eligiblePlayers[0].getDisplayName()+": ");
            }
            firstMessage.append(event.getMessage());
            secondMessage.append(event.getMessage());
            firstMessage.send(eligiblePlayers[0]);
            secondMessage.send(eligiblePlayers[1]);
        }
        else{
            JsonMsg prefix=channel.getPrefix();
            JsonMsg message=formatJsonMessage(event);
            prefix.append(message);
            Set<Player> players=event.getRecipients();
            for(Player player:players){
                if(channel.canSee(player)){
                    prefix.send(player);
                }
            }
        }
    }

    public static JsonMsg formatJsonMessage(AsyncPlayerChatEvent event){
        String name=event.getPlayer().getDisplayName();
        EntityPlayerMP playerMP=PlayerAPI.getNMSPlayer(event.getPlayer());
        JsonMsg message=new JsonMsg(name);
        if(GoldenGlow.permissionUtils.checkPermissionWithStart(playerMP, "hover.")){
            String hoverText=GoldenGlow.permissionUtils.getNodeWithStart(playerMP, "hover.").replace("hover.","");
            message=message.hoverEvent(JsonHoverEvent.showText(hoverText));
        }
        if(GoldenGlow.permissionUtils.checkPermissionWithStart(playerMP, "link.")){
            String linkUrl=GoldenGlow.permissionUtils.getNodeWithStart(playerMP, "link.").replace("link.","");
            message=message.clickEvent(JsonClickEvent.openUrl(linkUrl));
        }
        message.append(": "+event.getMessage());
        return message;
    }
}

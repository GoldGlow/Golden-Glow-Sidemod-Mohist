package com.goldenglow.common.handlers.events;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.util.Reference;
import com.google.gson.stream.JsonWriter;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.minecraft.entity.player.EntityPlayerMP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import red.mohist.api.PlayerAPI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

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
            GoldenGlow.logger.error("Error occurred saving player stats.");
            e.printStackTrace();
        }
    }
}

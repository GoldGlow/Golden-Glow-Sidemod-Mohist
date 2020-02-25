package com.goldenglow.common.handlers.events;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.events.OOPokedexEvent;
import com.goldenglow.common.inventory.CustomItem;
import com.goldenglow.common.inventory.social.PlayerProfile;
import com.goldenglow.common.seals.SealManager;
import com.goldenglow.common.util.TitleMethods;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.AggressionEvent;
import com.pixelmonmod.pixelmon.api.events.PokedexEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.PixelmonSpawnerEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.pokedex.EnumPokedexRegisterStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherEventHandler {
    @SubscribeEvent
    public void pokedexRegisteredEvent(PokedexEvent event){
        Map<Integer, EnumPokedexRegisterStatus> seen= Pixelmon.storageManager.getParty(event.uuid).pokedex.getSeenMap();
        ArrayList<Integer> caught=new ArrayList<Integer>();
        int bugTypes=0;
        for(Map.Entry<Integer, EnumPokedexRegisterStatus> entry:seen.entrySet()){
            if(entry.getValue()==EnumPokedexRegisterStatus.caught){
                caught.add(entry.getKey());
            }
        }
        PlayerScriptData scriptData= PlayerData.get(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(event.uuid)).scriptData;
        OOPokedexEvent dexEvent=new OOPokedexEvent(new PlayerWrapper(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(event.uuid)), caught);
        TitleMethods.unlockBugCatcher(dexEvent);
        for(ScriptContainer s : scriptData.getScripts()){
            s.run("pokedexEvent", dexEvent);
        }
    }

    @SubscribeEvent
    public void onVanish(PlayerEvent.StartTracking event){
        IPlayerData playerData=event.getEntityPlayer().getCapability(OOPlayerProvider.OO_DATA, null);
        if(event.getTarget() instanceof EntityPlayerMP){
            if(playerData.getPlayerVisibility()&&!playerData.getFriendList().contains(event.getTarget().getUniqueID())){
                ((EntityPlayerMP)event.getEntityPlayer()).removeEntity(event.getTarget());
            }
        }
    }

    @SubscribeEvent
    public void onMessage(ServerChatEvent event) {
        List<String> matches = new ArrayList<>();
        Matcher m = Pattern.compile("<\\w*:\\d>").matcher(event.getMessage());
        while(m.find()) {
            matches.add(m.group());
        }

        TextComponentString pre = new TextComponentString(event.getComponent().getFormattedText().split(event.getMessage())[0]);
        //pre.appendText(event.getMessage().split(matches.get(0))[0]);
        String[] inbetweens = event.getMessage().split("<\\w*:\\d>");
        int i = 0;
        for(String match : matches) {
            pre.appendText(inbetweens[i]);
            try {
                String split = (match.split("<\\w*:")[1].replace(">", ""));
                int num = Integer.valueOf(split);
                if(num>0 && num<7) {
                    Pokemon p = Pixelmon.storageManager.getParty(event.getPlayer()).get(num - 1);
                    if(p!=null) {
                        TextComponentString share = new TextComponentString("["+ (p.getNickname()!=null ? p.getNickname() : p.getSpecies().name) +"]");
                        share.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponentString(CustomItem.getPokemonItem(p).getItem().writeToNBT(new NBTTagCompound()).toString()))).setBold(true).setColor(TextFormatting.DARK_AQUA);
                        pre.appendSibling(share);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }
        if(inbetweens.length>i)
            pre.appendText(inbetweens[i]);
        event.setComponent(pre);
    }

    @SubscribeEvent
    public void onPixelmonSpawner(PixelmonSpawnerEvent event){
        if(event.spawner.getWorld().isDaytime()){
            for(String pokemon: GoldenGlow.pixelmonSpawnerHandler.nightPokemon){
                if(event.spec.name.equals(pokemon))
                    event.setCanceled(true);
            }
        }
        else{
            for(String pokemon:GoldenGlow.pixelmonSpawnerHandler.dayPokemon){
                if(event.spec.name.equals(pokemon))
                    event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if(!(event.getObject() instanceof EntityPlayer)) return;

        event.addCapability(new ResourceLocation("obscureobsidian", "playerdata"), new OOPlayerProvider());
    }

    @SubscribeEvent
    public void entityJoinWorld(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof EntityPixelmon) {
            Pokemon p = ((EntityPixelmon)event.getEntity()).getPokemonData();
            if(p.getOwnerPlayer()!= null) {
                String s = p.getOwnerPlayer().getCapability(OOPlayerProvider.OO_DATA, null).getEquippedSeals()[p.getPosition().order];
                if(s!=null && s.isEmpty() && SealManager.loadedSeals.containsKey(s))
                    SealManager.loadedSeals.get(s).execute(event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public void onPokemonAggro(AggressionEvent event) {
        if(PlayerData.get(event.player).editingNpc!=null)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event){
        if(event.getTarget() instanceof EntityPlayerMP){
            PlayerProfile.openInventory((EntityPlayerMP) event.getEntityPlayer(), (EntityPlayerMP) event.getTarget());
        }
    }
}

package com.goldenglow.common.util.scripting;

import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.Requirement;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.PlayerDeath;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.entity.EntityCustomNpc;

import javax.script.ScriptEngine;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class VisibilityFunctions {
    public static void refreshPlayerVisibility(PlayerWrapper playerWrapper){
        EntityPlayerMP player=(EntityPlayerMP) playerWrapper.getMCEntity();
        IPlayerData playerData=player.getCapability(OOPlayerProvider.OO_DATA, null);
        IEntity[] players=playerWrapper.getWorld().getNearbyEntities(playerWrapper.getPos(), 512, 1);
        for(IEntity otherPlayer:players){
            EntityPlayerMP otherPlayerMP=(EntityPlayerMP)otherPlayer.getMCEntity();
            if(playerData.getPlayerVisibility()&&!playerData.getFriendList().contains(otherPlayerMP.getUniqueID())){
                if(!player.equals(otherPlayerMP))
                    player.removeEntity(otherPlayerMP);
            }
            else if(!playerData.getPlayerVisibility()){
                if(!player.equals(otherPlayerMP)) {
                    player.removeEntity(otherPlayerMP);
                    player.connection.sendPacket(new SPacketSpawnPlayer(otherPlayerMP));
                }
            }
        }
    }

    public static void refreshNPCVisibility(PlayerWrapper playerWrapper){
        EntityPlayerMP player=(EntityPlayerMP) playerWrapper.getMCEntity();
        IPlayerData playerData=player.getCapability(OOPlayerProvider.OO_DATA, null);
        IEntity[] npcs=playerWrapper.getWorld().getNearbyEntities(playerWrapper.getPos(), 512, 2);
        for(IEntity npc:npcs){
            NPCWrapper customNpc=(NPCWrapper)npc;
            boolean visible=isNpcVisible(playerWrapper, customNpc);
            if(visible){
                player.removeEntity(customNpc.getMCEntity());
                player.connection.sendPacket(new SPacketSpawnMob((EntityLivingBase) customNpc.getMCEntity()));
            }
            else{
                player.removeEntity(customNpc.getMCEntity());
            }
        }
    }

    public static boolean isNpcVisible(PlayerWrapper player, NPCWrapper npc){
        Field f = null;
        try {
            f = ScriptContainer.class.getDeclaredField("engine");
            f.setAccessible(true);
            try {
                ScriptEngine engine = (ScriptEngine)f.get(((EntityCustomNpc)npc.getMCEntity()).script.getScripts().get(0));
                ScriptObjectMirror visibilityRequirements=(ScriptObjectMirror)engine.getContext().getAttribute("visibilityRequirements");
                ScriptObjectMirror invisibilityRequirements=(ScriptObjectMirror)engine.getContext().getAttribute("invisibilityRequirements");
                if(invisibilityRequirements!=null){
                    ArrayList<Requirement> requirements=new ArrayList<Requirement>();
                    for(int i=0;i<invisibilityRequirements.size();i++){
                        ScriptObjectMirror requirementObject=(ScriptObjectMirror) invisibilityRequirements.getSlot(i);
                        Requirement requirement=new Requirement();
                        requirement.type= Requirement.RequirementType.valueOf(((String)requirementObject.getMember("type")).toUpperCase());
                        Object value=requirementObject.getMember("value");
                        if(value instanceof String){
                            requirement.value=(String) value;
                        }
                        else{
                            requirement.id=(int)value;
                        }
                        requirements.add(requirement);
                        GGLogger.info(requirement.getClass());
                    }
                    if(Requirement.checkRequirements(requirements, (EntityPlayerMP)player.getMCEntity())){
                        return false;
                    }
                }
                if(visibilityRequirements!=null){
                    ArrayList<Requirement> requirements=new ArrayList<Requirement>();
                    for(int i=0;i<visibilityRequirements.size();i++){
                        ScriptObjectMirror requirementObject=(ScriptObjectMirror) visibilityRequirements.getSlot(i);
                        Requirement requirement=new Requirement();
                        requirement.type= Requirement.RequirementType.valueOf(((String)requirementObject.getMember("type")).toUpperCase());
                        Object value=requirementObject.getMember("value");
                        if(value instanceof String){
                            requirement.value=(String) value;
                        }
                        else{
                            requirement.id=(int)value;
                        }
                        requirements.add(requirement);
                        GGLogger.info(requirement.getClass());
                    }
                    if(!Requirement.checkRequirements(requirements, (EntityPlayerMP)player.getMCEntity())){
                        return false;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return true;
    }
}

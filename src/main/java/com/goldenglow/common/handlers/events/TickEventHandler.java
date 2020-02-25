package com.goldenglow.common.handlers.events;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.music.SongManager;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.customOverlays.CustomNoticePacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;
import java.util.Map;

public class TickEventHandler {

    public static Map<NPCWrapper, Integer> battleNPCs = new HashMap();

    @SubscribeEvent
    public static void onPlayerTick(PlayerEvent.UpdateEvent event) {
        IPlayerData data = event.player.getMCEntity().getCapability(OOPlayerProvider.OO_DATA, null);
        if (data.getDialogTicks() != -1) {
            int ticks = data.getDialogTicks();
            if (ticks > 0)
                data.setDialogTicks(ticks - 1);
            else {
                Pixelmon.network.sendTo(new CustomNoticePacket().setEnabled(false), event.player.getMCEntity());
                data.setDialogTicks(-1);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if(!battleNPCs.isEmpty()) {
            for(NPCWrapper npc : battleNPCs.keySet()) {
                if(npc!=null) {
                    raytraceNPCBattle(npc, battleNPCs.get(npc));
                }
            }
        }
    }

    static void raytraceNPCBattle(NPCWrapper npc, int initDialogID) {
        IEntity[] losEntities = npc.rayTraceEntities(5, true, true);
        if(losEntities.length > 0) {
            for(IEntity e : losEntities) {
                if(e instanceof PlayerWrapper) {
                    PlayerWrapper p = (PlayerWrapper)e;
                    if(!p.hasReadDialog(initDialogID) && !(p.getGamemode()==1 || p.getGamemode()==3)) {
                        npcBattleDialog((EntityPlayerMP)p.getMCEntity(), (EntityNPCInterface)npc.getMCEntity(), initDialogID);
                    }
                }
            }
        }
    }

    public static void npcBattleDialog(EntityPlayerMP player, EntityNPCInterface npc, int initDialogID) {
        SongManager.setCurrentSong(player, GoldenGlow.songManager.encounterDefault);
        NoppesUtilServer.openDialog(player, npc, (Dialog) DialogController.instance.get(initDialogID));
    }

}

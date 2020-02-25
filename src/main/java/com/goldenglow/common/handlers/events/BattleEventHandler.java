package com.goldenglow.common.handlers.events;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.battles.npc.CustomNPCBattle;
import com.goldenglow.common.battles.npc.DoubleNPCBattle;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.events.CNPCBattleEvent;
import com.goldenglow.common.gyms.GymBattleRules;
import com.goldenglow.common.music.SongManager;
import com.goldenglow.common.teams.PlayerParty;
import com.goldenglow.common.util.PermissionUtils;
import com.goldenglow.common.util.PixelmonBattleUtils;
import com.goldenglow.common.util.scripting.WorldFunctions;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.events.battles.TurnEndEvent;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.comm.packetHandlers.PlayerDeath;
import com.pixelmonmod.pixelmon.enums.battle.BattleResults;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.controllers.ScriptContainer;

public class BattleEventHandler {
    @SubscribeEvent
    public void onBattleStart(BattleStartedEvent event){
        BattleParticipant[] participants=event.participant1;
        BattleParticipant[] opponents=event.participant2;
        if(event.bc.rules instanceof GymBattleRules){
            GymBattleRules gymBattle=(GymBattleRules)event.bc.rules;
            for (BattleParticipant participant : participants) {
                if (participant instanceof PlayerParticipant) {
                    if(gymBattle.getGym().getPlayerTeams().containsKey(((PlayerParticipant) participant).player.getUniqueID())){
                        SongManager.setToPvpMusic(((PlayerParticipant) participant).player, opponents);
                        gymBattle.getGym().setTimeOfChallenge(((PlayerParticipant) participant).player);
                    }
                }
            }
            for (BattleParticipant participant : opponents) {
                if (participant instanceof PlayerParticipant) {
                    if(gymBattle.getGym().getPlayerTeams().containsKey(((PlayerParticipant) participant).player.getUniqueID())){
                        SongManager.setToPvpMusic(((PlayerParticipant) participant).player, participants);
                    }
                }
            }
        }
        else if(event.bc.rules instanceof CustomNPCBattle) {
            CustomNPCBattle battle=(CustomNPCBattle)event.bc.rules;
            NBTTagCompound data=battle.getNpc().getEntityData();
            for (BattleParticipant participant : participants) {
                if (participant instanceof PlayerParticipant) {
                    if(data.hasKey("battleTheme")){
                        SongManager.setCurrentSong(((PlayerParticipant) participant).player, data.getString("battleTheme"));
                    }
                    else {
                        SongManager.setToTrainerMusic(((PlayerParticipant) participant).player);
                    }
                }
            }
        }
        else{
            boolean wildBattle= PixelmonBattleUtils.isWildBattle(opponents);
            if(wildBattle){
                for (BattleParticipant participant : participants) {
                    if (participant instanceof PlayerParticipant) {
                        SongManager.setToWildMusic(((PlayerParticipant) participant).player);
                    }
                }
            }
            if(!wildBattle){
                for(BattleParticipant participant: participants){
                    if(participant instanceof PlayerParticipant){
                        SongManager.setToPvpMusic(((PlayerParticipant) participant).player, opponents);
                    }
                }
                for(BattleParticipant participant: opponents){
                    if(participant instanceof PlayerParticipant){
                        SongManager.setToPvpMusic(((PlayerParticipant) participant).player, participants);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onTurnEnd(TurnEndEvent event) {
        if(event.bcb.rules instanceof CustomNPCBattle) {
            CustomNPCBattle rules = (CustomNPCBattle)event.bcb.rules;
            CNPCBattleEvent.TurnEnd npcEvent = new CNPCBattleEvent.TurnEnd(new NPCWrapper(rules.getNpc()), new PlayerWrapper(event.bcb.getPlayers().get(0).player), event.bcb);
            for(ScriptContainer s : ((CustomNPCBattle)event.bcb.rules).getNpc().script.getScripts()) {
                s.run("turnEnd", npcEvent);
            }
        }
    }

    @SubscribeEvent
    public void onBattleEnd(BattleEndEvent event)
    {
        if(event.bc.rules instanceof GymBattleRules){
            BattleResults results=event.results.get(event.bc.participants.get(0));
            if (results == BattleResults.VICTORY && event.cause!= EnumBattleEndCause.FORCE) {
                PermissionUtils.addPermissionNode(((PlayerParticipant)event.bc.participants.get(0).getParticipantList()[0]).player, "badge."+((GymBattleRules) event.bc.rules).getGym().getName().replace(" ","_").toLowerCase()+".player");
                SongManager.setRouteSong(((PlayerParticipant)event.bc.participants.get(0).getParticipantList()[0]).player);
            }
            else{
                ((GymBattleRules) event.bc.rules).getGym().challengingPlayer.getCapability(OOPlayerProvider.OO_DATA, null).setBackupFullpos(null);
                ((GymBattleRules) event.bc.rules).getGym().challengingPlayer=null;
                WorldFunctions.warpToSafeZone(new PlayerWrapper(((PlayerParticipant)event.bc.participants.get(0).getParticipantList()[0]).player));
            }
            SongManager.setRouteSong(((PlayerParticipant)event.bc.participants.get(1).getParticipantList()[0]).player);
            PlayerParty.emptyParty(((GymBattleRules) event.bc.rules).getGym().currentLeader);
        }
        else if(event.bc.rules instanceof CustomNPCBattle) {
            EntityPlayerMP mcPlayer = event.getPlayers().get(0);
            BattleResults results = event.results.get(event.bc.participants.get(0));
            Pixelmon.instance.network.sendTo(new PlayerDeath(), mcPlayer);
            CustomNPCBattle battle = (CustomNPCBattle) event.bc.rules;
            BattleRegistry.deRegisterBattle(event.bc);
            if (results == BattleResults.VICTORY) {
                if(battle.getNpc().getEntityData().hasKey("victoryTheme")){
                    SongManager.setCurrentSong(mcPlayer, battle.getNpc().getEntityData().getString("victoryTheme"));
                }
                else{
                    SongManager.setCurrentSong(mcPlayer, GoldenGlow.songManager.victoryDefault);
                }
                NoppesUtilServer.openDialog(mcPlayer, battle.getNpc(), battle.getWinDialog());
            }
            if (results == BattleResults.DEFEAT) {
                SongManager.setRouteSong(mcPlayer);
                NoppesUtilServer.openDialog(mcPlayer, battle.getNpc(), battle.getLoseDialog());
            }
        }
        else if(event.bc.rules instanceof DoubleNPCBattle){
            DoubleNPCBattle rules=(DoubleNPCBattle)event.bc.rules;
            rules.getFirstNpc().getEntityData().setBoolean("inBattle", false);
            rules.getSecondNpc().getEntityData().setBoolean("inBattle", false);
            rules.getSecondPokemon().unloadEntity();
            rules.getFirstPokemon().unloadEntity();
        }
        else {
            for(BattleParticipant participant:event.bc.participants){
                if(participant instanceof PlayerParticipant){
                    if(event.results.get(participant)==BattleResults.DEFEAT || (!PixelmonBattleUtils.isWildBattle(event.bc.participants)&&event.results.get(participant)!=BattleResults.VICTORY)){
                        WorldFunctions.warpToSafeZone(new PlayerWrapper(((PlayerParticipant) participant).player));
                    }
                    else {
                        SongManager.setRouteSong(((PlayerParticipant) participant).player);
                    }
                }
            }
        }
    }
}

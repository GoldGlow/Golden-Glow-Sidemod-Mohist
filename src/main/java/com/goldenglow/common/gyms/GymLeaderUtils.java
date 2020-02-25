package com.goldenglow.common.gyms;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.teams.PlayerParty;
import com.goldenglow.common.util.FullPos;
import com.goldenglow.common.util.PermissionUtils;
import com.goldenglow.common.util.Reference;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GymLeaderUtils {
    public static void openGym(String gymName){
        openGym(GoldenGlow.gymManager.getGym(gymName));
    }

    public static void openGym(Gym gym){
        if(!gym.open){
            gym.open=true;
            FMLCommonHandler.instance().getMinecraftServerInstance().sendMessage(new TextComponentString(Reference.darkPurple+gym.name+" has just opened!"));
        }
    }

    public static void closeGym(String gymName){
        closeGym(GoldenGlow.gymManager.getGym(gymName));
    }

    public static void closeGym(Gym gym){
        gym.open=false;
        gym.queue.clear();
        if(gym.currentLeader!=null){
            GymLeaderUtils.stopTakingChallengers(gym, gym.currentLeader);
        }
        FMLCommonHandler.instance().getMinecraftServerInstance().sendMessage(new TextComponentString(Reference.darkPurple+gym.name+" is now closed!"));
    }

    public static void takeChallengers(String gymName, EntityPlayerMP leader){
        takeChallengers(GoldenGlow.gymManager.getGym(gymName), leader);
    }

    public static void takeChallengers(Gym gym, EntityPlayerMP leader){
        if(gym.currentLeader==null){
            gym.currentLeader=leader;
            PlayerParty.backupTeam(leader, Reference.gymsDir+"/backups/");
            PlayerParty.emptyParty(leader);
            IPlayerData playerData = leader.getCapability(OOPlayerProvider.OO_DATA, null);
            playerData.setBackupFullpos(new FullPos(leader));
            FullPos gymPos=new FullPos(gym.world, gym.warpPos);
            gymPos.warpToWorldPos(leader);
        }
        else{
            leader.sendMessage(new TextComponentString(Reference.red+"Another leader is taking on challenges already!"));
        }
    }

    public static void stopTakingChallengers(String gymName, EntityPlayerMP leader){
        stopTakingChallengers(GoldenGlow.gymManager.getGym(gymName), leader);
    }

    public static void stopTakingChallengers(Gym gym, EntityPlayerMP leader){
        gym.currentLeader=null;
        IPlayerData playerData = leader.getCapability(OOPlayerProvider.OO_DATA, null);
        FullPos backupPos=playerData.getBackupFullpos();
        backupPos.warpToWorldPos(leader);
        PlayerParty.loadSavedTeam(leader, Reference.gymsDir+"/backups/");
        if(gym.challengingPlayer!=null){
            EntityPlayerMP challenger = gym.challengingPlayer;
            IPlayerData challengerData = challenger.getCapability(OOPlayerProvider.OO_DATA, null);
            FullPos returnPos = challengerData.getBackupFullpos();
            returnPos.warpToWorldPos(challenger);
            challengerData.setBackupFullpos(null);
            gym.challengingPlayer = null;
        }
        playerData.setBackupFullpos(null);
        PermissionUtils.unsetPermissionsWithStart(leader, "gymleader.active");
    }

    public static void nextInQueue(String gymName, EntityPlayerMP leader){
        nextInQueue(GoldenGlow.gymManager.getGym(gymName), leader);
    }

    public static void nextInQueue(Gym gym, EntityPlayerMP leader){
        if(gym.queue.size()>0) {
            if (gym.challengingPlayer != null) {
                EntityPlayerMP challenger = gym.challengingPlayer;
                IPlayerData playerData = challenger.getCapability(OOPlayerProvider.OO_DATA, null);
                FullPos returnPos = playerData.getBackupFullpos();
                returnPos.warpToWorldPos(challenger);
                gym.challengingPlayer = null;
            }
            gym.challengingPlayer = gym.queue.get(0);
            gym.queue.remove(0);
            IPlayerData challengerData = gym.challengingPlayer.getCapability(OOPlayerProvider.OO_DATA, null);
            challengerData.setBackupFullpos(new FullPos(gym.challengingPlayer));
            FullPos challengerWarpTo=new FullPos(gym.world, gym.challengerWarp);
            challengerWarpTo.warpToWorldPos(gym.challengingPlayer);
        }else if(gym.challengingPlayer != null){
            EntityPlayerMP challenger = gym.challengingPlayer;
            IPlayerData playerData = challenger.getCapability(OOPlayerProvider.OO_DATA, null);
            FullPos returnPos = playerData.getBackupFullpos();
            returnPos.warpToWorldPos(challenger);
            gym.challengingPlayer = null;
            leader.sendMessage(new TextComponentString(Reference.red+"There's no one in the queue!"));
        }else{
            leader.sendMessage(new TextComponentString(Reference.red+"There's no one in the queue!"));
        }
    }

    public static void startGymBattle(String gymName){
        startGymBattle(GoldenGlow.gymManager.getGym(gymName));
    }

    public static void startGymBattle(Gym gym){
        for(Pokemon pokemon:gym.playerTeams.get(gym.currentLeader.getUniqueID()).getMembers()){
            Pixelmon.storageManager.getParty(gym.currentLeader).add(pokemon);
        }
        BattleParticipant challenger=new PlayerParticipant(gym.challengingPlayer, Pixelmon.storageManager.getParty(gym.challengingPlayer).getAndSendOutFirstAblePokemon(gym.challengingPlayer));
        BattleParticipant leader=new PlayerParticipant(gym.currentLeader, Pixelmon.storageManager.getParty(gym.currentLeader).getAndSendOutFirstAblePokemon(gym.currentLeader));
        BattleRegistry.startBattle(new BattleParticipant[]{challenger}, new BattleParticipant[]{leader}, gym.rules);
    }
}

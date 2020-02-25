package com.goldenglow.common.util;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.util.scripting.WorldFunctions;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.client.gui.custom.overlays.ScoreboardLocation;
import com.pixelmonmod.pixelmon.comm.packetHandlers.customOverlays.CustomScoreboardDisplayPacket;
import com.pixelmonmod.pixelmon.comm.packetHandlers.customOverlays.CustomScoreboardUpdatePacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.handler.data.IQuestCategory;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.WorldWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Scoreboards {
    public enum EnumScoreboardType{
        NONE,
        DEBUG,
        QUEST_LOG,
        CHAIN_INFO,
        ONLINE_FRIENDS
    }

    public static void buildScoreboard(EntityPlayerMP player){
        EnumScoreboardType type=player.getCapability(OOPlayerProvider.OO_DATA, null).getScoreboardType();
        if(type==EnumScoreboardType.NONE){
            Pixelmon.network.sendTo(new CustomScoreboardDisplayPacket(ScoreboardLocation.RIGHT_MIDDLE, false), player);
            return;
        }
        else if(type==EnumScoreboardType.DEBUG){
            Scoreboards.buildDebugScoreboard(player);
            return;
        }
        else if(type==EnumScoreboardType.QUEST_LOG){
            Scoreboards.buildQuestLogScoreboard(player);
        }
        else if(type==EnumScoreboardType.CHAIN_INFO){
            Scoreboards.buildChainsScoreboard(player);
        }
        else if(type==EnumScoreboardType.ONLINE_FRIENDS){
            Scoreboards.buildOnlineFriendsScoreboard(player);
        }
    }

    public static void buildDebugScoreboard(EntityPlayerMP player){
        IPlayerData playerData=player.getCapability(OOPlayerProvider.OO_DATA, null);
        ArrayList<String> lines = new ArrayList<>();
        lines.add("Route");
        if(PermissionUtils.checkPermission(player, "*")) {
            lines.add("Song");
        }
        lines.add("Time");
        if(PermissionUtils.checkPermission(player, "*")) {
            lines.add("Day");
        }
        ArrayList<String> scores = new ArrayList<>();
        if(playerData.getRoute()!=null)
            scores.add(playerData.getRoute().displayName);
        else
            scores.add("null");
        if(PermissionUtils.checkPermission(player, "*")) {
            scores.add(playerData.getCurrentSong());
        }
        Long time=player.getServerWorld().getWorldTime()%24000L;
        String extra="";
        if((time%1000)*60/1000<10){
            extra="0";
        }
        scores.add(((time/1000)+6)%24+":"+extra+(time%1000)*60/1000);
        if(PermissionUtils.checkPermission(player, "*")) {
            PlayerWrapper playerWrapper = new PlayerWrapper(player);
            scores.add(WorldFunctions.getCurrentDay((WorldWrapper) playerWrapper.getWorld()) + "");
        }
        Pixelmon.network.sendTo(new CustomScoreboardUpdatePacket("Debug", lines, scores), player);
        Pixelmon.network.sendTo(new CustomScoreboardDisplayPacket(ScoreboardLocation.RIGHT_MIDDLE, true), player);
    }

    public static void buildQuestLogScoreboard(EntityPlayerMP player){
        PlayerWrapper playerWrapper=new PlayerWrapper(player);
        IQuest[] activeQuests=playerWrapper.getActiveQuests();
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> scores = new ArrayList<>();
        for(IQuest quest:activeQuests){
            String questTitle="";
            IQuestCategory category=quest.getCategory();
            if(category.getName().toLowerCase().contains("main")){
                questTitle=Reference.gold;
            }
            else {
                questTitle=Reference.aqua;
            }
            questTitle+=quest.getName();
            lines.add(questTitle);
            IQuestObjective[] objectives=quest.getObjectives(playerWrapper);
            boolean isComplete=true;
            for(IQuestObjective objective: objectives){
                if(!objective.isCompleted()){
                    isComplete=false;
                    break;
                }
            }
            if(isComplete){
                scores.add(Reference.darkGreen+"COMPLETE");
            }
            else{
                scores.add("");
                for(IQuestObjective objective: objectives){
                    if(objective.isCompleted()){
                        lines.add(" -"+Reference.strike+objective.getText().replace("(read)", ""));
                        scores.add("");
                    }
                    else{
                        lines.add(" -"+objective.getText().split(":")[0].replace("(unread)", ""));
                        scores.add(objective.getProgress()+"/"+objective.getMaxProgress());
                    }
                }
            }
        }
        Pixelmon.network.sendTo(new CustomScoreboardUpdatePacket("Quest Log", lines, scores), player);
        Pixelmon.network.sendTo(new CustomScoreboardDisplayPacket(ScoreboardLocation.RIGHT_MIDDLE, true), player);
    }

    public static void buildChainsScoreboard(EntityPlayerMP player){
        OOPlayerData data = (OOPlayerData)player.getCapability(OOPlayerProvider.OO_DATA, null);
        ArrayList<String> lines=new ArrayList<String>();
        ArrayList<String> scores=new ArrayList<String>();
        if(data.getChainSpecies()!=null)
            addChain("Capture", data.getChainSpecies().name, data.getCaptureChain(), lines, scores);
        if(data.getLastKOPokemon()!=null)
            addChain("Battle", data.getLastKOPokemon().name, data.getKOChain(), lines, scores);
        if(data.getChainSpecies()!=null && data.getChainSpecies()==data.getLastKOPokemon())
            addChain("Combined", data.getChainSpecies().name, data.getCaptureChain()+data.getKOChain(), lines, scores);
        Pixelmon.network.sendTo(new CustomScoreboardUpdatePacket("Chains", lines, scores), player);
        Pixelmon.network.sendTo(new CustomScoreboardDisplayPacket(ScoreboardLocation.RIGHT_MIDDLE, true), player);
    }

    static void addChain(String chainName, String chainSpecies, int chainCount, ArrayList<String> lines, ArrayList<String> scores) {
        if(lines.isEmpty() || !lines.get(lines.size()-1).endsWith("=")) {
            lines.add("=================="); scores.add("");
        }
        lines.add(chainName+" chain"); scores.add("");
        lines.add(chainSpecies); scores.add(chainCount+"");
        lines.add("=================="); scores.add("");
    }

    public static void buildOnlineFriendsScoreboard(EntityPlayerMP player){
        List<UUID> friendList=player.getCapability(OOPlayerProvider.OO_DATA, null).getFriendList();
        ArrayList<String> lines=new ArrayList<String>();
        ArrayList<String> scores=new ArrayList<String>();
        for(UUID friend:friendList){
            EntityPlayerMP friendEntity=FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(friend);
            if(friendEntity!=null){
                lines.add(Reference.darkGreen+friendEntity.getName());
                scores.add(GoldenGlow.routeManager.getRoute(friendEntity).displayName);
            }
        }
        Pixelmon.network.sendTo(new CustomScoreboardUpdatePacket("Online Friends", lines, scores), player);
        Pixelmon.network.sendTo(new CustomScoreboardDisplayPacket(ScoreboardLocation.RIGHT_MIDDLE, true), player);
    }
}

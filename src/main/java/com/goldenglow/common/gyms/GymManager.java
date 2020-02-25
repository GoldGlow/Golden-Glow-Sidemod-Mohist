package com.goldenglow.common.gyms;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.teams.Team;
import com.goldenglow.common.teams.TeamManager;
import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.PermissionUtils;
import com.goldenglow.common.util.Reference;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.rules.clauses.BattleClause;
import com.pixelmonmod.pixelmon.battles.rules.clauses.BattleClauseRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GymManager {
    List<Gym> gyms=new ArrayList<Gym>();

    File dir;

    public void init(){
        dir=new File(Reference.gymsDir);
        if(!dir.exists()) {
            if (!dir.getParentFile().exists())
                dir.getParentFile().mkdirs();
            try {
                dir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            this.loadGyms();
    }

    public void loadGyms(){
        GGLogger.info("Loading Gym...");
        this.gyms.clear();
        try {
            for (File f : Objects.requireNonNull(dir.listFiles())) {
                if (f.getName().endsWith(".json")) {
                    loadGym(f.getName().replace(".json", ""));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGym(String gymName) throws IOException {
        InputStream iStream = new FileInputStream(new File(dir, gymName+".json"));
        JsonObject json=new JsonParser().parse(new InputStreamReader(iStream, StandardCharsets.UTF_8)).getAsJsonObject();
        Gym gym=new Gym();

        if(json.has("name")){
            gym.name=json.get("name").getAsString();
        }

        if(json.has("warpCoords")){
            JsonObject coords=json.get("warpCoords").getAsJsonObject();
            gym.warpPos=new BlockPos(coords.get("warpX").getAsInt(), coords.get("warpY").getAsInt(), coords.get("warpZ").getAsInt());
        }

        if(json.has("challengerWarp")){
            JsonObject coords=json.get("challengerWarp").getAsJsonObject();
            gym.challengerWarp=new BlockPos(coords.get("warpX").getAsInt(), coords.get("warpY").getAsInt(), coords.get("warpZ").getAsInt());
        }

        if(json.has("world")){
            int worldUUID=json.get("world").getAsInt();
            World w = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(worldUUID);
            if(w!=null)
                gym.world= w;
            else {
                GoldenGlow.logger.error("Gym World not found! - Gym: "+gymName);
                return;
            }
        }

        if(json.has("gymTheme")){
            gym.theme=json.get("gymTheme").getAsString();
        }

        if(json.has("battleRules")){
            GymBattleRules rules=new GymBattleRules(gym);
            rules.fullHeal=true;
            rules.turnTime=90;
            JsonObject battleRules=json.getAsJsonObject("battleRules");
            if(battleRules.has("levelCap"))
                rules.levelCap=battleRules.get("levelCap").getAsInt();
            if(battleRules.has("clauses")){
                JsonArray clausesJson=battleRules.getAsJsonArray("clauses");
                List<BattleClause> clauses=new ArrayList<BattleClause>();
                for(JsonElement clause:clausesJson){
                    clauses.add(BattleClauseRegistry.getClauseRegistry().getClause(clause.getAsString()));
                }
                rules.setNewClauses(clauses);
            }
        }

        File teamsDir=new File(Reference.gymsDir+"/"+gym.name);
        if(!teamsDir.exists()) {
            try {
                teamsDir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            this.loadTeams(gym, teamsDir);
    }

    public void loadTeams(Gym gym, File teamsDir){
        for (File f : Objects.requireNonNull(teamsDir.listFiles())) {
            if (f.getName().endsWith(".team")) {
                loadTeam(f.getName().replace(".team", ""), gym);
            }
        }
    }

    public void loadTeam(String file, Gym gym){
        Team playerTeam=new Team(file);
        String path=Reference.gymsDir+"/"+gym.name+file+".team";
        List<Pokemon> pokemon= TeamManager.singleTeamFromFile(path);
        for(Pokemon pixelmon:pokemon){
            playerTeam.addMember(pixelmon);
        }
        gym.playerTeams.put(UUID.fromString(file), playerTeam);
    }

    public Gym getGym(String name){
        for(Gym gym: this.gyms){
            if(gym.name.equals(name)){
                return gym;
            }
        }
        return null;
    }

    public void removeFromQueues(EntityPlayerMP player){
        for(Gym gym:this.gyms){
            if(gym.queue.contains(player)){
                gym.queue.remove(player);
            }
        }
    }

    public Gym challengingGym(EntityPlayerMP player){
        for(Gym gym:this.gyms){
            if(gym.challengingPlayer.getName().equals(player.getName())){
                return gym;
            }
        }
        return null;
    }

    public Gym leadingGym(EntityPlayerMP player){
        for(Gym gym:this.gyms){
            if(gym.currentLeader.getName().equals(player.getName())){
                return gym;
            }
        }
        return null;
    }

    public List<EntityPlayerMP> hasGymLeaderOnline(Gym gym){
        List<EntityPlayerMP> onlinePlayers=FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
        ArrayList<EntityPlayerMP> onlineLeaders=new ArrayList<EntityPlayerMP>();
        for(EntityPlayerMP player:onlinePlayers){

            if(PermissionUtils.checkPermission(player, "staff.gym_leader."+gym.getName().toLowerCase().replace(" ","_")))
                onlineLeaders.add(player);
        }
        return onlineLeaders;
    }

    public List<Gym> getGyms(){
        return this.gyms;
    }
}

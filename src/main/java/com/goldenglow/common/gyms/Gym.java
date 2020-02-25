package com.goldenglow.common.gyms;

import com.goldenglow.common.teams.Team;
import com.goldenglow.common.util.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Gym {
    String name;
    BlockPos warpPos;
    BlockPos challengerWarp;
    World world;
    HashMap<UUID, Team> playerTeams;
    String theme;
    public boolean open;
    GymBattleRules rules;
    public EntityPlayerMP currentLeader;
    public EntityPlayerMP challengingPlayer;
    HashMap<UUID, Long> timeOfChallenge;
    ArrayList<EntityPlayerMP> queue;

    public Gym(){
        this.name="Cottonee gym";
        this.warpPos=new BlockPos(6, 6,6 );
        this.world= FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
        this.playerTeams=new HashMap<UUID, Team>();
        this.open=false;
        this.theme="obscureobsidian:trainer.blue";
        this.rules=new GymBattleRules(this);
        this.currentLeader=null;
        this.challengingPlayer=null;
        this.queue=new ArrayList<EntityPlayerMP>();
    }

    public HashMap<UUID, Team> getPlayerTeams(){ return this.playerTeams; }

    public void setTimeOfChallenge(EntityPlayerMP player){
        if(this.timeOfChallenge.containsKey(player.getUniqueID())){
            this.timeOfChallenge.remove(player.getUniqueID());
        }
        this.timeOfChallenge.put(player.getUniqueID(), System.currentTimeMillis());
    }

    public String getName(){
        return this.name;
    }

    public void JoinQueue(EntityPlayerMP player){
        if(this.open) {
            if (this.timeOfChallenge.containsKey(player.getUniqueID())) {
                if (this.timeOfChallenge.get(player.getUniqueID()) + 3600000L <= System.currentTimeMillis()) {
                    this.queue.add(player);
                } else {
                    player.sendMessage(new TextComponentString(Reference.red + "You must wait an hour before challenging again!"));
                }
            } else {
                this.queue.add(player);
            }
        }
    }
}

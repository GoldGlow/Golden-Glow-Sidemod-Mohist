package com.goldenglow.common.battles.npc;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.teams.Team;
import com.goldenglow.common.util.Reference;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.TrainerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.comm.SetTrainerData;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.entities.npcs.NPCTrainer;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.List;

public class CustomBattleHandler
{
    static GoldenGlow mod;
    public static CustomBattleHandler instance;

    public static List<CustomNPCBattle> battles = new ArrayList<CustomNPCBattle>();

    public CustomBattleHandler()
    {
        this.mod = GoldenGlow.instance;
        this.instance = this;
    }

    public static void createCustomBattle(EntityPlayerMP player, String teamName, int initDialogID, int winDialogID, int loseDialogID, EntityNPCInterface npc) {
        Team npcTeam;
        int[] levels=new int[6];
        if(teamName!=null) {
            npcTeam = GoldenGlow.instance.teamManager.getTeam(teamName);
            for(int i=0;i<npcTeam.getMembers().size();i++){
                levels[i]=npcTeam.getMember(i).getLevel();
            }
        }else{
            npcTeam = new Team("");
        }

        if (BattleRegistry.getBattle(player) != null){
            player.sendMessage(new TextComponentString(Reference.messagePrefix +Reference.red + "Cannot Battle!"));
            return;
        }else
            try {
                NPCTrainer trainer = (NPCTrainer) PixelmonEntityList.createEntityByName(npc.display.getName(), player.getEntityWorld());
                SetTrainerData data=new SetTrainerData("npc", " ", " ", " ", 0, null);
                trainer.update(data);
                trainer.loadPokemon(npcTeam.getMembers());
                trainer.setPosition(npc.posX,npc.posY,npc.posZ);
                ArrayList<Pokemon> playerParty = new ArrayList<Pokemon>();
                EntityPixelmon pixelmon= Pixelmon.storageManager.getParty(player).getAndSendOutFirstAblePokemon(player);
                if(pixelmon!=null)
                {
                    PlayerParticipant playerParticipant = new PlayerParticipant(player, pixelmon);
                    TrainerParticipant trainerParticipant = new TrainerParticipant(trainer, player, 1);

                    Dialog winDialog = DialogController.instance.dialogs.get(winDialogID);
                    Dialog loseDialog = DialogController.instance.dialogs.get(loseDialogID);
                    Dialog initDialog = DialogController.instance.dialogs.get(initDialogID);
                    CustomNPCBattle rules=new CustomNPCBattle(npc, initDialog, winDialog, loseDialog);
                    rules.setRemainingNPCPokemon(trainerParticipant.countAblePokemon());
                    for(int i=0;i<npcTeam.getMembers().size();i++){
                        trainer.getPokemonStorage().get(i).setLevel(levels[i]);
                    }
                    BattleRegistry.startBattle(new BattleParticipant[]{playerParticipant}, new BattleParticipant[] {trainerParticipant}, rules);
                }else{
                    player.sendMessage(new TextComponentString(Reference.messagePrefix + Reference.red + "You have no pokemon that are able to battle!"));
                }
            }
            catch (Exception e){}
    }

    public static void createCustomNPCBattle(EntityNPCInterface firstNpc, String firstTeam, EntityNPCInterface secondNpc, String secondTeam){
        World world=firstNpc.getEntityWorld();
        Team firstNpcTeam;
        Team secondNpcTeam;
        EntityPixelmon firstPokemon;
        EntityPixelmon secondPokemon;
        if(firstTeam!=null) {
            Pokemon first = GoldenGlow.instance.teamManager.getTeam(firstTeam).getMember(0);
            firstPokemon= new EntityPixelmon(world);
            firstPokemon.setPokemon(first);
            firstPokemon.canDespawn=false;
            firstPokemon.setPosition(firstNpc.posX, firstNpc.posY, firstNpc.posZ);
            firstPokemon.setSpawnLocation(firstPokemon.getDefaultSpawnLocation());
        }else{
            firstPokemon=null;
        }
        if(secondTeam!=null) {
            Pokemon first = GoldenGlow.instance.teamManager.getTeam(secondTeam).getMember(0);
            secondPokemon= new EntityPixelmon(world);
            secondPokemon.setPokemon(first);
            firstPokemon.canDespawn=false;
            secondPokemon.setSpawnLocation(secondPokemon.getDefaultSpawnLocation());
            secondPokemon.setSpawnLocation(secondPokemon.getDefaultSpawnLocation());
            secondPokemon.setPosition(secondNpc.posX, secondNpc.posY, secondNpc.posZ);
        }else{
            secondPokemon=null;
        }
        try {
            if(firstPokemon!=null&&secondPokemon!=null) {
                WildPixelmonParticipant firstTrainer = new WildPixelmonParticipant(firstPokemon);
                firstTrainer.setNewPositions(firstNpc.getPosition());
                WildPixelmonParticipant secondTrainer = new WildPixelmonParticipant(secondPokemon);
                secondTrainer.setNewPositions(secondNpc.getPosition());
                if (!firstNpc.getEntityData().hasKey("inBattle")) {
                    firstNpc.getEntityData().setBoolean("inBattle", false);
                }
                if (!secondNpc.getEntityData().hasKey("inBattle")) {
                    secondNpc.getEntityData().setBoolean("inBattle", false);
                }

                DoubleNPCBattle rules = new DoubleNPCBattle(firstNpc, firstPokemon, secondNpc, secondPokemon);

                if (!firstNpc.getEntityData().getBoolean("inBattle") && !secondNpc.getEntityData().getBoolean("inBattle")) {
                    world.spawnEntity(firstPokemon);
                    world.spawnEntity(secondPokemon);
                    firstNpc.getEntityData().setBoolean("inBattle", true);
                    secondNpc.getEntityData().setBoolean("inBattle", true);
                    BattleRegistry.startBattle(new BattleParticipant[]{firstTrainer}, new BattleParticipant[]{secondTrainer}, rules);
                }
            }
        }catch (Exception e){
        }
    }
}
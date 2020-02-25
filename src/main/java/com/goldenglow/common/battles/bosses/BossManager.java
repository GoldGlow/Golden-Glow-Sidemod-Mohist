package com.goldenglow.common.battles.bosses;

import com.goldenglow.common.battles.bosses.fights.BossBase;
import com.goldenglow.common.battles.bosses.fights.BossBirdBear;
import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.Reference;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.battles.TurnEndEvent;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class BossManager {

    private static HashMap<String, BossBase> bosses = new HashMap<>();

    public static void register(String registryName, BossBase bossBase) {
        if(!bosses.containsKey(registryName))
            bosses.put(registryName, bossBase);
    }

    public static void init() {
        GGLogger.info("Loading Bosses...");
        //Load and Register json Boss files
        File dir = new File(Reference.bossDir);
        if(!dir.exists())
            dir.mkdir();
        for(File f : dir.listFiles()) {
            if(f.getName().endsWith(".json")) {
                try {
                    bosses.put(f.getName().replace(".json",""), BossBase.loadFromFile(f));
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        //Register hard-coded bosses here
        bosses.put("bird_bear", new BossBirdBear());
    }

    public static BattleControllerBase startBossBattle(EntityPlayerMP player, String bossName) {
        if(bosses.containsKey(bossName)) {
            BossParticipant bossParticipant = new BossParticipant(bosses.get(bossName), player);
            PlayerParticipant playerParticipant = new PlayerParticipant(player, new EntityPixelmon[]{Pixelmon.storageManager.getParty(player).getAndSendOutFirstAblePokemon(player)});
            BattleControllerBase bc = BattleRegistry.startBattle(new BattleParticipant[]{playerParticipant}, new BattleParticipant[]{bossParticipant}, new BossBattleRules(bossParticipant));
        }
        return null;
    }

    public static String getBosses() {
        StringBuilder b = new StringBuilder();
        for(String s : bosses.keySet()) {
            b.append(s);
        }
        return b.toString();
    }

    @SubscribeEvent
    public static void onTurnEnd(TurnEndEvent event) {
        if(event.bcb.rules instanceof BossBattleRules) {
            ((BossBattleRules)event.bcb.rules).bossParticipant.onTurnEnd(event.bcb);
        }
    }
}

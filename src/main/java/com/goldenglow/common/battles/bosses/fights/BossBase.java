package com.goldenglow.common.battles.bosses.fights;

import com.goldenglow.common.battles.bosses.BossParticipant;
import com.goldenglow.common.battles.bosses.phase.Phase;
import com.goldenglow.common.battles.bosses.phase.Trigger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.EnumType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BossBase {

    Pokemon pokemon;
    EnumType[] initialTypes;
    List<Phase> phases = new ArrayList<>();

    BossBase(){}

    public BossBase(Pokemon pokemon, List<Phase> phases) {
        this.pokemon = pokemon;
        this.phases = phases;
    }

    public static BossBase loadFromFile(File file) throws FileNotFoundException {
        BossBase boss = new BossBase();

        InputStream iStream = new FileInputStream(file);
        JsonObject json = new JsonParser().parse(new InputStreamReader(iStream, StandardCharsets.UTF_8)).getAsJsonObject();

        //Load Base Pokemon
        JsonObject p = json.getAsJsonObject("pokemon");
        Optional<EnumSpecies> species = EnumSpecies.contains(p.get("species").getAsString());
        if(species.isPresent()) {
            boss.pokemon = Pixelmon.pokemonFactory.create(species.get());
            if (p.has("nickname"))
                boss.pokemon.setNickname(p.get("nickname").getAsString());
            if (p.has("level"))
                boss.pokemon.setLevel(p.get("level").getAsInt());
            if (p.has("form"))
                boss.pokemon.setForm(p.get("form").getAsInt());
            if (p.has("ability"))
                boss.pokemon.setAbility(p.get("ability").getAsString());
            if (p.has("nature"))
                boss.pokemon.setNature(EnumNature.valueOf(p.get("nature").getAsString()));
            if (p.has("growth"))
                boss.pokemon.setGrowth(EnumGrowth.growthFromString(p.get("growth").getAsString()));
            if (p.has("heldItem"))
                boss.pokemon.setHeldItem(new ItemStack(Item.getByNameOrId(p.get("heldItem").getAsString())));
            if (p.has("type")) {
                JsonArray array = p.getAsJsonArray("type");
                EnumType[] types = new EnumType[2];
                for (int i = 0; i < types.length; i++) {
                    types[i] = EnumType.valueOf(array.get(i).getAsString());
                }
                boss.initialTypes = types;
            }
            if (p.has("stats")) {
                JsonObject stats = p.getAsJsonObject("stats");
                if (stats.has("hp"))
                    boss.pokemon.getStats().hp = stats.get("hp").getAsInt();
                if (stats.has("atk"))
                    boss.pokemon.getStats().attack = stats.get("atk").getAsInt();
                if (stats.has("spAtk"))
                    boss.pokemon.getStats().specialAttack = stats.get("spAtk").getAsInt();
                if (stats.has("def"))
                    boss.pokemon.getStats().defence = stats.get("def").getAsInt();
                if (stats.has("spDef"))
                    boss.pokemon.getStats().specialDefence = stats.get("spDef").getAsInt();
                if (stats.has("speed"))
                    boss.pokemon.getStats().speed = stats.get("speed").getAsInt();
            }
            if (p.has("moveset")) {
                JsonArray array = p.getAsJsonArray("moveset");
                boss.pokemon.getMoveset().clear();
                for (JsonElement e : array) {
                    boss.pokemon.getMoveset().add(new Attack(e.getAsString()));
                }
            }

            //Load Phases
            if (json.has("phases")) {
                JsonArray phases = json.getAsJsonArray("phases");
                for (JsonElement e : phases) {
                    JsonObject phaseObj = e.getAsJsonObject();

                    JsonArray triggersArray = phaseObj.getAsJsonArray("triggers");
                    List<Trigger> triggers = new ArrayList<>();
                    for (JsonElement t : triggersArray) {
                        JsonObject triggerObj = t.getAsJsonObject();
                        Trigger trigger = new Trigger();
                        if (triggerObj.has("hpPercentage"))
                            trigger.hpPercentage = triggerObj.get("hpPercentage").getAsFloat();
                        if (triggerObj.has("status"))
                            trigger.status = StatusType.valueOf(triggerObj.get("status").getAsString());
                        if (triggerObj.has("hitByType"))
                            trigger.hitByType = EnumType.valueOf(triggerObj.get("hitByType").getAsString());
                        if (triggerObj.has("turn"))
                            trigger.turnNumber = triggerObj.get("turn").getAsInt();
                        triggers.add(trigger);
                    }

                    Phase phase = new Phase(triggers.toArray(new Trigger[0]));

                    if (phaseObj.has("nickname"))
                        phase.setNickname(phaseObj.get("nickname").getAsString());
                    if (phaseObj.has("ability"))
                        phase.setAbility(AbilityBase.getAbility(phaseObj.get("ability").getAsString()).get());
                    if (phaseObj.has("nature"))
                        phase.setNature(EnumNature.valueOf(phaseObj.get("nature").getAsString()));
                    if (phaseObj.has("form"))
                        phase.setForm(phaseObj.get("form").getAsInt());
                    if (phaseObj.has("growth"))
                        phase.setGrowth(EnumGrowth.growthFromString(phaseObj.get("growth").getAsString()));
                    if (phaseObj.has("heldItem"))
                        phase.setHeldItem(new ItemStack(Item.getByNameOrId(phaseObj.get("heldItem").getAsString())));
                    if (phaseObj.has("type")) {
                        JsonArray array = phaseObj.getAsJsonArray("type");
                        EnumType[] types = new EnumType[2];
                        for (int i = 0; i < types.length; i++) {
                            types[i] = EnumType.valueOf(array.get(i).getAsString());
                        }
                        phase.setType(types);
                    }
                    if (phaseObj.has("moveset")) {
                        JsonArray array = phaseObj.getAsJsonArray("moveset");
                        Moveset moveset = new Moveset();
                        for (JsonElement m : array) {
                            moveset.add(new Attack(m.getAsString()));
                        }
                        phase.setMoveset(moveset);
                    }

                    boss.phases.add(phase);
                }
            }
        }
        return boss;
    }

    public Pokemon getPokemon() {
        return this.pokemon;
    }

    public void phaseCheck(BattleControllerBase bc, BossParticipant participant) {
        for(Phase p : this.phases) {
            p.checkTriggers(participant, participant.controlledPokemon.get(0));
        }
    }

}

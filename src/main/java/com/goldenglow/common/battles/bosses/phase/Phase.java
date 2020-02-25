package com.goldenglow.common.battles.bosses.phase;

import com.goldenglow.common.battles.bosses.BossParticipant;
import com.goldenglow.common.util.GGLogger;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumType;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class Phase {

    Trigger[] triggers;

    String nickname;
    EnumType[] type;
    AbilityBase ability;
    EnumNature nature;
    Moveset moveset;
    int form = -1;
    EnumGrowth growth;
    ItemStack heldItem;

    public Phase(Trigger[] triggers) {
        this.triggers = triggers;
    }

    public void onPhaseChange(BossParticipant bossParticipant, PixelmonWrapper activePokemon) {
        GGLogger.info("Phase Change");
        if(nickname!=null)
            activePokemon.pokemon.setNickname(nickname);
        if(type!=null)
            activePokemon.setTempType(Arrays.asList(this.type));
        if(ability!=null) {
            activePokemon.setTempAbility(ability);
            activePokemon.tempAbility.applySwitchInEffect(activePokemon);
        }
        if(nature!=null)
            activePokemon.pokemon.setNature(nature);
        if(moveset!=null)
            activePokemon.setTemporaryMoveset(this.moveset);
        if(form!=-1)
            activePokemon.setForm(form);
        if(growth!=null)
            activePokemon.pokemon.setGrowth(growth);
        if(heldItem!=null)
            activePokemon.setHeldItem(heldItem);
    }

    public boolean checkTriggers(BossParticipant participant, PixelmonWrapper activePokemon) {
        if(participant.getCurrentPhase()!=this) {
            for (Trigger t : this.triggers) {
                if (t.conditionsMet(participant, activePokemon)) {
                    this.onPhaseChange(participant, activePokemon);
                    return true;
                }
            }
        }
        return false;
    }

    public Phase setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }
    public Phase setType(EnumType[] type) {
        this.type = type;
        return this;
    }
    public Phase setAbility(AbilityBase ability) {
        this.ability = ability;
        return this;
    }
    public Phase setNature(EnumNature nature) {
        this.nature = nature;
        return this;
    }
    public Phase setMoveset(Moveset moveset) {
        this.moveset = moveset;
        return this;
    }
    public Phase setForm(int form) {
        this.form = form;
        return this;
    }
    public Phase setGrowth(EnumGrowth growth) {
        this.growth = growth;
        return this;
    }
    public Phase setHeldItem(ItemStack heldItem) {
        this.heldItem = heldItem;
        return this;
    }

}

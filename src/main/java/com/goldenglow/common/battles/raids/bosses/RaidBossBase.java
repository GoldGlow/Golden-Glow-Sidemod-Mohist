package com.goldenglow.common.battles.raids.bosses;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

public class RaidBossBase {

    EntityPixelmon entity;
    WildPixelmonParticipant participant;

    public RaidBossBase(EntityPixelmon bossEntity) {
        this.entity = bossEntity;
    }

    public void onAttack(BattleControllerBase bc) {

    }

    public void onTurnEnd(BattleControllerBase bc) {

    }

    public void onBattleEnd(BattleControllerBase bc) {

    }

    public EntityPixelmon getEntity() {
        return entity;
    }

    public Pokemon getPokemonData() {
        return entity.getPokemonData();
    }

    public WildPixelmonParticipant getParticipant() {
        if(this.participant==null) {
            this.participant = new WildPixelmonParticipant(this.entity);
        }
        return this.participant;
    }
}

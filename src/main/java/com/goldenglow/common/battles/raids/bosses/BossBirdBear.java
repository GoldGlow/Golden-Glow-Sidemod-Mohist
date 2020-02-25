package com.goldenglow.common.battles.raids.bosses;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

public class BossBirdBear extends RaidBossBase {

    public BossBirdBear(EntityPixelmon bossEntity) {
        super(bossEntity);
        EntityPixelmon p = PokemonSpec.from("Fletchinder lvl:25").create(bossEntity.world);
        p.startRiding(bossEntity);
    }

    public void onAttack(BattleControllerBase bc) {

    }

    public void onTurnEnd(BattleControllerBase bc) {

    }

    public void onBattleEnd(BattleControllerBase bc) {

    }
}

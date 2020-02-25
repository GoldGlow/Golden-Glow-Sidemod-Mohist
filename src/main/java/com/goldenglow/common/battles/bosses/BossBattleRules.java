package com.goldenglow.common.battles.bosses;

import com.pixelmonmod.pixelmon.battles.rules.BattleRules;

public class BossBattleRules extends BattleRules {

    BossParticipant bossParticipant;

    public BossBattleRules(BossParticipant participant) {
        this.bossParticipant = participant;
    }

}

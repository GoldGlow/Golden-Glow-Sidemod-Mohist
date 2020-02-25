package com.goldenglow.common.battles.raids;

import com.pixelmonmod.pixelmon.battles.rules.BattleRules;

public class RaidBattleRules extends BattleRules {

    private RaidController raidController;

    public RaidBattleRules(RaidController controller) {
        this.raidController = controller;
    }

    public RaidController getRaidController() {
        return this.raidController;
    }
}

package com.goldenglow.common.gyms;

import com.pixelmonmod.pixelmon.battles.rules.BattleRules;

public class GymBattleRules extends BattleRules {
    Gym gym;

    public GymBattleRules(Gym gym){
        super();
        this.gym=gym;
    }

    public Gym getGym(){
        return this.gym;
    }
}

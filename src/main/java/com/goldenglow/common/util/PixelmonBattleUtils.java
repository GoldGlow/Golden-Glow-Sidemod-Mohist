package com.goldenglow.common.util;

import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;

import java.util.ArrayList;

/**
 * Created by JeanMarc on 6/15/2019.
 */
public class PixelmonBattleUtils {
    public static boolean isWildBattle(BattleParticipant[] opponents){
        for(BattleParticipant opponent: opponents){
            if(opponent instanceof WildPixelmonParticipant) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWildBattle(ArrayList<BattleParticipant> opponents){
        for(BattleParticipant opponent: opponents){
            if(opponent instanceof WildPixelmonParticipant) {
                return true;
            }
        }
        return false;
    }
}

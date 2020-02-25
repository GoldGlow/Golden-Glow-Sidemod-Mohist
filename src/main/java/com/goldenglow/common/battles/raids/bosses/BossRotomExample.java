package com.goldenglow.common.battles.raids.bosses;

import com.goldenglow.common.battles.raids.RaidBattleRules;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.items.heldItems.ItemLeftovers;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Random;

public class BossRotomExample extends RaidBossBase {

    Random random;

    int phase = 0;
    int[] forms = new int[] {4,2,1};

    public BossRotomExample(EntityPixelmon bossEntity) {
        super(bossEntity);
        bossEntity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200f);
        random = new Random();
    }

    public void onAttack(BattleControllerBase bc) {
        super.onAttack(bc);
    }

    public void onTurnEnd(BattleControllerBase bc) {
        super.onTurnEnd(bc);
        for(BattleControllerBase b : ((RaidBattleRules)bc.rules).getRaidController().getBattleList()) {
            PixelmonWrapper pw = b.getPokemonFromUUID(this.entity.getUniqueID());
            if (phase == 0) {
                changeForm(pw);
                pw.setTemporaryMoveset(new Moveset(new Attack[]{new Attack("Leaf Storm"), new Attack("Will-o-wisp"), new Attack("Thunderbolt"), null}));
            } else if (phase == 1 && pw.getHealthPercent() <= 75F) {
                changeForm(pw);
                pw.setHeldItem(new ItemLeftovers());
                pw.setTempAbility(AbilityBase.getAbility("Levitate").get());
                pw.setTemporaryMoveset(new Moveset(new Attack[]{new Attack("Hydro Pump"), new Attack("Will-o-wisp"), new Attack("Thunderbolt"), new Attack("Pain Split")}));
            } else if (phase == 2 && pw.getHealthPercent() <= 40F) {
                changeForm(pw);
                pw.setTemporaryMoveset(new Moveset(new Attack[]{new Attack("Overheat"), new Attack("Will-o-wisp"), new Attack("Thunderbolt"), null}));
            }
        }
    }

    public void onBattleEnd(BattleControllerBase bc) {
        super.onBattleEnd(bc);
    }


    void changeForm(PixelmonWrapper pw) {
        pw.setForm(forms[this.phase]);
        pw.bc.sendToAll(new TextComponentTranslation("pixelmon.abilities.changeform", pw.getNickname()));
        this.phase++;
    }

}

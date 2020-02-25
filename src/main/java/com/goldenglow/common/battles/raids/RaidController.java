package com.goldenglow.common.battles.raids;

import com.goldenglow.common.battles.raids.bosses.BossRotomExample;
import com.goldenglow.common.battles.raids.bosses.RaidBossBase;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.battles.AttackEvents;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BonusStats;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;
import java.util.List;

public class RaidController {

    private List<BattleControllerBase> battleList = new ArrayList<>();

    private RaidBossBase raidBoss;
    private BonusStats bossBuffs;
    private BonusStats partyBuffs;

    public RaidController(EntityPixelmon bossEntity) {
        this.raidBoss = new BossRotomExample(bossEntity);
    }

    /*
    public BattleControllerBase startRaidBattle(EntityPlayerMP player) {
        EntityPixelmon pixelmon = Pixelmon.storageManager.getParty(player).getAndSendOutFirstAblePokemon(player);
        if(pixelmon!=null) {
            PlayerParticipant p = new PlayerParticipant(player, pixelmon);
            BattleControllerBase bc = BattleRegistry.startBattle(new BattleParticipant[]{p}, new BattleParticipant[]{this.raidBoss.getParticipant()}, new RaidBattleRules(this));
            this.battleList.add(bc);
            return bc;
        }
        else {
            player.sendMessage(new TextComponentString("Unable to join raid! No able pokemon available!"));
            return null;
        }
    }

    public BattleControllerBase startRaidBattle(EntityPlayerMP player) {
        EntityPixelmon pixelmon = Pixelmon.storageManager.getParty(player).getAndSendOutFirstAblePokemon(player);
        PlayerParticipant p = new PlayerParticipant(player, pixelmon);
        WildPixelmonParticipant w = new WildPixelmonParticipant(this.raidBoss.getEntity());
        BattleControllerBase bc = BattleRegistry.startBattle(new BattleParticipant[]{ p }, new BattleParticipant[]{ w }, new RaidBattleRules(this));
        this.battleList.add(bc);
        return bc;
    }
    */

    public BattleControllerBase startExperimentalRaid(List<EntityPlayerMP> players) {
        List<PlayerParticipant> participants = new ArrayList<>();
        for(EntityPlayerMP player : players) {
            EntityPixelmon pixelmon = Pixelmon.storageManager.getParty(player).getAndSendOutFirstAblePokemon(player);
            if(pixelmon != null) {
                participants.add(new PlayerParticipant(player, pixelmon));
            }
        }
        if(!participants.isEmpty()) {
            BattleControllerBase bc = BattleRegistry.startBattle(participants.toArray(new PlayerParticipant[0]), new BattleParticipant[]{ this.raidBoss.getParticipant() }, new RaidBattleRules(this));
            this.battleList.add(bc);
            return bc;
        }
        return null;
    }

    public void applyBonusStats(Pokemon pokemon) {
        if(pokemon.getOwnerPlayer() != null && partyBuffs != null) {
            pokemon.setBonusStats(partyBuffs);
        } else if (bossBuffs != null) {
            pokemon.setBonusStats(bossBuffs);
        }
    }

    public void onAttack(BattleControllerBase bc, AttackEvents.DamageEvent event) {
        this.raidBoss.onAttack(bc);
        for(BattleControllerBase b : this.battleList) {
            if(b!=bc && event.target.getPlayerOwner()==null) {
                b.sendDamagePacket(event.target, (int)event.damage);
            }
        }
    }

    public void turnEnd(BattleControllerBase bc) {
        this.raidBoss.onTurnEnd(bc);
    }

    public void battleEnd(BattleControllerBase bc) {
        this.raidBoss.onBattleEnd(bc);
        this.battleList.remove(bc);
    }

    public RaidBossBase getBoss() {
        return this.raidBoss;
    }

    public BonusStats getBossBuffs() {
        return this.bossBuffs;
    }

    public void setBossBuffs(BonusStats bossBuffs) {
        this.bossBuffs = bossBuffs;
    }

    public BonusStats getPartyBuffs() {
        return this.partyBuffs;
    }

    public void setPartyBuffs(BonusStats partyBuffs) {
        this.partyBuffs = partyBuffs;
    }

    public List<BattleControllerBase> getBattleList() {
        return battleList;
    }

}

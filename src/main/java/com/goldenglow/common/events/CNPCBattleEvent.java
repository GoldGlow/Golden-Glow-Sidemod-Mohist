package com.goldenglow.common.events;

import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;

public class CNPCBattleEvent extends Event {

    public NPCWrapper npc;
    public PlayerWrapper player;
    public BattleControllerBase bcb;

    public static class TurnEnd extends CNPCBattleEvent {
        public TurnEnd(NPCWrapper npc, PlayerWrapper player, BattleControllerBase bcb) {
            this.npc = npc;
            this.player = player;
            this.bcb = bcb;
        }
    }

}

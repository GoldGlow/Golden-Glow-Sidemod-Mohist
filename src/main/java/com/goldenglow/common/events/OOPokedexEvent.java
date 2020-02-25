package com.goldenglow.common.events;

import com.pixelmonmod.pixelmon.pokedex.EnumPokedexRegisterStatus;
import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.api.wrapper.PlayerWrapper;
import scala.tools.nsc.doc.model.Public;

import java.util.ArrayList;
import java.util.Map;

public class OOPokedexEvent extends Event {
    public ArrayList<Integer> caughtList;
    public PlayerWrapper player;

    public OOPokedexEvent(PlayerWrapper player, ArrayList<Integer> caughtList){
        this.player=player;
        this.caughtList=caughtList;
    }
}

package com.goldenglow.common.util.scripting;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.world.OOWorldData;
import com.goldenglow.common.routes.Route;
import com.goldenglow.common.routes.RouteManager;
import com.goldenglow.common.routes.SpawnPokemon;
import net.minecraft.world.World;
import noppes.npcs.api.wrapper.BlockScriptedWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.WorldWrapper;

public class DailyFunctions {

    //Used for the extra apricorn script functionalities, more specifically for wild pokemon spawning
    public static void apricornScript(PlayerWrapper playerWrapper, BlockScriptedWrapper block) {
        Route apricornRoute = GoldenGlow.routeManager.getRoute(block.getPos().getMCBlockPos());
        if (Math.random()*5<2) {
            BattleFunctions.startWildBattle(playerWrapper, SpawnPokemon.getWeightedPokemonFromList(apricornRoute.apricornPokemon));
        }
    }

    public static int getLastEarthDay(WorldWrapper world) {
        OOWorldData data = OOWorldData.get(world.world);
        return data.getLastEarthDay();
    }
}

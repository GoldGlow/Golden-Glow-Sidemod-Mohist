package com.goldenglow.common.handlers.events;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.inventory.BetterTrading.TradeManager;
import com.goldenglow.common.music.SongManager;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import com.pixelmonmod.pixelmon.api.events.LevelUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SoundRelatedEventHandler {
    @SubscribeEvent
    public void onEvolutionStart(EvolveEvent.PreEvolve event){
        IPlayerData playerData= event.player.getCapability(OOPlayerProvider.OO_DATA, null);
        playerData.setEvolvingPokemon(true);
        SongManager.setCurrentSong(event.player, GoldenGlow.songManager.evolutionDefault);
    }

    @SubscribeEvent
    public void onEvolutionEnd(EvolveEvent.PostEvolve event){
        IPlayerData playerData= event.player.getCapability(OOPlayerProvider.OO_DATA, null);
        playerData.setEvolvingPokemon(false);
        SongManager.setRouteSong(event.player);
        if(playerData.getWaitToEvolve().size()>0){
            playerData.removePokemonWaiting(0);
            TradeManager.evolutionTest(event.player);
        }
    }

    @SubscribeEvent
    public void onLevelUp(LevelUpEvent event){
        SongManager.playSound(event.player, "neutral", GoldenGlow.songManager.levelUpDefault);
    }
}

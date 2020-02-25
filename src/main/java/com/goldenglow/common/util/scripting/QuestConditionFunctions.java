package com.goldenglow.common.util.scripting;

import com.goldenglow.common.events.OOPokedexEvent;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.EnumType;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.wrapper.PlayerWrapper;
import java.util.List;

public class QuestConditionFunctions {
    public static boolean registeredAmountOfTypeInDex(OOPokedexEvent event, String typeName, int amount){
        int caughtType=0;
        EnumType type=EnumType.parseType(typeName);
        for(int species:event.caughtList) {
            for (EnumType pokemonType : EnumSpecies.getFromDex(species).getBaseStats().types) {
                if (type == pokemonType) {
                    caughtType++;
                    if(caughtType>=amount){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getCaughtOfType(OOPokedexEvent event, String typeName){
        int caughtType=0;
        EnumType type=EnumType.parseType(typeName);
        for(int species:event.caughtList) {
            for (EnumType pokemonType : EnumSpecies.getFromDex(species).getBaseStats().types) {
                if (type == pokemonType) {
                    caughtType++;
                }
            }
        }
        return caughtType;
    }

    public static boolean caughtPokemon(OOPokedexEvent event, String pokemonName){
        return Pixelmon.storageManager.getParty((EntityPlayerMP)event.player.getMCEntity()).pokedex.hasCaught(EnumSpecies.getPokedexNumber(pokemonName));
    }

    public static int getCaughtSpeciesNumber(OOPokedexEvent event){
        return event.caughtList.size();
    }

    public static boolean hasTypeInParty(PlayerWrapper player, String typeName){
        EnumType type=EnumType.parseType(typeName);
        List<Pokemon> party= Pixelmon.storageManager.getParty((EntityPlayerMP)player.getMCEntity()).getTeam();
        for(Pokemon pokemon:party){
            for (EnumType pokemonType : pokemon.getBaseStats().types) {
                if (type == pokemonType) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasPokemonInPart(PlayerWrapper player, String species){
        List<Pokemon> party= Pixelmon.storageManager.getParty((EntityPlayerMP)player.getMCEntity()).getTeam();
        for(Pokemon pokemon:party){
            if(pokemon.getSpecies().name.equals(species)){
                return true;
            }
        }
        return false;
    }
}

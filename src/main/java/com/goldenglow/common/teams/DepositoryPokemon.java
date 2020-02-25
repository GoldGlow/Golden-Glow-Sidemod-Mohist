package com.goldenglow.common.teams;

import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import java.util.ArrayList;

public class DepositoryPokemon {
    public static Pokemon generateDepositoryPokemon(PokemonSpec pokemonSpec){
        pokemonSpec.level=5;
        Pokemon pokemon=pokemonSpec.create();
        if(Math.random()*3<1){
            pokemon.setAbilitySlot(2);
        }
        else{
            pokemon.setAbilitySlot(RandomHelper.getRandomNumberBetween(0, pokemon.getBaseStats().abilities[1] == null ? 0 : 1));
        }
        Moveset moves=pokemon.getMoveset();
        while(moves.size()>2){
            moves.remove(moves.size()-1);
        }
        ArrayList<Attack> eggMoves=pokemon.getBaseStats().getEggMoves();
        for(Attack move: moves){
            if(eggMoves.contains(move)){
                eggMoves.remove(move);
            }
        }
        while(moves.size()<4&&eggMoves.size()>0){
            int index=((int)Math.random()*eggMoves.size());
            moves.add(eggMoves.get(index));
            eggMoves.remove(index);
        }
        IVStore IVs=IVStore.CreateNewIVs3Perfect();
        pokemon.getIVs().CopyIVs(IVs);
        if(Math.random()*1024<1){
            pokemon.setShiny(true);
        }
        else{
            pokemon.setShiny(false);
        }
        return pokemon;
    }
}

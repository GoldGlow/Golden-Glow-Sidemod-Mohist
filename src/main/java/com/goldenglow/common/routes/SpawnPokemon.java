package com.goldenglow.common.routes;

import com.goldenglow.common.util.GGLogger;
import com.pixelmonmod.pixelmon.RandomHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JeanMarc on 5/26/2016.
 */
public class SpawnPokemon {
    public String species;
    public Byte form;
    public int minLvl;
    public int maxLvl;
    public int weight;

    public SpawnPokemon(){
        this.species="Dunsparce";
        this.form=0;
        this.minLvl=5;
        this.maxLvl=5;
        this.weight=1;
    }

    public static SpawnPokemon getWeightedPokemonFromList(List<SpawnPokemon> spawns){
        List<Integer> weights=new ArrayList<>();
        for(SpawnPokemon pokemon:spawns){
            weights.add(pokemon.weight);
        }
        return spawns.get(RandomHelper.getRandomIndexFromWeights(weights));
    }
}


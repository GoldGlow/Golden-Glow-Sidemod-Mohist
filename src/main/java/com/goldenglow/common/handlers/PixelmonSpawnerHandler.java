package com.goldenglow.common.handlers;

import com.goldenglow.common.util.Reference;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by JeanMarc on 6/5/2019.
 */
public class PixelmonSpawnerHandler {
    public ArrayList<String> dayPokemon=new ArrayList<>();
    public ArrayList<String> nightPokemon=new ArrayList<>();
    File dayFile;
    File nightFile;

    public void init(){
        dayFile = new File(Reference.spawnerDir, "dayPokemon.cfg");
        if(!dayFile.exists()) {
            if (!dayFile.getParentFile().exists())
                dayFile.getParentFile().mkdirs();
            try {
                dayFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        nightFile = new File(Reference.spawnerDir, "nightPokemon.cfg");
        if(!nightFile.exists()) {
            if (!nightFile.getParentFile().exists())
                nightFile.getParentFile().mkdirs();
            try {
                nightFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            loadTimePokemon(dayFile, dayPokemon);
            loadTimePokemon(nightFile, nightPokemon);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadTimePokemon(File pokemon, ArrayList<String> list) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(pokemon));
        String readLine;
        while((readLine=reader.readLine())!=null){
            if(EnumSpecies.hasPokemonAnyCase(readLine)){
                list.add(readLine);
            }
        }
    }
}

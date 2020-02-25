package com.goldenglow.common.teams;

import com.goldenglow.common.util.Reference;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.config.PixelmonEntityList;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.enums.EnumGrowth;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PlayerParty {

    public static void backupTeam(EntityPlayerMP player, String folder){
        try {
            if(! new File(folder).exists()){
                new File(folder).createNewFile();
            }
            File backup = new File(Reference.configDir+folder+player.getUUID(player.getGameProfile())+".json");
            if(!backup.exists())
                backup.createNewFile();
            JsonWriter json=new JsonWriter(new FileWriter(backup));
            json.setIndent("\t");
            PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player);
            Pokemon[] party=storage.getAll();
            json.beginObject();
            json.name("pokemon");
            json.beginArray();
            for(Pokemon pokemon:party){
                if(pokemon!=null){
                    json.beginObject();
                    json.name("Name").value(pokemon.getSpecies().name);
                    json.name("Nickname").value(pokemon.getNickname());
                    json.name("Form").value(pokemon.getForm());
                    json.name("Ability").value(pokemon.getAbilityName());
                    json.name("EVHP").value(pokemon.getEVs().hp);
                    json.name("EVAttack").value(pokemon.getEVs().attack);
                    json.name("EVDefence").value(pokemon.getEVs().defence);
                    json.name("EVSpecialAttack").value(pokemon.getEVs().specialAttack);
                    json.name("EVSpecialDefence").value(pokemon.getEVs().specialDefence);
                    json.name("EVSpeed").value(pokemon.getEVs().speed);
                    json.name("EXP").value(pokemon.getExperience());
                    json.name("Friendship").value(pokemon.getFriendship());
                    json.name("Gender").value(pokemon.getGender().getForm());
                    json.name("Growth").value(pokemon.getGrowth().index);
                    json.name("IsShiny").value(pokemon.isShiny());
                    json.name("IVHP").value(pokemon.getIVs().hp);
                    json.name("IVAttack").value(pokemon.getIVs().attack);
                    json.name("IVDefence").value(pokemon.getIVs().defence);
                    json.name("IVSpAtt").value(pokemon.getIVs().specialAttack);
                    json.name("IVSpDef").value(pokemon.getIVs().specialDefence);
                    json.name("IVSpeed").value(pokemon.getIVs().speed);
                    json.name("Level").value(pokemon.getLevel());
                    json.name("Nature").value(pokemon.getNature().index);
                    json.name("originalTrainer").value(pokemon.getOriginalTrainer());
                    json.name("originalTrainerUUID").value(pokemon.getOriginalTrainerUUID().toString());
                    for(int i=0;i<pokemon.getMoveset().size();i++){
                        json.name("PixelmonMoveID"+i).value(pokemon.getMoveset().get(i).getActualMove().getAttackName());
                    }
                    json.endObject();
                }
            }
            json.endArray();
            json.endObject();
            json.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void emptyParty(EntityPlayerMP player){
        PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player);
        for(int i=0;i<6;i++){
            storage.set(i, null);
        }
        storage.updatePartyCache();
    }

    public static void loadSavedTeam(EntityPlayerMP player, String folder){
        try {
            File backup = new File(Reference.configDir+folder+player.getUUID(player.getGameProfile())+".json");
            if(!backup.exists())
                backup.createNewFile();
            InputStream istream= new FileInputStream(backup);
            PlayerPartyStorage storage=Pixelmon.storageManager.getParty(player);
            JsonObject file= new JsonParser().parse(new InputStreamReader(istream, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray party=file.get("pokemon").getAsJsonArray();
            for(int i=0;i<party.size();i++){
                JsonObject pokemon=party.get(i).getAsJsonObject();
                String name=pokemon.get("Name").getAsString();
                PokemonSpec pokemonSpec=PokemonSpec.from(name);
                pokemonSpec.form=pokemon.get("Form").getAsByte();
                pokemonSpec.ability=pokemon.get("Ability").getAsString();
                pokemonSpec.gender=pokemon.get("Gender").getAsByte();
                Pokemon pixelmon=pokemonSpec.create();
                if(!pokemon.get("Nickname").isJsonNull())
                    pixelmon.setNickname(pokemon.get("Nickname").getAsString());
                pixelmon.getEVs().hp=pokemon.get("EVHP").getAsInt();
                pixelmon.getEVs().attack=pokemon.get("EVAttack").getAsInt();
                pixelmon.getEVs().defence=pokemon.get("EVDefence").getAsInt();
                pixelmon.getEVs().specialAttack=pokemon.get("EVSpecialAttack").getAsInt();
                pixelmon.getEVs().specialDefence=pokemon.get("EVSpecialDefence").getAsInt();
                pixelmon.getEVs().speed=pokemon.get("EVSpeed").getAsInt();
                pixelmon.setFriendship(pokemon.get("Friendship").getAsInt());
                pixelmon.setGender(Gender.getGender(pokemon.get("Gender").getAsShort()));
                pixelmon.setGrowth(EnumGrowth.getGrowthFromIndex(pokemon.get("Growth").getAsShort()));
                pixelmon.setShiny(pokemon.get("IsShiny").getAsBoolean());
                pixelmon.getIVs().hp=pokemon.get("IVHP").getAsInt();
                pixelmon.getIVs().attack=pokemon.get("IVAttack").getAsInt();
                pixelmon.getIVs().defence=pokemon.get("IVDefence").getAsInt();
                pixelmon.getIVs().specialAttack=pokemon.get("IVSpAtt").getAsInt();
                pixelmon.getIVs().specialDefence=pokemon.get("IVSpDef").getAsInt();
                pixelmon.getIVs().speed=pokemon.get("IVSpeed").getAsInt();
                pixelmon.setLevel(pokemon.get("Level").getAsInt());
                pixelmon.setNature(EnumNature.getNatureFromIndex(pokemon.get("Nature").getAsShort()));
                pixelmon.setOriginalTrainer(UUID.fromString(pokemon.get("originalTrainerUUID").getAsString()), pokemon.get("originalTrainer").getAsString());
                Moveset moveset = new Moveset();

                for(int j=0;j<4;j++) {
                    if(pokemon.has("PixelmonMoveID"+j))
                        pixelmon.getMoveset().attacks[j] = new Attack(pokemon.get("PixelmonMoveID"+j).getAsString());
                }
                pixelmon.setExperience(pokemon.get("EXP").getAsInt());
                storage.add(pixelmon);
            }
            storage.updatePartyCache();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

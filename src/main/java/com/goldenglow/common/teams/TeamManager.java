package com.goldenglow.common.teams;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.util.Reference;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.config.PixelmonItemsHeld;
import com.pixelmonmod.pixelmon.entities.pixelmon.abilities.AbilityBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.EVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.IVStore;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.enums.EnumNature;
import net.minecraft.item.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class TeamManager {
    File teamFile = new File(Reference.configDir, "teams.cfg");
    ArrayList<Team> teams = new ArrayList<Team>();
    public Map<String, String> otherMoves = new HashMap();
    public ArrayList<String> otherPixelmon = new ArrayList<String>();
    
    public TeamManager() {
        otherMoves.put("Soft-Boiled", "Softboiled");
        otherMoves.put("Ancient Power", "AncientPower");
        otherMoves.put("ThunderPunch", "Thunder Punch");
        otherMoves.put("Dynamic Punch", "DynamicPunch");
        otherMoves.put("Bubble Beam", "BubbleBeam");
        otherMoves.put("High Jump Kick", "Hi Jump Kick");
        otherMoves.put("Extreme Speed", "ExtremeSpeed");
        otherMoves.put("Dragon Breath", "DragonBreath");
        otherMoves.put("Grass Whistle", "GrassWhistle");
        otherMoves.put("Solar Beam", "SolarBeam");

        otherPixelmon.add("Ralts");
        otherPixelmon.add("Misdreavus");
        otherPixelmon.add("Froslass");
        otherPixelmon.add("Cubchoo");
        otherPixelmon.add("Beartic");
    }
    
    public void init() {
        GoldenGlow.logger.teamInfo("Loading teams!");
        if(!teamFile.exists())
            try {
                teamFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            loadTeams();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GoldenGlow.logger.teamInfo("Loaded teams!");
    }

    public void loadTeams()throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(teamFile));
        String readLine;
        String teamName = "";
        ArrayList<String> pokemon = new ArrayList<String>();
        boolean pokemonSequence=false;
        teams.clear();
        while((readLine=reader.readLine())!=null)
        {
            if(readLine.startsWith("===")){
                if(pokemonSequence){
                    getTeam(teamName).addMember(convertPokemon(pokemon));
                    pokemon.clear();
                    pokemonSequence = false;
                    teams.add(getTeam(teamName));
                }
                else if(!teamName.equals("")){
                    teams.add(getTeam(teamName));
                }
                teamName = readLine.replace("===","");
                teamName = teamName.replace(" ","");
                createTeam(teamName);
            }
            else {
                if (!readLine.equals("") && !pokemonSequence) {
                    pokemonSequence = true;
                }
                if (readLine.equals("") && pokemonSequence) {
                    getTeam(teamName).addMember(convertPokemon(pokemon));
                    pokemon.clear();
                    pokemonSequence = false;
                }
                if (pokemonSequence) {
                    pokemon.add(readLine);
                }
            }
        }
        if(pokemonSequence)
            getTeam(teamName).addMember(convertPokemon(pokemon));
        teams.add(getTeam(teamName));
    }

    public static Pokemon convertPokemon(ArrayList<String> pokemon){
        String line=pokemon.get(0);
        String name=line.split(" @ ")[0];
        String genderString="";
        if(line.contains("(F)")){
            name=name.replace(" (F)","");
            genderString="f";
        }
        else if(line.contains("(M)")){
            name=name.replace(" (M)","");
            genderString="m";
        }
        PokemonSpec pixelmon= PokemonSpec.from(name);
        Pokemon pokemonData=pixelmon.create();
        if(genderString.equals("m")){
            pokemonData.setGender(Gender.Male);
        }
        else if(genderString.equals("f"))
            pokemonData.setGender(Gender.Female);
        if(line.split(" @ ").length>1) {
            if ((PixelmonItemsHeld.getHeldItem(line.split(" @ ")[1].replace("  ",""))) != null) {
                pokemonData.setHeldItem(new ItemStack(PixelmonItemsHeld.getHeldItem(line.split(" @ ")[1].replace("  ",""))));
            } else {
                GoldenGlow.instance.logger.error("HeldItem not found: '" + line.split(" @ ")[1] + "' on Pokemon: " + name);
            }
        }
        Moveset moveset = new Moveset();
        for(int i=1;i<pokemon.size();i++){
            line=pokemon.get(i);
            line=line.replace("  ","");
            if(line.startsWith("Ability:"))
            {
                String a = line.replace("Ability: ","").replace("  ","");
                if(AbilityBase.getAbility(a)==null)
                    a = a.replaceAll("\\s+", "");
                if(AbilityBase.getAbility(a)!=null)
                    pokemonData.setAbility(a);
            }
            else if(line.startsWith("Level")){
                int lvl = Integer.parseInt(line.replace("Level: ","").replace(" ",""));
                if(lvl<=100&&lvl>0){
                    pokemonData.setLevel(lvl);
                }
                else
                    GoldenGlow.instance.logger.error("Could not set a pokemons level!");
            }
            else if(line.startsWith("Shiny: ")&&line.contains("Yes"))
            {
                pokemonData.setShiny(true);
            }
            else if(line.startsWith("Happiness:")){
                int happiness = Integer.parseInt(line.replace("Happiness: ",""));
                if(happiness<=255&&happiness>=0){
                    pokemonData.setFriendship(happiness);
                }
            }
            else if(line.startsWith("EVs")){
                line = line.replace("EVs: ","");
                EVStore evStore = pokemonData.getStats().evs;
                for(String evs : line.split(" / ")) {
                    int ev = Integer.parseInt(evs.split(" ")[0]);
                    if (evs.split(" ")[1].equalsIgnoreCase("hp") && ev <= 255 && ev > 0)
                        evStore.hp = ev;
                    if (evs.split(" ")[1].equalsIgnoreCase("atk") && ev <= 255 && ev > 0)
                        evStore.attack = ev;
                    if (evs.split(" ")[1].equalsIgnoreCase("def") && ev <= 255 && ev > 0)
                        evStore.defence = ev;
                    if (evs.split(" ")[1].equalsIgnoreCase("spa") && ev <= 255 && ev > 0)
                        evStore.specialAttack = ev;
                    if (evs.split(" ")[1].equalsIgnoreCase("spd") && ev <= 255 && ev > 0)
                        evStore.specialDefence = ev;
                    if (evs.split(" ")[1].equalsIgnoreCase("spe") && ev <= 255 && ev > 0)
                        evStore.speed = ev;
                }
                pokemonData.getStats().evs=evStore;
            }
            else if(line.startsWith("IVs:"))
            {
                line = line.replace("IVs: ","");
                IVStore ivStore = pokemonData.getStats().ivs;
                for(String ivs : line.split(" / "))
                {
                    int iv = Integer.parseInt(ivs.split(" ")[0]);
                    if(ivs.split(" ")[1].equalsIgnoreCase("hp")&&iv<=31&&iv>0)
                        ivStore.hp=iv;
                    if(ivs.split(" ")[1].equalsIgnoreCase("atk")&&iv<=31&&iv>0)
                        ivStore.attack=iv;
                    if(ivs.split(" ")[1].equalsIgnoreCase("def")&&iv<=31&&iv>0)
                        ivStore.defence=iv;
                    if(ivs.split(" ")[1].equalsIgnoreCase("spa")&&iv<=31&&iv>0)
                        ivStore.specialAttack=iv;
                    if(ivs.split(" ")[1].equalsIgnoreCase("spd")&&iv<=31&&iv>0)
                        ivStore.specialDefence=iv;
                    if(ivs.split(" ")[1].equalsIgnoreCase("spe")&&iv<=31&&iv>0)
                        ivStore.speed=iv;
                }
                pokemonData.getStats().ivs = ivStore;
            }
            else if(line.contains(" Nature")&&!line.contains("Nature Power"))
            {
                line = line.replace(" Nature","");
                if(EnumNature.hasNature(line))
                {
                    pokemonData.setNature(EnumNature.natureFromString(line));
                }
            }
            else if(line.startsWith("- ")&&moveset.size()<4){
                String move=line.replace("- ","");
                Attack attack = new Attack(move);
                if(attack==null && GoldenGlow.teamManager.otherMoves.containsKey(move))
                    move=GoldenGlow.teamManager.otherMoves.get(move);

                attack = new Attack(move);
                if(attack!=null)
                        moveset.add(attack);
                else
                    GoldenGlow.instance.logger.error("Move not found: "+move);
            }
        }
        if(moveset!=null){
            for(int i=0;i<moveset.size();i++){
                pokemonData.getMoveset().set(i, moveset.get(i));
            }
        }
        return pokemonData;
    }

    public static List<Pokemon> singleTeamFromFile(String path){
        List<Pokemon> pixelmon=new ArrayList<Pokemon>();
        ArrayList<String> pokemon = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String readLine;
            boolean pokemonSequence=false;
            while((readLine=reader.readLine())!=null)
            {
                if (!readLine.equals("") && !pokemonSequence) {
                    pokemonSequence = true;
                }
                if (readLine.equals("") && pokemonSequence) {
                    pixelmon.add(convertPokemon(pokemon));
                    pokemon.clear();
                    pokemonSequence = false;
                }
                if (pokemonSequence) {
                    pokemon.add(readLine);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pixelmon;
    }

    public void createTeam(String name)
    {
        Team team = new Team(name);
        this.teams.add(team);
    }

    public Team getTeam(String name)
    {
        for(Team team : this.teams)
        {
            if(team.name.equalsIgnoreCase(name)){
                return team;
            }
        }
        return null;
    }

    public ArrayList<Team> getTeams()
    {
        return this.teams;
    }

    public void printTeams(){
        for(int i=0;i<this.getTeams().size();i++){
            GoldenGlow.logger.info(this.getTeams().get(i).name);
        }
    }
}

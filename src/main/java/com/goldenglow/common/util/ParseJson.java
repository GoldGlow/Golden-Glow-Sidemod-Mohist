package com.goldenglow.common.util;

import com.goldenglow.common.inventory.Action;
import com.goldenglow.common.routes.SpawnPokemon;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by JeanMarc on 7/10/2019.
 */
public class ParseJson {

    public static Requirement parseRequirement(JsonObject o){
        Requirement r = new Requirement();
        r.type = Requirement.RequirementType.valueOf(o.getAsJsonObject().get("type").getAsString());
        if (r.type == Requirement.RequirementType.TIME || r.type == Requirement.RequirementType.PERMISSION || r.type== Requirement.RequirementType.FRIEND_ONLY) {
            r.value = o.getAsJsonObject().get("value").getAsString();
        } else {
            r.id = o.getAsJsonObject().get("id").getAsInt();
        }
        r.override = o.getAsJsonObject().get("override").getAsString();
        return r;
    }

    public static Action parseAction(JsonObject o){
        Action a = new Action();
        a.actionType = Action.ActionType.valueOf(o.getAsJsonObject().get("actionType").getAsString());
        a.value = o.get("value").getAsString();
        if(o.has("requirements")){
            JsonArray requirementArray=o.getAsJsonArray("requirements");
            Requirement[] requirements=new Requirement[requirementArray.size()];
            for(int k=0;k<requirementArray.size();k++){
                requirements[k]=ParseJson.parseRequirement((JsonObject) requirementArray.get(k));
            }
            a.requirements=requirements;
        }
        if(o.has("closeInv")){
            a.closeInv=o.get("closeInv").getAsBoolean();
        }
        return a;
    }

    public static SpawnPokemon parseSpawnPokemon(JsonObject o){
        SpawnPokemon pokemon=new SpawnPokemon();
        pokemon.species=o.getAsJsonObject().get("species").getAsString();
        if(o.has("form")){
            pokemon.form=o.getAsJsonObject().get("form").getAsByte();
        }
        pokemon.minLvl=o.getAsJsonObject().get("minLvl").getAsInt();
        pokemon.maxLvl=o.getAsJsonObject().get("maxLvl").getAsInt();
        pokemon.weight=o.getAsJsonObject().get("weight").getAsInt();
        return pokemon;
    }
}

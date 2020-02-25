package com.goldenglow.common.routes;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.ParseJson;
import com.goldenglow.common.util.Reference;
import com.goldenglow.common.util.Requirement;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RouteManager {

    List<Route> routes = new ArrayList<Route>();
    File dir;

    public void init() {
        dir = new File(Reference.routeDir);
        if(!dir.exists()) {
            if (!dir.getParentFile().exists())
                dir.getParentFile().mkdirs();
            try {
                dir.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            this.loadRoutes();
    }

    public void loadRoutes() {
        GGLogger.info("Loading Routes...");
        this.routes.clear();
        try {
            for (File f : Objects.requireNonNull(dir.listFiles())) {
                if (f.getName().endsWith(".json")) {
                    loadRoute(f.getName().replace(".json", ""));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadRoute(String routeName) throws IOException {
        InputStream iStream = new FileInputStream(new File(dir, routeName+".json"));
        JsonObject json = new JsonParser().parse(new InputStreamReader(iStream, StandardCharsets.UTF_8)).getAsJsonObject();

        String name = json.get("Name").getAsString();
        String dName = "";
        if(json.has("DisplayName"))
             dName = json.get("DisplayName").getAsString();
        String song = json.get("Song").getAsString();
        int priority = json.get("Priority").getAsInt();

        List<BlockVector2D> points = new ArrayList<>();

        JsonObject regionObj = json.get("region").getAsJsonObject();
        int minY = regionObj.get("minY").getAsInt();
        int maxY = regionObj.get("maxY").getAsInt();

        JsonArray pointsArray = json.get("points").getAsJsonArray();
        for(JsonElement o : pointsArray) {
            int posX = o.getAsJsonObject().get("posX").getAsInt();
            int posZ = o.getAsJsonObject().get("posZ").getAsInt();
            BlockVector2D vec = new BlockVector2D(posX, posZ);
            points.add(vec);
        }
        World world;
        if(json.has("world")) {
            World w = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(json.get("world").getAsInt());
            if(w!=null)
                world = w;
            else {
                GoldenGlow.logger.error("Route World not found! - Route: "+routeName);
                return;
            }
        } else {
            world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
        }

        Route route = new Route(name, song, new Polygonal2DRegion(ForgeWorldEdit.inst.getWorld(DimensionManager.getWorld(0)), points, minY, maxY), priority, world);

        if(!dName.isEmpty())
            route.displayName = dName;

        if(json.has("safeZone")){
            route.isSafeZone=json.get("safeZone").getAsBoolean();
        }

        if(json.has("warpX")){
            route.warpX=json.get("warpX").getAsInt();
        }

        if(json.has("warpY")){
            route.warpY=json.get("warpY").getAsInt();
        }

        if(json.has("warpZ")){
            route.warpZ=json.get("warpZ").getAsInt();
        }

        if(json.has("kickWarp")){
            route.kickWarp=json.get("kickWarp").getAsBoolean();
        }

        if(json.has("kickWarpX")){
            route.kickWarpX=json.get("kickWarpX").getAsInt();
        }

        if(json.has("kickWarpY")){
            route.kickWarpY=json.get("kickWarpY").getAsInt();
        }

        if(json.has("kickWarpZ")){
            route.kickWarpZ=json.get("kickWarpZ").getAsInt();
        }

        if(json.has("requirements")) {
            JsonArray requirementsArray = json.get("requirements").getAsJsonArray();
            for (JsonElement o : requirementsArray) {
                Requirement r = ParseJson.parseRequirement((JsonObject)o);
                route.requirements.add(r);
            }
        }

        if(json.has("apricornPokemon")){
            JsonArray apricornPokemonArray=json.getAsJsonArray("apricornPokemon");
            for(JsonElement o:apricornPokemonArray){
                SpawnPokemon pokemon=ParseJson.parseSpawnPokemon((JsonObject) o);
                route.apricornPokemon.add(pokemon);
            }
        }

        this.routes.add(route);
    }

    public List<Route> getRoutes(){
        return this.routes;
    }

    public void saveRoutes() {
        GGLogger.info("Saving Routes...");
        for (Route route : routes) {
            try {
                saveRoute(route);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addRoute(Route route) {
        try {
            saveRoute(route);
            this.routes.add(route);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRoute(Route route) throws IOException {
        File dir = new File(Reference.routeDir, route.unlocalizedName + ".json");
        if (!dir.exists())
            dir.createNewFile();
        JsonWriter file = new JsonWriter(new FileWriter(dir));
        file.setIndent("\t");

        file.beginObject();

        file.name("Name").value(route.unlocalizedName);
        file.name("DisplayName").value(route.displayName);
        file.name("Song").value(route.song);
        file.name("Priority").value(route.priority);

        file.name("region");
        file.beginObject();
        file.name("minY").value(route.region.getMinimumY());
        file.name("maxY").value(route.region.getMaximumY());
        file.endObject();

        file.name("points");
        file.beginArray();
        for (BlockVector2D vec : route.region.getPoints()) {
            file.beginObject();
            file.name("posX").value(vec.getBlockX());
            file.name("posZ").value(vec.getBlockZ());
            file.endObject();
        }
        file.endArray();

        file.name("world").value(route.world.provider.getDimension());

        file.name("safeZone").value(route.isSafeZone);
        file.name("warpX").value(route.warpX);
        file.name("warpY").value(route.warpY);
        file.name("warpZ").value(route.warpZ);

        file.name("kickWarp").value(route.kickWarp);
        file.name("kickWarpX").value(route.kickWarpX);
        file.name("kickWarpY").value(route.kickWarpY);
        file.name("kickWarpZ").value(route.kickWarpZ);

        file.name("requirements");
        file.beginArray();
        for(Requirement requirement : route.requirements) {
            file.beginObject();
            file.name("type").value(requirement.type.toString());
            if(requirement.type == Requirement.RequirementType.TIME || requirement.type == Requirement.RequirementType.PERMISSION) {
                file.name("value").value(requirement.value);
            } else {
                file.name("id").value(requirement.id);
            }
            file.name("override").value(requirement.override);
            file.endObject();
        }
        file.endArray();

        file.name("apricornPokemon");
        file.beginArray();
        for(SpawnPokemon pokemon: route.apricornPokemon){
            file.beginObject();
            file.name("species").value(pokemon.species);
            file.name("form").value(pokemon.form);
            file.name("minLvl").value(pokemon.minLvl);
            file.name("maxLvl").value(pokemon.minLvl);
            file.name("weight").value(pokemon.weight);
            file.endObject();
        }
        file.endArray();

        file.endObject();
        file.close();
    }

    public boolean doesRouteExist(String name) {
        for(Route route : this.routes) {
            if(route.unlocalizedName.equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public Route getRoute(String name) {
        for(Route route : routes) {
            if(route.unlocalizedName.equalsIgnoreCase(name))
                return route;
        }
        return null;
    }

    public Route getRoute(EntityPlayer player) {
        return getRoute(player.getPosition());
    }

    public Route getRoute(BlockPos pos) {
        int highestPrio = -1;
        Route r = null;
        for(Route route : routes) {
            if(route.region.contains(new Vector(pos.getX(), pos.getY(), pos.getZ())) && route.priority>highestPrio) {
                highestPrio = route.priority;
                r = route;
            }
        }
        return r;
    }
}
package com.goldenglow.common.inventory.shops;

import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.ParseJson;
import com.goldenglow.common.util.Reference;
import com.goldenglow.common.util.Requirement;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomShopHandler {
    public List<CustomShopData> shops=new ArrayList<CustomShopData>();

    File dir;

    public void init() {
        dir = new File(Reference.shopsDir);
        if(!dir.exists()) {
            if (!dir.getParentFile().exists())
                dir.getParentFile().mkdirs();
            dir.mkdir();
        }
        else
            this.loadShops();
    }

    public void loadShops(){
        GGLogger.info("Loading Shops...");
        String shops="";
        try {
            for (File f : Objects.requireNonNull(dir.listFiles())) {
                if (f.getName().endsWith(".json")) {
                    loadShop(f.getName().replace(".json", ""));
                    shops+=f.getName().replace(".json", "")+" ";
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        GGLogger.info("Loaded Shops: "+shops);
    }

    public void loadShop(String inventoryName) throws IOException{
        InputStream iStream = new FileInputStream(new File(dir, inventoryName+".json"));
        JsonObject json = new JsonParser().parse(new InputStreamReader(iStream, StandardCharsets.UTF_8)).getAsJsonObject();
        String displayName = json.get("name").getAsString();
        boolean pixelmonGUI=false;
        if(json.has("pixelmonGUI")){
            pixelmonGUI=json.get("pixelmonGUI").getAsBoolean();
        }
        int rows=json.get("rows").getAsInt();
        JsonObject items=json.get("items").getAsJsonObject();
        CustomShopItem[][] customItems=new CustomShopItem[rows*9][];
        if(pixelmonGUI) {
            for (int i = 0; i < rows * 9; i++) {
                if(items.has("slot"+i)) {
                    JsonArray slot = items.getAsJsonArray("slot" + i);
                    CustomShopItem[] itemList = new CustomShopItem[slot.size()];
                    for (int j = 0; j < slot.size(); j++) {
                        CustomShopItem customItem;
                        JsonObject item = slot.get(j).getAsJsonObject();
                        ItemStack itemStack = null;
                        if (item.getAsJsonObject("item").get("id").getAsString().toLowerCase().startsWith("pokemon:")) {
                            JsonObject jsonItem = item.getAsJsonObject("item");
                            String species = jsonItem.get("id").getAsString().toLowerCase().replace("pokemon:", "");
                            int form = 0;
                            if (jsonItem.has("form")) {
                                form = jsonItem.get("form").getAsInt();
                            }
                            jsonItem.remove("id");
                            jsonItem.addProperty("id", "pixelmon:pixelmon_sprite");
                            if (!jsonItem.has("Count")) {
                                jsonItem.addProperty("Count", 1);
                            }
                            if (!jsonItem.has("tag")) {
                                jsonItem.add("tag", new JsonObject());
                                EnumSpecies pokemon = EnumSpecies.getFromNameAnyCase(species);
                                jsonItem.getAsJsonObject("tag").addProperty("ndex", pokemon.getNationalPokedexInteger());
                                if (form > 0) {
                                    jsonItem.getAsJsonObject("tag").addProperty("form", form);
                                }
                            }
                            int price = item.get("buy").getAsInt();
                            try {
                                jsonItem.getAsJsonObject("tag").add("display", new JsonObject());
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").add("Lore", new JsonArray());
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").addProperty("Name", Reference.resetText + species);
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").getAsJsonArray("Lore").add(Reference.resetText + "pokemon");
                                itemStack = new ItemStack(JsonToNBT.getTagFromJson(item.getAsJsonObject("item").toString()));
                            } catch (NBTException e) {
                                e.printStackTrace();
                            }
                            if (item.has("requirements")) {
                                JsonArray requirementArray = item.getAsJsonArray("requirements");
                                Requirement[] requirements = new Requirement[requirementArray.size()];
                                for (int k = 0; k < requirementArray.size(); k++) {
                                    requirements[k] = ParseJson.parseRequirement(requirementArray.get(k).getAsJsonObject());
                                }
                                customItem = new CustomShopItem(itemStack, requirements);
                            } else {
                                customItem = new CustomShopItem(itemStack, new Requirement[0]);
                            }
                        } else if (item.getAsJsonObject("item").get("id").getAsString().toLowerCase().startsWith("depository:")) {
                            JsonObject jsonItem = item.getAsJsonObject("item");
                            String species = jsonItem.get("id").getAsString().toLowerCase().replace("depository:", "");
                            int form = 0;
                            if (jsonItem.has("form")) {
                                form = jsonItem.get("form").getAsInt();
                            }
                            jsonItem.remove("id");
                            jsonItem.addProperty("id", "pixelmon:pixelmon_sprite");
                            if (!jsonItem.has("Count")) {
                                jsonItem.addProperty("Count", 1);
                            }
                            if (!jsonItem.has("tag")) {
                                jsonItem.add("tag", new JsonObject());
                                EnumSpecies pokemon = EnumSpecies.getFromNameAnyCase(species);
                                jsonItem.getAsJsonObject("tag").addProperty("ndex", pokemon.getNationalPokedexInteger());
                                if (form > 0) {
                                    jsonItem.getAsJsonObject("tag").addProperty("form", form);
                                }
                            }
                            int price = item.get("buy").getAsInt();
                            try {
                                jsonItem.getAsJsonObject("tag").add("display", new JsonObject());
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").add("Lore", new JsonArray());
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").addProperty("Name", Reference.resetText + species);
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").getAsJsonArray("Lore").add(Reference.resetText + "depository");
                                itemStack = new ItemStack(JsonToNBT.getTagFromJson(item.getAsJsonObject("item").toString()));
                            } catch (NBTException e) {
                                e.printStackTrace();
                            }
                            if (item.has("requirements")) {
                                JsonArray requirementArray = item.getAsJsonArray("requirements");
                                Requirement[] requirements = new Requirement[requirementArray.size()];
                                for (int k = 0; k < requirementArray.size(); k++) {
                                    requirements[k] = ParseJson.parseRequirement(requirementArray.get(k).getAsJsonObject());
                                }
                                customItem = new CustomShopItem(itemStack, requirements);
                            } else {
                                customItem = new CustomShopItem(itemStack, new Requirement[0]);
                            }
                        } else {
                            String itemNbt = item.getAsJsonObject("item").toString();
                            JsonObject jsonItem = item.getAsJsonObject("item");
                            int price = 0;
                            int sell = 0;
                            if (jsonItem.has("tag")) {
                                if (jsonItem.getAsJsonObject("tag").has("display")) {
                                    jsonItem.getAsJsonObject("tag").remove("display");
                                }
                            }
                            if (item.has("buy")) {
                                price = item.get("buy").getAsInt();
                                sell = 0;
                                if (!item.has("buyOnly")) {
                                    sell = price / 2;
                                } else if (!item.get("buyOnly").getAsBoolean()) {
                                    sell = price / 2;
                                }
                            }
                            if (item.has("sell")) {
                                sell = item.get("sell").getAsInt();
                            }
                            try {
                                itemStack = new ItemStack(JsonToNBT.getTagFromJson(item.getAsJsonObject("item").toString()));
                            } catch (NBTException e) {
                                e.printStackTrace();
                            }
                            if (item.has("requirements")) {
                                JsonArray requirementArray = item.getAsJsonArray("requirements");
                                Requirement[] requirements = new Requirement[requirementArray.size()];
                                for (int k = 0; k < requirementArray.size(); k++) {
                                    requirements[k] = ParseJson.parseRequirement(requirementArray.get(k).getAsJsonObject());
                                }
                                customItem = new CustomShopItem(itemStack, requirements);
                            } else {
                                customItem = new CustomShopItem(itemStack, new Requirement[0]);
                            }
                            if (price > 0) {
                                customItem.setLeftClickActions(price, "giveitem " + itemNbt);
                            }
                            if (sell > 0) {
                                customItem.setRightClickActions(sell, itemNbt);
                            }
                        }
                        itemList[j] = customItem;
                    }
                    customItems[i] = itemList;
                }
                else {
                    customItems[i] = new CustomShopItem[1];
                    customItems[i][0] = null;
                }
            }
        }
        else {
            for (int i = 0; i < rows * 9; i++) {
                if (items.has("slot" + i)) {
                    JsonArray slot = items.getAsJsonArray("slot" + i);
                    CustomShopItem[] itemList = new CustomShopItem[slot.size()];
                    for (int j = 0; j < slot.size(); j++) {
                        CustomShopItem customItem;
                        JsonObject item = slot.get(j).getAsJsonObject();
                        ItemStack itemStack = null;
                        if (item.getAsJsonObject("item").get("id").getAsString().toLowerCase().startsWith("pokemon:")) {
                            JsonObject jsonItem = item.getAsJsonObject("item");
                            String species = jsonItem.get("id").getAsString().toLowerCase().replace("pokemon:", "");
                            int form = 0;
                            if (jsonItem.has("form")) {
                                form = jsonItem.get("form").getAsInt();
                            }
                            jsonItem.remove("id");
                            jsonItem.addProperty("id", "pixelmon:pixelmon_sprite");
                            if (!jsonItem.has("Count")) {
                                jsonItem.addProperty("Count", 1);
                            }
                            if (!jsonItem.has("tag")) {
                                jsonItem.add("tag", new JsonObject());
                                EnumSpecies pokemon = EnumSpecies.getFromNameAnyCase(species);
                                jsonItem.getAsJsonObject("tag").addProperty("ndex", pokemon.getNationalPokedexInteger());
                                if (form > 0) {
                                    jsonItem.getAsJsonObject("tag").addProperty("form", form);
                                }
                            }
                            int price = item.get("buy").getAsInt();
                            try {
                                jsonItem.getAsJsonObject("tag").add("display", new JsonObject());
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").add("Lore", new JsonArray());
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").addProperty("Name", Reference.resetText + species);
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").getAsJsonArray("Lore").add(Reference.resetText + "Left-Click: buy for " + price);
                                itemStack = new ItemStack(JsonToNBT.getTagFromJson(item.getAsJsonObject("item").toString()));
                            } catch (NBTException e) {
                                e.printStackTrace();
                            }
                            String buyCommand = "pokegive @dp " + species + " lvl:5";
                            if (form > 0)
                                buyCommand += " form:" + form;
                            if (item.has("requirements")) {
                                JsonArray requirementArray = item.getAsJsonArray("requirements");
                                Requirement[] requirements = new Requirement[requirementArray.size()];
                                for (int k = 0; k < requirementArray.size(); k++) {
                                    requirements[k] = ParseJson.parseRequirement(requirementArray.get(k).getAsJsonObject());
                                }
                                customItem = new CustomShopItem(itemStack, requirements);
                            } else {
                                customItem = new CustomShopItem(itemStack, new Requirement[0]);
                            }
                            customItem.setLeftClickActions(price, buyCommand);
                        } else if (item.getAsJsonObject("item").get("id").getAsString().toLowerCase().startsWith("depository:")) {
                            JsonObject jsonItem = item.getAsJsonObject("item");
                            String species = jsonItem.get("id").getAsString().toLowerCase().replace("depository:", "");
                            int form = 0;
                            if (jsonItem.has("form")) {
                                form = jsonItem.get("form").getAsInt();
                            }
                            jsonItem.remove("id");
                            jsonItem.addProperty("id", "pixelmon:pixelmon_sprite");
                            if (!jsonItem.has("Count")) {
                                jsonItem.addProperty("Count", 1);
                            }
                            if (!jsonItem.has("tag")) {
                                jsonItem.add("tag", new JsonObject());
                                EnumSpecies pokemon = EnumSpecies.getFromNameAnyCase(species);
                                jsonItem.getAsJsonObject("tag").addProperty("ndex", pokemon.getNationalPokedexInteger());
                                if (form > 0) {
                                    jsonItem.getAsJsonObject("tag").addProperty("form", form);
                                }
                            }
                            int price = item.get("buy").getAsInt();
                            try {
                                jsonItem.getAsJsonObject("tag").add("display", new JsonObject());
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").add("Lore", new JsonArray());
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").addProperty("Name", Reference.resetText + species);
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").getAsJsonArray("Lore").add(Reference.resetText + "Left-Click: buy for " + price);
                                itemStack = new ItemStack(JsonToNBT.getTagFromJson(item.getAsJsonObject("item").toString()));
                            } catch (NBTException e) {
                                e.printStackTrace();
                            }
                            String buyCommand = "depository " + species;
                            if (form > 0)
                                buyCommand += " " + form;
                            if (item.has("requirements")) {
                                JsonArray requirementArray = item.getAsJsonArray("requirements");
                                Requirement[] requirements = new Requirement[requirementArray.size()];
                                for (int k = 0; k < requirementArray.size(); k++) {
                                    requirements[k] = ParseJson.parseRequirement(requirementArray.get(k).getAsJsonObject());
                                }
                                customItem = new CustomShopItem(itemStack, requirements);
                            } else {
                                customItem = new CustomShopItem(itemStack, new Requirement[0]);
                            }
                            customItem.setLeftClickActions(price, buyCommand);
                        } else {
                            String itemNbt = item.getAsJsonObject("item").toString();
                            JsonObject jsonItem = item.getAsJsonObject("item");
                            int price = 0;
                            int sell = 0;
                            if (jsonItem.has("tag")) {
                                if (jsonItem.getAsJsonObject("tag").has("display")) {
                                    jsonItem.getAsJsonObject("tag").remove("display");
                                }
                            }
                            if (!jsonItem.has("tag")) {
                                jsonItem.add("tag", new JsonObject());
                            }
                            jsonItem.getAsJsonObject("tag").add("display", new JsonObject());
                            jsonItem.getAsJsonObject("tag").getAsJsonObject("display").add("Lore", new JsonArray());
                            if (item.has("buy")) {
                                price = item.get("buy").getAsInt();
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").getAsJsonArray("Lore").add(Reference.darkGreen + "Left-Click: buy for " + price);
                                sell = 0;
                                if (item.has("sell")) {
                                    sell = item.get("sell").getAsInt();
                                    jsonItem.getAsJsonObject("tag").getAsJsonObject("display").getAsJsonArray("Lore").add(Reference.darkRed + "Right-Click: sell for " + sell);
                                } else if (!item.has("buyOnly")) {
                                    sell = price / 2;
                                    jsonItem.getAsJsonObject("tag").getAsJsonObject("display").getAsJsonArray("Lore").add(Reference.darkRed + "Right-Click: sell for " + sell);
                                } else if (!item.get("buyOnly").getAsBoolean()) {
                                    sell = price / 2;
                                    jsonItem.getAsJsonObject("tag").getAsJsonObject("display").getAsJsonArray("Lore").add(Reference.darkRed + "Right-Click: sell for " + sell);
                                }
                            } else if (item.has("sell")) {
                                sell = item.get("sell").getAsInt();
                                jsonItem.getAsJsonObject("tag").getAsJsonObject("display").getAsJsonArray("Lore").add(Reference.darkRed + "Right-Click: sell for " + sell);
                            }
                            try {
                                itemStack = new ItemStack(JsonToNBT.getTagFromJson(item.getAsJsonObject("item").toString()));
                            } catch (NBTException e) {
                                e.printStackTrace();
                            }
                            if (item.has("requirements")) {
                                JsonArray requirementArray = item.getAsJsonArray("requirements");
                                Requirement[] requirements = new Requirement[requirementArray.size()];
                                for (int k = 0; k < requirementArray.size(); k++) {
                                    requirements[k] = ParseJson.parseRequirement(requirementArray.get(k).getAsJsonObject());
                                }
                                customItem = new CustomShopItem(itemStack, requirements);
                            } else {
                                customItem = new CustomShopItem(itemStack, new Requirement[0]);
                            }
                            if (price > 0) {
                                customItem.setLeftClickActions(price, "giveitem " + itemNbt);
                            }
                            if (sell > 0) {
                                customItem.setRightClickActions(sell, itemNbt);
                            }
                        }
                        itemList[j] = customItem;
                    }
                    customItems[i] = itemList;
                } else {
                    customItems[i] = new CustomShopItem[1];
                    customItems[i][0] = null;
                }
            }
        }
        Requirement[] requirements = new Requirement[0];
        if(json.has("requirements")){
            JsonArray requirementsArray=json.getAsJsonArray("requirements");
            requirements=new Requirement[requirementsArray.size()];
            int i=0;
            for(JsonElement requirement: requirementsArray){
                requirements[i++]=ParseJson.parseRequirement(requirement.getAsJsonObject());
            }
        }
        CustomShopData data=new CustomShopData(rows, inventoryName, displayName, customItems, requirements);
        data.setPixelmonGui(pixelmonGUI);
        this.shops.add(data);
    }
}

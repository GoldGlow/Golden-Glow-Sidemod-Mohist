package com.goldenglow.common.inventory;

import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.ParseJson;
import com.goldenglow.common.util.Reference;
import com.goldenglow.common.util.Requirement;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by JeanMarc on 6/25/2019.
 */
public class CustomInventoryHandler {
    public List<CustomInventoryData> inventories=new ArrayList<CustomInventoryData>();

    File dir;

    public void init() {
        dir = new File(Reference.inventoryDir);
        if(!dir.exists()) {
            if (!dir.getParentFile().exists())
                dir.getParentFile().mkdirs();
            dir.mkdir();
        }
        else
            this.loadInventories();
    }

    public void loadInventories(){
        GGLogger.info("Loading Inventories...");
        try {
            for (File f : Objects.requireNonNull(dir.listFiles())) {
                if (f.getName().endsWith(".json")) {
                    this.loadInventory(f.getName().replace(".json", ""));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadInventory(String inventoryName) throws IOException{
        InputStream iStream = new FileInputStream(new File(dir, inventoryName+".json"));
        JsonObject json = new JsonParser().parse(new InputStreamReader(iStream, StandardCharsets.UTF_8)).getAsJsonObject();
        String displayName = json.get("name").getAsString();
        int rows=json.get("rows").getAsInt();
        JsonObject items=json.get("items").getAsJsonObject();
        CustomItem[][] customItems=new CustomItem[rows*9][];
        for(int i=0;i<rows*9;i++){
            if(items.has("slot"+i)){
                JsonArray slot=items.getAsJsonArray("slot"+i);
                CustomItem[] itemList=new CustomItem[slot.size()];
                for (int j=0;j<slot.size();j++) {
                    CustomItem customItem;
                    JsonObject item=slot.get(j).getAsJsonObject();
                    ItemStack itemStack=null;
                    if(item.getAsJsonObject("item").get("id").getAsString().toLowerCase().startsWith("aw:")){
                        String awItem=item.getAsJsonObject("item").get("id").getAsString().replace("aw:","");
                        LibraryFile file=new LibraryFile(awItem);
                        Skin skin = SkinIOUtils.loadSkinFromLibraryFile(file);
                        CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, file);
                        SkinIdentifier identifier = new SkinIdentifier(0, file, 0, skin.getSkinType());
                        itemStack = SkinNBTHelper.makeEquipmentSkinStack(new SkinDescriptor(identifier));
                    }
                    else {
                        try {
                            itemStack = new ItemStack(JsonToNBT.getTagFromJson(item.getAsJsonObject("item").toString()));
                        } catch (NBTException e) {
                            e.printStackTrace();
                        }
                    }
                    if(item.has("requirements")){
                        JsonArray requirementArray=item.getAsJsonArray("requirements");
                        Requirement[] requirements=new Requirement[requirementArray.size()];
                        for(int k=0;k<requirementArray.size();k++){
                            requirements[k]=ParseJson.parseRequirement(requirementArray.get(k).getAsJsonObject());
                        }
                        customItem=new CustomItem(itemStack, requirements);
                    }
                    else {
                        customItem=new CustomItem(itemStack, new Requirement[0]);
                    }
                    if(item.has("leftClickActions")){
                        JsonArray actionArray=item.getAsJsonArray("leftClickActions");
                        Action[] actions=new Action[actionArray.size()];
                        for(int k=0;k<actionArray.size();k++){
                            actions[k]=ParseJson.parseAction(actionArray.get(k).getAsJsonObject());
                        }
                        customItem.leftClickActions=actions;
                    }
                    if(item.has("rightClickActions")){
                        JsonArray actionArray=item.getAsJsonArray("rightClickActions");
                        Action[] actions=new Action[actionArray.size()];
                        for(int k=0;k<actionArray.size();k++){
                            actions[k]=ParseJson.parseAction(actionArray.get(k).getAsJsonObject());
                        }
                        customItem.rightClickActions=actions;
                    }
                    itemList[j]=customItem;
                }
                customItems[i]=itemList;
            }
            else {
                customItems[i]=new CustomItem[1];
                customItems[i][0]=null;
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
        this.inventories.add(new CustomInventoryData(rows, inventoryName, displayName, customItems, requirements));
    }
}

package com.goldenglow.common.util.scripting;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.inventory.CustomInventoryData;
import com.goldenglow.common.inventory.InstancedContainer;
import com.goldenglow.common.util.GGLogger;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.network.play.server.SPacketOpenWindow;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.QuestData;

import java.util.HashMap;

public class InventoryFunctions {

    //Add a key item to the player's Key Items Pocket. Used for different main and side quests
    public static void addKeyItem(PlayerWrapper playerWrapper, String itemStack){
        IPlayerData playerData = playerWrapper.getMCEntity().getCapability(OOPlayerProvider.OO_DATA, null);
        ItemStack item=null;
        try {
            item=new ItemStack(JsonToNBT.getTagFromJson(itemStack));
        } catch (NBTException e) {
            e.printStackTrace();
        }
        if(item!=null) {
            GGLogger.info(item.getItemDamage());
            playerData.addKeyItem(item);
            /*IQuest[] quests=playerWrapper.getActiveQuests();
            for(IQuest quest: quests){
                if((QuestInterface)quest instanceof QuestItem){
                    for(ItemStack questItem:((QuestItem) quest).items.items){
                        if(questItem.equals(item)){
                            quest.
                        }
                    }
                }
            }*/
        }
    }

    //Remove Key items from quests when they're completed/you give the item
    public static void removeKeyItem(PlayerWrapper playerWrapper, String displayName){
        IPlayerData playerData = playerWrapper.getMCEntity().getCapability(OOPlayerProvider.OO_DATA, null);
        playerData.removeKeyItem(displayName);
    }

    public static void createCustomChest(EntityPlayerMP playerMP, String inventoryName){
        CustomInventoryData data=null;
        for(CustomInventoryData inventoryData: GoldenGlow.customInventoryHandler.inventories){
            if(inventoryData.getName().equals(inventoryName)){
                data=inventoryData;
            }
        }
        if(data!=null){
        }
    }

    //Needs to be updated
    public static void createInstancedInv(EntityPlayerMP playerMP, String[] items, String containerName, int questID) {
        HashMap<Integer, QuestData> data = PlayerData.get(playerMP).questData.activeQuests;
        if(data.containsKey(questID)) {
            QuestData qData = data.get(questID);
            if(!qData.isCompleted) {
                //Create Inventory with specified items
                InventoryBasic inv = new InventoryBasic(containerName, false, (9+(9 % items.length)));
                for (String tag : items) {
                    try {
                        ItemStack stack = new ItemStack(JsonToNBT.getTagFromJson(tag));
                        if(stack!=null) {
                            inv.addItem(stack);
                        }
                    } catch (NBTException e) {
                        e.printStackTrace();
                    }
                }
                //Show inventory to player
                playerMP.getNextWindowId();
                playerMP.connection.sendPacket(new SPacketOpenWindow(playerMP.currentWindowId, "minecraft:container", inv.getDisplayName(), inv.getSizeInventory()));
                playerMP.openContainer = new InstancedContainer(playerMP.inventory, inv, playerMP);
                playerMP.openContainer.windowId = playerMP.currentWindowId;
                playerMP.openContainer.addListener(playerMP);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(playerMP, playerMP.openContainer));
            }
        }
    }

    public static void addAwItem(PlayerWrapper player, String item){
        IPlayerData playerData = player.getMCEntity().getCapability(OOPlayerProvider.OO_DATA, null);
        ItemStack itemStack=null;
        try {
            itemStack=new ItemStack(JsonToNBT.getTagFromJson(item));
        } catch (NBTException e) {
            e.printStackTrace();
        }
        if(itemStack!=null)
            playerData.addAWItem(itemStack);
    }

    //Used at a few pokeloots, probably for a quest too
    public static boolean unlockTM(PlayerWrapper player, String ItemID) {
        ItemStack tm=new ItemStack(Item.getByNameOrId(ItemID));
        IPlayerData playerData = player.getMCEntity().getCapability(OOPlayerProvider.OO_DATA, null);
        return playerData.unlockTM(tm);
    }
}

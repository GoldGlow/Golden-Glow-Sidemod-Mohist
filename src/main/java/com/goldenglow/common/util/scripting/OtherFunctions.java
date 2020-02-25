package com.goldenglow.common.util.scripting;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.inventory.shops.CustomShop;
import com.goldenglow.common.inventory.shops.CustomShopData;
import com.goldenglow.common.inventory.shops.CustomShopItem;
import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.PermissionUtils;
import com.goldenglow.common.util.Scoreboards;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.OpenScreen;
import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.SetNPCData;
import com.pixelmonmod.pixelmon.entities.npcs.NPCShopkeeper;
import com.pixelmonmod.pixelmon.entities.npcs.registry.*;
import com.pixelmonmod.pixelmon.enums.EnumGuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.world.WorldServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;

import java.util.UUID;

public class OtherFunctions {
    //Probably needs to be updated to use the MC notification system instead of CNPCs
    public static void showAchievement(PlayerWrapper playerWrapper, String firstLine, String secondLine){
        playerWrapper.sendNotification(firstLine, secondLine, Integer.valueOf(playerWrapper.getMCEntity().getEntityData().getInteger("RouteNotification")));
    }

    //Debug?
    public static void test() {
        System.out.println("Test works");
    }

    public static void unlockBugCatcher(EntityPlayerMP player){
        if(!PermissionUtils.checkPermission(player, "titles.bug_catcher")) {
            showAchievement(new PlayerWrapper(player), "Titles", "Unlocked title: Bug Catcher");
            PermissionUtils.addPermissionNode(player, "titles.bug_catcher");
            GGLogger.info("unlocked Bug Catcher");
        }
    }

    //Opens a dialog for a NPC. Used for other scripting stuff
    public static void openDialog(PlayerWrapper player, NPCWrapper npc, int dialogId){
        NoppesUtilServer.openDialog((EntityPlayerMP) player.getMCEntity(), (EntityNPCInterface) npc.getMCEntity(), (Dialog) DialogController.instance.get(dialogId));
    }

    public static void setScoreboard(PlayerWrapper player){
        Scoreboards.buildScoreboard((EntityPlayerMP)player.getMCEntity());
    }

    public static void equipArmor(EntityPlayerMP player, int slot, String item){
        ItemStack itemStack=null;
        try {
            itemStack=new ItemStack(JsonToNBT.getTagFromJson(item));
        } catch (NBTException e) {
            e.printStackTrace();
        }
        player.inventory.setInventorySlotContents(100+slot, itemStack);
    }

    public static ItemStack getNPCDialogItem(NPCWrapper npc) {
        IItemStack item=npc.getInventory().getDropItem(0);
        return item.getMCItemStack();
    }

    public static void openShopMenu(PlayerWrapper player, String name){
        for(CustomShopData inventoryData: GoldenGlow.customShopHandler.shops) {
            if (inventoryData.getName().equals(name)) {
                openShopMenu(player, inventoryData);
                return;
            }
        }
    }

    public static void openShopMenu(PlayerWrapper player, CustomShopData data){
        EntityPlayerMP playerMP=(EntityPlayerMP)player.getMCEntity();
        ArrayList<ShopItemWithVariation> buyList=getBuyList(player, data);
        ArrayList<ShopItemWithVariation> sellList=getSellList(player, data);
        IPlayerData playerData=playerMP.getCapability(OOPlayerProvider.OO_DATA, null);
        playerData.setShopName(data.getName());
        Pixelmon.network.sendTo(new SetNPCData("", new ShopkeeperChat("",""), buyList, sellList), (EntityPlayerMP)player.getMCEntity());
        NPCShopkeeper shopkeeper = new NPCShopkeeper(player.getWorld().getMCWorld());
        shopkeeper.setId(998);
        shopkeeper.setPosition(player.getX(), player.getY(), player.getZ());
        ((EntityPlayerMP)player.getMCEntity()).connection.sendPacket(new SPacketSpawnMob(shopkeeper));
        OpenScreen.open((EntityPlayerMP)player.getMCEntity(), EnumGuiScreen.Shopkeeper, 998);
        ((EntityPlayerMP)player.getMCEntity()).removeEntity(shopkeeper);
    }

    public static void openShopMenu(PlayerWrapper player, String name, String openMsg, String closeMsg) {
        ArrayList<ShopItemWithVariation> buyList = getBuyList(player);
        ArrayList<ShopItemWithVariation> sellList = getSellList(player);
        Pixelmon.network.sendTo(new SetNPCData(name, new ShopkeeperChat(openMsg,closeMsg), buyList, sellList), (EntityPlayerMP)player.getMCEntity());
        NPCShopkeeper shopkeeper = new NPCShopkeeper(player.getWorld().getMCWorld());
        shopkeeper.setId(999);
        shopkeeper.setPosition(player.getX(), player.getY(), player.getZ());
        ((EntityPlayerMP)player.getMCEntity()).connection.sendPacket(new SPacketSpawnMob(shopkeeper));
        OpenScreen.open((EntityPlayerMP)player.getMCEntity(), EnumGuiScreen.Shopkeeper, 999);
        ((EntityPlayerMP)player.getMCEntity()).removeEntity(shopkeeper);
    }

    public static ArrayList<ShopItemWithVariation> getBuyList(PlayerWrapper player) {
        ArrayList<ShopItemWithVariation> buyList = new ArrayList<>();
        buyList.add(new ShopItemWithVariation(new ShopItem(new BaseShopItem("minecraft:stick", new ItemStack(Items.STICK), 1, 2), 1,0, false)));
        return buyList;
    }

    public static ArrayList<ShopItemWithVariation> getBuyList(PlayerWrapper player, String name) {
        for(CustomShopData inventoryData: GoldenGlow.customShopHandler.shops) {
            if (inventoryData.getName().equals(name)) {
                return getBuyList(player, inventoryData);
            }
        }
        return new ArrayList<ShopItemWithVariation>();
    }

    public static ArrayList<ShopItemWithVariation> getBuyList(PlayerWrapper player, CustomShopData data) {
        ArrayList<ShopItemWithVariation> buyList = new ArrayList<>();
        for (int i = 0; i < data.getRows() * 9 - 1; i++) {
            if (i < data.getItems().length) {
                if(data.getItems()[i]!=null) {
                    CustomShopItem item = CustomShop.getItem(data.getItems()[i], (EntityPlayerMP) player.getMCEntity());
                    if (item != null) {
                        ShopItemWithVariation shopItemWithVariation = new ShopItemWithVariation(new ShopItem(new BaseShopItem(item.getItem().getDisplayName(), item.getItem(), item.buyPrice, item.sellPrice), 1, 0, false));
                        if (item.buyPrice > 0) {
                            buyList.add(shopItemWithVariation);
                        }
                    }
                }
            }
        }
        return buyList;
    }

    public static ArrayList<ShopItemWithVariation> getSellList(PlayerWrapper player) {
        ArrayList<ShopItemWithVariation> sellList = new ArrayList<>();
        return sellList;
    }

    public static ArrayList<ShopItemWithVariation> getSellList(PlayerWrapper player, String name){
        for(CustomShopData inventoryData: GoldenGlow.customShopHandler.shops) {
            if (inventoryData.getName().equals(name)) {
                return getSellList(player, inventoryData);
            }
        }
        return new ArrayList<ShopItemWithVariation>();
    }

    public static ArrayList<ShopItemWithVariation> getSellList(PlayerWrapper player, CustomShopData data) {
        ArrayList<ShopItemWithVariation> sellList = new ArrayList<>();
        for (int i = 0; i < data.getRows() * 9 - 1; i++) {
            if (i < data.getItems().length) {
                CustomShopItem item = CustomShop.getItem(data.getItems()[i], (EntityPlayerMP)player.getMCEntity());
                if(item!=null) {
                    ShopItemWithVariation shopItemWithVariation = new ShopItemWithVariation(new ShopItem(new BaseShopItem(item.getItem().getDisplayName(), item.getItem(), item.buyPrice, item.sellPrice), 1, 0, false));
                    if (item.sellPrice>0&&doesPlayerHaveItem(shopItemWithVariation, player)) {
                        sellList.add(shopItemWithVariation);
                    }
                }
            }
        }
        return sellList;
    }

    public static boolean doesPlayerHaveItem(ShopItemWithVariation shopItemWithVariation, PlayerWrapper playerWrapper){
        EntityPlayerMP playerMP=(EntityPlayerMP) playerWrapper.getMCEntity();
        for(int i=0;i<playerMP.inventory.getSizeInventory();i++){
            ItemStack item=playerMP.inventory.getStackInSlot(i);
            if(item!=null){
                if(Item.getIdFromItem(item.getItem())==Item.getIdFromItem(shopItemWithVariation.getItem().getItem())){
                    return true;
                }
            }
        }
        return false;
    }

    public static void addPlayerMark(PlayerWrapper player, int color){
        MarkData data=player.getMCEntity().getCapability(MarkData.MARKDATA_CAPABILITY, null);
        data.addMark(1, color);
    }

    public static void clearPlayerMarks(PlayerWrapper player){
        MarkData data=player.getMCEntity().getCapability(MarkData.MARKDATA_CAPABILITY, null);
        data.marks.clear();
        data.syncClients();
    }
}

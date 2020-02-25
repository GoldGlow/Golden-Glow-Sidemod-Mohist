package com.goldenglow.common.inventory.shops;

import com.goldenglow.common.inventory.Action;
import com.goldenglow.common.inventory.CustomInventory;
import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.Reference;
import com.goldenglow.common.util.Requirement;
import com.goldenglow.common.util.scripting.OtherFunctions;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccount;
import com.pixelmonmod.pixelmon.comm.packetHandlers.OpenScreen;
import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.SetNPCData;
import com.pixelmonmod.pixelmon.entities.npcs.NPCShopkeeper;
import com.pixelmonmod.pixelmon.entities.npcs.registry.BaseShopItem;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItem;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItemWithVariation;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopkeeperChat;
import com.pixelmonmod.pixelmon.enums.EnumGuiScreen;
import net.minecraft.command.ICommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.PlayerWrapper;

import java.util.ArrayList;

public class CustomShop extends CustomInventory {
    CustomShopData data;

    public CustomShop(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public static CustomShopItem getItem(CustomShopItem[] items, EntityPlayerMP player) {
        if (items.length>0){
            for (CustomShopItem item : items) {
                if (item != null) {
                    if (Requirement.checkRequirements(item.getRequirements(), player)) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public void setData(CustomShopData data){this.data=data;}

    public CustomShopData getData(){
        return this.data;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        InventoryPlayer inventoryplayer = player.inventory;
        Slot slot = getSlot(slotId);

        if(slot.isSameInventory(getSlot(0))) {
            CustomShopItem item = CustomShop.getItem(data.getItems()[slotId], (EntityPlayerMP) player);
            if (item != null) {
                if (dragType == 0) {
                        boolean didAction=false;
                        for (Action action : item.getLeftClickActions()) {
                            if (Requirement.checkRequirements(action.requirements, (EntityPlayerMP) player)) {
                                IPixelmonBankAccount bankAccount = (IPixelmonBankAccount) Pixelmon.moneyManager.getBankAccount((EntityPlayerMP) player).orElse(null);
                                if (action.getValue().startsWith("pokegive")) {
                                    ((EntityPlayerMP) player).sendMessage(new TextComponentString("Successfully bought " + action.getValue().split(" ")[2] + "!"));
                                    bankAccount.changeMoney(-1 * item.buyPrice);
                                    action.doAction((EntityPlayerMP)player);
                                    didAction=true;
                                    break;
                                }
                                else if(action.getActionType()== Action.ActionType.DEPOSITORY_POKEMON){
                                    ((EntityPlayerMP) player).sendMessage(new TextComponentString("Successfully bought " + action.getValue().split(" ")[0] + "!"));
                                    bankAccount.changeMoney(-1 * item.buyPrice);
                                    action.doAction((EntityPlayerMP)player);
                                    didAction=true;
                                    break;
                                }
                                else {
                                    try {
                                        if(Requirement.checkSpaceRequirement(((EntityPlayerMP)player), new ItemStack(JsonToNBT.getTagFromJson(action.value)))) {
                                            action.doAction((EntityPlayerMP) player);
                                            didAction=true;
                                            bankAccount.changeMoney(-1 * item.buyPrice);
                                            break;
                                        }
                                    } catch (NBTException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if(!didAction) {
                            ICommandManager icommandmanager = ((EntityPlayerMP)player).getEntityWorld().getMinecraftServer().getCommandManager();
                            icommandmanager.executeCommand(new RConConsoleSource(((EntityPlayerMP)player).getEntityWorld().getMinecraftServer()), "tellraw "+((EntityPlayerMP)player).getName()+" [\"\",{\"text\":\"You can't buy this!\",\"color\":\"dark_red\"}]");
                        }else {
                            if(this.data.getName().equals("Starter")){
                                player.closeScreen();
                                new PlayerWrapper((EntityPlayerMP)player).showDialog(328, "Poke Depository Clerk");
                            }
                            else{
                                openCustomShop(((EntityPlayerMP) player), data);
                            }
                        }
                        return null;
                } else if (dragType == 1) {
                    for (Action action : item.getRightClickActions()) {
                        if (Requirement.checkRequirements(action.requirements, (EntityPlayerMP) player)) {
                            PlayerWrapper playerWrapper=new PlayerWrapper((EntityPlayerMP) player);
                            IItemStack iItemStack= NpcAPI.Instance().getIItemStack(item.getItem());
                            playerWrapper.removeItem(iItemStack, 1);
                            action.doAction((EntityPlayerMP) player);
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void openCustomShop(EntityPlayerMP playerMP, CustomShopData data){
        if(data.pixelmonGui){
            GGLogger.info("test");
            if (Requirement.checkRequirements(data.getRequirements(), playerMP)) {
                OtherFunctions.openShopMenu(new PlayerWrapper(playerMP), data);
            }
        }
        else {
            InventoryBasic chestInventory = new InventoryBasic(data.getName(), true, data.getRows() * 9);
            if (Requirement.checkRequirements(data.getRequirements(), playerMP)) {
                for (int i = 0; i < data.getRows() * 9 - 1; i++) {
                    if (i < data.getItems().length) {
                        CustomShopItem item = CustomShop.getItem(data.getItems()[i], playerMP);
                        if (item != null) {
                            if (item.getItem() != null) {
                                chestInventory.setInventorySlotContents(i, item.getItem());
                            }
                        }
                    }
                }
                ItemStack balance = new ItemStack(Item.getByNameOrId("variedcommodities:coin_bronze"));
                IPixelmonBankAccount bankAccount = (IPixelmonBankAccount) Pixelmon.moneyManager.getBankAccount(playerMP).orElse(null);
                int amount = 0;
                if (bankAccount != null) {
                    amount = bankAccount.getMoney();
                }
                balance.setStackDisplayName(Reference.resetText + "Current Balance: " + amount);
                chestInventory.setInventorySlotContents(data.getRows() * 9 - 1, balance);
                playerMP.getNextWindowId();
                playerMP.connection.sendPacket(new SPacketOpenWindow(playerMP.currentWindowId, "minecraft:container", new TextComponentString(data.getDisplayName()), data.getRows() * 9));
                playerMP.openContainer = new CustomShop(playerMP.inventory, chestInventory, playerMP);
                ((CustomShop) playerMP.openContainer).setData(data);
                playerMP.openContainer.windowId = playerMP.currentWindowId;
                playerMP.openContainer.addListener(playerMP);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(playerMP, playerMP.openContainer));
            }
        }
    }
}

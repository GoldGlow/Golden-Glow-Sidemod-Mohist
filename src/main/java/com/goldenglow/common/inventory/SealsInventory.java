package com.goldenglow.common.inventory;

import com.goldenglow.common.data.player.OOPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.config.PixelmonItemsPokeballs;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SealsInventory {
    static void openCustomSealsInventory(EntityPlayerMP player) {
        OOPlayerData playerData = (OOPlayerData)player.getCapability(OOPlayerProvider.OO_DATA, null);
        CustomInventoryData data = new CustomInventoryData(9, "Seals", "Seals", new CustomItem[9][], null);
        InventoryBasic inventory = new InventoryBasic("Seals", true, 9);
        PlayerPartyStorage partyStorage = Pixelmon.storageManager.getParty(player);
        for(int i = 0; i < 6; i++) {
            String name = playerData.getEquippedSeals()[i];
            if(name==null || name.isEmpty())
                name = "None";
            ItemStack stack = new ItemStack(Blocks.BARRIER);
            if(partyStorage.get(i)!=null)
                stack = ItemPixelmonSprite.getPhoto(partyStorage.get(i));
            inventory.setInventorySlotContents(i+1, stack.setStackDisplayName(TextFormatting.RESET+"Slot "+(i+1)+": "+name));
            data.items[i] = new CustomItem[]{ new CustomItem(stack, null).setLeftClickActions(new Action[]{ new Action(Action.ActionType.OPEN_INV, "sealChoice "+i) }) };
        }
        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString(TextFormatting.GOLD+"Seals"), 9));
        player.openContainer = new CustomInventory(player.inventory, inventory, player);
        ((CustomInventory)player.openContainer).setData(data);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
    }

    static void openSealSelection(EntityPlayerMP player, int slot) {
        Random r = new Random();
        OOPlayerData playerData = (OOPlayerData)player.getCapability(OOPlayerProvider.OO_DATA, null);
        List<String> unlockedSeals = playerData.getUnlockedSeals();
        int rows = Math.max((int)Math.ceil(playerData.getUnlockedSeals().size()/6.0), 1);

        PlayerPartyStorage partyStorage = Pixelmon.storageManager.getParty(player);
        InventoryBasic inventory = new InventoryBasic("Choose Seal", true, rows*9);
        CustomInventoryData data = new CustomInventoryData(rows*9, "Choose a Seal: Slot "+slot, "Choose a Seal: Slot "+slot, new CustomItem[rows*9][], null);

        for(int i = 0; i < unlockedSeals.size(); i++) {
            int s = (((i/6)*9)+(i%6))+1;
            if(!Arrays.asList(playerData.getEquippedSeals()).contains(unlockedSeals.get(i))) {
                ItemStack stack = new ItemStack(PixelmonItemsPokeballs.getPokeballListNoMaster().get(r.nextInt(PixelmonItemsPokeballs.getPokeballListNoMaster().size()))).setStackDisplayName(unlockedSeals.get(i));
                inventory.setInventorySlotContents(s, stack);
                data.items[s] = new CustomItem[]{new CustomItem(stack, null).setLeftClickActions(new Action[]{new Action(Action.ActionType.SEAL_SET, unlockedSeals.get(i) + ":" + i)})};
            }
        }
        ItemStack stack = new ItemStack(Blocks.BARRIER).setStackDisplayName("Back");
        inventory.setInventorySlotContents(8, stack);
        data.items[8] = new CustomItem[]{new CustomItem(stack, null).setLeftClickActions(new Action[]{new Action(Action.ActionType.OPEN_INV, "seals")})};

        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString(TextFormatting.GOLD+"Seal Choice: Slot "+slot), rows*9));
        player.openContainer = new CustomInventory(player.inventory, inventory, player);
        ((CustomInventory)player.openContainer).setData(data);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
    }
}

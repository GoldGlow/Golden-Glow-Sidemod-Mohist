package com.goldenglow.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InstancedContainer extends ContainerChest {

    private List<ItemStack> items = new ArrayList<>();

    public InstancedContainer(IInventory playerInventory, IInventory chestInventory, EntityPlayer player) {
        super(playerInventory, chestInventory, player);

        for(int i = 0; i < this.getLowerChestInventory().getSizeInventory(); i++) {
            ItemStack stack = this.getLowerChestInventory().getStackInSlot(i);
            if(!stack.isEmpty()) {
                items.add(stack);
            }
        }
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        InventoryPlayer inventoryplayer = player.inventory;
        Slot slot = getSlot(slotId);

        if(!slot.isSameInventory(getSlot(0))) {
            super.slotClick(slotId, dragType, clickTypeIn, player);
        }
        else {

            Iterator<ItemStack> iterator = inventoryplayer.mainInventory.iterator();
            int freeSlots = 0;
            while (freeSlots < items.size() && iterator.hasNext()) {
                if (iterator.next().isEmpty())
                    freeSlots++;
            }

            if (freeSlots < items.size()) {
                TextComponentString msg = new TextComponentString("You don't have enough free spaces in your inventory! Make some room!");
                msg.getStyle().setUnderlined(true).setBold(true).setColor(TextFormatting.DARK_RED);
                player.sendStatusMessage(msg, true);
            } else {
                for (ItemStack stack : items) {
                    player.addItemStackToInventory(stack);
                    player.inventoryContainer.detectAndSendChanges();
                }
            }
            player.closeScreen();
        }
        return inventoryItemStacks.get(slotId);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        playerIn.inventoryContainer.detectAndSendChanges();
    }
}

package com.goldenglow.common.inventory;

import com.goldenglow.common.util.Reference;
import com.goldenglow.common.util.Requirement;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;

public class ChangeFormInventory extends CustomInventory {
    String species;
    int form;

    public ChangeFormInventory(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public void setSpecies(String species){
        this.species=species;
    }

    public void setForm(int form){
        this.form=form;
    }

        public static void openInventory(EntityPlayerMP playerMP, String species, int form){
        ItemStack[] party=CustomInventory.getPartyIcons(playerMP);
        InventoryBasic changeFormChest=new InventoryBasic("Change form", true, 9);
        for(ItemStack itemStack:party){
            if(itemStack.getDisplayName().equalsIgnoreCase(species)){
                itemStack.setStackDisplayName(Reference.green+"Able");
            }
            else{
                itemStack.setStackDisplayName(Reference.red+"Unable");
            }
            changeFormChest.addItem(itemStack);
        }
        playerMP.getNextWindowId();
        playerMP.connection.sendPacket(new SPacketOpenWindow(playerMP.currentWindowId, "minecraft:container", changeFormChest.getDisplayName(), changeFormChest.getSizeInventory()));
        playerMP.openContainer = new ChangeFormInventory(playerMP.inventory, changeFormChest, playerMP);
        ((ChangeFormInventory)playerMP.openContainer).form=form;
        ((ChangeFormInventory)playerMP.openContainer).species=species;
        playerMP.openContainer.windowId = playerMP.currentWindowId;
        playerMP.openContainer.addListener(playerMP);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(playerMP, playerMP.openContainer));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        InventoryPlayer inventoryplayer = player.inventory;
        Slot slot = getSlot(slotId);

        if(slot.isSameInventory(getSlot(0))) {
            if(slot.getStack().getDisplayName().equals(Reference.green+"Able")){
                Pokemon pokemon=Pixelmon.storageManager.getParty((EntityPlayerMP)player).get(slotId);
                if(pokemon.getForm()==this.form){
                    pokemon.setForm(0);
                }else{
                    pokemon.setForm(this.form);
                }
                Pixelmon.storageManager.getParty((EntityPlayerMP)player).updatePartyCache();
            }
        }
        player.closeScreen();
        return null;
    }
}

package com.goldenglow.common.handlers.events;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.inventory.CustomInventory;
import com.goldenglow.common.util.PermissionUtils;
import com.goldenglow.common.util.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemEventHandler {
    @SubscribeEvent
    public void itemDroppedEvent(ItemTossEvent event){
        if ((event.getEntityItem().getItem().getItem().getRegistryName() + "").equals("variedcommodities:diamond_dagger")) {
            ItemStack itemStack = event.getEntityItem().getItem();
            event.setCanceled(true);
            event.getPlayer().inventory.addItemStackToInventory(itemStack);
            event.getPlayer().sendMessage(new TextComponentString(Reference.red + "Cannot drop this item!"));
        }
    }

    //ToDo: Possibly update this code for efficiency/to use more appropriate Forge methods.
    @SubscribeEvent
    public void onPhoneItemRightClick(PlayerInteractEvent.RightClickItem event){
        if((event.getItemStack().getItem().getRegistryName()+"").equals("variedcommodities:diamond_dagger")) {
            if (event.getItemStack().getItemDamage() >= 100 && event.getItemStack().getItemDamage() < 200) {
                event.setCanceled(true);
                if(GoldenGlow.gymManager.leadingGym((EntityPlayerMP) event.getEntityPlayer())!=null)
                    CustomInventory.openInventory("GYM:"+GoldenGlow.gymManager.leadingGym((EntityPlayerMP)event.getEntityPlayer()), (EntityPlayerMP) event.getEntityPlayer());
                else
                    CustomInventory.openInventory("PokeHelper", (EntityPlayerMP) event.getEntityPlayer());
            }
            else if(event.getItemStack().getItemDamage()==201){
                event.getEntityPlayer().setItemStackToSlot(EntityEquipmentSlot.HEAD, event.getItemStack().copy());
                event.getItemStack().setCount(0);
            }
        }
    }
}

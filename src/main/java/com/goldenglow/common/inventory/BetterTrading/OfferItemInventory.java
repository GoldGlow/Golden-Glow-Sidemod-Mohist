package com.goldenglow.common.inventory.BetterTrading;

import com.goldenglow.common.inventory.CustomInventory;
import com.goldenglow.common.inventory.CustomInventoryData;
import com.goldenglow.common.inventory.CustomItem;
import com.goldenglow.common.util.Reference;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

public class OfferItemInventory extends CustomInventory {
    TradingOffer offer;

    public OfferItemInventory(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public void setOffer(TradingOffer offer){
        this.offer=offer;
    }

    public static void openInventory(EntityPlayerMP player, TradingOffer offer){
        InventoryBasic chestInventory=new InventoryBasic("Your offer", true, 18);
        PlayerPartyStorage storage= Pixelmon.storageManager.getParty(player);

        CustomItem[][] customItems=new CustomItem[18][];
        //offer

        List<ItemStack> tradedItems=offer.items;
        for(int i=0;i<tradedItems.size();i++){
            customItems[i]=new CustomItem[]{new CustomItem(tradedItems.get(i), null)};
            chestInventory.setInventorySlotContents(i, customItems[i][0].getItem());
        }

        ItemStack itemInfo=new ItemStack(Item.getByNameOrId("variedcommodities:diamond_dagger"));
        itemInfo.setStackDisplayName(Reference.resetText+"Item info");
        itemInfo.setItemDamage(17);
        itemInfo.getTagCompound().setInteger("Unbreakable", 1);
        NBTTagList offerLore=new NBTTagList();
        offerLore.appendTag(new NBTTagString(Reference.resetText+"Click on the first row to remove items"));
        offerLore.appendTag(new NBTTagString(Reference.resetText+"Click in the inventory to add items"));
        offerLore.appendTag(new NBTTagString(Reference.resetText+"Left click to add/remove 1"));
        offerLore.appendTag(new NBTTagString(Reference.resetText+"Right click to add/remove the full stack"));
        itemInfo.getTagCompound().getCompoundTag("display").setTag("Lore", offerLore);
        customItems[9]=new CustomItem[]{new CustomItem(itemInfo, null)};
        chestInventory.setInventorySlotContents(9, itemInfo);

        CustomItem returnButton=CustomItem.returnButton();
        customItems[17]=new CustomItem[]{returnButton};
        chestInventory.setInventorySlotContents(17, returnButton.getItem());

        CustomInventoryData data=new CustomInventoryData(2, "TradeOffer", "Your Offer", customItems, null);
        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString(data.getDisplayName()), data.getRows() * 9));
        player.openContainer = new OfferItemInventory(player.inventory, chestInventory, player);
        ((OfferItemInventory)player.openContainer).setData(data);
        ((OfferItemInventory)player.openContainer).setOffer(offer);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        InventoryPlayer inventoryplayer = player.inventory;
        Slot slot = getSlot(slotId);
        if(slot.isSameInventory(getSlot(0))) {
            if(slotId>=0&&slotId<=8&&CustomInventory.getItem(this.getData().getItems()[slotId], (EntityPlayerMP) player)!=null){
                if(dragType==0){
                    ItemStack item=slot.getStack().copy();
                    item.setCount(1);
                    this.offer.items.get(slotId).setCount(this.offer.items.get(slotId).getCount()-1);
                    ((EntityPlayerMP)player).inventory.addItemStackToInventory(item);
                }
                else if(dragType==1){
                    ((EntityPlayerMP)player).inventory.addItemStackToInventory(this.offer.items.get(slotId));
                    this.offer.items.remove(slotId);
                }
                player.closeScreen();
                OfferItemInventory.openInventory(((EntityPlayerMP)player), this.offer);
            }
            else if(slotId==17){
                player.closeScreen();
                OfferMakingInventory.openInventory(this.offer.player, this.offer);
            }
        }
        else{
            if(dragType==0){
                ItemStack item=slot.getStack().copy();
                item.setCount(1);
                this.offer.addItem(item);
                slot.getStack().setCount(slot.getStack().getCount()-1);
            }
            else if(dragType==1){
                if(!slot.getStack().getItem().getRegistryName().equals("variedcommodities:diamond_dagger")){
                    ItemStack itemStack=slot.getStack().copy();
                    this.offer.addItem(itemStack);
                    slot.decrStackSize(slot.getStack().getCount());
                }
            }
            player.closeScreen();
            OfferItemInventory.openInventory(((EntityPlayerMP)player), this.offer);
        }
        return null;
    }
}

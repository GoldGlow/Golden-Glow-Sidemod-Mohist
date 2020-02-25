package com.goldenglow.common.inventory.BetterTrading;

import com.goldenglow.common.inventory.CustomInventory;
import com.goldenglow.common.inventory.CustomInventoryData;
import com.goldenglow.common.inventory.CustomItem;
import com.goldenglow.common.util.Reference;
import com.pixelmonmod.pixelmon.Pixelmon;
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

public class OfferMoneyInventory extends CustomInventory {
    TradingOffer offer;

    public OfferMoneyInventory(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public void setOffer(TradingOffer offer){
        this.offer=offer;
    }

    public static void openInventory(EntityPlayerMP player, TradingOffer offer){
        InventoryBasic chestInventory=new InventoryBasic("Your offer", true, 27);
        PlayerPartyStorage storage= Pixelmon.storageManager.getParty(player);

        CustomItem[][] customItems=new CustomItem[27][];

        for(int i=0;i<9;i++){
            ItemStack itemInfo=new ItemStack(Item.getByNameOrId("variedcommodities:diamond_dagger"));
            itemInfo.setStackDisplayName(Reference.resetText+"Click to add $"+(int)Math.pow(10, i)+" to the offer");
            itemInfo.setItemDamage(39);
            itemInfo.getTagCompound().setInteger("Unbreakable", 1);
            customItems[8-i]=new CustomItem[]{new CustomItem(itemInfo, null)};
            chestInventory.setInventorySlotContents(8-i, customItems[8-i][0].getItem());
        }

        ItemStack balanceInfo=new ItemStack(Item.getByNameOrId("variedcommodities:coin_gold"));
        balanceInfo.setStackDisplayName(Reference.resetText+"Remaining balance: "+Pixelmon.storageManager.getParty(player).getMoney());
        customItems[9]=new CustomItem[]{new CustomItem(balanceInfo, null)};
        chestInventory.setInventorySlotContents(9, balanceInfo);

        ItemStack offerInfo=new ItemStack(Item.getByNameOrId("variedcommodities:coin_gold"));
        offerInfo.setStackDisplayName(Reference.resetText+"Offered money: "+offer.money);
        customItems[13]=new CustomItem[]{new CustomItem(offerInfo, null)};
        chestInventory.setInventorySlotContents(13, offerInfo);

        ItemStack returnButton=new ItemStack(Item.getByNameOrId("pixelmon:poke_ball"));
        returnButton.setStackDisplayName(Reference.resetText+"Return to the offer");
        customItems[17]=new CustomItem[]{new CustomItem(returnButton, null)};
        chestInventory.setInventorySlotContents(17, returnButton);

        for(int i=0;i<9;i++){
            ItemStack itemInfo=new ItemStack(Item.getByNameOrId("variedcommodities:diamond_dagger"));
            itemInfo.setStackDisplayName(Reference.resetText+"Click to remove $"+(int)Math.pow(10, 8)+" to the offer");
            itemInfo.setItemDamage(18);
            itemInfo.getTagCompound().setInteger("Unbreakable", 1);
            customItems[26-i]=new CustomItem[]{new CustomItem(itemInfo, null)};
            chestInventory.setInventorySlotContents(26-i, customItems[26-i][0].getItem());
        }

        CustomInventoryData data=new CustomInventoryData(3, "TradeOffer", "Your Offer", customItems, null);
        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString(data.getDisplayName()), data.getRows() * 9));
        player.openContainer = new OfferMoneyInventory(player.inventory, chestInventory, player);
        ((OfferMoneyInventory)player.openContainer).setData(data);
        ((OfferMoneyInventory)player.openContainer).setOffer(offer);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        InventoryPlayer inventoryplayer = player.inventory;
        Slot slot = getSlot(slotId);
        if(slot.isSameInventory(getSlot(0))) {
            if(slotId>=0&&slotId<=8){
                this.offer.addMoney((int)Math.pow(10, (8-slotId)));
                player.closeScreen();
                OfferMoneyInventory.openInventory(((EntityPlayerMP)player), this.offer);
            }else if(slotId>=18&&slotId<=26){
                this.offer.removeMoney((int)Math.pow(10, (8-(slotId-18))));
                player.closeScreen();
                OfferMoneyInventory.openInventory(((EntityPlayerMP)player), this.offer);
            }
            else if(slotId==17){
                player.closeScreen();
                OfferMakingInventory.openInventory(this.offer.player, this.offer);
            }
        }
        return null;
    }
}

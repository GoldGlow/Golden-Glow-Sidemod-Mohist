package com.goldenglow.common.inventory.BetterTrading;

import com.goldenglow.GoldenGlow;
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
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;

public class ConfirmOfferInventory extends CustomInventory {
    Trade trade;
    InventoryBasic inventoryBackup;

    public ConfirmOfferInventory(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public void setTrade(Trade trade){
        this.trade=trade;
    }

    public static void openInventory(EntityPlayerMP player, Trade trade){
        EntityPlayerMP otherPlayer=null;
        TradingOffer playerOffer=null;
        TradingOffer otherOffer=null;
        if(trade.getPlayers()[0].getName().equals(player.getName())){
            playerOffer=trade.getOffers()[0];
            otherPlayer=trade.getPlayers()[1];
            otherOffer=trade.getOffers()[1];
        }
        else{
            playerOffer=trade.getOffers()[1];
            otherPlayer=trade.getPlayers()[0];
            otherOffer=trade.getOffers()[0];
        }
        InventoryBasic offerInventory=new InventoryBasic("Offers", true, 54);

        CustomItem[][] customItems=new CustomItem[54][];

        ItemStack otherOfferItem=new ItemStack(Item.getByNameOrId("pixelmon:poke_ball"));
        otherOfferItem.setStackDisplayName(otherPlayer.getName()+"'s offer");
        customItems[0]=new CustomItem[]{new CustomItem(otherOfferItem, null)};
        offerInventory.setInventorySlotContents(0, otherOfferItem);

        for(int i=0;i<otherOffer.pokemonList.size();i++){
            CustomItem pokemon=CustomItem.getPokemonItem(otherOffer.pokemonList.get(i));
            customItems[i+2]=new CustomItem[]{pokemon};
            offerInventory.setInventorySlotContents(i+2, pokemon.getItem());
        }

        for(int i=0;i<otherOffer.items.size();i++){
            ItemStack itemStack=otherOffer.items.get(i);
            customItems[9+i]=new CustomItem[]{new CustomItem(itemStack, null)};
            offerInventory.setInventorySlotContents(9+i, itemStack);
        }

        ItemStack otherMoneyInfo=new ItemStack(Item.getByNameOrId("variedcommodities:coin_gold"));
        otherMoneyInfo.setStackDisplayName(Reference.resetText+"Money offered: "+otherOffer.money);
        customItems[18]=new CustomItem[]{new CustomItem(otherMoneyInfo, null)};
        offerInventory.setInventorySlotContents(18, otherMoneyInfo);

        ItemStack yourOfferItem=new ItemStack(Item.getByNameOrId("pixelmon:poke_ball"));
        yourOfferItem.setStackDisplayName("your offer");
        customItems[27]=new CustomItem[]{new CustomItem(yourOfferItem, null)};
        offerInventory.setInventorySlotContents(27, yourOfferItem);

        for(int i=0;i<playerOffer.pokemonList.size();i++){
            CustomItem pokemon=CustomItem.getPokemonItem(playerOffer.pokemonList.get(i));
            customItems[29+i]=new CustomItem[]{pokemon};
            offerInventory.setInventorySlotContents(29+i, pokemon.getItem());
        }

        for(int i=0;i<playerOffer.items.size();i++){
            ItemStack itemStack=playerOffer.items.get(i);
            customItems[36+i]=new CustomItem[]{new CustomItem(itemStack, null)};
            offerInventory.setInventorySlotContents(36+i, itemStack);
        }

        ItemStack yourMoneyInfo=new ItemStack(Item.getByNameOrId("variedcommodities:coin_gold"));
        yourMoneyInfo.setStackDisplayName(Reference.resetText+"Money offered: "+playerOffer.money);
        customItems[45]=new CustomItem[]{new CustomItem(yourMoneyInfo, null)};
        offerInventory.setInventorySlotContents(45, yourMoneyInfo);

        ItemStack decline = new ItemStack(Item.getByNameOrId("minecraft:barrier"));
        decline.setStackDisplayName(Reference.resetText + "Decline Trade");
        CustomItem declineItem = new CustomItem(decline, null);
        customItems[52] = new CustomItem[]{declineItem};
        offerInventory.setInventorySlotContents(52, decline);

        ItemStack accept = new ItemStack(Item.getByNameOrId("pixelmon:green_clock"));
        accept.setStackDisplayName(Reference.resetText + "Accept offer");
        CustomItem acceptItem = new CustomItem(accept, null);
        customItems[53] = new CustomItem[]{acceptItem};
        offerInventory.setInventorySlotContents(53, accept);

        CustomInventoryData data=new CustomInventoryData(6, "TradeOffer", otherPlayer.getName()+"'s offer", customItems, null);
        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString(data.getDisplayName()), data.getRows() * 9));
        player.openContainer = new ConfirmOfferInventory(player.inventory, offerInventory, player);
        ((ConfirmOfferInventory)player.openContainer).setData(data);
        ((ConfirmOfferInventory)player.openContainer).setTrade(trade);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        InventoryPlayer inventoryplayer = player.inventory;
        Slot slot = getSlot(slotId);
        if(slot.isSameInventory(getSlot(0))) {
            if(slotId==52){
                Trade trade= GoldenGlow.tradeManager.getPlayerTrade((EntityPlayerMP) player);
                trade.getPlayers()[0].sendMessage(new TextComponentString("One of the players cancelled the trade."));
                trade.getPlayers()[1].sendMessage(new TextComponentString("One of the players cancelled the trade."));
                GoldenGlow.tradeManager.cancelTrade((EntityPlayerMP)player);
            }
            else if(slotId==53){
                if(this.trade.getPlayers()[0].getName().equals(player.getName())){
                    this.trade.getOffers()[0].ready=true;
                    if(this.trade.getPlayers()[0].getName().equals(this.trade.getPlayers()[1].getName())){
                        this.trade.getOffers()[1].ready=true;
                    }
                }
                else{
                    this.trade.getOffers()[1].ready=true;
                }
                if(trade.getOffers()[0].ready&&trade.getOffers()[1].ready){
                    this.trade.complete();
                }
            }
        }
        return null;
    }
}

package com.goldenglow.common.inventory.BetterTrading;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.inventory.Action;
import com.goldenglow.common.inventory.CustomInventory;
import com.goldenglow.common.inventory.CustomInventoryData;
import com.goldenglow.common.inventory.CustomItem;
import com.goldenglow.common.util.Reference;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
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

import java.util.List;

public class OfferMakingInventory extends CustomInventory {
    TradingOffer offer;

    public OfferMakingInventory(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public void setOffer(TradingOffer offer){
        this.offer=offer;
    }

    public static void openInventory(EntityPlayerMP player, TradingOffer offer){
        if(!offer.ready) {
            InventoryBasic chestInventory = new InventoryBasic("Your offer", true, 36);
            CustomItem[][] customItems = new CustomItem[36][];
            //offer
            List<Pokemon> tradedPokemon = offer.pokemonList;
            for (int i = 0; i < tradedPokemon.size(); i++) {
                customItems[i] = new CustomItem[]{CustomItem.getPokemonItem(tradedPokemon.get(i))};
                chestInventory.setInventorySlotContents(i, customItems[i][0].getItem());
            }
            List<ItemStack> tradedItems = offer.items;
            for (int i = 0; i < tradedItems.size(); i++) {
                customItems[9 + i] = new CustomItem[]{new CustomItem(tradedItems.get(i), null)};
                chestInventory.setInventorySlotContents(9 + i, tradedItems.get(i));
            }
            ItemStack money = new ItemStack(Item.getByNameOrId("variedcommodities:coin_gold"));
            money.setStackDisplayName(Reference.resetText + "Money offered: " + offer.money);
            customItems[18] = new CustomItem[]{new CustomItem(money, null)};
            chestInventory.setInventorySlotContents(18, money);


            //action buttons

            //pokemon
            ItemStack addPokemon = new ItemStack(Item.getByNameOrId("pixelmon:poke_ball"));
            addPokemon.setStackDisplayName(Reference.resetText + "Add/Remove Pokemon");
            CustomItem addPokemonItem = new CustomItem(addPokemon, null);
            customItems[27] = new CustomItem[]{addPokemonItem};
            chestInventory.setInventorySlotContents(27, addPokemon);

            //items
            ItemStack addItems = new ItemStack(Item.getByNameOrId("variedcommodities:diamond_dagger"));
            addItems.setStackDisplayName(Reference.resetText + "Add/Remove items");
            addItems.setItemDamage(19);
            addItems.getTagCompound().setInteger("Unbreakable", 1);
            CustomItem addItemsItem = new CustomItem(addItems, null);
            customItems[28] = new CustomItem[]{addItemsItem};
            chestInventory.setInventorySlotContents(28, addItems);

            //money
            ItemStack addMoney = new ItemStack(Item.getByNameOrId("variedcommodities:coin_gold"));
            addMoney.setStackDisplayName(Reference.resetText + "Add/Remove Money");
            CustomItem addMoneyItem = new CustomItem(addMoney, null);
            customItems[29] = new CustomItem[]{addMoneyItem};
            chestInventory.setInventorySlotContents(29, addMoney);

            //cancel trade
            ItemStack decline = new ItemStack(Item.getByNameOrId("minecraft:barrier"));
            decline.setStackDisplayName(Reference.resetText + "Cancel Trade");
            CustomItem declineItem = new CustomItem(decline, null);
            customItems[34] = new CustomItem[]{declineItem};
            chestInventory.setInventorySlotContents(34, decline);

            //lock offer
            ItemStack lock = new ItemStack(Item.getByNameOrId("pixelmon:green_clock"));
            lock.setStackDisplayName(Reference.resetText + "Lock offer");
            CustomItem lockItem = new CustomItem(lock, null);
            customItems[35] = new CustomItem[]{lockItem};
            chestInventory.setInventorySlotContents(35, lock);

            CustomInventoryData data = new CustomInventoryData(4, "TradeOffer", "Your Offer", customItems, null);
            player.getNextWindowId();
            player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString(data.getDisplayName()), data.getRows() * 9));
            player.openContainer = new OfferMakingInventory(player.inventory, chestInventory, player);
            ((OfferMakingInventory) player.openContainer).setData(data);
            ((OfferMakingInventory) player.openContainer).setOffer(offer);
            player.openContainer.windowId = player.currentWindowId;
            player.openContainer.addListener(player);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
        }
        else if(GoldenGlow.tradeManager.getPlayerTrade(player).step== Trade.EnumTradeStep.OFFER_CONFIRMATION){
            ConfirmOfferInventory.openInventory(player, GoldenGlow.tradeManager.getPlayerTrade(player));
        }
        else {
            player.sendMessage(new TextComponentString("Please wait until the other person is ready! Do not leave the Pokemon Center until then!"));
        }
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        InventoryPlayer inventoryplayer = player.inventory;
        Slot slot = getSlot(slotId);
        if(slot.isSameInventory(getSlot(0))) {
            if(slotId==27){
                player.closeScreen();
                OfferPokemonInventory.openInventory((EntityPlayerMP) player, this.offer);
            }
            else if(slotId==28){
                player.closeScreen();
                OfferItemInventory.openInventory((EntityPlayerMP) player, this.offer);
            }
            else if(slotId==29){
                player.closeScreen();
                OfferMoneyInventory.openInventory((EntityPlayerMP) player, this.offer);
            }
            else if(slotId==34){
                Trade trade= GoldenGlow.tradeManager.getPlayerTrade((EntityPlayerMP) player);
                trade.getPlayers()[0].sendMessage(new TextComponentString("One of the players cancelled the trade."));
                trade.getPlayers()[1].sendMessage(new TextComponentString("One of the players cancelled the trade."));
                GoldenGlow.tradeManager.cancelTrade((EntityPlayerMP) player);
            }
            else if(slotId==35){
                this.offer.ready=true;
                player.closeScreen();
                Trade trade=GoldenGlow.tradeManager.getPlayerTrade((EntityPlayerMP) player);
                if(trade.getPlayers()[0].getName().equals(trade.getPlayers()[1].getName())){
                    trade.getOffers()[0].ready=true;
                    trade.getOffers()[1].ready=true;
                }
                if(trade.getOffers()[0].ready&&trade.getOffers()[1].ready){
                    trade.step= Trade.EnumTradeStep.OFFER_CONFIRMATION;
                    trade.getOffers()[0].ready=false;
                    trade.getOffers()[1].ready=false;
                    ConfirmOfferInventory.openInventory(trade.getPlayers()[1], trade);
                    ConfirmOfferInventory.openInventory(trade.getPlayers()[0], trade);
                }
                else {
                    player.sendMessage(new TextComponentString("Your offer has been locked. Please don't leave the area until the other player is done."));
                }
            }
        }
        return null;
    }
}

package com.goldenglow.common.util;

import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.util.scripting.OtherFunctions;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.comm.ISyncHandler;
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccount;
import com.pixelmonmod.pixelmon.api.events.ShopkeeperEvent;
import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.ShopKeeperPacket;
import com.pixelmonmod.pixelmon.config.PixelmonItemsPokeballs;
import com.pixelmonmod.pixelmon.entities.npcs.EntityNPC;
import com.pixelmonmod.pixelmon.entities.npcs.NPCShopkeeper;
import com.pixelmonmod.pixelmon.entities.npcs.registry.EnumBuySell;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItemWithVariation;
import com.pixelmonmod.pixelmon.enums.items.EnumPokeballs;
import com.pixelmonmod.pixelmon.items.ItemPokeball;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import noppes.npcs.api.wrapper.PlayerWrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class ShopPacketHandler implements ISyncHandler<ShopKeeperPacket> {
    public ShopPacketHandler() {
    }

    public void onSyncMessage(ShopKeeperPacket message, MessageContext ctx) {
        try {
            EntityPlayerMP p = ctx.getServerHandler().player;
            IPlayerData playerData=p.getCapability(OOPlayerProvider.OO_DATA, null);
            int amount = ReflectionHelper.getPrivateValue(message, "amount");
            int shopkeeperID = ReflectionHelper.getPrivateValue(message, "shopKeeperID");
            Optional<NPCShopkeeper> npcOptional = EntityNPC.locateNPCServer(p.world, shopkeeperID, NPCShopkeeper.class);
            if ((shopkeeperID == 999 || shopkeeperID==998 || npcOptional.isPresent()) && amount > 0) {
                Optional<? extends IPixelmonBankAccount> acc = Pixelmon.moneyManager.getBankAccount(p);
                if (acc.isPresent()) {
                    IPixelmonBankAccount account = acc.get();
                    String itemID = ReflectionHelper.getPrivateValue(message, "itemID");
                    ArrayList itemList;
                    Iterator var8;
                    ShopItemWithVariation s;
                    ItemStack sStack;
                    int initialAmount;
                    int cost;

                    Field privateField = message.getClass().getDeclaredField("buySell");
                    privateField.setAccessible(true);
                    EnumBuySell buySell = (EnumBuySell) privateField.get(message);
                    if (buySell == EnumBuySell.Buy) {
                        if (shopkeeperID == 999)
                            itemList = OtherFunctions.getBuyList(new PlayerWrapper(p));
                        else if(shopkeeperID==998){
                            itemList=OtherFunctions.getBuyList(new PlayerWrapper(p), playerData.getShopName());
                        }
                        else
                            itemList = npcOptional.get().getItemList();
                        var8 = itemList.iterator();

                        while (var8.hasNext()) {
                            s = (ShopItemWithVariation) var8.next();
                            if (s.getBaseShopItem().id.equals(itemID) && account.getMoney() >= s.getBuyCost() * amount) {
                                ItemStack item = s.getItem();
                                sStack = item.copy();
                                initialAmount = amount;
                                sStack.setCount(amount);
                                if (Pixelmon.EVENT_BUS.post(new ShopkeeperEvent.Purchase(p, sStack, EnumBuySell.Buy))) {
                                    return;
                                }

                                if (p.addItemStackToInventory(sStack)) {
                                    Item buyItem = sStack.getItem();
                                    if (buyItem instanceof ItemPokeball && ((ItemPokeball) buyItem).type == EnumPokeballs.PokeBall && amount >= 10) {
                                        ItemStack premierBall = new ItemStack(PixelmonItemsPokeballs.premierBall, 1);
                                        p.addItemStackToInventory(premierBall);
                                    }

                                    account.changeMoney(-s.getBuyCost() * amount);
                                    this.updateTransaction(p, npcOptional);
                                    return;
                                }

                                if (initialAmount > sStack.getCount()) {
                                    cost = initialAmount - sStack.getCount();
                                    account.changeMoney(-s.getBuyCost() * cost);
                                    this.updateTransaction(p, npcOptional);
                                    return;
                                }
                            }
                        }
                    } else {
                        if (shopkeeperID == 999)
                            itemList = OtherFunctions.getSellList(new PlayerWrapper(p));
                        else if(shopkeeperID==998){
                            itemList=OtherFunctions.getSellList(new PlayerWrapper(p), playerData.getShopName());
                        }
                        else
                            itemList = npcOptional.get().getSellList(p);
                        var8 = itemList.iterator();

                        while (true) {
                            int count;
                            do {
                                do {
                                    if (!var8.hasNext()) {
                                        return;
                                    }

                                    s = (ShopItemWithVariation) var8.next();
                                } while (!s.getBaseShopItem().id.equals(itemID));

                                count = 0;
                                sStack = s.getItem();

                                for (initialAmount = 0; initialAmount < p.inventory.mainInventory.size(); ++initialAmount) {
                                    ItemStack item = (ItemStack) p.inventory.mainInventory.get(initialAmount);
                                    if (this.areItemsEqual(item, sStack)) {
                                        count += item.getCount();
                                    }
                                }

                                ItemStack copy = sStack.copy();
                                copy.setCount(amount);
                                if(count>0) {
                                    if (Pixelmon.EVENT_BUS.post(new ShopkeeperEvent.Sell(p, EnumBuySell.Sell, copy))) {
                                        return;
                                    }
                                }
                                else {
                                    itemList.remove(s);
                                }
                            } while (count < amount);

                            cost = s.getSellCost();
                            count = amount;

                            for (int i = 0; i < p.inventory.mainInventory.size(); ++i) {
                                ItemStack item = (ItemStack) p.inventory.mainInventory.get(i);
                                if (this.areItemsEqual(item, sStack)) {
                                    if (item.getCount() >= count) {
                                        item.setCount(item.getCount() - count);
                                        count = 0;
                                    } else {
                                        count -= item.getCount();
                                        item.setCount(0);
                                    }

                                    if (item.getCount() == 0) {
                                        p.inventory.mainInventory.set(i, ItemStack.EMPTY);
                                    }
                                }

                                if (count <= 0) {
                                    break;
                                }
                            }

                            account.changeMoney(cost * amount);
                            this.updateTransaction(p, npcOptional);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTransaction(EntityPlayerMP p, Optional<? extends NPCShopkeeper> npc) {
        p.inventoryContainer.detectAndSendChanges();
        if(npc.isPresent())
            npc.get().sendItemsToPlayer(p);
    }

    private boolean areItemsEqual(ItemStack item1, ItemStack item2) {
        return !item1.isEmpty() && ItemStack.areItemsEqual(item1, item2) && ItemStack.areItemStackTagsEqual(item1, item2) && item1.getItemDamage() == item2.getItemDamage();
    }
}

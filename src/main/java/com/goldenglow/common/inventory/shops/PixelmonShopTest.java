package com.goldenglow.common.inventory.shops;

import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.SetNPCData;
import com.pixelmonmod.pixelmon.comm.packetHandlers.npc.ShopKeeperPacket;
import com.pixelmonmod.pixelmon.entities.npcs.registry.BaseShopItem;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItem;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopItemWithVariation;
import com.pixelmonmod.pixelmon.entities.npcs.registry.ShopkeeperChat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class PixelmonShopTest extends SetNPCData {

    public PixelmonShopTest(String name, ShopkeeperChat chat, ArrayList<ShopItemWithVariation> buy, ArrayList<ShopItemWithVariation> sell){
        super("", new ShopkeeperChat("", ""), buy, sell);
    }

    public static SetNPCData init(){
        ArrayList<ShopItemWithVariation> items=new ArrayList<>();
        ItemStack item=new ItemStack(Item.getByNameOrId("pixelmon:potion"));
        BaseShopItem firstBaseItem=new BaseShopItem("potion", item, 200, 100);
        ShopItem firstShopItem=new ShopItem(firstBaseItem, 1.00f, 1.00f, false);
        ShopItemWithVariation firstShopItemWithVariation=new ShopItemWithVariation(firstShopItem, 1.00f);
        items.add(firstShopItemWithVariation);
        return new SetNPCData("", new ShopkeeperChat("", ""), items, items);
    }
}

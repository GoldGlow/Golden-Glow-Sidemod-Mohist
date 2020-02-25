package com.goldenglow.common.inventory.BetterTrading;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TradingOffer {
    EntityPlayerMP player;
    int money;
    List<Pokemon> pokemonList;
    List<ItemStack> items;
    List<ItemStack> inventoryBackup;
    public boolean ready;

    public TradingOffer(EntityPlayerMP player){
        this.player=player;
        this.money=0;
        this.pokemonList=new ArrayList<Pokemon>();
        this.items=new ArrayList<ItemStack>();
        this.ready=false;
        this.inventoryBackup=new ArrayList<ItemStack>();
    }

    public void addPokemon(int pokemonSlot){
        if(Pixelmon.storageManager.getParty(this.player).getTeam().size()>1) {
            this.pokemonList.add(Pixelmon.storageManager.getParty(this.player).get(pokemonSlot));
            Pixelmon.storageManager.getParty(this.player).set(pokemonSlot, null);
        }
    }

    public void removePokemon(int pokemonSlot){
        Pixelmon.storageManager.getParty(this.player).add(this.pokemonList.get(pokemonSlot));
        this.pokemonList.remove(pokemonSlot);
    }

    public void addMoney(int amount){
        if(Pixelmon.storageManager.getParty(this.player).getMoney()>=amount){
            Pixelmon.storageManager.getParty(this.player).changeMoney(-1*amount);
            this.money+=amount;
        }
    }

    public void removeMoney(int amount){
        if(Pixelmon.storageManager.getParty(this.player).getMoney()>=amount){
            Pixelmon.storageManager.getParty(this.player).changeMoney(-1*amount);
            this.money+=amount;
        }
    }

    public void addItem(ItemStack itemStack){
        for(ItemStack item:this.items){
            if(Item.getIdFromItem(item.getItem())==Item.getIdFromItem(itemStack.getItem())&&item.getCount()<item.getMaxStackSize()){
                if(itemStack.getCount()<item.getMaxStackSize()-item.getCount()){
                    item.setCount(item.getCount()+itemStack.getCount());
                    return;
                }
                else if(this.items.size()<9){
                    itemStack.setCount(itemStack.getCount()-(item.getMaxStackSize()-item.getCount()));
                    item.setCount(item.getMaxStackSize());
                    this.items.add(itemStack);
                    return;
                }
            }
        }
        if(this.items.size()<9){
            this.items.add(itemStack);
        }
    }

    public void addItem(int itemStackSlot){
        ItemStack itemStack=this.player.inventory.getStackInSlot(itemStackSlot);
        for(ItemStack item:this.items){
            if(Item.getIdFromItem(item.getItem())==Item.getIdFromItem(itemStack.getItem())&&item.getCount()<item.getMaxStackSize()){
                if(itemStack.getCount()<item.getMaxStackSize()-item.getCount()){
                    item.setCount(item.getCount()+itemStack.getCount());
                    this.player.inventory.removeStackFromSlot(itemStackSlot);
                    return;
                }
                else if(this.items.size()<9){
                    itemStack.setCount(itemStack.getCount()-(item.getMaxStackSize()-item.getCount()));
                    item.setCount(item.getMaxStackSize());
                    this.items.add(itemStack);
                    this.player.inventory.removeStackFromSlot(itemStackSlot);
                    return;
                }
            }
        }
        if(this.items.size()<9){
            this.items.add(itemStack);
            this.player.inventory.removeStackFromSlot(itemStackSlot);
        }
    }
}

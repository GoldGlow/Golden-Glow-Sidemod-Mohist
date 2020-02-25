package com.goldenglow.common.util;

import com.google.gson.stream.JsonWriter;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InventoryUtil {

    public static boolean doesPlayerHaveSpace(EntityPlayerMP player, List<ItemStack> items){
        List<ItemStack> comparisonList=new ArrayList<ItemStack>();
        for(ItemStack stack:items){
            comparisonList.add(stack.copy());
        }
        ArrayList<Integer> removedItems=new ArrayList<Integer>();
        int[] spacesLeft=new int[comparisonList.size()-1];
        for(int i=0;i<spacesLeft.length;i++){
            spacesLeft[i]=0;
        }
        for(int i=0;i<comparisonList.size();i++){
            int sharedIndex=doesItemShareId(comparisonList.get(i), comparisonList);
            if(sharedIndex!=1&&sharedIndex<i){
                if(spacesLeft[sharedIndex]>=comparisonList.get(i).getCount()){
                    spacesLeft[sharedIndex]-=comparisonList.get(i).getCount();
                    removedItems.add(i);
                }
            }
            else{
                ItemStack item = comparisonList.get(i);
                Iterator<ItemStack> iterator = player.inventory.mainInventory.iterator();
                int spaces = 0;
                while (iterator.hasNext()) {
                    ItemStack itemStack = iterator.next();
                    if (Item.getIdFromItem(itemStack.getItem()) == Item.getIdFromItem(item.getItem()) && itemStack.getCount() < itemStack.getMaxStackSize()) {
                        spaces += itemStack.getMaxStackSize()-itemStack.getCount();
                    }
                }
                spacesLeft[i]=spaces;
                if(spaces>=item.getCount()){
                    spacesLeft[i]-=item.getCount();
                    removedItems.add(i);
                }
            }
        }
        for(int i=removedItems.size()-1;i>=0;i--){
            comparisonList.remove(removedItems.get(i));
        }
        int freeSpace=0;
        Iterator<ItemStack> iterator = player.inventory.mainInventory.iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (item.isEmpty())
                freeSpace++;
        }
        if(freeSpace>=comparisonList.size()){
            return true;
        }
        return false;
    }

    public static int doesItemShareId(ItemStack item, List<ItemStack> items){
        for(int i=0;i<items.size();i++){
            if(Item.getIdFromItem(item.getItem())==Item.getIdFromItem(items.get(i).getItem())){
                return i;
            }
        }
        return -1;
    }
}

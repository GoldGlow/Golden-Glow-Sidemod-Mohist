package com.goldenglow.common.gyms;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.inventory.Action;
import com.goldenglow.common.inventory.CustomInventory;
import com.goldenglow.common.inventory.CustomInventoryData;
import com.goldenglow.common.inventory.CustomItem;
import com.goldenglow.common.util.Reference;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;

import java.util.ArrayList;
import java.util.List;

public class GymInventories extends CustomInventory {
    Gym gym;

    public GymInventories(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public void setGym(Gym gym){
        this.gym=gym;
    }

    public static void openInventory(String gymName, EntityPlayerMP player){
        openInventory(GoldenGlow.gymManager.getGym(gymName), player);
    }

    public static void openInventory(Gym gym, EntityPlayerMP player){
        List<CustomItem> items=new ArrayList<CustomItem>();
        if(gym.open){
            ItemStack closeGym=new ItemStack(Item.getByNameOrId("minecraft:barrier"));
            closeGym.setStackDisplayName("Close gym");
            CustomItem item=new CustomItem(closeGym, null);
            item.setBothClickActions(new Action[]{new Action(Action.ActionType.CLOSE_GYM, gym.name)});
            items.add(item);
            if(gym.currentLeader==null&&gym.queue.size()>0){
                ItemStack takeChallengers=new ItemStack(Item.getByNameOrId("pixelmon:poke_ball"));
                takeChallengers.setStackDisplayName("Take on challengers");
                CustomItem takeOnChallengers=new CustomItem(takeChallengers, null);
                takeOnChallengers.setBothClickActions(new Action[]{new Action(Action.ActionType.TAKE_CHALLENGERS, gym.name)});
                items.add(takeOnChallengers);
            }else if(gym.currentLeader.getName().equals(player.getName())){
                ItemStack takeChallengers=new ItemStack(Item.getByNameOrId("pixelmon:hive_badge"));
                takeChallengers.setStackDisplayName("Stop taking challengers");
                CustomItem takeOnChallengers=new CustomItem(takeChallengers, null);
                takeOnChallengers.setBothClickActions(new Action[]{new Action(Action.ActionType.STOP_CHALLENGERS, gym.name)});
                items.add(takeOnChallengers);

                ItemStack nextChallenger=new ItemStack(Item.getByNameOrId("variedcommodities:diamond_dagger"));
                nextChallenger.setStackDisplayName(Reference.resetText +"Next Challenger");
                nextChallenger.setItemDamage(18);
                nextChallenger.setTagInfo("Unbreakable", new NBTTagInt(1));
                CustomItem nextChallengerCustom=new CustomItem(nextChallenger, null);
                nextChallengerCustom.setBothClickActions(new Action[]{new Action(Action.ActionType.NEXT_CHALLENGER, gym.name)});
                items.add(nextChallengerCustom);

                ItemStack startBattle=new ItemStack(Item.getByNameOrId("minecraft:diamond_sword"));
                startBattle.setStackDisplayName(Reference.resetText+"Start the battle");
                CustomItem startBattleCustom= new CustomItem(startBattle, null);
                startBattleCustom.setBothClickActions(new Action[]{new Action(Action.ActionType.START_BATTLE, gym.name)});
                items.add(startBattleCustom);
            }
            ItemStack viewQueue=new ItemStack(Item.getByNameOrId("minecraft:skull"));
            viewQueue.setItemDamage(3);
            viewQueue.setStackDisplayName(Reference.resetText+"View queue");
            CustomItem viewQueueCustom=new CustomItem(viewQueue, null);
            viewQueueCustom.setBothClickActions(new Action[]{new Action(Action.ActionType.OPEN_INV, "QUEUE:"+gym.name)});
            items.add(viewQueueCustom);
        }
        else{
            ItemStack openGym=new ItemStack(Item.getByNameOrId("pixelmon:green_clock"));
            openGym.setStackDisplayName("Open gym");
            CustomItem item=new CustomItem(openGym, null);
            item.setBothClickActions(new Action[]{new Action(Action.ActionType.OPEN_GYM, gym.name)});
            items.add(item);
        }
        CustomItem[][] customItems=new CustomItem[9][];
        for(int i=0;i<items.size();i++){
            customItems[i]=new CustomItem[]{items.get(i)};
        }
        if(!gym.currentLeader.getName().equals(player.getName())){
            CustomItem returnButton=CustomItem.returnButton();
            returnButton.setBothClickActions(new Action[]{new Action(Action.ActionType.OPEN_INV, "GymTools")});
            customItems[8]=new CustomItem[]{returnButton};
        }
    }

    public static void openQueue(String gymName, EntityPlayerMP player){
        openQueue(GoldenGlow.gymManager.getGym(gymName), player);
    }

    public static void openQueue(Gym gym, EntityPlayerMP player){
        List<ItemStack> players=new ArrayList<ItemStack>();
        CustomItem[][] customItems=new CustomItem[(Math.max((gym.queue.size()-1)/9+1, 1))][];
        for(int i=0;i<gym.queue.size();i++){
            customItems[i]=new CustomItem[]{new CustomItem(CustomItem.getPlayerHead(gym.queue.get(i)), null)};
        }
        CustomInventoryData data=new CustomInventoryData(Math.max((players.size()-1)/9+1, 1), "Queue", "Queue", customItems, null);
        CustomInventory.openCustomInventory(player, data);
    }
}

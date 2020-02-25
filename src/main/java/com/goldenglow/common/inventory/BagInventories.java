package com.goldenglow.common.inventory;

import com.goldenglow.common.data.player.OOPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.Reference;
import com.goldenglow.common.util.Requirement;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.items.ItemTM;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

import java.util.ArrayList;
import java.util.List;

public class BagInventories {
    static void openKeyItems(EntityPlayerMP player){
        OOPlayerData playerData = (OOPlayerData)player.getCapability(OOPlayerProvider.OO_DATA, null);
        List<ItemStack> items=playerData.getKeyItems();
        int rows= Math.max((items.size()/9)+1, 1);
        CustomInventoryData data=new CustomInventoryData(rows, "KeyItems", "Key Items", new CustomItem[rows*9][], new Requirement[0]);
        InventoryBasic chestInventory=new InventoryBasic(data.getName(), true, data.getRows()*9);
        CustomItem returnButton=CustomItem.returnButton();
        returnButton.setLeftClickActions(new Action[]{new Action(Action.ActionType.OPEN_INV, "HelperBag")});
        returnButton.setRightClickActions(new Action[]{new Action(Action.ActionType.OPEN_INV, "HelperBag")});
        for(int i=0;i<items.size();i++){
            data.items[i]=new CustomItem[]{new CustomItem(items.get(i), null)};
            chestInventory.setInventorySlotContents(i, items.get(i));
        }
        data.items[(rows*9)-1]=new CustomItem[]{returnButton};
        chestInventory.setInventorySlotContents((rows*9)-1, returnButton.item);
        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString("Key Items"), rows*9));
        player.openContainer = new CustomInventory(player.inventory, chestInventory, player);
        ((CustomInventory)player.openContainer).setData(data);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
    }

    static void openTMCase(EntityPlayerMP player){
        OOPlayerData playerData = (OOPlayerData)player.getCapability(OOPlayerProvider.OO_DATA, null);
        List<ItemStack> items=playerData.getTMs();
        int rows= Math.max((items.size()/9)+1, 1);
        CustomInventoryData data=new CustomInventoryData(rows, "TMCase", "TM Case", new CustomItem[rows*9][], new Requirement[0]);
        InventoryBasic chestInventory=new InventoryBasic(data.getName(), true, data.getRows()*9);
        CustomItem returnButton=CustomItem.returnButton();
        returnButton.setLeftClickActions(new Action[]{new Action(Action.ActionType.OPEN_INV, "HelperBag")});
        returnButton.setRightClickActions(new Action[]{new Action(Action.ActionType.OPEN_INV, "HelperBag")});
        for(int i=0;i<items.size();i++){
            GGLogger.info("Adding item: "+items.get(i).serializeNBT());
            data.items[i]=new CustomItem[]{new CustomItem(items.get(i), null)};
            Action openTM=new Action(Action.ActionType.TM_PARTY, ((ItemTM)items.get(i).getItem()).attackName);
            data.items[i][0].setLeftClickActions(new Action[]{openTM});
            data.items[i][0].setRightClickActions(new Action[]{openTM});
            chestInventory.setInventorySlotContents(i, items.get(i));
        }
        data.items[(rows*9)-1]=new CustomItem[]{returnButton};
        chestInventory.setInventorySlotContents((rows*9)-1, returnButton.item);
        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString("TM Case"), rows*9));
        player.openContainer = new CustomInventory(player.inventory, chestInventory, player);
        ((CustomInventory)player.openContainer).setData(data);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
    }

    static void openAWItems(EntityPlayerMP player){
        OOPlayerData playerData = (OOPlayerData)player.getCapability(OOPlayerProvider.OO_DATA, null);
        List<ItemStack> items=playerData.getAWItems();
        int rows= Math.max((items.size()/9)+1, 1);
        CustomInventoryData data=new CustomInventoryData(rows, "Clothes", "Clothes", new CustomItem[rows*9][], new Requirement[0]);
        InventoryBasic chestInventory=new InventoryBasic(data.getName(), true, data.getRows()*9);
        CustomItem returnButton=CustomItem.returnButton();
        returnButton.setBothClickActions(new Action[]{new Action(Action.ActionType.OPEN_INV, "HelperBag")});
        for(int i=0;i<items.size();i++){
            data.items[i]=new CustomItem[]{new CustomItem(items.get(i), null)};
            Action openTM=new Action(Action.ActionType.EQUIP_ARMOR, 3+"@"+items.get(i).serializeNBT());
            data.items[i][0].setBothClickActions(new Action[]{openTM});
            chestInventory.setInventorySlotContents(i, items.get(i));
        }
        data.items[(rows*9)-1]=new CustomItem[]{returnButton};
        chestInventory.setInventorySlotContents((rows*9)-1, returnButton.item);
        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString("TM Case"), rows*9));
        player.openContainer = new CustomInventory(player.inventory, chestInventory, player);
        ((CustomInventory)player.openContainer).setData(data);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
    }

    static void openTMMenu(EntityPlayerMP player, String attackName){
        PlayerPartyStorage partyStorage = Pixelmon.storageManager.getParty(player);
        ItemStack[] party=CustomInventory.getPartyIcons(player);
        CustomInventoryData data=new CustomInventoryData(1, "TMCase", "TM "+attackName, new CustomItem[6][], new Requirement[0]);
        InventoryBasic chestInventory=new InventoryBasic("TM "+attackName, true, 9);
        for(int i=0;i<6;i++){
            if(partyStorage.get(i)!=null) {
                ArrayList<String> lore=new ArrayList<>();
                if(canLearnTM(partyStorage.get(i), attackName)){
                    if(partyStorage.get(i).getMoveset().hasAttack(new Attack(attackName))){
                        party[i].setStackDisplayName(Reference.red + "Already learned");
                        data.items[i] = new CustomItem[]{new CustomItem(party[i], null)};
                        Action teachMove = new Action(Action.ActionType.TEACH_MOVE, i + ":" + attackName);
                    }
                    else {
                        party[i].setStackDisplayName(Reference.green + "Can learn");
                        data.items[i] = new CustomItem[]{new CustomItem(party[i], null)};
                        Action teachMove = new Action(Action.ActionType.TEACH_MOVE, i + ":" + attackName);
                        data.items[i][0].setRightClickActions(new Action[]{teachMove});
                        data.items[i][0].setLeftClickActions(new Action[]{teachMove});
                    }
                }
                else {
                    party[i].setStackDisplayName(Reference.red+"Cannot learn");
                    data.items[i]=new CustomItem[]{new CustomItem(party[i], null)};
                }
                chestInventory.setInventorySlotContents(i, party[i]);
            }
        }
        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString("TM Case"), 9));
        player.openContainer = new CustomInventory(player.inventory, chestInventory, player);
        ((CustomInventory)player.openContainer).setData(data);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
    }

    static boolean canLearnTM(Pokemon pokemon, String moveName){
        List<Attack> tms= pokemon.getBaseStats().tmMoves;
        for(Attack tm:tms){
            if(tm.getActualMove().getAttackName().equals(moveName)){
                return true;
            }
        }
        return false;
    }
}

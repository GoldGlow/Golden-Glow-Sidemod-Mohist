package com.goldenglow.common.inventory.social;

import com.goldenglow.common.inventory.CustomInventory;
import com.goldenglow.common.inventory.CustomItem;
import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.PermissionUtils;
import com.goldenglow.common.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;

public class BadgeCase extends CustomInventory {
    public EntityPlayerMP targetPlayer;

    public BadgeCase(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public static void openInventory(EntityPlayerMP player, EntityPlayerMP targetPlayer){
        InventoryBasic chestInventory=new InventoryBasic(Reference.resetText+targetPlayer.getName()+"'s Badge Case", true, 9);
        ItemStack sakuraBadge=null;
        if(PermissionUtils.checkPermission(targetPlayer, "badge.sakura_gym.player")){
            sakuraBadge= CustomItem.getDiamondDagger(9);
            sakuraBadge.setStackDisplayName(Reference.resetText+"Magic Badge");
            ArrayList<String> sakuraLore=new ArrayList<String>();
            sakuraLore.add(Reference.resetText+"This badge is awarded for defeating a player gym");
            sakuraLore.add(Reference.resetText+"leader from Sakura City.");
            CustomItem.addLore(sakuraBadge, sakuraLore);
            sakuraBadge.setTagInfo("Unbreakable", new NBTTagInt(1));
        }
        else if(PermissionUtils.checkPermission(targetPlayer, "badge.sakura_gym.npc")){
            sakuraBadge= CustomItem.getDiamondDagger(9);
            sakuraBadge.setStackDisplayName(Reference.resetText+"Magic Badge");
            ArrayList<String> sakuraLore=new ArrayList<String>();
            sakuraLore.add(Reference.resetText+"This badge is awarded for defeating the Gym Leader Naoko.");
            CustomItem.addLore(sakuraBadge, sakuraLore);
            sakuraBadge.setTagInfo("Unbreakable", new NBTTagInt(1));
        }
        else{
            sakuraBadge= CustomItem.getDiamondDagger(1);
            sakuraBadge.setStackDisplayName(Reference.resetText+"? Badge");
            ArrayList<String> sakuraLore=new ArrayList<String>();
            sakuraLore.add(Reference.resetText+"Player gym level cap: 15");
            CustomItem.addLore(sakuraBadge, sakuraLore);
            sakuraBadge.setTagInfo("Unbreakable", new NBTTagInt(1));
        }
        chestInventory.setInventorySlotContents(0, sakuraBadge);
        ItemStack secondBadge=CustomItem.getDiamondDagger(2);
        secondBadge.setStackDisplayName(Reference.resetText+"? Badge");
        ArrayList<String> secondLore=new ArrayList<String>();
        secondLore.add(Reference.resetText+"Player gym level cap: 20");
        CustomItem.addLore(secondBadge, secondLore);
        secondBadge.setTagInfo("Unbreakable", new NBTTagInt(1));
        chestInventory.setInventorySlotContents(1, secondBadge);

        for(int i=2;i<8;i++){
            ItemStack remainingBadge=CustomItem.getDiamondDagger(i+1);
            remainingBadge.setStackDisplayName(Reference.resetText+"? Badge");
            chestInventory.setInventorySlotContents(i, remainingBadge);
        }

        ItemStack backButton=CustomItem.getDiamondDagger(18);
        backButton.setStackDisplayName(Reference.resetText+"Back");
        chestInventory.setInventorySlotContents(8, backButton);

        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString(Reference.resetText+targetPlayer.getName()+"'s Badge Case"), 9));
        player.openContainer = new BadgeCase(player.inventory, chestInventory, player);
        ((BadgeCase)player.openContainer).targetPlayer=targetPlayer;
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if(slotId==8){
            PlayerProfile.openInventory((EntityPlayerMP) player, this.targetPlayer);
        }
        return null;
    }
}

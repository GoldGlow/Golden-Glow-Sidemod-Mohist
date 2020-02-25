package com.goldenglow.common.inventory.social;

import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.inventory.CustomInventory;
import com.goldenglow.common.inventory.CustomItem;
import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.Reference;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class PlayerProfile extends CustomInventory {
    EntityPlayerMP targetPlayer;

    public PlayerProfile(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public static void openInventory(EntityPlayerMP viewer, EntityPlayerMP targetPlayer){
        InventoryBasic chestInventory=new InventoryBasic(Reference.resetText+targetPlayer.getName()+"'s profile", true, 9);
        IPlayerData otherPlayerData = targetPlayer.getCapability(OOPlayerProvider.OO_DATA, null);
        IPlayerData playerData = viewer.getCapability(OOPlayerProvider.OO_DATA, null);

        ItemStack playerHead=new ItemStack(Item.getByNameOrId("minecraft:skull"));
        playerHead.setItemDamage(3);
        NBTTagCompound headNBT=new NBTTagCompound();
        NBTTagCompound headDisplay=new NBTTagCompound();
        headDisplay.setString("name", Reference.resetText+Reference.bold +targetPlayer.getName());
        ArrayList<String> headLore=new ArrayList<String>();
        headLore.add(Reference.resetText+"Pokedex: "+ Pixelmon.storageManager.getParty(targetPlayer.getUniqueID()).pokedex.countCaught());
        long sessionTime = Math.abs(Duration.between(Instant.now(), otherPlayerData.getLoginTime()).getSeconds());
        long totalTime = targetPlayer.getEntityData().getLong("playtime") + sessionTime;
        headLore.add(Reference.resetText+"Play Time: "+String.format("%sh:%sm", totalTime / 3600, (totalTime % 3600) / 60));
        CustomItem.addLore(playerHead, headLore);
        chestInventory.setInventorySlotContents(0, playerHead);

        ItemStack badgeCase=CustomItem.getDiamondDagger(28);
        badgeCase.setStackDisplayName(Reference.resetText+"Badge Case");
        ArrayList<String> caseLore=new ArrayList<String>();
        caseLore.add(Reference.resetText+"Check out "+targetPlayer.getName()+"'s badge case.");
        NBTTagCompound badgeNBT=new NBTTagCompound();
        CustomItem.addLore(badgeCase, caseLore);
        badgeCase.setTagInfo("Unbreakable", new NBTTagInt(1));
        chestInventory.setInventorySlotContents(1, badgeCase);

        if(!otherPlayerData.getFriendList().contains(viewer.getUniqueID())){
            if(playerData.getFriendRequests().contains(targetPlayer.getUniqueID())){
                ItemStack accept= CustomItem.getDiamondDagger(40);
                accept.setStackDisplayName(Reference.resetText+Reference.green+"Accept Friend request");
                chestInventory.setInventorySlotContents(2, accept);

                ItemStack deny=CustomItem.getDiamondDagger(41);
                deny.setStackDisplayName(Reference.resetText+Reference.darkRed+"Deny the Friend request");
                chestInventory.setInventorySlotContents(3, deny);
            }
            else if(!(playerData.getFriendRequests().contains(targetPlayer.getUniqueID())||otherPlayerData.getFriendRequests().contains(viewer.getUniqueID()))){
                ItemStack accept=CustomItem.getDiamondDagger(40);
                accept.setStackDisplayName(Reference.resetText+Reference.green+"Send Friend request");
                chestInventory.setInventorySlotContents(2, accept);
            }
        }
        else {
            ItemStack deny=CustomItem.getDiamondDagger(41);
            deny.setStackDisplayName(Reference.resetText+Reference.darkRed+"Remove Friend");
            chestInventory.setInventorySlotContents(2, deny);
        }

        viewer.getNextWindowId();
        viewer.connection.sendPacket(new SPacketOpenWindow(viewer.currentWindowId, "minecraft:container", new TextComponentString(Reference.resetText+targetPlayer.getName()+"'s profile"), 9));
        viewer.openContainer = new PlayerProfile(viewer.inventory, chestInventory, viewer);
        ((PlayerProfile)viewer.openContainer).targetPlayer=targetPlayer;
        viewer.openContainer.windowId = viewer.currentWindowId;
        viewer.openContainer.addListener(viewer);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(viewer, viewer.openContainer));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        InventoryPlayer inventoryplayer = player.inventory;
        Slot slot = getSlot(slotId);
        IPlayerData playerData=player.getCapability(OOPlayerProvider.OO_DATA, null);
        IPlayerData otherPlayerData=this.targetPlayer.getCapability(OOPlayerProvider.OO_DATA, null);

        if(slot.isSameInventory(getSlot(0))&&slot.getStack().getCount()!=0) {
            if(slotId==1){
                BadgeCase.openInventory((EntityPlayerMP) player, this.targetPlayer);
                return null;
            }
            else if(slotId==2){
                if (slot.getStack().getDisplayName().contains("Accept")) {
                    otherPlayerData.addFriend(player.getUniqueID());
                    playerData.acceptFriendRequest(this.targetPlayer.getUniqueID());
                    targetPlayer.sendMessage(new TextComponentString(Reference.darkGreen+player.getName()+" accepted your friend request!"));
                    player.sendMessage(new TextComponentString(Reference.darkGreen+"You are now friends with "+targetPlayer.getName()+"!"));
                } else if (slot.getStack().getDisplayName().contains("Send")) {
                    targetPlayer.sendMessage(new TextComponentString(Reference.darkGreen+player.getName()+" sent a friend request! Accept it by checking his social or by checking the social menu on the phone."));
                    otherPlayerData.addFriendRequest(player.getUniqueID());;
                } else if (slot.getStack().getDisplayName().contains("Remove")) {
                    playerData.removeFriend(this.targetPlayer.getUniqueID());
                    otherPlayerData.removeFriend(player.getUniqueID());
                }
            }
            else if(slotId==3){
                playerData.denyFriendRequest(this.targetPlayer.getUniqueID());
            }
        }
        openInventory((EntityPlayerMP) player, this.targetPlayer);
        return null;
    }
}

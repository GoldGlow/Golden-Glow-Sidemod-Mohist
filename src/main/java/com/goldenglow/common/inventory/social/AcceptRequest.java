package com.goldenglow.common.inventory.social;

import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.inventory.CustomInventory;
import com.goldenglow.common.inventory.CustomItem;
import com.goldenglow.common.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;

public class AcceptRequest extends CustomInventory {
    EntityPlayerMP targetPlayer;

    public AcceptRequest(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public static void openInventory(EntityPlayerMP viewer, EntityPlayerMP targetPlayer){
        InventoryBasic chestInventory=new InventoryBasic(Reference.resetText+targetPlayer.getName()+"'s request", true, 9);
        ItemStack accept= CustomItem.getDiamondDagger(40);
        accept.setStackDisplayName(Reference.resetText+Reference.green+"Accept the Friend request");
        ItemStack deny=CustomItem.getDiamondDagger(41);
        deny.setStackDisplayName(Reference.resetText+Reference.darkRed+"Deny the Friend request");
        chestInventory.setInventorySlotContents(3, accept);
        chestInventory.setInventorySlotContents(5, deny);

        viewer.getNextWindowId();
        viewer.connection.sendPacket(new SPacketOpenWindow(viewer.currentWindowId, "minecraft:container", new TextComponentString(Reference.resetText+targetPlayer.getName()+"'s request"), 9));
        viewer.openContainer = new AcceptRequest(viewer.inventory, chestInventory, viewer);
        ((AcceptRequest)viewer.openContainer).targetPlayer=targetPlayer;
        viewer.openContainer.windowId = viewer.currentWindowId;
        viewer.openContainer.addListener(viewer);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(viewer, viewer.openContainer));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        IPlayerData playerData=player.getCapability(OOPlayerProvider.OO_DATA, null);
        IPlayerData otherPlayerData=this.targetPlayer.getCapability(OOPlayerProvider.OO_DATA, null);
        if(slotId==3){
            otherPlayerData.addFriend(player.getUniqueID());
            playerData.acceptFriendRequest(this.targetPlayer.getUniqueID());
            targetPlayer.sendMessage(new TextComponentString(Reference.darkGreen+player.getName()+" accepted your friend request!"));
            player.sendMessage(new TextComponentString(Reference.darkGreen+"You are now friends with "+targetPlayer.getName()+"!"));
            player.closeScreen();
        }
        else if(slotId==5){
            playerData.denyFriendRequest(this.targetPlayer.getUniqueID());
            player.closeScreen();
        }
        return null;
    }
}

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
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public class FriendRequests extends CustomInventory {
    public FriendRequests(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public static void openInventory(EntityPlayerMP player){
        IPlayerData playerData = player.getCapability(OOPlayerProvider.OO_DATA, null);
        InventoryBasic chestInventory=new InventoryBasic(Reference.resetText+"Friend requests", true, Math.max(9, (((playerData.getFriendRequests().size()-1)/9)+1)*9));
        int index=0;
        for(UUID request: playerData.getFriendRequests()){
            if(UsernameCache.containsUUID(request)&&FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(request)!=null){
                chestInventory.setInventorySlotContents(index, CustomItem.getPlayerHead(UsernameCache.getLastKnownUsername(request)));
                index++;
            }
            else {
                playerData.getFriendRequests().remove(request);
            }
        }
        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString("Friend Requests"), Math.max(9, (((playerData.getFriendRequests().size()-1)/9)+1)*9)));
        player.openContainer = new FriendRequests(player.inventory, chestInventory, player);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        Slot slot = getSlot(slotId);
        if(slot.isSameInventory(getSlot(0))&&slot.getStack().getCount()!=0) {
            ItemStack head=slot.getStack();
            NBTTagCompound tag=head.getTagCompound();
            String name=tag.getString("SkullOwner");
            EntityPlayerMP otherPlayer=FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(name);
            if(otherPlayer!=null)
                AcceptRequest.openInventory((EntityPlayerMP) player, otherPlayer);
            else
                openInventory((EntityPlayerMP)player);
        }
        return null;
    }
}

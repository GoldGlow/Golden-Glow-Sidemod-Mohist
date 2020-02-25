package com.goldenglow.common.inventory.social;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.inventory.CustomInventory;
import com.goldenglow.common.inventory.CustomItem;
import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendList extends CustomInventory {
    public FriendList(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public static void openInventory(EntityPlayerMP player){
        IPlayerData playerData = player.getCapability(OOPlayerProvider.OO_DATA, null);
        List<UUID> friendList=playerData.getFriendList();
        InventoryBasic chestInventory=new InventoryBasic(Reference.resetText+"Friend list", true, Math.max(9, (((friendList.size()-1)/9)+1)*9));
        List<UUID> onlineFriends=new ArrayList<UUID>();
        List<UUID> offlineFriends=new ArrayList<UUID>();
        for(UUID friend:friendList){
            if(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(friend)!=null){
                onlineFriends.add(friend);
            }
            else {
                offlineFriends.add(friend);
            }
        }
        int inventoryIndex=0;
        for(UUID friend:onlineFriends){
            EntityPlayerMP friendEntity=FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(friend);
            ItemStack friendHead= CustomItem.getPlayerHead(friendEntity);
            String name=friendHead.getTagCompound().getString("SkullOwner");
            ArrayList<String> lore=new ArrayList<String>();
            lore.add(Reference.resetText+Reference.darkGreen+"Online"+Reference.resetText+" on "+ GoldenGlow.routeManager.getRoute(friendEntity).displayName);
            lore.add(Reference.resetText+"Click to see their profile");
            friendHead= CustomItem.addLore(friendHead, lore);
            friendHead.getTagCompound().setTag("SkullOwner", new NBTTagString(name));
            chestInventory.setInventorySlotContents(inventoryIndex++, friendHead);
        }
        for(UUID friend:offlineFriends){
            ItemStack friendItem=new ItemStack(Item.getByNameOrId("pixelmon:poke_ball"));
            friendItem.setStackDisplayName(Reference.resetText+ UsernameCache.getLastKnownUsername(friend));
            ArrayList<String> lore=new ArrayList<String>();
            lore.add(Reference.resetText+Reference.darkRed+"Offline");
            friendItem=CustomItem.addLore(friendItem, lore);
            chestInventory.setInventorySlotContents(inventoryIndex++, friendItem);
        }
        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString("Friend list"), Math.max(9, (((playerData.getFriendList().size()-1)/9)+1)*9)));
        player.openContainer = new FriendList(player.inventory, chestInventory, player);
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
            GGLogger.info(name);
            EntityPlayerMP otherPlayer=FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(name);
            if(otherPlayer!=null)
                PlayerProfile.openInventory((EntityPlayerMP) player, otherPlayer);
            else
                openInventory((EntityPlayerMP)player);
        }
        return null;
    }
}

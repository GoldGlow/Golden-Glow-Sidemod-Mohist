package com.goldenglow.common.inventory.BetterTrading;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.inventory.CustomInventory;
import com.goldenglow.common.inventory.CustomInventoryData;
import com.goldenglow.common.inventory.CustomItem;
import com.goldenglow.common.util.Reference;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

public class OfferPokemonInventory extends CustomInventory {
    TradingOffer offer;

    public OfferPokemonInventory(IInventory playerInv, IInventory chestInv, EntityPlayerMP playerMP){
        super(playerInv, chestInv, playerMP);
    }

    public void setOffer(TradingOffer offer){
        this.offer=offer;
    }

    public static void openInventory(EntityPlayerMP player, TradingOffer offer){
        InventoryBasic chestInventory=new InventoryBasic("Your offer", true, 27);
        PlayerPartyStorage storage= Pixelmon.storageManager.getParty(player);

        CustomItem[][] customItems=new CustomItem[27][];
        //offer
        ItemStack yourOffer=new ItemStack(Item.getByNameOrId("pixelmon:poke_ball"));
        yourOffer.setStackDisplayName(Reference.resetText+Reference.bold+"Your offered pokemon");
        List<Pokemon> tradedPokemon=offer.pokemonList;
        NBTTagList offerLore=new NBTTagList();
        offerLore.appendTag(new NBTTagString(Reference.resetText+"Click them to remove them from the offer"));
        yourOffer.getTagCompound().getCompoundTag("display").setTag("Lore", offerLore);
        customItems[0]=new CustomItem[]{new CustomItem(yourOffer, null)};
        chestInventory.setInventorySlotContents(0, yourOffer);

        for(int i=0;i<tradedPokemon.size();i++){
            customItems[2+i]=new CustomItem[]{CustomItem.getPokemonItem(tradedPokemon.get(i))};
            chestInventory.setInventorySlotContents(2+i, customItems[2+i][0].getItem());
        }

        //Your party
        ItemStack yourParty=new ItemStack(Item.getByNameOrId("pixelmon:poke_ball"));
        yourParty.setStackDisplayName(Reference.resetText+Reference.bold+"Your remaining pokemon");
        NBTTagList partyLore=new NBTTagList();
        partyLore.appendTag(new NBTTagString(Reference.resetText+"Click them to add them to the offer"));
        yourParty.getTagCompound().getCompoundTag("display").setTag("Lore", partyLore);
        customItems[9]=new CustomItem[]{new CustomItem(yourParty, null)};
        chestInventory.setInventorySlotContents(9, yourParty);

        for(int i=0;i<6;i++){
            if(storage.get(i)!=null){
                customItems[11+i]=new CustomItem[]{CustomItem.getPokemonItem(storage.get(i))};
                chestInventory.setInventorySlotContents(11+i, customItems[11+i][0].getItem());
            }
        }

        CustomItem returnButton=CustomItem.returnButton();
        customItems[26]=new CustomItem[]{returnButton};
        chestInventory.setInventorySlotContents(26, returnButton.getItem());

        CustomInventoryData data=new CustomInventoryData(3, "TradeOffer", "Your Offer", customItems, null);
        player.getNextWindowId();
        player.connection.sendPacket(new SPacketOpenWindow(player.currentWindowId, "minecraft:container", new TextComponentString(data.getDisplayName()), data.getRows() * 9));
        player.openContainer = new OfferPokemonInventory(player.inventory, chestInventory, player);
        ((OfferPokemonInventory)player.openContainer).setData(data);
        ((OfferPokemonInventory)player.openContainer).setOffer(offer);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(player, player.openContainer));
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        InventoryPlayer inventoryplayer = player.inventory;
        Slot slot = getSlot(slotId);
        if(slot.isSameInventory(getSlot(0))) {
            if(slotId>=2&&slotId<=6&&CustomInventory.getItem(this.getData().getItems()[slotId], (EntityPlayerMP) player)!=null){
                this.offer.removePokemon(slotId-2);
                player.closeScreen();
                OfferPokemonInventory.openInventory(this.offer.player, this.offer);
            }
            else if(slotId>=11&&slotId<=16&&CustomInventory.getItem(this.getData().getItems()[slotId], (EntityPlayerMP) player)!=null){
                this.offer.addPokemon(slotId-11);
                player.closeScreen();
                OfferPokemonInventory.openInventory(this.offer.player, this.offer);
            }
            else if(slotId==26){
                player.closeScreen();
                OfferMakingInventory.openInventory(this.offer.player, this.offer);
            }
        }
        return null;
    }
}

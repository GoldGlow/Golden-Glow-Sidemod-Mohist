package com.goldenglow.common.inventory;

import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.Reference;
import com.goldenglow.common.util.Requirement;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JeanMarc on 6/18/2019.
 */
public class CustomItem  {
    ItemStack item;
    Requirement[] requirements;
    Action[] leftClickActions;
    Action[] rightClickActions;

    public CustomItem(ItemStack item, Requirement[] requirements){
        this.item=item;
        this.requirements=requirements;
    }

    public ItemStack getItem(){
        return this.item;
    }

    public Requirement[] getRequirements(){
        return this.requirements;
    }

    public Action[] getLeftClickActions(){
        return this.leftClickActions;
    }

    public CustomItem setLeftClickActions(Action[] actions){
        this.leftClickActions=actions;
        return this;
    }

    public CustomItem setBothClickActions(Action[] actions){
        this.leftClickActions=actions;
        this.rightClickActions=actions;
        return this;
    }

    public Action[] getRightClickActions(){
        return this.rightClickActions;
    }

    public void setRightClickActions(Action[] actions){
        this.rightClickActions=actions;
    }

    public Action getAction(Action[] actions, EntityPlayerMP playerMP){
        for(Action action:actions){
            if(Requirement.checkRequirements(action.requirements, playerMP)){
                return action;
            }
        }
        return null;
    }

    public static CustomItem returnButton(){
        try {
            return new CustomItem(new ItemStack(JsonToNBT.getTagFromJson("{\"id\":\"variedcommodities:diamond_dagger\",\"Count\":1,\"Damage\":18,\"tag\":{\"Unbreakable\":1,\"display\":{\"Name\":\""+Reference.resetText+"Back\"}}}")), null);
        } catch (NBTException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack getPlayerHead(EntityPlayerMP player){
        ItemStack itemStack=new ItemStack(Item.getByNameOrId("minecraft:skull"));
        itemStack.setStackDisplayName(Reference.resetText+player.getName());
        itemStack.setItemDamage(3);
        itemStack.setTagInfo("SkullOwner", new NBTTagString(player.getName()));
        return itemStack;
    }

    public static ItemStack getPlayerHead(String player){
        ItemStack itemStack=new ItemStack(Item.getByNameOrId("minecraft:skull"));
        itemStack.setStackDisplayName(Reference.resetText+player);
        itemStack.setItemDamage(3);
        itemStack.setTagInfo("SkullOwner", new NBTTagString(player));
        return itemStack;
    }

    public static ItemStack getDiamondDagger(int damage){
        ItemStack itemStack=new ItemStack(Item.getByNameOrId("variedcommodities:diamond_dagger"));
        itemStack.setItemDamage(damage);
        itemStack.setTagInfo("Unbreakable", new NBTTagInt(1));
        return itemStack;
    }

    public static ItemStack addLore(ItemStack item, ArrayList<String> lore){
        NBTTagCompound itemNbt=new NBTTagCompound();
        NBTTagCompound itemDisplay=item.getOrCreateSubCompound("display");
        NBTTagList loreTag=new NBTTagList();
        for(String loreLine: lore) {
            loreTag.appendTag(new NBTTagString(loreLine));
        }
        itemDisplay.setTag("Lore", loreTag);
        itemNbt.setTag("display", itemDisplay);
        item.setTagCompound(itemNbt);
        return item;
    }

    public static CustomItem getPokemonItem(Pokemon pokemon){
        ItemStack item= ItemPixelmonSprite.getPhoto(pokemon);
        NBTTagList lore=new NBTTagList();
        String shinyText="";
        if(pokemon.isShiny()){
            shinyText=Reference.gold;
        }
        item.setStackDisplayName(Reference.resetText+shinyText+pokemon.getSpecies().name);
        String itemString="none";
        if(!pokemon.getHeldItem().getDisplayName().equals("Air")){
            itemString=pokemon.getHeldItem().getDisplayName();
        }
        lore.appendTag(new NBTTagString(Reference.resetText+Reference.bold+"Item: "+Reference.resetText+itemString));
        lore.appendTag(new NBTTagString(Reference.resetText+Reference.bold+"Level: "+Reference.resetText+pokemon.getLevel()));
        lore.appendTag(new NBTTagString(Reference.resetText+Reference.bold+"Size: "+Reference.resetText+pokemon.getGrowth()));
        lore.appendTag(new NBTTagString(Reference.resetText+Reference.bold+"Nature: "+Reference.resetText+pokemon.getNature()));
        lore.appendTag(new NBTTagString(Reference.resetText+Reference.bold+"Ability: "+Reference.resetText+pokemon.getAbilityName()));
        lore.appendTag(new NBTTagString(Reference.resetText+Reference.bold+"IVs: "+Reference.resetText+pokemon.getIVs().hp+"HP / "+pokemon.getIVs().attack+"ATK / "+pokemon.getIVs().defence+"DEF / "+pokemon.getIVs().specialAttack+"SPA / "+pokemon.getIVs().specialDefence+"SPD / "+pokemon.getIVs().speed+"SPE"));
        lore.appendTag(new NBTTagString(Reference.resetText+Reference.bold+"EVs: "+Reference.resetText+pokemon.getEVs().hp+"HP / "+pokemon.getEVs().attack+"ATK / "+pokemon.getEVs().defence+"DEF / "+pokemon.getEVs().specialAttack+"SPA / "+pokemon.getEVs().specialDefence+"SPD / "+pokemon.getEVs().speed+"SPE"));
        lore.appendTag(new NBTTagString(Reference.resetText+Reference.bold+"Moves: "+Reference.resetText+pokemon.getMoveset()));
        item.getTagCompound().getCompoundTag("display").setTag("Lore", lore);
        CustomItem customItem=new CustomItem(item, null);
        return customItem;
    }
}

package com.goldenglow.common.util;

import com.goldenglow.common.data.player.OOPlayerProvider;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccount;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.wrapper.PlayerWrapper;

import java.util.Iterator;
import java.util.List;

/**
 * Created by JeanMarc on 6/17/2019.
 */
public class Requirement {
    public RequirementType type;
    //id of the dialog/quest if that's the requirement type, unused otherwise
    public int id;
    //night/day if time is the requirement, permission node if the type is permission, unused otherwise
    public String value;
    //permission to override the requirement. Defaults to *
    public String override="*";

    public Requirement(){
    }

    public Requirement(RequirementType type, int id){
        this.type=type;
        this.id=id;
    }

    public Requirement(RequirementType type, String value){
        this.type=type;
        this.value=value;
    }

    public static boolean checkRequirement(Requirement requirement, EntityPlayerMP playerEntity) {
        PlayerWrapper player = new PlayerWrapper(playerEntity);

        if(PermissionUtils.checkPermission(playerEntity, requirement.override)){
            return true;
        }

        if(requirement.type == RequirementType.QUEST_STARTED) {
            return ((IPlayer)player).hasActiveQuest(requirement.id);
        }
        else if(requirement.type == RequirementType.QUEST_FINISHED) {
            return ((IPlayer)player).hasFinishedQuest(requirement.id);
        }
        else if(requirement.type == RequirementType.DIALOG) {
            return ((IPlayer)player).hasReadDialog(requirement.id);
        }
        else if(requirement.type == RequirementType.PERMISSION) {
            return PermissionUtils.checkPermission(playerEntity, requirement.value);
        }
        else if(requirement.type == RequirementType.TIME) {
            if(requirement.value.equals("day")) {
                return playerEntity.getEntityWorld().isDaytime();
            }
            else if(requirement.value.equals("night")) {
                return !playerEntity.getEntityWorld().isDaytime();
            }
        }
        else if(requirement.type==RequirementType.MONEY){
            IPixelmonBankAccount bankAccount=(IPixelmonBankAccount) Pixelmon.moneyManager.getBankAccount(playerEntity).orElse(null);
            if(bankAccount!=null){
                return bankAccount.getMoney() >= requirement.id;
            }
        }
        else if(requirement.type==RequirementType.ITEM){
            try {
                if(playerEntity.inventory.hasItemStack(new ItemStack(JsonToNBT.getTagFromJson(requirement.value)))) {
                    return true;
                }
            } catch (NBTException e) {
                e.printStackTrace();
            }
        }
        else if(requirement.type==RequirementType.FRIEND_ONLY){
            if(requirement.value.equalsIgnoreCase("true"))
                return playerEntity.getCapability(OOPlayerProvider.OO_DATA, null).getPlayerVisibility();
            else
                return !playerEntity.getCapability(OOPlayerProvider.OO_DATA, null).getPlayerVisibility();
        }
        return false;
    }

    public static boolean checkSpaceRequirement(EntityPlayerMP player, ItemStack itemStack){
        Iterator<ItemStack> iterator = player.inventory.mainInventory.iterator();
        ItemStack item=null;
        int freeItemSpace=0;
        while (iterator.hasNext()) {
            item=iterator.next();
            if (item.isEmpty())
                return true;
            else if(Item.getIdFromItem(item.getItem())==Item.getIdFromItem(itemStack.getItem())&&item.getCount()<item.getMaxStackSize()){
                freeItemSpace+=item.getMaxStackSize()-item.getCount();
                if(freeItemSpace>=itemStack.getCount())
                    return true;
            }
        }
        GGLogger.info("failed the check");
        return false;
    }

    public static boolean checkRequirements(Requirement[] requirements, EntityPlayerMP player){
        if(requirements!=null) {
            for (Requirement requirement : requirements) {
                if (!checkRequirement(requirement, player)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkRequirements(List<Requirement> requirements, EntityPlayerMP player){
        for(Requirement requirement:requirements) {
            if(!checkRequirement(requirement, player)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if(this.type==RequirementType.TIME || this.type==RequirementType.PERMISSION)
            return this.type+" : "+this.value;
        else
            return this.type+" : "+this.id;
    }

    public enum RequirementType {
        QUEST_STARTED,
        QUEST_FINISHED,
        DIALOG,
        PERMISSION,
        TIME,
        MONEY,
        ITEM,
        HAS_SPACE,
        FRIEND_ONLY
    }
}

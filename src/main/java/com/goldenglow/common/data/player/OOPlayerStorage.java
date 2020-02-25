package com.goldenglow.common.data.player;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.seals.SealManager;
import com.goldenglow.common.util.FullPos;
import com.goldenglow.common.util.Scoreboards;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.UUID;

public class OOPlayerStorage implements Capability.IStorage<IPlayerData> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side) {
        final NBTTagCompound tag = new NBTTagCompound();

        if(instance.getFriendList().size()>0){
            NBTTagList friendList=new NBTTagList();
            for(UUID player:instance.getFriendList()){
                friendList.appendTag(new NBTTagString(player.toString()));
            }
            tag.setTag("friendList", friendList);
        }

        if(instance.getWildTheme()!=null)
            tag.setString("theme_wild", instance.getWildTheme());

        if(instance.getTrainerTheme()!=null)
            tag.setString("theme_trainer", instance.getTrainerTheme());

        if(instance.getPVPTheme()!=null)
            tag.setString("theme_pvp", instance.getPVPTheme());

        tag.setInteger("pvpOption", instance.getPvpThemeOption());

        tag.setInteger("notification_style", instance.getNotificationScheme());

        tag.setString("scoreboardType", instance.getScoreboardType().name());

        if(instance.getKeyItems()!=null){
            NBTTagList items=new NBTTagList();
            for(ItemStack item:instance.getKeyItems()){
                if(item!=null)
                    items.appendTag(item.serializeNBT());
            }
            tag.setTag("KeyItems", items);
        }

        if(instance.getAWItems()!=null){
            NBTTagList items=new NBTTagList();
            for(ItemStack item:instance.getAWItems()){
                if(item!=null)
                    items.appendTag(item.serializeNBT());
            }
            tag.setTag("AWItems", items);
        }

        if(instance.getTMs()!=null){
            NBTTagList items=new NBTTagList();
            for(ItemStack item:instance.getTMs()){
                if(item!=null)
                    items.appendTag(item.serializeNBT());
            }
            tag.setTag("TMs", items);
        }

        if(instance.getEquippedSeals()!=null) {
            NBTTagList equippedSeals = new NBTTagList();
            for (int i = 0; i < instance.getEquippedSeals().length; i++) {
                String s = instance.getEquippedSeals()[i];
                if (s!=null && !s.isEmpty()) {
                    NBTTagCompound sealTag = new NBTTagCompound();
                    sealTag.setInteger("slot", i);
                    sealTag.setString("name", instance.getEquippedSeals()[i]);
                    equippedSeals.appendTag(sealTag);
                }
            }
            if (equippedSeals.tagCount()!=0)
                tag.setTag("equippedSeals", equippedSeals);
        }

        NBTTagList unlockedSeals = new NBTTagList();
        for(String s : instance.getUnlockedSeals()) {
            unlockedSeals.appendTag(new NBTTagString(s));
        }
        if(unlockedSeals.tagCount()!=0)
            tag.setTag("unlockedSeals", unlockedSeals);

        if(instance.getSafezone()!=null){
            tag.setString("safezone", instance.getSafezone().unlocalizedName);
        }

        if(instance.getBackupFullpos()!=null){
            NBTTagCompound fullPos=new NBTTagCompound();
            fullPos.setInteger("world", instance.getBackupFullpos().getWorld().provider.getDimension());
            fullPos.setInteger("posX", instance.getBackupFullpos().getPos().getX());
            fullPos.setInteger("posY", instance.getBackupFullpos().getPos().getY());
            fullPos.setInteger("posZ", instance.getBackupFullpos().getPos().getZ());
            tag.setTag("backupFullPos", fullPos);
        }

        if(instance.getCaptureChain()>0) {
            tag.setString("lastCaughtSpecies", instance.getChainSpecies().name);
            tag.setInteger("catchChain", instance.getCaptureChain());
        }
        if(instance.getKOChain()>0) {
            tag.setString("lastKOSpecies", instance.getLastKOPokemon().name);
            tag.setInteger("koChain", instance.getKOChain());
        }

        return tag;
    }

    @Override
    public void readNBT(Capability<IPlayerData> capability, IPlayerData instance, EnumFacing side, NBTBase nbt) {
        final NBTTagCompound tag = (NBTTagCompound)nbt;

        if(tag.hasKey("theme_wild"))
            instance.setWildTheme(tag.getString("theme_wild"));
        else
            instance.setWildTheme(GoldenGlow.songManager.wildDefault);

        if(tag.hasKey("theme_trainer"))
            instance.setTrainerTheme(tag.getString("theme_trainer"));
        else
            instance.setTrainerTheme(GoldenGlow.songManager.trainerDefault);

        if(tag.hasKey("theme_pvp"))
            instance.setPVPTheme(tag.getString("theme_pvp"));
        else
            instance.setPVPTheme(GoldenGlow.songManager.trainerDefault);

        if(tag.hasKey("pvpOption")){
            instance.setPvpThemeOption(tag.getInteger("pvpOption"));
        }

        if(tag.hasKey("friendList")){
            NBTTagList friends=tag.getTagList("friendList", Constants.NBT.TAG_STRING);
            for(NBTBase friend: friends){
                NBTTagString uuid=(NBTTagString) friend;
                instance.addFriend(UUID.fromString(uuid.getString()));
            }
        }

        instance.setNotificationScheme(tag.getInteger("notification_style"));

        if(tag.hasKey("equippedSeals")) {
            NBTTagList list = tag.getTagList("equippedSeals", 8);
            for(NBTBase n : list) {
                NBTTagCompound seal = (NBTTagCompound)n;
                if(SealManager.loadedSeals.containsKey(seal.getString("name"))) {
                    String[] equipped = instance.getEquippedSeals();
                    equipped[seal.getInteger("slot")] = seal.getString("name");
                    instance.setPlayerSeals(equipped);
                }
            }
        }

        if(tag.hasKey("unlockedSeals")) {
            NBTTagList list = tag.getTagList("unlockedSeals", 8);
            for(NBTBase n : list) {
                NBTTagString string = (NBTTagString)n;
                if(SealManager.loadedSeals.containsKey(string.getString())) {
                    instance.unlockSeal(string.getString());
                }
            }
        }

        if(tag.hasKey("safezone")){
            instance.setSafezone(tag.getString("safezone"));
        }
        if(tag.hasKey("scoreboardType")){
            instance.setScoreboardType(Scoreboards.EnumScoreboardType.valueOf(tag.getString("scoreboardType")));
        }

        if(tag.hasKey("KeyItems")){
            NBTTagList items=tag.getTagList("KeyItems", Constants.NBT.TAG_COMPOUND);
            for(NBTBase item: items){
                try {
                    instance.addKeyItem(new ItemStack(JsonToNBT.getTagFromJson(item.toString())));
                } catch (NBTException e) {
                    e.printStackTrace();
                }
            }
        }

        if(tag.hasKey("AWItems")){
            NBTTagList items=tag.getTagList("AWItems", Constants.NBT.TAG_COMPOUND);
            for(NBTBase item: items){
                try {
                    instance.addAWItem(new ItemStack(JsonToNBT.getTagFromJson(item.toString())));
                } catch (NBTException e) {
                    e.printStackTrace();
                }
            }
        }

        if(tag.hasKey("TMs")){
            NBTTagList items=tag.getTagList("TMs", Constants.NBT.TAG_COMPOUND);
            for(NBTBase item: items){
                try {
                    instance.unlockTM(new ItemStack(JsonToNBT.getTagFromJson(item.toString())));
                } catch (NBTException e) {
                    e.printStackTrace();
                }
            }
        }

        if(tag.hasKey("backupFullPos")){
            NBTTagCompound fullPos=tag.getCompoundTag("backupFullPos");
            World world= FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(fullPos.getInteger("world"));
            BlockPos pos=new BlockPos(fullPos.getInteger("posX"), fullPos.getInteger("posY"), fullPos.getInteger("posZ"));
            instance.setBackupFullpos(new FullPos(world, pos));
        }

        if(tag.hasKey("lastCaughtSpecies")) {
            instance.setChainSpecies(EnumSpecies.getFromName(tag.getString("lastCaughtSpecies")).get());
            instance.setCaptureChain(tag.getInteger("catchChain"));
        }
        if(tag.hasKey("lastKOSpecies")) {
            instance.setChainSpecies(EnumSpecies.getFromName(tag.getString("lastKOSpecies")).get());
            instance.setCaptureChain(tag.getInteger("koChain"));
        }
    }
}

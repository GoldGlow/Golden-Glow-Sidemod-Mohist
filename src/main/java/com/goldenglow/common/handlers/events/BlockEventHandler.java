package com.goldenglow.common.handlers.events;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.tiles.ICustomScript;
import com.goldenglow.common.tiles.TileEntityCustomApricornTree;
import com.goldenglow.common.tiles.TileEntityCustomBerryTree;
import com.goldenglow.common.util.GGLogger;
import com.goldenglow.common.util.PermissionUtils;
import com.goldenglow.common.util.ReflectionHelper;
import com.mrcrayfish.furniture.tileentity.TileEntityTV;
import com.pixelmonmod.pixelmon.api.events.ApricornEvent;
import com.pixelmonmod.pixelmon.api.events.BerryEvent;
import com.pixelmonmod.pixelmon.blocks.BlockBerryTree;
import com.pixelmonmod.pixelmon.blocks.MultiBlock;
import com.pixelmonmod.pixelmon.blocks.apricornTrees.BlockApricornTree;
import com.pixelmonmod.pixelmon.blocks.enums.EnumBlockPos;
import com.pixelmonmod.pixelmon.blocks.enums.EnumMultiPos;
import com.pixelmonmod.pixelmon.blocks.multiBlocks.BlockFridge;
import moe.plushie.armourers_workshop.common.blocks.BlockSkinnable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.event.BlockEvent;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.items.ItemScripted;

public class BlockEventHandler {
    @SubscribeEvent
    public void onPickApricorn(ApricornEvent.PickApricorn event) {
        if(event.tree instanceof TileEntityCustomApricornTree) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPickBerry(BerryEvent.PickBerry event) {
        if(event.tree instanceof TileEntityCustomBerryTree) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        IBlockState blockState = event.getWorld().getBlockState(event.getPos());
        /*
        if(!blockState.getBlock().onBlockActivated(event.getWorld(), event.getPos(), blockState, event.getEntityPlayer(), event.getHand(), event.getFace(), (float)event.getHitVec().x, (float)event.getHitVec().y, (float)event.getHitVec().z)) {
            if ((event.getItemStack().getItem().getRegistryName() + "").equals("variedcommodities:diamond_dagger")) {
                if (event.getItemStack().getItemDamage() >= 100 && event.getItemStack().getItemDamage() < 200) {
                    event.setCanceled(true);
                    CustomInventory.openInventory("PokeHelper", (EntityPlayerMP) event.getEntityPlayer());
                }
            }
        }*/
        if(event.getHand()== EnumHand.MAIN_HAND && event.getUseBlock()!= Event.Result.DENY ) {
            TileEntity tile = null;
            if(blockState.getBlock() instanceof BlockApricornTree || blockState.getBlock() instanceof BlockBerryTree) {
                if(blockState.getValue(BlockApricornTree.BLOCKPOS) == EnumBlockPos.TOP) {
                    tile = event.getWorld().getTileEntity(event.getPos().down());
                } else {
                    tile = event.getWorld().getTileEntity(event.getPos());
                }
            }
            else if(blockState.getBlock() instanceof BlockFridge){
                if(blockState.getValue(MultiBlock.MULTIPOS)== EnumMultiPos.TOP){
                    tile = event.getWorld().getTileEntity(event.getPos().down());
                }
                else{
                    tile = event.getWorld().getTileEntity(event.getPos());
                }
            }
            else if(blockState.getBlock() instanceof BlockSkinnable){
                tile = event.getWorld().getTileEntity(event.getPos());
            }
            if(tile instanceof ICustomScript) {
                ICustomScript customTile = (ICustomScript) tile;
                if (event.getItemStack().getItem() instanceof ItemScripted && !event.getEntityPlayer().isSneaking()) { // && new PlayerWrapper((EntityPlayerMP)event.getEntityPlayer()).hasPermission("goldglow.scripting")) {
                    ItemScriptedWrapper item = (ItemScriptedWrapper) NpcAPI.Instance().getIItemStack(event.getEntityPlayer().getHeldItemMainhand());
                    customTile.getScriptedTile().setNBT(item.getScriptNBT(new NBTTagCompound()));
                    customTile.getScriptedTile().setEnabled(true);
                    final BlockEvent.InitEvent initEvent = new BlockEvent.InitEvent(customTile.getScriptedTile().getBlock());
                    customTile.getScriptedTile().runScript(EnumScriptType.INIT, initEvent);
                    WrapperNpcAPI.EVENT_BUS.post(initEvent);
                    event.getEntityPlayer().sendMessage(new TextComponentString("Applied Script!"));
                } else {
                    EventHooks.onScriptBlockInteract(customTile.getScriptedTile(), event.getEntityPlayer(), 0, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
                }
            }
            else if(blockState.getBlock().getRegistryName().toString().equals("cfm:modern_tv")||blockState.getBlock().getRegistryName().toString().equals("cfm:tv")){
                GGLogger.info("in");
                TileEntityTV tileEntityTV= (TileEntityTV) event.getWorld().getTileEntity(event.getPos());
                try {
                    if (PermissionUtils.checkPermission(((EntityPlayerMP) event.getEntityPlayer()), "group.builder")) {
                        ReflectionHelper.setPrivateValue(tileEntityTV, "disabled", false);
                        GGLogger.info("builder");
                    } else {
                        ReflectionHelper.setPrivateValue(tileEntityTV, "disabled", true);
                        GGLogger.info("out");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                return;
            }
        }
        else if((GoldenGlow.rightClickBlacklistHandler.blacklistedItems.contains(blockState.getBlock().getRegistryName().toString()) || blockState.getBlock() instanceof BlockContainer) && !(PermissionUtils.checkPermission((EntityPlayerMP) event.getEntityPlayer(), "builder"))) {
            event.setCanceled(true);
        }
        else{
            return;
        }
    }

    void runOnPickEvent(ICustomScript tile, Event event) {
        for (final ScriptContainer scriptContainer4 : tile.getScriptedTile().scripts) {
            scriptContainer4.run("onPick", event);
        }
    }
}

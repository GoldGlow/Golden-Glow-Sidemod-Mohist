package com.goldenglow.common.command;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.controllers.ScriptContainer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandInstanceInv extends CommandBase {

    public String getName() {
        return "instanceinv";
    }

    public String getUsage(ICommandSender sender) {
        return "/instanceinv X Y Z";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3)
        {
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        }
        else
        {
            World world = sender.getEntityWorld();
            BlockPos blockpos = parseBlockPos(sender, args, 0, false);
            IBlockState originalBlock = world.getBlockState(blockpos);
            TileEntity oldTile = world.getTileEntity(blockpos);
            IBlockState newBlock = CustomItems.scripted.getDefaultState();
            if(world.isBlockLoaded(blockpos)) {
                if(oldTile instanceof IInventory) {
                    List<String> items = new ArrayList<>();
                    for (int i = 0; i < ((IInventory)oldTile).getSizeInventory(); i++) {
                        ItemStack stack = ((IInventory)oldTile).getStackInSlot(i);
                        if(!stack.isEmpty()) {
                            NBTTagCompound tag = stack.serializeNBT();
                            items.add("\'"+tag+"\'");
                        }
                        ((IInventory)oldTile).removeStackFromSlot(i);
                    }
                    world.setBlockState(blockpos, newBlock);
                    TileScripted tile = (TileScripted) world.getTileEntity(blockpos);
                    tile.setItemModel(new ItemStack(originalBlock.getBlock(), 1, originalBlock.getBlock().getMetaFromState(originalBlock)), originalBlock.getBlock());
                    tile.setRotation(0, 270, 0);
                    ScriptContainer script = new ScriptContainer(tile);
                    script.scripts.add("quests/inv.js");
                    script.script = "var name = '"+originalBlock.getBlock().getLocalizedName()+"';\nvar items = "+items.toString()+";\nvar questId = 0;";
                    tile.scripts.add(script);
                    tile.setEnabled(true);
                }
            } else {
                sender.sendMessage(new TextComponentString("ERROR: Block either not loaded, or is not a BlockContainer. If you believe this is wrong, tell Jean or Ryan."));
            }
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length > 0 && args.length <= 3)
        {
            return getTabCompletionCoordinate(args, 0, targetPos);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    int getRotationForFacing(EnumFacing facing) {
        int rot = 0;
        switch (facing) {
            case NORTH:
                rot = 90;
                break;
            case EAST:
                rot = 0;
                break;
            case SOUTH:
                rot = 270;
                break;
            case WEST:
                rot = 180;
                break;
        }
        return rot;
    }
}

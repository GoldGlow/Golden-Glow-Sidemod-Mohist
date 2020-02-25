package com.goldenglow.common.command;

import com.goldenglow.common.tiles.TileEntityCustomAW;
import com.goldenglow.common.tiles.TileEntityCustomApricornTree;
import com.goldenglow.common.tiles.TileEntityCustomBerryTree;
import com.goldenglow.common.tiles.TileEntityCustomFridge;
import com.pixelmonmod.pixelmon.blocks.tileEntities.TileEntityApricornTree;
import com.pixelmonmod.pixelmon.blocks.tileEntities.TileEntityBerryTree;
import com.pixelmonmod.pixelmon.blocks.tileEntities.TileEntityFridge;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandScriptable extends OOCommand {

    public String getName() {
        return "scriptable";
    }

    public String getUsage(ICommandSender sender) {
        return "/scriptable x y z";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3)
        {
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        }
        else {
            World world = sender.getEntityWorld();
            BlockPos blockpos = parseBlockPos(sender, args, 0, false);
            TileEntity oldTile = world.getTileEntity(blockpos);
            if(world.isBlockLoaded(blockpos) && oldTile != null) {
                TileEntity newTile = null;
                if(oldTile instanceof TileEntityApricornTree) {
                    newTile = new TileEntityCustomApricornTree(world.getBlockState(blockpos).getBlock(), blockpos);
                }
                else if(oldTile instanceof TileEntityBerryTree) {
                    newTile = new TileEntityCustomBerryTree(world.getBlockState(blockpos).getBlock(), blockpos);
                }
                else if(oldTile instanceof TileEntitySkinnable) {
                    newTile = new TileEntityCustomAW(world.getBlockState(blockpos).getBlock(), blockpos);
                }
                else if(oldTile instanceof TileEntityFridge){
                    newTile = new TileEntityCustomFridge(world.getBlockState(blockpos).getBlock(), oldTile.getPos());
                }

                if (newTile != null) {
                    newTile.readFromNBT(oldTile.writeToNBT(new NBTTagCompound()));
                    world.setTileEntity(blockpos, newTile);
                    sender.sendMessage(new TextComponentString("Successfully changed Block to Scripted Block."));
                }
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
}

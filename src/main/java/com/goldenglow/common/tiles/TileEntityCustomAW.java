package com.goldenglow.common.tiles;

import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.blocks.tiles.TileScripted;

public class TileEntityCustomAW extends TileEntitySkinnable implements ICustomScript, ITickable {

    private TileEntityCustomScripted tile;

    public TileEntityCustomAW() {
        this.tile = new TileEntityCustomScripted();
    }

    public TileEntityCustomAW(Block blockType, BlockPos pos) {
        this.tile = new TileEntityCustomScripted();
        this.blockType = blockType;
        this.setPos(pos);
        this.tile.setPos(pos);
    }

    @Override
    protected void setWorldCreate(World worldIn) {
        super.setWorldCreate(worldIn);
        if(this.tile!=null)
            this.tile.setWorld(worldIn);
    }

    @Override
    public void setWorld(World worldIn) {
        super.setWorld(worldIn);
        this.tile.setWorld(worldIn);
    }

    @Override
    public TileEntityCustomScripted getScriptedTile() {
        return this.tile;
    }

    @Override
    public void update() {
        tile.update();
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound tile = new NBTTagCompound();
        this.tile.writeToNBT(tile);
        nbt.setTag("scriptedTile", tile);
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("scriptedTile")) {
            NBTTagCompound tile = (NBTTagCompound)nbt.getTag("scriptedTile");
            this.tile.readFromNBT(tile);
        }
    }
}

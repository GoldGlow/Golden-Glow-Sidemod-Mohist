package com.goldenglow.common.tiles;

import com.pixelmonmod.pixelmon.blocks.tileEntities.TileEntityBerryTree;
import com.pixelmonmod.pixelmon.blocks.tileEntities.TileEntityFridge;
import com.pixelmonmod.pixelmon.client.models.animations.AnimateTask;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.EventHooks;
import noppes.npcs.blocks.tiles.TileScripted;

public class TileEntityCustomFridge extends TileEntityFridge implements ICustomScript {

    private TileEntityCustomScripted tile;

    public TileEntityCustomFridge() {
        this.tile = new TileEntityCustomScripted();
    }

    public TileEntityCustomFridge(Block blockType, BlockPos pos) {
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
    public void closeFridge() {
    }

    @Override
    public void openFridge() {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound tile = new NBTTagCompound();
        this.tile.writeToNBT(tile);
        nbt.setTag("scriptedTile", tile);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("scriptedTile")) {
            NBTTagCompound tile = (NBTTagCompound)nbt.getTag("scriptedTile");
            this.tile.readFromNBT(tile);
        }
    }
}

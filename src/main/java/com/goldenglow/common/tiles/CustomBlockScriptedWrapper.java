package com.goldenglow.common.tiles;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.npcs.api.wrapper.BlockScriptedWrapper;
import noppes.npcs.blocks.tiles.TileScripted;

public class CustomBlockScriptedWrapper extends BlockScriptedWrapper {

    public CustomBlockScriptedWrapper(World world, Block block, BlockPos pos, TileScripted tileEntity) {
        super(world, block, pos);
    }

    @Override
    protected void setTile(TileEntity tile) {
        if(tile instanceof ICustomScript) {
            super.setTile(((ICustomScript)tile).getScriptedTile());
        } else {
            super.setTile(tile);
        }
    }
}

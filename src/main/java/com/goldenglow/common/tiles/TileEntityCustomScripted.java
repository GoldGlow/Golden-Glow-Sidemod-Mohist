package com.goldenglow.common.tiles;

import noppes.npcs.api.block.IBlock;
import noppes.npcs.blocks.tiles.TileScripted;

public class TileEntityCustomScripted extends TileScripted {

    private short ticksExisted;
    IBlock block;

    public TileEntityCustomScripted() {
        super();
    }

    @Override
    public IBlock getBlock() {
        if(this.block==null) {
            this.block = new CustomBlockScriptedWrapper(this.getWorld(), this.getBlockType(), this.getPos(), this);
        }
        return this.block;
    }
}

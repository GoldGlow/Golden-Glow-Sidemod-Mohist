package com.goldenglow.common.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class FullPos {
    World world;
    BlockPos pos;

    public FullPos(World world, BlockPos pos){
        this.world=world;
        this.pos=pos;
    }

    public FullPos(EntityPlayerMP player){
        this.world= FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(player.getUniqueID()).world;
        this.pos=player.getPosition();
    }

    public void warpToWorldPos(EntityPlayerMP player){
        player.setWorld(this.world);
        player.setPositionAndUpdate(this.pos.getX(), this.pos.getY(), this.pos.getZ());
    }

    public World getWorld(){
        return this.world;
    }

    public BlockPos getPos(){
        return this.pos;
    }
}

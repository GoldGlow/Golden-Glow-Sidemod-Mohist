package com.goldenglowspigot.common.util.requirements;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.util.requirements.Requirement;
import com.goldenglowspigot.common.util.PermissionUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IPlayer;

public class PermissionRequirement implements Requirement {
    private final String name="PERMISSION";

    public String getName(){
        return this.name;
    }

    public boolean hasRequirement(String data, EntityPlayerMP player){
        return GoldenGlow.permissionUtils.checkPermission(player, data);
    }
}

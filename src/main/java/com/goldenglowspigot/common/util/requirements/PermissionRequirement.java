package com.goldenglowspigot.common.util.requirements;

import com.goldenglow.GoldenGlow;
import com.pixelmonessentials.common.api.requirement.Requirement;
import net.minecraft.entity.player.EntityPlayerMP;

public class PermissionRequirement implements Requirement {
    private final String name="PERMISSION";

    public String getName(){
        return this.name;
    }

    public boolean hasRequirement(String data, EntityPlayerMP player){
        return GoldenGlow.permissionUtils.checkPermission(player, data);
    }
}

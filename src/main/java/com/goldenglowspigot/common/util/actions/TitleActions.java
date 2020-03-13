package com.goldenglowspigot.common.util.actions;

import com.goldenglow.common.inventory.BagInventories;
import com.goldenglow.GoldenGlow;
import com.goldenglowspigot.common.util.PermissionUtils;
import com.goldenglow.common.util.actions.Action;
import net.minecraft.entity.player.EntityPlayerMP;

public class TitleActions implements Action {
    public final String name="TM_PARTY";

    public String getName(){
        return this.name;
    }

    public void doAction(String value, EntityPlayerMP player){
        GoldenGlow.permissionUtils.unsetPermissionsWithStart(player, "prefix.3.");
        GoldenGlow.permissionUtils.setPrefix(player, value);
    }
}

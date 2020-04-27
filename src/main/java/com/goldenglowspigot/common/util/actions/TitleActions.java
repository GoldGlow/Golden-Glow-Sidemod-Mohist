package com.goldenglowspigot.common.util.actions;

import com.goldenglow.GoldenGlow;
import com.pixelmonessentials.common.api.action.Action;
import net.minecraft.entity.player.EntityPlayerMP;

public class TitleActions implements Action {
    public final String name="CHANGE_TITLE";

    public String getName(){
        return this.name;
    }

    public void doAction(String value, EntityPlayerMP player){
        GoldenGlow.permissionUtils.unsetPermissionsWithStart(player, "prefix.3.");
        GoldenGlow.permissionUtils.setPrefix(player, value);
    }
}

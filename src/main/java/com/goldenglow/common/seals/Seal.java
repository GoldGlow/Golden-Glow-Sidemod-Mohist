package com.goldenglow.common.seals;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.ScriptContainer;

public class Seal {

    ScriptContainer scriptContainer;

    public Seal(String script) {
        this.scriptContainer = new ScriptContainer(SealScriptHandler.instance);
        this.scriptContainer.script = script;
    }

    public void execute(Entity entity) {
        EntityEvent event = new EntityEvent(entity);
        this.scriptContainer.run(EnumScriptType.INIT, event);
    }

}

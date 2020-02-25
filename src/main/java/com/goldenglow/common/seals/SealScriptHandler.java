package com.goldenglow.common.seals;

import net.minecraftforge.fml.common.eventhandler.Event;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;

import java.util.List;
import java.util.Map;

public class SealScriptHandler implements IScriptHandler {

    public static SealScriptHandler instance = new SealScriptHandler();

    @Override
    public void runScript(EnumScriptType scriptType, Event event) {
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public boolean getEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean arg1) {
    }

    @Override
    public String getLanguage() {
        return "ECMAScript";
    }

    @Override
    public void setLanguage(String arg1) {

    }

    @Override
    public List<ScriptContainer> getScripts() {
        return null;
    }

    @Override
    public String noticeString() {
        return null;
    }

    @Override
    public Map<Long, String> getConsoleText() {
        return null;
    }

    @Override
    public void clearConsole() {

    }
}

package com.goldenglowspigot;

import com.goldenglowspigot.common.chatChannels.ChannelsManager;
import com.goldenglowspigot.common.commands.CommandChannel;
import com.goldenglowspigot.common.handlers.events.*;
import com.goldenglowspigot.common.util.PermissionUtils;
import com.goldenglowspigot.common.util.actions.TitleActions;
import com.goldenglowspigot.common.util.requirements.PermissionRequirement;
import com.pixelmonessentials.PixelmonEssentials;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GoldenGlow extends JavaPlugin{

    public String VERSION = "1.0.3";

    public static Thread statsServer;

    public static ChannelsManager channelsManager=new ChannelsManager();

    public GoldenGlow() {
    }

    @Override
    public void onEnable(){
        Bukkit.getServer().getPluginManager().registerEvents(new SpigotEvents(), this);
        com.goldenglow.GoldenGlow.permissionUtils=new PermissionUtils();
        this.getCommand("channel").setExecutor(new CommandChannel());
        PixelmonEssentials.actionHandler.addAction(new TitleActions());
        PixelmonEssentials.requirementHandler.addRequirement(new PermissionRequirement());
    }
}
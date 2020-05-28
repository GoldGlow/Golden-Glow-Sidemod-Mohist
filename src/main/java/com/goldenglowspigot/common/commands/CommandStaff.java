package com.goldenglowspigot.common.commands;

import com.goldenglowspigot.GoldenGlow;
import com.goldenglowspigot.common.chatChannels.Channel;
import com.goldenglowspigot.common.chatChannels.ChannelsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import red.mohist.api.PlayerAPI;

import java.util.Set;

public class CommandStaff implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(com.goldenglow.GoldenGlow.permissionUtils.checkPermission(PlayerAPI.getNMSPlayer(player), "channel.staff")) {
                Channel oldChannel = GoldenGlow.channelsManager.getPlayerChannel(player);
                if (args.length == 0) {
                    GoldenGlow.channelsManager.setPlayerChannel(player, ChannelsManager.EnumChannels.STAFF);
                } else {
                    String message = "";
                    for (String arg : args) {
                        if (!message.equals("")) {
                            message += " ";
                        }
                        message += arg;
                    }
                    GoldenGlow.channelsManager.setTempChannel(player, ChannelsManager.EnumChannels.STAFF);
                    Bukkit.getPluginManager().callEvent(new AsyncPlayerChatEvent(true, player, message, (Set<Player>) Bukkit.getOnlinePlayers()));
                    GoldenGlow.channelsManager.setTempChannel(player, oldChannel);
                }
                return true;
            }
        }
        return false;
    }
}

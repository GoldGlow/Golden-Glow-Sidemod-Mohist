package com.goldenglowspigot.common.commands;

import com.goldenglowspigot.GoldenGlow;
import com.goldenglowspigot.common.chatChannels.ChannelsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChannel implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length<1){
                return false;
            }
            if(args[0].equalsIgnoreCase("global")){
                GoldenGlow.channelsManager.setPlayerChannel(player, ChannelsManager.EnumChannels.GLOBAL);
            }
            else if(args[0].equalsIgnoreCase("private")){
                if(args.length!=2){
                    return false;
                }
                Player otherPlayer=Bukkit.getPlayer(Bukkit.getPlayerUniqueId(args[1]));
                if(otherPlayer!=null&&otherPlayer instanceof Player){
                    GoldenGlow.channelsManager.setPlayerChannel(player, otherPlayer);
                }
            }
        }
        return true;
    }
}

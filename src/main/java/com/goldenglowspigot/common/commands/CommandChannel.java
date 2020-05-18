package com.goldenglowspigot.common.commands;

import com.coloredcarrot.jsonapi.impl.JsonMsg;
import com.goldenglowspigot.GoldenGlow;
import com.goldenglowspigot.common.chatChannels.Channel;
import com.goldenglowspigot.common.chatChannels.ChannelsManager;
import com.goldenglowspigot.common.chatChannels.GlobalChannel;
import com.goldenglowspigot.common.chatChannels.StaffChannel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CommandChannel implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length<1){
                Channel channel=GoldenGlow.channelsManager.getPlayerChannel(player);
                JsonMsg message=new JsonMsg("You're currently chatting in the channel ");
                message.append(channel.getPrefix());
                message.send(player);
                return true;
            }
            else if(args[0].equalsIgnoreCase("list")){
                ArrayList<JsonMsg> messages=new ArrayList<>();
                messages.add(new JsonMsg("List of channels you have access to"));
                messages.add(new JsonMsg(GlobalChannel.getPrefixStatic()));
                JsonMsg staffChat= StaffChannel.getPrefixStatic(player);
                if(staffChat!=null){
                    messages.add(staffChat);
                }
                for(JsonMsg message:messages){
                    message.send(player);
                }
            }
            else if(args[0].equalsIgnoreCase("global")){
                GoldenGlow.channelsManager.setPlayerChannel(player, ChannelsManager.EnumChannels.GLOBAL);
            }
            else if(args[0].equalsIgnoreCase("staff")){
                GoldenGlow.channelsManager.setPlayerChannel(player, ChannelsManager.EnumChannels.STAFF);
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

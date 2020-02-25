package com.goldenglow.common.command;

import com.pixelmonmod.pixelmon.util.PixelmonPlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;

/**
 * Created by JeanMarc on 5/16/2019.
 */
public class CommandRouteNotificationOption extends CommandBase {
    public String getName(){
        return "notification";
    }

    public String getUsage(ICommandSender sender){
        return "/notification <player name> <0-3>";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length!=2){
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        }
        else{
            if(Integer.parseInt(args[1])>=0&&Integer.parseInt(args[1])<=3){
                PixelmonPlayerUtils.getUniquePlayerStartingWith(args[0]).getEntityData().setInteger("RouteNotification", Integer.parseInt(args[1]));
                Server.sendData(PixelmonPlayerUtils.getUniquePlayerStartingWith(args[0]), EnumPacketClient.MESSAGE, "This is your new box!", "", Integer.valueOf(PixelmonPlayerUtils.getUniquePlayerStartingWith(args[0]).getEntityData().getInteger("RouteNotification")));
            }
        }
    }
}

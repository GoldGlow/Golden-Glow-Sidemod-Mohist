package com.goldenglow.common.command;

import com.pixelmonmod.pixelmon.util.PixelmonPlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

/**
 * Created by JeanMarc on 6/14/2019.
 */
public class CommandSetTheme extends CommandBase {
    public String getName(){return "theme";}

    public String getUsage(ICommandSender sender){return "/theme <wild|trainer|pvp> <player name> <song>";}

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length!=3){
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        }
        else{
            if(args[0].equalsIgnoreCase("wild")){
                PixelmonPlayerUtils.getUniquePlayerStartingWith(args[1]).getEntityData().setString("WildTheme", args[2]);
            }
            else if(args[0].equalsIgnoreCase("trainer")){
                PixelmonPlayerUtils.getUniquePlayerStartingWith(args[1]).getEntityData().setString("TrainerTheme", args[2]);
            }
            else if(args[0].equalsIgnoreCase("pvp")){
                PixelmonPlayerUtils.getUniquePlayerStartingWith(args[1]).getEntityData().setString("PVPTheme", args[2]);
            }
        }
    }
}

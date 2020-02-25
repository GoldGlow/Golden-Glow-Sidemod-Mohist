package com.goldenglow.common.command;

import com.pixelmonmod.pixelmon.util.PixelmonPlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

/**
 * Created by JeanMarc on 6/15/2019.
 */
public class CommandSetPvpMusicOption extends CommandBase {
    //0=always the opponent's theme
    //1=opponent's theme as long as it's not the default theme
    //2=opponent's theme if it's a unique theme
    //3=always the player's theme, no matter what
    public String getName() {return "pvpsong";}

    public String getUsage(ICommandSender sender) {return "/pvpsong <player name> <0-3>";}

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length!=2){
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        }
        else{
            if(Integer.parseInt(args[1])>=0&&Integer.parseInt(args[1])<=3){
                PixelmonPlayerUtils.getUniquePlayerStartingWith(args[0]).getEntityData().setInteger("PvpOption", Integer.parseInt(args[1]));
            }
        }
    }
}

package com.goldenglow.common.command;

import com.goldenglow.common.util.scripting.OtherFunctions;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.wrapper.PlayerWrapper;

/**
 * Created by JeanMarc on 7/1/2019.
 */
public class CommandMoneyreward extends CommandBase {
    public String getName(){
        return "moneyreward";
    }

    public String getUsage(ICommandSender sender){
        return "/moneyreward <amount> <player>";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 2){
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        }
        else{
            EntityPlayerMP player = getPlayer(server, sender, args[1]);
            NoppesUtilServer.runCommand(sender, sender.getName(), "givemoney "+args[1]+" "+args[0], (EntityPlayerMP)null);
            OtherFunctions.showAchievement(new PlayerWrapper(player), "Reward", "Obtained $"+args[0]);
        }
    }
}

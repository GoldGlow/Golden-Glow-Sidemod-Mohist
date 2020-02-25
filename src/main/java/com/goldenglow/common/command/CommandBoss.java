package com.goldenglow.common.command;

import com.goldenglow.common.battles.bosses.BossManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandBoss extends CommandBase {

    public String getName() {
        return "boss";
    }

    public String getUsage(ICommandSender sender) {
        return "/boss [bossName] [player]";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length>=2) {
            String bossName = args[0];
            String playerName = args[1];
            BossManager.startBossBattle(server.getPlayerList().getPlayerByUsername(playerName), bossName);
        } else {
            sender.sendMessage(new TextComponentString(BossManager.getBosses()));
        }
    }
}

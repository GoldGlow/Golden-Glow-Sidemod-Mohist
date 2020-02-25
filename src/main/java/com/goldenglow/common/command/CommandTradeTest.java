package com.goldenglow.common.command;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.inventory.shops.CustomShop;
import com.goldenglow.common.inventory.shops.CustomShopData;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.events.PixelmonTradeEvent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

public class CommandTradeTest extends CommandBase {
    public String getName() {
        return "tradetest";
    }

    public String getUsage(ICommandSender sender) {
        return "/tradetest <username> <slot>";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length!=2){
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        }
        else{
            EntityPlayerMP playerMP=getPlayer(server, sender, sender.getName());
            EntityPlayerMP targetPlayer = getPlayer(server, sender, args[0]);
            MinecraftForge.EVENT_BUS.post(new PixelmonTradeEvent(playerMP, targetPlayer, Pixelmon.storageManager.getParty(playerMP).get(Integer.parseInt(args[1])), null));
        }
    }
}
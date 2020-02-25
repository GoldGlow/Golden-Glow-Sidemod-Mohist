package com.goldenglow.common.command;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.inventory.shops.CustomShop;
import com.goldenglow.common.inventory.shops.CustomShopData;
import com.goldenglow.common.util.GGLogger;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandShop extends CommandBase {
    public String getName() {
        return "cshop";
    }

    public String getUsage(ICommandSender sender) {
        return "/cshop <username> <shopname>";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length!=2){
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        }
        else{
            EntityPlayerMP player = getPlayer(server, sender, args[0]);
            for(CustomShopData inventoryData: GoldenGlow.customShopHandler.shops) {
                if (inventoryData.getName().equals(args[1])) {
                    CustomShop.openCustomShop(player, inventoryData);
                    return;
                }
            }
        }
    }
}

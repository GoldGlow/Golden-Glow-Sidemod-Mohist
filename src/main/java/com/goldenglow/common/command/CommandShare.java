package com.goldenglow.common.command;

import com.goldenglow.common.inventory.CustomItem;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import org.codehaus.plexus.util.StringUtils;

public class CommandShare extends CommandBase {

    public String getName() {
        return "share";
    }

    public String getUsage(ICommandSender sender) {
        return "/share [partySlot]";
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = (EntityPlayerMP) sender;
        if(args.length>0 && StringUtils.isNumeric(args[0])) {
            int i = Integer.parseInt(args[0]) - 1;
            TextComponentString msg;
            if(i > 5 || i < 0) {
                msg = new TextComponentString("Error: Number must be at least 1, or at most 6.");
                msg.getStyle().setColor(TextFormatting.RED);
                player.sendMessage(msg);
            }
            else {
                PlayerPartyStorage storage = Pixelmon.storageManager.getParty(player);
                Pokemon pokemon = storage.get(i);
                String s = pokemon.getNickname()!=null ? pokemon.getSpecies()+":"+pokemon.getNickname() : pokemon.getSpecies().name;
                msg = new TextComponentString("<"+ s +">");
                msg.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponentString(CustomItem.getPokemonItem(pokemon).getItem().writeToNBT(new NBTTagCompound()).toString())));
                server.getPlayerList().sendMessage(msg, false);
            }
        }
    }
}

package com.goldenglow.common.command;

import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.routes.Route;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandDebug extends CommandBase {
    @Override
    public String getName() {
        return "rvdebug";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        /*
        //Pixelmon Custom Scoreboard Overlay
        ArrayList<String> lines = new ArrayList<>();
        lines.add("[ ]");
        lines.add("");
        ArrayList<String> scores = new ArrayList<>();
        scores.add("This is a test for a bit of");
        scores.add("Score2");
        Pixelmon.network.sendTo(new CustomScoreboardUpdatePacket("Quest Title", lines, scores), (EntityPlayerMP)sender);
        Pixelmon.network.sendTo(new CustomScoreboardDisplayPacket(ScoreboardLocation.RIGHT_MIDDLE, true), (EntityPlayerMP)sender);

        CustomInventory.openInventory("Seals", (EntityPlayerMP)sender);
         */
        EntityPlayerMP player = (EntityPlayerMP)sender;
        if(player.getCapability(OOPlayerProvider.OO_DATA, null).hasRoute()) {
            Route route = player.getCapability(OOPlayerProvider.OO_DATA, null).getRoute();
            LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(player.getName());
            Polygonal2DRegionSelector selector = new Polygonal2DRegionSelector(session.getSelectionWorld(), route.region.getPoints(), route.region.getMinimumY(), route.region.getMaximumY());
            session.setRegionSelector(ForgeWorldEdit.inst.wrap(player).getWorld(), selector);
            session.dispatchCUISelection(ForgeWorldEdit.inst.wrap(player));
        }
    }
}

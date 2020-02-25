package com.goldenglow.common.command;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.routes.Route;
import com.goldenglow.common.util.Requirement;
import com.goldenglow.common.util.RequirementTypeArgument;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;

public class CommandRoutes extends OOCommand {

    public String getName() {
        return "routes";
    }

    public String getUsage(ICommandSender sender) {
        return "/routes [create|edit|list]";
    }

    public static void register(CommandDispatcher<ICommandSender> dispatcher) {
        dispatcher.register(
                literal("routes")
                        .requires(
                                (commandSender) -> commandSender.canUseCommand(2, "routes")
                        )
                        .then(
                                literal("create")
                                        .then(
                                                argument("routeName", StringArgumentType.string())
                                                        .executes(c -> {
                                                            try {
                                                                LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(c.getSource().getName());
                                                                Region region = session.getRegionSelector(session.getSelectionWorld()).getRegion();
                                                                if (region != null && region instanceof Polygonal2DRegion) {
                                                                    Polygonal2DRegion selection = (Polygonal2DRegion) region;
                                                                    Route route = new Route(c.getArgument("routeName", String.class), selection, FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(c.getSource().getName()).world);
                                                                    GoldenGlow.routeManager.addRoute(route);
                                                                    TextComponentString msg = new TextComponentString("New Route '" + route.unlocalizedName + "' created successfully!");
                                                                    msg.getStyle().setColor(TextFormatting.AQUA);
                                                                    c.getSource().sendMessage(msg);
                                                                } else {
                                                                    TextComponentString msg = new TextComponentString("ERROR: Make sure you make a WorldEdit selection, using poly selection mode. (//sel poly)!");
                                                                    msg.getStyle().setColor(TextFormatting.RED);
                                                                    c.getSource().sendMessage(msg);
                                                                }
                                                            } catch (IncompleteRegionException e) {
                                                                e.printStackTrace();
                                                            }
                                                            return 1;
                                                        })
                                        )
                                        .executes(c -> {
                                            c.getSource().sendMessage(new TextComponentString("/routes create [routeName]"));
                                            return 1;
                                        })
                        )
                        .then(
                                literal("edit")
                                        .then(
                                                argument("routeName", StringArgumentType.string())
                                                        .then(
                                                                literal("display")
                                                                        .then(
                                                                                argument("name", StringArgumentType.greedyString())
                                                                                        .executes(c -> {
                                                                                            Route r = GoldenGlow.routeManager.getRoute(c.getArgument("routeName", String.class));
                                                                                            String s = r.displayName;
                                                                                            r.displayName = c.getArgument("name", String.class);
                                                                                            if (!s.isEmpty())
                                                                                                c.getSource().sendMessage(new TextComponentString("Changed " + c.getArgument("routeName", String.class) + " display name from \"" + s + "\" to \"" + c.getArgument("name", String.class) + "\""));
                                                                                            else
                                                                                                c.getSource().sendMessage(new TextComponentString("Changed " + c.getArgument("routeName", String.class) + " display name to \"" + c.getArgument("name", String.class) + "\""));
                                                                                            return 1;
                                                                                        })
                                                                        )
                                                                        .executes(c -> {
                                                                            c.getSource().sendMessage(new TextComponentString("/routes edit " + c.getArgument("routeName", String.class) + " display [New Display Name...]"));
                                                                            return 1;
                                                                        })
                                                        )
                                                        .then(
                                                                literal("song")
                                                                        .then(
                                                                                argument("song_name", StringArgumentType.string())
                                                                                        .executes(c -> {
                                                                                            Route r = GoldenGlow.routeManager.getRoute(c.getArgument("routeName", String.class));
                                                                                            String s = r.song;
                                                                                            r.song = c.getArgument("song_name", String.class);
                                                                                            if (!s.isEmpty())
                                                                                                c.getSource().sendMessage(new TextComponentString("Changed " + c.getArgument("routeName", String.class) + " song from \"" + s + "\" to \"" + c.getArgument("song", String.class) + "\""));
                                                                                            else
                                                                                                c.getSource().sendMessage(new TextComponentString("Changed " + c.getArgument("routeName", String.class) + " song to \"" + c.getArgument("song", String.class) + "\""));
                                                                                            return 1;
                                                                                        })
                                                                        )
                                                                        .executes(c -> {
                                                                            c.getSource().sendMessage(new TextComponentString("/routes edit " + c.getArgument("routeName", String.class) + " song [songLocation]"));
                                                                            return 1;
                                                                        })
                                                        )
                                                        .then(
                                                                literal("priority")
                                                                        .then(
                                                                                argument("priority", IntegerArgumentType.integer())
                                                                                        .executes(c -> {
                                                                                            Route r = GoldenGlow.routeManager.getRoute(c.getArgument("routeName", String.class));
                                                                                            int i = r.priority;
                                                                                            r.priority = c.getArgument("priority", Integer.class);
                                                                                            c.getSource().sendMessage(new TextComponentString("Changed " + c.getArgument("routeName", String.class) + " priority from \"" + i + "\" to \"" + c.getArgument("priority", Integer.class) + "\""));
                                                                                            return 1;
                                                                                        })
                                                                        )
                                                                        .executes(c -> {
                                                                            c.getSource().sendMessage(new TextComponentString("/routes edit " + c.getArgument("routeName", String.class) + " priority [0-100...]"));
                                                                            return 1;
                                                                        })
                                                        )
                                                        .then(
                                                                literal("requirements")
                                                                        .then(
                                                                                literal("new")
                                                                                        .then(
                                                                                                argument("type", new RequirementTypeArgument())
                                                                                                        .then(
                                                                                                                argument("value", IntegerArgumentType.integer())
                                                                                                                        .executes(c -> {
                                                                                                                            Requirement.RequirementType type = Requirement.RequirementType.valueOf(c.getArgument("type", String.class));
                                                                                                                            if (type == Requirement.RequirementType.TIME || type == Requirement.RequirementType.PERMISSION) {
                                                                                                                                c.getSource().sendMessage(new TextComponentString("Requirement Type: " + c.getArgument("type", String.class) + "requires a String, not an Integer"));
                                                                                                                            } else {
                                                                                                                                Requirement requirement = new Requirement(type, c.getArgument("value", Integer.class));
                                                                                                                                GoldenGlow.routeManager.getRoute(c.getArgument("routeName", String.class)).requirements.add(requirement);
                                                                                                                            }
                                                                                                                            return 1;
                                                                                                                        })
                                                                                                        )
                                                                                                        .then(
                                                                                                                argument("value", StringArgumentType.string())
                                                                                                                        .executes(c -> {
                                                                                                                            Requirement.RequirementType type = Requirement.RequirementType.valueOf(c.getArgument("type", String.class));
                                                                                                                            if (type == Requirement.RequirementType.TIME || type == Requirement.RequirementType.PERMISSION) {
                                                                                                                                Requirement requirement = new Requirement(type, c.getArgument("value", String.class));
                                                                                                                                GoldenGlow.routeManager.getRoute(c.getArgument("routeName", String.class)).requirements.add(requirement);
                                                                                                                            } else {
                                                                                                                                c.getSource().sendMessage(new TextComponentString("Requirement Type: " + c.getArgument("type", String.class) + "requires an Integer, not a String"));
                                                                                                                            }
                                                                                                                            return 1;
                                                                                                                        })
                                                                                                        )
                                                                                                        .executes(c -> {
                                                                                                            c.getSource().sendMessage(new TextComponentString("/routes edit " + c.getArgument("routeName", String.class) + " requirements new [type] [value]"));
                                                                                                            return 1;
                                                                                                        })
                                                                                        )
                                                                                        .executes(c -> {
                                                                                            c.getSource().sendMessage(new TextComponentString("/routes edit " + c.getArgument("routeName", String.class) + " requirements new [type] [value]"));
                                                                                            return 1;
                                                                                        })
                                                                        )
                                                                        .then(
                                                                                argument("id", IntegerArgumentType.integer())
                                                                                        .then(
                                                                                                literal("type")
                                                                                                        .then(
                                                                                                                argument("type", new RequirementTypeArgument())
                                                                                                                        .executes(c -> {
                                                                                                                            Requirement.RequirementType type = Requirement.RequirementType.valueOf(c.getArgument("type", String.class));
                                                                                                                            GoldenGlow.routeManager.getRoute(c.getArgument("routeName", String.class)).requirements.get(c.getArgument("id", Integer.class)).type = type;
                                                                                                                            c.getSource().sendMessage(new TextComponentString("Updated Requirement for " + c.getArgument("routeName", String.class)));
                                                                                                                            return 1;
                                                                                                                        })
                                                                                                        )
                                                                                                        .executes(c -> {
                                                                                                            c.getSource().sendMessage(new TextComponentString("/routes edit " + c.getArgument("routeName", String.class) + " requirements " + c.getArgument("id", Integer.class) + " type [newType]"));
                                                                                                            return 1;
                                                                                                        })
                                                                                        )
                                                                                        .then(
                                                                                                literal("value")
                                                                                                        .then(
                                                                                                                argument("value", StringArgumentType.string())
                                                                                                                        .executes(c -> {
                                                                                                                            Route route = GoldenGlow.routeManager.getRoute(c.getArgument("routeName", String.class));
                                                                                                                            Requirement requirement = route.requirements.get(c.getArgument("id", Integer.class));
                                                                                                                            if (requirement.type == Requirement.RequirementType.PERMISSION || requirement.type == Requirement.RequirementType.TIME) {
                                                                                                                                requirement.value = c.getArgument("value", String.class);
                                                                                                                                c.getSource().sendMessage(new TextComponentString("Updated Requirement for " + c.getArgument("routeName", String.class)));
                                                                                                                            } else {
                                                                                                                                c.getSource().sendMessage(new TextComponentString("Requirement Type: " + c.getArgument("type", String.class) + "requires an Integer, not a String"));
                                                                                                                            }
                                                                                                                            return 1;
                                                                                                                        })
                                                                                                        )
                                                                                                        .executes(c -> {
                                                                                                            c.getSource().sendMessage(new TextComponentString("/routes edit " + c.getArgument("routeName", String.class) + " requirements " + c.getArgument("id", Integer.class) + " value [newValue]"));
                                                                                                            return 1;
                                                                                                        })
                                                                                        )
                                                                                        .then(
                                                                                                literal("id")
                                                                                                        .then(
                                                                                                                argument("idVal", IntegerArgumentType.integer())
                                                                                                                        .executes(c -> {
                                                                                                                            Route route = GoldenGlow.routeManager.getRoute(c.getArgument("routeName", String.class));
                                                                                                                            Requirement requirement = route.requirements.get(c.getArgument("idVal", Integer.class));
                                                                                                                            if (requirement.type == Requirement.RequirementType.PERMISSION || requirement.type == Requirement.RequirementType.TIME) {
                                                                                                                                c.getSource().sendMessage(new TextComponentString("Requirement Type: " + c.getArgument("type", String.class) + "requires a String, not an Integer"));
                                                                                                                            } else {
                                                                                                                                requirement.id = c.getArgument("value", Integer.class);
                                                                                                                                c.getSource().sendMessage(new TextComponentString("Updated Requirement for " + c.getArgument("routeName", String.class)));
                                                                                                                            }
                                                                                                                            return 1;
                                                                                                                        })
                                                                                                        )
                                                                                                        .executes(c -> {
                                                                                                            c.getSource().sendMessage(new TextComponentString("/routes edit " + c.getArgument("routeName", String.class) + " requirements " + c.getArgument("id", Integer.class) + " id [newId]"));
                                                                                                            return 1;
                                                                                                        })
                                                                                        )
                                                                                        .executes(c -> {
                                                                                            c.getSource().sendMessage(new TextComponentString("/routes edit " + c.getArgument("routeName", String.class) + " requirements " + c.getArgument("id", Integer.class) + " [type|value|id]"));
                                                                                            return 1;
                                                                                        })
                                                                        )
                                                                        .executes(c -> {
                                                                            Route r = GoldenGlow.routeManager.getRoute(c.getArgument("routeName", String.class));
                                                                            List<TextComponentString> buttons = new ArrayList<>();
                                                                            int i = 0;
                                                                            for (Requirement requirement : r.requirements) {
                                                                                buttons.add(cuiButton(requirement.toString(), TextFormatting.LIGHT_PURPLE, r.displayName, ClickEvent.Action.RUN_COMMAND, "/routes edit " + r.unlocalizedName + " requirements " + i));
                                                                                i++;
                                                                            }
                                                                            buttons.add(cuiButton("New", TextFormatting.YELLOW, r.displayName, ClickEvent.Action.SUGGEST_COMMAND, "/routes edit " + r.unlocalizedName + " requirements new"));
                                                                            sendCuiMsg(c.getSource(), buttons);
                                                                            return 1;
                                                                        })
                                                        )
                                                        .executes(c -> {
                                                            Route r = GoldenGlow.routeManager.getRoute(c.getArgument("routeName", String.class));
                                                            List<TextComponentString> buttons = new ArrayList<>();
                                                            buttons.add(cuiButton("Display Name", TextFormatting.RED, r.displayName, ClickEvent.Action.SUGGEST_COMMAND, "/routes edit " + r.unlocalizedName + " display "));
                                                            buttons.add(cuiButton("Song", TextFormatting.GREEN, '"' + r.song + '"', ClickEvent.Action.SUGGEST_COMMAND, "/routes edit " + r.unlocalizedName + " song "));
                                                            buttons.add(cuiButton("Priority", TextFormatting.BLUE, String.valueOf(r.priority), ClickEvent.Action.SUGGEST_COMMAND, "/routes edit " + r.unlocalizedName + " priority "));
                                                            buttons.add(cuiButton("Requirements", TextFormatting.LIGHT_PURPLE, r.getRequirementHoverText(), ClickEvent.Action.RUN_COMMAND, "/routes edit " + r.unlocalizedName + " requirements"));
                                                            sendCuiMsg(c.getSource(), buttons);
                                                            return 1;
                                                        })
                                        )
                                        .executes(c -> {
                                            Route r = GoldenGlow.routeManager.getRoute((EntityPlayerMP) c.getSource().getCommandSenderEntity());
                                            if(r==null) {
                                                c.getSource().sendMessage(new TextComponentString("You're not in a route!"));
                                            } else {
                                                List<TextComponentString> buttons = new ArrayList<>();
                                                buttons.add(cuiButton("Display Name", TextFormatting.RED, r.displayName, ClickEvent.Action.SUGGEST_COMMAND, "/routes edit " + r.unlocalizedName + " display "));
                                                buttons.add(cuiButton("Song", TextFormatting.GREEN, '"' + r.song + '"', ClickEvent.Action.SUGGEST_COMMAND, "/routes edit " + r.unlocalizedName + " song "));
                                                buttons.add(cuiButton("Priority", TextFormatting.BLUE, String.valueOf(r.priority), ClickEvent.Action.SUGGEST_COMMAND, "/routes edit " + r.unlocalizedName + " priority "));
                                                buttons.add(cuiButton("Requirements", TextFormatting.LIGHT_PURPLE, r.getRequirementHoverText(), ClickEvent.Action.RUN_COMMAND, "/routes edit " + r.unlocalizedName + " requirements"));
                                                sendCuiMsg(c.getSource(), buttons);
                                            }
                                            return 1;
                                        })
                        )
                        .then(
                                literal("list")
                                        .executes(c -> {
                                            sendRouteList(c.getSource());
                                            return 1;
                                        })
                        )
                        .executes(c -> {
                            c.getSource().sendMessage(new TextComponentString("/routes [create|edit|list]"));
                            return 1;
                        })
        );
    }

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            String s = args.length>0 ? "routes " : "routes";
            GoldenGlow.commandDispatcher.execute(s+buildString(args, 0), sender);
        }
        catch (CommandSyntaxException e) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            e.printStackTrace();
        }
    }

    static void sendRouteList(ICommandSender sender) {
        StringBuilder s = new StringBuilder();
        for (Route route : GoldenGlow.routeManager.getRoutes()) {
            if (s.length() > 0)
                s.append(", ");
            s.append(route.unlocalizedName);
        }
        sender.sendMessage(new TextComponentString("Routes: " + s.toString()));
    }

    static TextComponentString cuiButton(String buttonText, TextFormatting color, String hoverText, ClickEvent.Action action, String value) {
        return cuiButton(buttonText, color, new TextComponentString(hoverText), action, value);
    }

    static TextComponentString cuiButton(String buttonText, TextFormatting color, TextComponentString hoverText, ClickEvent.Action action, String value) {
        TextComponentString button = new TextComponentString("["+buttonText+"] ");
        button.getStyle().setColor(color).setBold(true)
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText))
                .setClickEvent(new ClickEvent(action, value));
        return button;
    }

    static void sendCuiMsg(ICommandSender sender, List<TextComponentString> buttons) {
        TextComponentString lineBreak = (TextComponentString)new TextComponentString("============================================").setStyle(new Style().setColor(TextFormatting.GOLD).setBold(true));

        sender.sendMessage(lineBreak);

        TextComponentString msg = new TextComponentString("");
        for (TextComponentString btn : buttons) {
            msg.appendSibling(btn);
        }
        sender.sendMessage(msg);

        sender.sendMessage(lineBreak);
    }
}

package com.goldenglowspigot.common.chatChannels;

import com.coloredcarrot.jsonapi.impl.JsonClickEvent;
import com.coloredcarrot.jsonapi.impl.JsonColor;
import com.coloredcarrot.jsonapi.impl.JsonHoverEvent;
import com.coloredcarrot.jsonapi.impl.JsonMsg;
import net.minecraft.entity.player.EntityPlayerMP;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GlobalChannel implements Channel {
    private ArrayList<Player> players=new ArrayList<>();

    public boolean canSee(Player player){
        return true;
    }

    public ArrayList<Player> getPlayers(){
        return this.players;
    }

    public JsonMsg getPrefix(){
        JsonMsg prefix=new JsonMsg("[GLOBAL]", JsonColor.AQUA);
        prefix.hoverEvent(JsonHoverEvent.showText("Click to change the channel to Global"));
        prefix.clickEvent(JsonClickEvent.runCommand("/channel global"));
        return prefix.append(" ");
    }

    public static JsonMsg getPrefixStatic(){
        JsonMsg prefix=new JsonMsg("[GLOBAL]", JsonColor.AQUA);
        prefix.hoverEvent(JsonHoverEvent.showText("Click to change the channel to Global"));
        prefix.clickEvent(JsonClickEvent.runCommand("/channel global"));
        return prefix.append(" ");
    }
}

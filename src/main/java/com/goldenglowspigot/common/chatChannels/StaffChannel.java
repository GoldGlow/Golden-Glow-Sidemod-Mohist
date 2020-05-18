package com.goldenglowspigot.common.chatChannels;

import com.coloredcarrot.jsonapi.impl.JsonClickEvent;
import com.coloredcarrot.jsonapi.impl.JsonColor;
import com.coloredcarrot.jsonapi.impl.JsonHoverEvent;
import com.coloredcarrot.jsonapi.impl.JsonMsg;
import com.goldenglow.GoldenGlow;
import org.bukkit.entity.Player;
import red.mohist.api.PlayerAPI;

import java.util.ArrayList;

public class StaffChannel implements Channel {
    private ArrayList<Player> players;

    public StaffChannel(){
        this.players=new ArrayList<Player>();
    }

    public ArrayList<Player> getPlayers(){
        return this.players;
    }

    public boolean canSee(Player player){
        return GoldenGlow.permissionUtils.checkPermission(PlayerAPI.getNMSPlayer(player), "channel.staff");
    }

    public JsonMsg getPrefix(){
        JsonMsg prefix=new JsonMsg("[STAFF]", JsonColor.GOLD);
        prefix.hoverEvent(JsonHoverEvent.showText("Click to change the channel to Staff"));
        prefix.clickEvent(JsonClickEvent.runCommand("/channel staff"));
        return prefix.append(" ");
    }

    public static JsonMsg getPrefixStatic(Player player){
        if(GoldenGlow.permissionUtils.checkPermission(PlayerAPI.getNMSPlayer(player), "channel.staff")) {
            JsonMsg prefix = new JsonMsg("[STAFF]", JsonColor.GOLD);
            prefix.hoverEvent(JsonHoverEvent.showText("Click to change the channel to Staff"));
            prefix.clickEvent(JsonClickEvent.runCommand("/channel staff"));
            return prefix.append(" ");
        }
        else {
            return null;
        }
    }
}

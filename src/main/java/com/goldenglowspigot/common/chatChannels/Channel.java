package com.goldenglowspigot.common.chatChannels;

import com.coloredcarrot.jsonapi.impl.JsonMsg;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public interface Channel {
    public boolean canSee(Player player);
    public ArrayList<Player> getPlayers();
    public JsonMsg getPrefix();
}

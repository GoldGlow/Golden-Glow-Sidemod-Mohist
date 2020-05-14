package com.goldenglowspigot.common.chatChannels;

import com.coloredcarrot.jsonapi.impl.JsonClickEvent;
import com.coloredcarrot.jsonapi.impl.JsonColor;
import com.coloredcarrot.jsonapi.impl.JsonHoverEvent;
import com.coloredcarrot.jsonapi.impl.JsonMsg;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PrivateChannel implements Channel {
    private ArrayList<Player> players=new ArrayList<>();
    private Player[] eligiblePlayers;

    public PrivateChannel(Player[] players){
        this.eligiblePlayers=players;
    }

    public boolean canSee(Player player){
        for(Player eligiblePlayer:eligiblePlayers){
            if(player.equals(eligiblePlayer)){
                return true;
            }
        }
        return false;
    }

    public Player[] getEligiblePlayers() {
        return eligiblePlayers;
    }

    public ArrayList<Player> getPlayers(){
        return this.players;
    }

    public JsonMsg getPrefix(){
        return new JsonMsg("[PRIVATE]", JsonColor.DARK_GREEN);
    }

    public JsonMsg getPrefix(Player otherPlayer){
        JsonMsg prefix=this.getPrefix();
        prefix.hoverEvent(JsonHoverEvent.showText("Click to talk privately with "+otherPlayer.getPlayerListName()));
        prefix.clickEvent(JsonClickEvent.runCommand("/channel private "+otherPlayer.getName()));
        return prefix.append(" ");
    }
}

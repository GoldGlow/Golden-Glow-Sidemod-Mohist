package com.goldenglowspigot.common.chatChannels;

import com.goldenglowspigot.GoldenGlow;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ChannelsManager {
    private GlobalChannel globalChannel=new GlobalChannel();
    private ArrayList<Channel> channels=new ArrayList<>();

    public PrivateChannel checkOrAddPrivateChannel(Player[] players){
        PrivateChannel privateChannel=null;
        for(Channel channel: channels){
            if(channel instanceof PrivateChannel){
                boolean correctChannel=true;
                for(Player player: players){
                    if(!channel.canSee(player)){
                        correctChannel=false;
                        break;
                    }
                }
                if(correctChannel){
                    return (PrivateChannel) channel;
                }
            }
        }
        privateChannel=new PrivateChannel(players);
        this.channels.add(privateChannel);
        return privateChannel;
    }

    public void removeChannel(Player player){
        Channel oldChannel= GoldenGlow.channelsManager.getPlayerChannel(player);
        oldChannel.getPlayers().remove(player);
        if(!(oldChannel instanceof GlobalChannel)&&oldChannel.getPlayers().size()==0){
            this.channels.remove(oldChannel);
        }
    }

    public Channel getPlayerChannel(Player player){
        for(Channel channel: channels){
            if(channel.getPlayers().contains(player)){
                return channel;
            }
        }
        if(!globalChannel.getPlayers().contains(player)){
            globalChannel.getPlayers().add(player);
        }
        return globalChannel;
    }

    public void setPlayerChannel(Player player, EnumChannels channel){
        if(channel==EnumChannels.GLOBAL){
            Channel oldChannel= GoldenGlow.channelsManager.getPlayerChannel(player);
            if(!(oldChannel instanceof GlobalChannel)){
                GoldenGlow.channelsManager.removeChannel(player);
                globalChannel.getPlayers().add(player);
            }
        }
    }

    public void setPlayerChannel(Player changingPlayer, Player otherPlayer){
        Player[] players={changingPlayer, otherPlayer};
        PrivateChannel channel=GoldenGlow.channelsManager.checkOrAddPrivateChannel(players);
        GoldenGlow.channelsManager.removeChannel(changingPlayer);
        channel.getPlayers().add(changingPlayer);
    }

    public enum EnumChannels{
        GLOBAL,
        PRIVATE
    }
}

package com.goldenglow.common.inventory.BetterTrading;

import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class TradeManager {
    public List<Trade> activeTrades=new ArrayList<Trade>();

    public boolean alreadyTrading(EntityPlayerMP player){
        for(Trade trade: this.activeTrades){
            for(EntityPlayerMP playerMP:trade.getPlayers()){
                if(playerMP.getName().equals(player.getName())){
                    return true;
                }
            }
        }
        return false;
    }

    public Trade getPlayerTrade(EntityPlayerMP player){
        for(Trade trade: this.activeTrades){
            for(EntityPlayerMP playerMP:trade.getPlayers()){
                if(playerMP.getName().equals(player.getName())){
                    return trade;
                }
            }
        }
        return null;
    }

    public void cancelTrade(EntityPlayerMP player){
        Trade trade=this.getPlayerTrade(player);
        for(int i=0;i<trade.getPlayers().length;i++){
            EntityPlayerMP playerMP=trade.getPlayers()[i];
            TradingOffer offer=trade.getOffers()[i];
            PlayerPartyStorage storage= Pixelmon.storageManager.getParty(playerMP);
            for (ItemStack itemStack : offer.items) {
                ItemHandlerHelper.giveItemToPlayer(playerMP, itemStack);
            }
            storage.changeMoney(offer.money);
            for(Pokemon pokemon:offer.pokemonList){
                storage.add(pokemon);
            }
            playerMP.closeScreen();
        }
        this.activeTrades.remove(trade);
    }

    public static void evolutionTest(EntityPlayerMP player){
        IPlayerData playerData= player.getCapability(OOPlayerProvider.OO_DATA, null);
        for(Pokemon pokemon:playerData.getWaitToEvolve()){
            EntityPixelmon pixelmon = pokemon.getOrSpawnPixelmon(player.world, (double)player.getPosition().getX(), (double)player.getPosition().getY(), (double)player.getPosition().getZ());
            pixelmon.testTradeEvolution(EnumSpecies.getFromNameAnyCase(pokemon.getSpecies().name));
            /*if(playerData.isEvolvingPokemon()){
                return;
            }*/
            //playerData.removePokemonWaiting(pokemon);
        }
    }
}

package com.goldenglow.common.inventory.BetterTrading;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.util.InventoryUtil;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.ItemHandlerHelper;

public class Trade {
    private EntityPlayerMP[] players;
    private TradingOffer[] offers;
    public EnumTradeStep step;

    public Trade(EntityPlayerMP player1, EntityPlayerMP player2){
        this.players=new EntityPlayerMP[]{player1, player2};
        this.offers=new TradingOffer[]{new TradingOffer(player1), new TradingOffer(player2)};
        this.step=EnumTradeStep.OFFER_MAKING;
    }

    public EntityPlayerMP[] getPlayers(){
        return this.players;
    }

    public TradingOffer[] getOffers(){
        return this.offers;
    }

    public void complete(){
        this.getPlayers()[0].closeScreen();
        this.getPlayers()[1].closeScreen();
        PlayerPartyStorage firstParty= Pixelmon.storageManager.getParty(this.getPlayers()[0]);
        PlayerPartyStorage secondParty=Pixelmon.storageManager.getParty(this.getPlayers()[1]);
        IPlayerData firstPlayerData=this.getPlayers()[0].getCapability(OOPlayerProvider.OO_DATA, null);
        IPlayerData secondPlayerData=this.getPlayers()[1].getCapability(OOPlayerProvider.OO_DATA, null);
        if(firstParty.getTeam().size()+this.getOffers()[1].pokemonList.size()>6){
            this.getPlayers()[0].sendMessage(new TextComponentString(this.players[0].getName()+" didn't have enough party space"));
            this.getPlayers()[1].sendMessage(new TextComponentString(this.players[0].getName()+" didn't have enough party space"));
            GoldenGlow.tradeManager.cancelTrade(this.getPlayers()[0]);
            return;
        }
        else if(secondParty.getTeam().size()+this.getOffers()[0].pokemonList.size()>6){
            this.getPlayers()[0].sendMessage(new TextComponentString(this.players[1].getName()+" didn't have enough party space"));
            this.getPlayers()[1].sendMessage(new TextComponentString(this.players[1].getName()+" didn't have enough party space"));
            GoldenGlow.tradeManager.cancelTrade(this.getPlayers()[0]);
            return;
        }
        if(this.getOffers()[1].items.size()>0) {
            if (!InventoryUtil.doesPlayerHaveSpace(this.getPlayers()[0], this.getOffers()[1].items)) {
                this.getPlayers()[0].sendMessage(new TextComponentString(this.players[0].getName() + " didn't have enough inventory space"));
                this.getPlayers()[1].sendMessage(new TextComponentString(this.players[0].getName() + " didn't have enough inventory space"));
                GoldenGlow.tradeManager.cancelTrade(this.getPlayers()[0]);
                return;
            }
        }
        if(this.getOffers()[0].items.size()>0) {
            if (!InventoryUtil.doesPlayerHaveSpace(this.getPlayers()[1], this.getOffers()[0].items)) {
                this.getPlayers()[0].sendMessage(new TextComponentString(this.players[1].getName() + " didn't have enough inventory space"));
                this.getPlayers()[1].sendMessage(new TextComponentString(this.players[1].getName() + " didn't have enough inventory space"));
                GoldenGlow.tradeManager.cancelTrade(this.getPlayers()[0]);
                return;
            }
        }
        for(Pokemon pokemon: this.getOffers()[0].pokemonList){
            //secondParty.add(pokemon);
            secondPlayerData.addPokemonWaiting(pokemon);
        }
        for(Pokemon pokemon: this.getOffers()[1].pokemonList){
            //firstParty.add(pokemon);
            firstPlayerData.addPokemonWaiting(pokemon);
        }
        for (ItemStack itemStack : this.getOffers()[0].items) {
            ItemHandlerHelper.giveItemToPlayer(this.getPlayers()[1], itemStack);
        }
        for (ItemStack itemStack : this.getOffers()[1].items) {
            ItemHandlerHelper.giveItemToPlayer(this.getPlayers()[0], itemStack);
        }
        Pixelmon.storageManager.getParty(this.getPlayers()[0]).changeMoney(this.offers[1].money);
        Pixelmon.storageManager.getParty(this.getPlayers()[1]).changeMoney(this.offers[0].money);
        TradeManager.evolutionTest(this.getPlayers()[0]);
        if(!this.getPlayers()[0].getName().equals(this.getPlayers()[1].getName()))
            TradeManager.evolutionTest(this.getPlayers()[1]);
        GoldenGlow.tradeManager.cancelTrade(this.getPlayers()[0]);
    }

    public enum EnumTradeStep{
        OFFER_MAKING,
        OFFER_CONFIRMATION
    }
}

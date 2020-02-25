package com.goldenglow.common.battles.npc;

import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityNPCInterface;

public class CustomNPCBattle extends BattleRules
{
    private Dialog winDiag;
    private Dialog loseDiag;
    private Dialog initDiag;
    private int remainingNPCPokemon;
    public static EntityPlayer player;
    private static EntityNPCInterface npc;

    public CustomNPCBattle(EntityNPCInterface npc, Dialog initDialog, Dialog winDialog, Dialog loseDialog) throws Exception
    {
        super();
        this.npc=npc;
        this.initDiag=initDialog;
        this.winDiag=winDialog;
        this.loseDiag=loseDialog;
    }

    public void addWinDialog(Dialog winDiag)
    {
        this.winDiag=winDiag;
    }

    public void addLoseDialog(Dialog loseDiag)
    {
        this.loseDiag=loseDiag;
    }

    public Dialog getWinDialog()
    {
        return this.winDiag;
    }

    public Dialog getLoseDialog()
    {
        return this.loseDiag;
    }

    public Dialog getInitDiag() {return this.initDiag;}

    public EntityNPCInterface getNpc()
    {
        return this.npc;
    }

    public int getRemainingNPCPokemon(){return this.remainingNPCPokemon;}

    public void setRemainingNPCPokemon(int count){this.remainingNPCPokemon=count;}

    public boolean hasPlayer(EntityPlayer participant)
    {
        if(participant==player)
            return true;
        else
            return false;
    }
}
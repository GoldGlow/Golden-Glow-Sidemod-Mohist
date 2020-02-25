package com.goldenglow.common.inventory;

import com.goldenglow.common.data.player.OOPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.gyms.GymLeaderUtils;
import com.goldenglow.common.teams.DepositoryPokemon;
import com.goldenglow.common.util.PermissionUtils;
import com.goldenglow.common.util.Reference;
import com.goldenglow.common.util.Requirement;
import com.goldenglow.common.util.Scoreboards;
import com.goldenglow.common.util.scripting.OtherFunctions;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.comm.packetHandlers.OpenReplaceMoveScreen;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.command.ICommandManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.api.wrapper.PlayerWrapper;

/**
 * Created by JeanMarc on 6/19/2019.
 */
public class Action {
    public ActionType actionType;
    public String value;
    public Requirement[] requirements;
    public boolean closeInv;

    public Action(){
        this.actionType=ActionType.COMMAND;
        this.value="";
        this.requirements=new Requirement[0];
        this.closeInv=true;
    }

    public Action(ActionType type, String value) {
        this();
        this.actionType = type;
        this.value = value;
    }

    public ActionType getActionType(){
        return this.actionType;
    }

    public Action setActionType(ActionType actionType){
        this.actionType=actionType;
        return this;
    }

    public String getValue(){
        return this.value;
    }

    public Action setValue(String value){
        this.value=value;
        return this;
    }

    public Requirement[] getRequirements(){
        return this.requirements;
    }

    public void setRequirements(Requirement[] requirements){
        this.requirements=requirements;
    }

    public void doAction(EntityPlayerMP player){
        if(this.closeInv)
            player.closeScreen();
        if(this.actionType==ActionType.COMMAND){
            PlayerWrapper playerWrapper=new PlayerWrapper(player);
            String command=this.getValue().replace("@dp",player.getName());
            ICommandManager icommandmanager = player.getEntityWorld().getMinecraftServer().getCommandManager();
            icommandmanager.executeCommand(new RConConsoleSource(playerWrapper.getMCEntity().getEntityWorld().getMinecraftServer()), command);
        }
        else if(this.actionType==ActionType.GIVEITEM){
            try {
                player.inventory.addItemStackToInventory(new ItemStack(JsonToNBT.getTagFromJson(this.value)));
            } catch (NBTException e) {
                e.printStackTrace();
            }
        }
        else if(this.actionType==ActionType.CHANGESKIN){
            String[] words=this.value.split(" ");
            String name="";
            player.getHeldItemMainhand().setItemDamage(Integer.parseInt(words[0]));
            for(int i=1;i<words.length;i++){
                if(i>1){
                    name+=" ";
                }
                name+=words[i];
            }
            player.getHeldItemMainhand().setStackDisplayName(name);
        }
        else if(this.actionType==ActionType.OPEN_INV) {
            //player.closeScreen();
            CustomInventory.openInventory(this.value, player);
        }
        else if(this.actionType==ActionType.SEAL_SET) {
            OOPlayerData data = (OOPlayerData)player.getCapability(OOPlayerProvider.OO_DATA, null);
            String[] equippedSeals = data.getEquippedSeals();
            equippedSeals[Integer.parseInt(value.split(":")[1])] = value.split(":")[0];
            data.setPlayerSeals(equippedSeals);
            player.closeScreen();
            CustomInventory.openInventory("seals", player);
        }
        else if(this.actionType==ActionType.TM_PARTY){
            BagInventories.openTMMenu(player, value);
        }
        else if(this.actionType==ActionType.TEACH_MOVE){
            int slot=Integer.parseInt(this.value.split(":")[0]);
            Attack attack=new Attack(this.value.split(":")[1]);
            PlayerPartyStorage partyStorage = Pixelmon.storageManager.getParty(player);
            Pokemon pokemon=partyStorage.get(slot);
            if (!pokemon.getMoveset().hasAttack(attack)) {
                if (pokemon.getMoveset().size() >= 4) {
                    player.closeScreen();
                    Pixelmon.network.sendTo(new OpenReplaceMoveScreen(pokemon.getUUID(), attack.getActualMove().getAttackId()), player);
                } else {
                    player.closeScreen();
                    pokemon.getMoveset().add(attack);
                    player.sendMessage(new TextComponentString(pokemon.getDisplayName()+" successfully learned the move "+attack.getActualMove().getAttackName()));
                }
            } else {
                player.closeScreen();
                player.sendMessage(new TextComponentString(Reference.red+"Already knows the move!"));
            }
        }
        else if(this.actionType==ActionType.CHANGE_TITLE){
            PermissionUtils.unsetPermissionsWithStart(player, "prefix.3.");
            PermissionUtils.setPrefix(player, this.value);
        }
        else if(this.actionType==ActionType.SCOREBOARD_TYPE){
            OOPlayerData data = (OOPlayerData)player.getCapability(OOPlayerProvider.OO_DATA, null);
            data.setScoreboardType(Scoreboards.EnumScoreboardType.valueOf(this.value));
        }
        else if(this.actionType==ActionType.CLOSE_GYM){
            GymLeaderUtils.closeGym(this.value);
        }
        else if(this.actionType==ActionType.OPEN_GYM){
            GymLeaderUtils.openGym(this.value);
        }
        else if(this.actionType==ActionType.TAKE_CHALLENGERS){
            GymLeaderUtils.takeChallengers(this.value, player);
        }
        else if(this.actionType==ActionType.STOP_CHALLENGERS){
            GymLeaderUtils.stopTakingChallengers(this.value, player);
        }
        else if(this.actionType==ActionType.NEXT_CHALLENGER){
            GymLeaderUtils.nextInQueue(this.value, player);
        }
        else if(this.actionType==ActionType.START_BATTLE){
            GymLeaderUtils.startGymBattle(this.value);
        }
        else if(this.actionType==ActionType.DEPOSITORY_POKEMON){
            String[] args=this.value.split(" ");
            if(EnumSpecies.hasPokemonAnyCase(args[0])){
                PokemonSpec pokemonSpec= PokemonSpec.from(args[0]);
                if(args.length==2){
                    int form=Integer.parseInt(args[1]);
                    int formIndex=0;
                    while(formIndex<form){
                        pokemonSpec.form++;
                        formIndex++;
                    }
                }
                Pokemon pokemon=DepositoryPokemon.generateDepositoryPokemon(pokemonSpec);
                Pixelmon.storageManager.getParty(player).add(pokemon);
            }
        }
        else if(this.actionType==ActionType.EQUIP_ARMOR){
            OtherFunctions.equipArmor(player, Integer.parseInt(this.value.split("@")[0]), this.value.split("@")[1]);
        }
        else if(this.actionType==ActionType.SET_FRIEND_VIEW){
            if(this.value.equalsIgnoreCase("true")){
                ((OOPlayerData)player.getCapability(OOPlayerProvider.OO_DATA, null)).setPlayerVisibility(true);
            }
            else{
                ((OOPlayerData)player.getCapability(OOPlayerProvider.OO_DATA, null)).setPlayerVisibility(false);
            }
        }
    }

    public enum ActionType{
        COMMAND,
        GIVEITEM,
        CHANGESKIN,
        OPEN_INV,
        SEAL_SET,
        TM_PARTY,
        TEACH_MOVE,
        CHANGE_TITLE,
        SCOREBOARD_TYPE,
        CLOSE_GYM,
        OPEN_GYM,
        TAKE_CHALLENGERS,
        STOP_CHALLENGERS,
        NEXT_CHALLENGER,
        START_BATTLE,
        DEPOSITORY_POKEMON,
        EQUIP_ARMOR,
        SET_FRIEND_VIEW
    }
}

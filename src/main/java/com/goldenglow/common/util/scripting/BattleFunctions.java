package com.goldenglow.common.util.scripting;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.battles.npc.CustomBattleHandler;
import com.goldenglow.common.battles.npc.CustomNPCBattle;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.handlers.TickHandler;
import com.goldenglow.common.music.SongManager;
import com.goldenglow.common.routes.SpawnPokemon;
import com.goldenglow.common.util.PermissionUtils;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.comm.packetHandlers.customOverlays.CustomNoticePacket;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityNPCInterface;

public class BattleFunctions {
    //used to do Blue vs May battle sequence, might be used again later
    public static void createNPCBattle(NPCWrapper firstNPC, String firstTeamName, NPCWrapper secondNPC, String secondTeamName){
        EntityNPCInterface firstNpc=(EntityNPCInterface) firstNPC.getMCEntity();
        EntityNPCInterface secondNpc=(EntityNPCInterface) secondNPC.getMCEntity();
        CustomBattleHandler.createCustomNPCBattle(firstNpc, firstTeamName, secondNpc, secondTeamName);
    }

    //Used for Trainer Battles, doesn't include LoS
    public static void createCustomBattle(PlayerWrapper playerWrapper, String teamName, int initDialogID, int winDialogID, int loseDialogID, NPCWrapper npcWrapper) {
        EntityNPCInterface npc=(EntityNPCInterface) npcWrapper.getMCEntity();
        EntityPlayerMP player=(EntityPlayerMP)playerWrapper.getMCEntity();
        if(PermissionUtils.checkPermission(player,"hard")){
            teamName+="-hard";
        }
        CustomBattleHandler.createCustomBattle(player, teamName, initDialogID, winDialogID, loseDialogID, npc);
    }

    // Line of sight code, used for sneaking portions and trainers
    public static void registerLOSBattle(NPCWrapper npc, int initDialogID) {
        TickHandler.battleNPCs.put(npc, initDialogID);
    }

    //code to start wild battles, currently used for apricorns
    public static void startWildBattle(PlayerWrapper player, SpawnPokemon pokemon){
        EntityPlayerMP playerMP=(EntityPlayerMP)player.getMCEntity();
        PokemonSpec pokemonSpec=PokemonSpec.from(pokemon.species);
        pokemonSpec.form=pokemon.form;
        pokemonSpec.level= RandomHelper.getRandomNumberBetween(pokemon.minLvl, pokemon.maxLvl);
        Pokemon wildPokemon=pokemonSpec.create();
        EntityPixelmon pixelmon=new EntityPixelmon(playerMP.world);
        pixelmon.setPokemon(wildPokemon);
        pixelmon.setPosition(playerMP.posX, playerMP.posY, playerMP.posZ);
        pixelmon.setSpawnLocation(pixelmon.getDefaultSpawnLocation());
        BattleRegistry.startBattle(new PlayerParticipant(playerMP, Pixelmon.storageManager.getParty(playerMP).getAndSendOutFirstAblePokemon(playerMP)), new WildPixelmonParticipant(pixelmon));
    }

    //Used to set a dialog as a battle start dialog, sets the song to the encounter theme
    public static void battleInitDialog(PlayerWrapper player, NPCWrapper npc, int dialogId){
        NBTTagCompound data=npc.getMCEntity().getEntityData();
        if(data.hasKey("encounterTheme")){
            SongManager.setCurrentSong((EntityPlayerMP) player.getMCEntity(), data.getString("encounterTheme"));
        }
        else{
            SongManager.setCurrentSong((EntityPlayerMP) player.getMCEntity(), GoldenGlow.songManager.encounterDefault);
        }
        NoppesUtilServer.openDialog((EntityPlayerMP) player.getMCEntity(), (EntityNPCInterface)npc.getMCEntity(), (Dialog) DialogController.instance.get(dialogId));
    }

    public static void battleDialog(PlayerWrapper player, NPCWrapper npc, String[] lines, int time) {
        ItemStack stack = OtherFunctions.getNPCDialogItem(npc);
        Pixelmon.network.sendTo(new CustomNoticePacket().setEnabled(true)
                .setLines(lines)
                .setItemStack(stack, EnumOverlayLayout.LEFT), (EntityPlayerMP)player.getMCEntity());
        player.getMCEntity().getCapability(OOPlayerProvider.OO_DATA, null).setDialogTicks(time);
    }

    public static boolean pokemonKOd(BattleControllerBase bcb){
        if(bcb.rules instanceof CustomNPCBattle){
            if(((CustomNPCBattle) bcb.rules).getRemainingNPCPokemon()!=bcb.participants.get(1).countAblePokemon()){
                ((CustomNPCBattle) bcb.rules).setRemainingNPCPokemon(bcb.participants.get(1).countAblePokemon());
                return true;
            }
        }
        return false;
    }

    public static int getRemainingPokemon(BattleControllerBase bcb){
        return bcb.participants.get(1).countAblePokemon();
    }

}

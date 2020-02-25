package com.goldenglow.common.music;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.util.Reference;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.SoundCategory;
import noppes.npcs.Server;
import noppes.npcs.constants.EnumPacketClient;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JeanMarc on 5/9/2019.
 */
public class SongManager {

    static File config = new File(Reference.configDir, "songs/config.cfg");
    public String wildDefault,trainerDefault,encounterDefault,victoryDefault,evolutionDefault,levelUpDefault,shinyDefault;
    //Key: UUID
    //Value: song name
    public HashMap<String, String> uniqueSongs;

    public void init() {
        GoldenGlow.logger.info("Loading Song Config...");
        try {
            if(!config.createNewFile()) {
                BufferedReader reader = new BufferedReader(new FileReader(config));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("wildBattleSong="))
                        this.wildDefault = line.replace("wildBattleSong=", "").replace(" ", "");
                    if (line.startsWith("trainerBattleSong="))
                        this.trainerDefault = line.replace("trainerBattleSong=", "").replace(" ", "");
                    if (line.startsWith("encounterSong="))
                        this.encounterDefault = line.replace("encounterSong=", "").replace(" ", "");
                    if (line.startsWith("victorySong="))
                        this.victoryDefault = line.replace("victorySong=", "").replace(" ", "");
                    if (line.startsWith("evolutionSong="))
                        this.evolutionDefault = line.replace("evolutionSong=", "").replace(" ", "");
                    if (line.startsWith("levelUpSound="))
                        this.levelUpDefault = line.replace("levelUpSound=", "").replace(" ", "");
                    if (line.startsWith("shinySound="))
                        this.shinyDefault = line.replace("shinySound=", "").replace(" ", "");
                }
            } else {
                FileOutputStream os = new FileOutputStream(config);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                this.wildDefault="obscureobsidian:wild.lgpe";
                writer.write("wildBattleSong="+wildDefault);
                writer.newLine();
                this.trainerDefault="obscureobsidian:trainer.lgpe";
                writer.write("trainerBattleSong="+trainerDefault);
                writer.newLine();
                this.encounterDefault="obscureobsidian:encounter.rival_lgpe";
                writer.write("encounterSong="+encounterDefault);
                writer.newLine();
                this.victoryDefault="obscureobsidian:victory.lgpe";
                writer.write("victorySong="+victoryDefault);
                writer.newLine();
                this.evolutionDefault="obscureobsidian:sound.evolution";
                writer.write("evolutionSong="+evolutionDefault);
                writer.newLine();
                this.levelUpDefault="obscureobsidian:sound.level_up";
                writer.write("levelUpSound="+levelUpDefault);
                writer.newLine();
                this.shinyDefault="obscureobsidian:sound.shiny";
                writer.write("shinySound="+shinyDefault);
                writer.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void playSound(EntityPlayerMP player, String source, String path) {
        player.connection.sendPacket(new SPacketCustomSound(path, SoundCategory.getByName(source), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), 1000, 1));
    }

    public static void setCurrentSong(EntityPlayerMP player, String newSong){
        player.getCapability(OOPlayerProvider.OO_DATA, null).setSong(newSong);
        Server.sendData(player, EnumPacketClient.PLAY_MUSIC, newSong);
    }

    public static void setRouteSong(EntityPlayerMP player) {
        if(player.getCapability(OOPlayerProvider.OO_DATA, null).getRoute()!=null)
            setCurrentSong(player, player.getCapability(OOPlayerProvider.OO_DATA, null).getRoute().song);
        else
            setCurrentSong(player, "null");
    }

    public static void setToTrainerMusic(EntityPlayerMP player){
        setCurrentSong(player, player.getCapability(OOPlayerProvider.OO_DATA, null).getTrainerTheme());
    }

    public static void setToWildMusic(EntityPlayerMP player){
        setCurrentSong(player, player.getCapability(OOPlayerProvider.OO_DATA, null).getWildTheme());
    }

    public static void setToPvpMusic(EntityPlayerMP player, BattleParticipant[] opponents){
        IPlayerData playerData = player.getCapability(OOPlayerProvider.OO_DATA, null);
        switch (playerData.getPvpThemeOption()){
            //Always use the opponent's battle theme
            case 0:
                SongManager.setCurrentSong(player, firstOpponentTheme(opponents));
                return;
            //Use the opponent's when he doesn't have the default theme
            case 1:
                ArrayList<String> themes=getOpponentsThemes(opponents);
                for(String theme:themes){
                    if(!theme.equals(GoldenGlow.songManager.trainerDefault)){
                        setCurrentSong(player, theme);
                        return;
                    }
                }
                setCurrentSong(player, playerData.getPVPTheme());
                return;
            //Use the opponent's if he has a unique theme
            case 2:
                ArrayList<String> opponentsThemes=getOpponentsThemes(opponents);
                for(String theme:opponentsThemes){
                    if(GoldenGlow.songManager.uniqueSongs.containsValue(theme)){
                        setCurrentSong(player, theme);
                        return;
                    }
                }
                setCurrentSong(player, playerData.getPVPTheme());
                return;
            //Use the player's theme every time
            case 3:
                setCurrentSong(player, playerData.getPVPTheme());
                return;
        }
    }

    public static ArrayList<String> getOpponentsThemes(BattleParticipant[] opponents){
        ArrayList<String> opponentPvpThemes=new ArrayList<String>();
        for(BattleParticipant opponent: opponents){
            if(opponent instanceof PlayerParticipant){
                opponentPvpThemes.add(((PlayerParticipant) opponent).player.getCapability(OOPlayerProvider.OO_DATA, null).getPVPTheme());
            }
        }
        return opponentPvpThemes;
    }

    public static String firstOpponentTheme(BattleParticipant[] opponents){
        for(BattleParticipant opponent: opponents){
            if(opponent instanceof PlayerParticipant){
                return  ((PlayerParticipant) opponent).player.getCapability(OOPlayerProvider.OO_DATA, null).getPVPTheme();
            }
        }
        return "";
    }
}

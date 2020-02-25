package com.goldenglow.common.data.player;

        import com.goldenglow.common.routes.Route;
        import com.goldenglow.common.seals.Seal;
        import com.goldenglow.common.util.FullPos;
        import com.goldenglow.common.util.Scoreboards;
        import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
        import com.pixelmonmod.pixelmon.enums.EnumSpecies;
        import net.minecraft.item.Item;
        import net.minecraft.item.ItemStack;

        import java.time.Instant;
        import java.util.List;
        import java.util.UUID;

public interface IPlayerData {

    Route getRoute();
    boolean hasRoute();
    void setRoute(Route routeName);
    void clearRoute();

    Route getSafezone();
    void setSafezone(String safezoneName);
    FullPos getBackupFullpos();
    void setBackupFullpos(FullPos pos);

    String getCurrentSong();
    void setSong(String song);

    String getWildTheme();
    void setWildTheme(String newTheme);
    String getTrainerTheme();
    void setTrainerTheme(String newTheme);
    String getPVPTheme();
    void setPVPTheme(String newTheme);
    int getPvpThemeOption();
    void setPvpThemeOption(int option);

    int getNotificationScheme();
    void setNotificationScheme(int id);

    Scoreboards.EnumScoreboardType getScoreboardType();
    void setScoreboardType(Scoreboards.EnumScoreboardType scoreboardType);

    String[] getEquippedSeals();
    List<String> getUnlockedSeals();
    void unlockSeal(String name);
    void setPlayerSeals(String[] seals);

    List<ItemStack> getKeyItems();
    void addKeyItem(ItemStack item);
    void removeKeyItem(String displayName);
    void removeKeyItem(ItemStack item);

    List<ItemStack> getTMs();
    boolean unlockTM(ItemStack tm);

    List<ItemStack> getAWItems();
    void addAWItem(ItemStack item);

    int getCaptureChain();
    int increaseCaptureChain(int i);
    void setCaptureChain(int i);
    EnumSpecies getChainSpecies();
    void setChainSpecies(EnumSpecies species);
    int getKOChain();
    EnumSpecies getLastKOPokemon();
    int increaseKOChain(int i);
    void setKOChain(int i);
    void setLastKOPokemon(EnumSpecies species);

     List<UUID> getFriendRequests();
     List<UUID> getFriendList();
    void addFriendRequest(UUID player);
    void acceptFriendRequest(UUID player);
    void denyFriendRequest(UUID player);
    void addFriend(UUID player);
    void removeFriend(UUID player);
    boolean getPlayerVisibility();
    void setPlayerVisibility(boolean onlySeesFriends);

    List<Pokemon> getWaitToEvolve();
    Pokemon getPokemonWaiting(int index);
    void removePokemonWaiting(int index);
    void removePokemonWaiting(Pokemon pokemon);
    void addPokemonWaiting(Pokemon pokemon);
    boolean isEvolvingPokemon();
    void setEvolvingPokemon(boolean evolvingPokemon);

    void setLoginTime(Instant loginTime);
    Instant getLoginTime();

    void setDialogTicks(int ticks);
    int getDialogTicks();

    String getShopName();
    void setShopName(String name);
}

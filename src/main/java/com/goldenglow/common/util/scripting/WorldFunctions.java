package com.goldenglow.common.util.scripting;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.data.player.IPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.routes.Route;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.wrapper.BlockScriptedWrapper;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.WorldWrapper;

public class WorldFunctions {
    private static long lastDailyRefresh = 0;

    //Whiting out
    public static void warpToSafeZone(PlayerWrapper playerWrapper){
        IPlayerData playerData = playerWrapper.getMCEntity().getCapability(OOPlayerProvider.OO_DATA, null);
        playerData.getSafezone().warp((EntityPlayerMP)playerWrapper.getMCEntity());
        Pixelmon.storageManager.getParty((EntityPlayerMP)playerWrapper.getMCEntity()).getTeam().forEach(Pokemon::heal);
        playerWrapper.message("You whited out!");
    }

    //World script
    public static void checkRoute(EntityPlayerMP playerMP, int lastPosX, int lastPosY, int lastPosZ) {
        IPlayerData playerData = playerMP.getCapability(OOPlayerProvider.OO_DATA, null);
        Route currentRoute = null;
        Route actualRoute = GoldenGlow.routeManager.getRoute(playerMP);

        if(playerData.hasRoute())
            currentRoute = playerData.getRoute();

        if(actualRoute!=null) {
            //If entering Different Route
            if(currentRoute==null || !currentRoute.unlocalizedName.equalsIgnoreCase(actualRoute.unlocalizedName)) {

                //Check if player is restricted from entering
                if (!actualRoute.canPlayerEnter(playerMP)) {
                    if(actualRoute.kickWarp) {
                        playerMP.setPositionAndUpdate(actualRoute.kickWarpX, actualRoute.kickWarpY, actualRoute.kickWarpZ);
                        playerMP.sendMessage(new TextComponentString("You were kicked out of the zone!"));
                    } else {
                        playerMP.setPositionAndUpdate(lastPosX, lastPosY, lastPosZ);
                        playerMP.sendMessage(actualRoute.getRequirementMessage(playerMP));
                    }
                }
                //If player is allowed to enter
                else {
                    //Clear old route if it exists
                    if (currentRoute != null)
                        currentRoute.removePlayer(playerMP);

                    actualRoute.addPlayer(playerMP); //Add player to new route

                    //If route has a safeZone, set player's last safeZone to this Route's safeZone
                    if (actualRoute.isSafeZone)
                        playerData.setSafezone(actualRoute.unlocalizedName);
                }
            }
        }
        else {
            playerData.setRoute(null);
            /* Ensure players are always in a route. (Stop players leaving the map)
            playerMP.setPositionAndUpdate(lastPosX, lastPosY, lastPosZ);
            playerMP.sendMessage(new TextComponentString("You can't go this way!").setStyle(new Style().setBold(true)));
	         */
        }
    }

    public static int getCurrentDay(WorldWrapper world) {
        return (int)(world.getTime() / 24000L % 2147483647L);
    }

    @Deprecated //Replaced by DailyFunctions.getLastEarthDay
    public static boolean newRLDay(WorldWrapper world) {
        IData worldData = world.getStoreddata();
        if(lastDailyRefresh==0) {
            if(worldData.has("dailyRefresh")) {
                lastDailyRefresh = (long)worldData.get("dailyRefresh");
            }
            else {
                lastDailyRefresh = System.currentTimeMillis();
                worldData.put("dailyRefresh", lastDailyRefresh);
            }
        }
        if(System.currentTimeMillis() - lastDailyRefresh >= 86400000L) {
            lastDailyRefresh = System.currentTimeMillis();
            worldData.put("dailyRefresh", lastDailyRefresh);
            return true;
        }
        return false;
    }

    public static boolean hasWaitedForDay(PlayerWrapper player, BlockScriptedWrapper scriptedBlock){
        if(scriptedBlock.getMCTileEntity().getTileData().hasKey(player.getUUID())) {
            if (scriptedBlock.getMCTileEntity().getTileData().getInteger(player.getUUID()) == getCurrentDay((WorldWrapper)player.getWorld())){
                return false;
            }
        }
        scriptedBlock.getMCTileEntity().getTileData().setInteger(player.getUUID(), getCurrentDay((WorldWrapper)player.getWorld()));
        return true;
    }

    public static void setDayChallengedNPC(PlayerWrapper player, NPCWrapper npc){
        npc.getMCEntity().getEntityData().setLong(player.getUUID(), getLastDailyRefresh());
    }

    public static boolean isDifferentIRLDay(PlayerWrapper player, BlockScriptedWrapper scriptedBlock){
        if(scriptedBlock.getMCTileEntity().getTileData().hasKey(player.getUUID())) {
            if (scriptedBlock.getMCTileEntity().getTileData().getLong(player.getUUID()) == lastDailyRefresh){
                return false;
            }
        }
        scriptedBlock.getMCTileEntity().getTileData().setLong(player.getUUID(), lastDailyRefresh);
        return true;
    }

    public static boolean isDifferentIRLDay(PlayerWrapper player, NPCWrapper npcWrapper){
        if(npcWrapper.getEntityNbt().getMCNBT().hasKey(player.getUUID())) {
            if (npcWrapper.getEntityNbt().getMCNBT().getLong(player.getUUID()) == lastDailyRefresh){
                return false;
            }
        }
        npcWrapper.getEntityNbt().getMCNBT().setLong(player.getUUID(), lastDailyRefresh);
        return true;
    }

    public static long getLastDailyRefresh(){return lastDailyRefresh;}
}

package com.goldenglow.common.handlers.events;

import com.goldenglow.GoldenGlow;
import com.goldenglow.common.battles.raids.RaidBattleRules;
import com.goldenglow.common.data.player.OOPlayerData;
import com.goldenglow.common.data.player.OOPlayerProvider;
import com.goldenglow.common.music.SongManager;
import com.goldenglow.common.util.PixelmonBattleUtils;
import com.goldenglow.common.util.ReflectionHelper;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.events.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.DropEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnActionPokemon;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.client.particle.ParticleSystems;
import com.pixelmonmod.pixelmon.comm.ChatHandler;
import com.pixelmonmod.pixelmon.comm.packetHandlers.PlayParticleSystem;
import com.pixelmonmod.pixelmon.comm.packetHandlers.itemDrops.ItemDropMode;
import com.pixelmonmod.pixelmon.comm.packetHandlers.itemDrops.ItemDropPacket;
import com.pixelmonmod.pixelmon.entities.npcs.registry.DropItemRegistry;
import com.pixelmonmod.pixelmon.entities.npcs.registry.PokemonDropInformation;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemQuery;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DropItemQueryList;
import com.pixelmonmod.pixelmon.entities.pixelmon.drops.DroppedItem;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Random;

public class HuntHandler {

    @SubscribeEvent
    public static void onPixelmonSpawnEvent(SpawnEvent event) {
        if(event.action instanceof SpawnActionPokemon) {
            SpawnActionPokemon action = (SpawnActionPokemon)event.action;
            action.usingSpec.shiny = false;
        }
    }

    @SubscribeEvent
    public static void battleStartedEvent(BattleStartedEvent event) {
        if(event.participant2[0] instanceof WildPixelmonParticipant && event.participant1[0] instanceof PlayerParticipant) {
            Random r = new Random();
            Pokemon wild = ((WildPixelmonParticipant)event.participant2[0]).allPokemon[0].pokemon;
            EntityPlayerMP player = (EntityPlayerMP)event.participant1[0].getEntity();
            int catchChain = player.getCapability(OOPlayerProvider.OO_DATA, null).getCaptureChain();

            int shinyChance = Math.max(4096-((catchChain/5)*716), 516);
            boolean shiny = r.nextInt(shinyChance) == 0;

            if(shiny) {
                wild.setShiny(true);
                player.sendMessage(new TextComponentString("What!? It's a Shiny!"));
                SongManager.playSound(player, "neutral", GoldenGlow.songManager.levelUpDefault);
            }

            int koChain = player.getCapability(OOPlayerProvider.OO_DATA, null).getKOChain();
            int abilityChance = Math.max(150 - ((koChain/5) * 20), 50);
            boolean hiddenAbility = r.nextInt(abilityChance) == 0;
            if(hiddenAbility) {
                BaseStats stats = BaseStats.allBaseStats.get(wild.getSpecies());
                if(stats.abilities[2]!=null)
                    wild.setAbility(stats.abilities[2]);
            }
        }
    }

    @SubscribeEvent
    public static void startCaptureEvent(CaptureEvent.StartCapture event) {
    }

    @SubscribeEvent
    public static void successfulCaptureEvent(CaptureEvent.SuccessfulCapture event) {
        OOPlayerData data = (OOPlayerData)event.player.getCapability(OOPlayerProvider.OO_DATA, null);
        if(data.getChainSpecies() != event.getPokemon().getSpecies()) {
            data.setChainSpecies(event.getPokemon().getSpecies());
            data.setCaptureChain(1);
        }
        else {
            int chain = data.increaseCaptureChain(1);
            if(chain % 5 == 0)
                event.player.sendStatusMessage(new TextComponentString(chain+" Catch Combo!"), true);
        }
    }

    @SubscribeEvent
    public static void failedCaptureEvent(CaptureEvent.FailedCapture event) {
    }

    @SubscribeEvent
    public static void beatWildPixelmonEvent(BeatWildPixelmonEvent event) {
        OOPlayerData data = (OOPlayerData)event.player.getCapability(OOPlayerProvider.OO_DATA, null);
        if(data.getLastKOPokemon() != event.wpp.allPokemon[0].getSpecies()) {
            data.setLastKOPokemon(event.wpp.allPokemon[0].getSpecies());
            data.setKOChain(1);
        }
        else {
            int chain = data.increaseKOChain(1);
            if(chain % 5 == 0)
                event.player.sendStatusMessage(new TextComponentString(chain+" KO Combo!"), true);
        }
    }

    @SubscribeEvent
    public static void wildDropEvent(DropEvent event) {
        if (event.isPokemon()) {
            OOPlayerData data = (OOPlayerData) event.player.getCapability(OOPlayerProvider.OO_DATA, null);
            if (data.getChainSpecies() == data.getLastKOPokemon()) {
                event.setCanceled(true);
                int total = data.getCaptureChain() + data.getKOChain();
                for (PokemonDropInformation info : DropItemRegistry.pokemonDrops.get(((EntityPixelmon) event.entity).getSpecies())) {
                    try {
                        ArrayList<DroppedItem> drops = new ArrayList();
                        int numDrops;
                        int i;
                        int id = 0;
                        ItemStack stack;
                        if (ReflectionHelper.getPrivateValue(info, "mainDrop") != null) {
                            numDrops = RandomHelper.getRandomNumberBetween(ReflectionHelper.getPrivateValue(info, "mainDropMin"), ReflectionHelper.getPrivateValue(info, "mainDropMax"));
                            stack = ((ItemStack)ReflectionHelper.getPrivateValue(info, "mainDrop")).copy();
                            stack.setCount(numDrops * Math.min((total / 10) + 1, 3));
                            drops.add(new DroppedItem(stack, id++));
                        }
                        if (ReflectionHelper.getPrivateValue(info, "optDrop1") != null) {
                            numDrops = RandomHelper.getRandomNumberBetween(ReflectionHelper.getPrivateValue(info, "optDrop1Min"), ReflectionHelper.getPrivateValue(info, "optDrop1Max"));
                            stack = ((ItemStack)ReflectionHelper.getPrivateValue(info, "optDrop1")).copy();
                            stack.setCount(numDrops * Math.min((total / 10) + 1, 3));
                            drops.add(new DroppedItem(stack, id++));
                        }
                        if (ReflectionHelper.getPrivateValue(info, "optDrop2") != null) {
                            numDrops = RandomHelper.getRandomNumberBetween(ReflectionHelper.getPrivateValue(info, "optDrop2Min"), ReflectionHelper.getPrivateValue(info, "optDrop2Max"));
                            stack = ((ItemStack)ReflectionHelper.getPrivateValue(info, "optDrop2")).copy();
                            stack.setCount(numDrops * Math.min((total / 10) + 1, 3));
                            drops.add(new DroppedItem(stack, id++));
                        }
                        if (ReflectionHelper.getPrivateValue(info, "rareDrop") != null && RandomHelper.getRandomChance(0.1F + (Math.min(total / 5, 5) * 0.05F))) {
                            numDrops = RandomHelper.getRandomNumberBetween(ReflectionHelper.getPrivateValue(info, "rareDropMin"), ReflectionHelper.getPrivateValue(info, "rareDropMax"));
                            for (i = 0; i < numDrops; ++i) {
                                drops.add(new DroppedItem(((ItemStack)ReflectionHelper.getPrivateValue(info, "rareDrop")).copy(), id++));
                            }
                        }
                        DropItemQuery diq = new DropItemQuery(new Vec3d(event.entity.posX, event.entity.posY, event.entity.posZ), event.player.getUniqueID(), drops);
                        DropItemQueryList.queryList.add(diq);
                        Pixelmon.network.sendTo(new ItemDropPacket(ItemDropMode.NormalPokemon, ChatHandler.getMessage("gui.guiItemDrops.beatPokemon", ((EntityPixelmon) event.entity).getNickname()), drops), event.player);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBattleEnd(BattleEndEvent event) {
        if(!(event.bc.rules instanceof RaidBattleRules) && PixelmonBattleUtils.isWildBattle(event.bc.participants.toArray(new BattleParticipant[0])) && event.cause == EnumBattleEndCause.FLEE) {
            OOPlayerData data = (OOPlayerData) event.getPlayers().get(0).getCapability(OOPlayerProvider.OO_DATA, null);
            if (event.bc.participants.get(1).getEntity() instanceof EntityPixelmon) {
                EntityPixelmon e = (EntityPixelmon)event.bc.participants.get(1).getEntity();
                if(e.getSpecies()==data.getLastKOPokemon()) {
                    data.setLastKOPokemon(null);
                    data.setKOChain(0);
                }
                if(e.getSpecies()==data.getChainSpecies()) {
                    data.setChainSpecies(null);
                    data.setCaptureChain(0);
                }
                Pixelmon.network.sendToDimension(new PlayParticleSystem(ParticleSystems.BLOOM, e.posX, e.posY, e.posZ, e.dimension, e.getPixelmonScale(), e.getPokemonData().isShiny(), new double[0]), e.dimension);
                e.setDead();
            }
        }
    }
}

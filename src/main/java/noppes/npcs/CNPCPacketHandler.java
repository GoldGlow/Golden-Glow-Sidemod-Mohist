package noppes.npcs;

import com.goldenglow.common.tiles.ICustomScript;
import com.goldenglow.common.util.GGLogger;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import noppes.npcs.*;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.IPermission;

import java.lang.reflect.Method;

public class CNPCPacketHandler extends PacketHandlerServer {

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        EntityPlayerMP player = ((NetHandlerPlayServer)event.getHandler()).player;
        if (CustomNpcs.OpsOnly && !NoppesUtilServer.isOp(player)) {
            GGLogger.error(player + " tried to use custom npcs without being an op");
        } else {
            ByteBuf buffer = event.getPacket().payload();
            final EnumPacketServer type = EnumPacketServer.values()[buffer.readInt()];
            player.getServer().addScheduledTask(() -> {
                try {
                    LogWriter.debug("Received: " + type);
                    ItemStack item = player.inventory.getCurrentItem();
                    EntityNPCInterface npc = NoppesUtilServer.getEditingNpc(player);
                    if ((!type.needsNpc || npc != null) && (!type.hasPermission() || CustomNpcsPermissions.hasPermission(player, type.permission))) {
                        if (!type.isExempt() && !this.allowItem(item, type)) {
                            GGLogger.error(player + " tried to use custom npcs without a tool in hand, possibly a hacker");
                        } else {
                            this.handlePacket(type, buffer, player, npc);
                        }
                    }
                } catch (Exception var9) {
                    LogWriter.error("Error with EnumPacketServer." + type, var9);
                } finally {
                    buffer.release();
                }

            });
        }
    }

    private void handlePacket(EnumPacketServer type, ByteBuf buffer, EntityPlayerMP player, EntityNPCInterface npc) throws Exception {
        if(type == EnumPacketServer.ScriptBlockDataGet) {
            GGLogger.info("ScriptBlockDataGet");
            BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            GGLogger.info(player.world.getBlockState(pos));
            final TileEntity tileEntity5 = player.world.getTileEntity(pos);
            if (!(tileEntity5 instanceof ICustomScript || tileEntity5 instanceof TileScripted)) {
                return;
            }
            final TileScripted tileScripted6 = tileEntity5 instanceof ICustomScript ? ((ICustomScript)tileEntity5).getScriptedTile() : (TileScripted)tileEntity5;
            GGLogger.info(tileScripted6);
            final NBTTagCompound nBTTagCompound9 = tileScripted6.getNBT(new NBTTagCompound());
            nBTTagCompound9.setTag("Languages", ScriptController.Instance.nbtLanguages());
            Server.sendData(player, EnumPacketClient.GUI_DATA, nBTTagCompound9);
        } else
        if (type == EnumPacketServer.ScriptBlockDataSave) {
            GGLogger.info("ScriptBlockDataSave");
            BlockPos pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
            final TileEntity tileEntity5 = player.world.getTileEntity(pos);
            if (!(tileEntity5 instanceof ICustomScript || tileEntity5 instanceof TileScripted)) {
                return;
            }
            final TileScripted tileScripted6 = tileEntity5 instanceof ICustomScript ? ((ICustomScript)tileEntity5).getScriptedTile() : (TileScripted)tileEntity5;
            tileScripted6.setNBT(Server.readNBT(buffer));
            tileScripted6.lastInited = -1L;
            //Sponge.getServer().getPlayer(player.getUniqueID()).get().resetBlockChange(pos.getX(), pos.getY(), pos.getZ());
        }
        else {
            PacketHandlerServer packetHandlerServer = new PacketHandlerServer();
            Method m = packetHandlerServer.getClass().getDeclaredMethod("handlePacket", EnumPacketServer.class, ByteBuf.class, EntityPlayerMP.class, EntityNPCInterface.class);
            m.setAccessible(true);
            m.invoke(packetHandlerServer, type, buffer, player, npc);
        }
    }

    private boolean allowItem(ItemStack stack, EnumPacketServer type) {
        if (stack != null && stack.getItem() != null) {
            Item item = stack.getItem();
            IPermission permission = null;
            if (item instanceof IPermission) {
                permission = (IPermission)item;
            } else if (item instanceof ItemBlock && ((ItemBlock)item).getBlock() instanceof IPermission) {
                permission = (IPermission)((ItemBlock)item).getBlock();
            }

            return permission != null && permission.isAllowed(type);
        } else {
            return false;
        }
    }

}

package be.openmc.ggames.tractor;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.bukkit.entity.Player;
import java.lang.reflect.Field;

public class PacketHandler {

    public static void movement(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);
                if (packet instanceof net.minecraft.network.protocol.game.PacketPlayInSteerVehicle) {
                    net.minecraft.network.protocol.game.PacketPlayInSteerVehicle ppisv = (net.minecraft.network.protocol.game.PacketPlayInSteerVehicle) packet;
                    TractorMovement movement = new TractorMovement();
                    movement.TractorMovement(player, ppisv);
                }
            }
        };
        Channel channel = null;
        try {
            Object entityPlayer = ((org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer) player).getHandle();

            Field playerConnectionField = entityPlayer.getClass().getField("c");
            net.minecraft.server.network.PlayerConnection playerConnection = (net.minecraft.server.network.PlayerConnection) playerConnectionField.get(entityPlayer);
            Field networkManagerField = net.minecraft.server.network.ServerCommonPacketListenerImpl.class.getDeclaredField("e");
            networkManagerField.setAccessible(true);
            net.minecraft.network.NetworkManager networkManager = (net.minecraft.network.NetworkManager) networkManagerField.get(playerConnection);
            Field channelField = networkManager.getClass().getField("n");
            channel = (Channel) channelField.get(networkManager);

            channel.pipeline()
                    .addBefore("packet_handler", player.getName(), channelDuplexHandler);
        } catch (IllegalArgumentException e) {
            if (channel == null) {
                return;
            }
            if (!channel.pipeline().names().contains(player.getName())) return;
            channel.pipeline().remove(player.getName());
            movement(player);
        } catch (IllegalAccessException | NoSuchFieldException e) {
        }
    }

}

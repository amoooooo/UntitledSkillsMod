package coffee.amo.untitledskillsmod;

import coffee.amo.untitledskillsmod.net.AOESpinPacket;
import coffee.amo.untitledskillsmod.net.ArrowBoostPacket;
import coffee.amo.untitledskillsmod.net.ArrowRainPacket;
import coffee.amo.untitledskillsmod.net.EntityHurtPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class UntitledSkillsModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(UntitledSkillsMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static int packetID = 0;

    public static void init() {
        CHANNEL.registerMessage(packetID++, AOESpinPacket.class, AOESpinPacket::encode, AOESpinPacket::new, AOESpinPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(packetID++, EntityHurtPacket.class, EntityHurtPacket::encode, EntityHurtPacket::new, EntityHurtPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(packetID++, ArrowRainPacket.class, ArrowRainPacket::encode, ArrowRainPacket::new, ArrowRainPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(packetID++, ArrowBoostPacket.class, ArrowBoostPacket::encode, ArrowBoostPacket::new, ArrowBoostPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static <T> void sendToServer(T t) {
        CHANNEL.sendToServer(t);
    }

    public static <T> void sendToPlayer(ServerPlayer player, T t) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> {
            return player;
        }), t);
    }
}

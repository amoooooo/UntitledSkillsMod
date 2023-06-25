package coffee.amo.untitledskillsmod.net;

import coffee.amo.untitledskillsmod.UntitledSkillsMod;
import coffee.amo.untitledskillsmod.combat.ArrowRainHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ArrowRainPacket {
    public int x;
    public int y;
    public int z;


    public ArrowRainPacket(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ArrowRainPacket(FriendlyByteBuf buffer) {
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
    }

    public static void handle(ArrowRainPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player p = ctx.get().getSender();
            if(p == null) return;
            UntitledSkillsMod.LOGGER.info("Received ArrowRainPacket");
            ArrowRainHolder.addArrowRainHolder(new ArrowRainHolder(msg.x, msg.y, msg.z, 0, 100, 5, p.level, p));
            p.releaseUsingItem();
            p.getMainHandItem().releaseUsing(p.level, p, p.getMainHandItem().getUseDuration());
        });
        ctx.get().setPacketHandled(true);
    }
}

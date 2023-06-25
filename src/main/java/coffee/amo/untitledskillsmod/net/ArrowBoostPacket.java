package coffee.amo.untitledskillsmod.net;

import coffee.amo.untitledskillsmod.UntitledSkillsMod;
import coffee.amo.untitledskillsmod.combat.ArrowRainHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ArrowBoostPacket {
    public int x;


    public ArrowBoostPacket(int x) {
        this.x = x;
    }

    public ArrowBoostPacket(FriendlyByteBuf buffer) {
        this.x = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(x);
    }

    public static void handle(ArrowBoostPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player p = ctx.get().getSender();
            if(p == null) return;
            UntitledSkillsMod.LOGGER.info("Received ArrowRainPacket");
            p.releaseUsingItem();
            p.getMainHandItem().releaseUsing(p.level, p, p.getMainHandItem().getUseDuration());
            // get the arrow the player just shot
            p.level.getEntities(p, p.getBoundingBox().inflate(1), (e) -> e instanceof AbstractArrow).forEach((e) -> {
                e.setDeltaMovement(e.getDeltaMovement().scale(2.0));
            });
        });
        ctx.get().setPacketHandled(true);
    }
}

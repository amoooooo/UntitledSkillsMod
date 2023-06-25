package coffee.amo.untitledskillsmod.net;

import coffee.amo.untitledskillsmod.UntitledSkillsMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AOESpinPacket {
    public int data;

    public AOESpinPacket(int data) {
        this.data = data;
    }

    public AOESpinPacket(FriendlyByteBuf buffer) {
        this.data = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(data);
    }

    public static void handle(AOESpinPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player p = ctx.get().getSender();
            if(p == null) return;
            UntitledSkillsMod.LOGGER.info("Received AOESpinPacket");
            p.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 0.35f);
            p.level.getEntities(p, p.getBoundingBox().inflate(msg.data/20f), (e) -> !(e instanceof Player) && e.isAlive() && e instanceof LivingEntity).forEach((e) -> {
                if (e != p) {
                    e.hurt(DamageSource.playerAttack(p), (float) p.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}

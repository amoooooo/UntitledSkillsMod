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

public class EntityHurtPacket {
    public int data;

    public EntityHurtPacket(int data) {
        this.data = data;
    }

    public EntityHurtPacket(FriendlyByteBuf buffer) {
        this.data = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(data);
    }

    public static void handle(EntityHurtPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player p = ctx.get().getSender();
            if(p == null) return;
            UntitledSkillsMod.LOGGER.info("Received AOESpinPacket");
            p.level.getEntity(msg.data).hurt(DamageSource.playerAttack(p), (float) p.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
        });
        ctx.get().setPacketHandled(true);
    }
}

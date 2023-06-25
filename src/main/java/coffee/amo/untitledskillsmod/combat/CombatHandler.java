package coffee.amo.untitledskillsmod.combat;

import coffee.amo.holdmeplease.controllers.LongPressController;
import coffee.amo.untitledskillsmod.UntitledSkillsModNetworking;
import coffee.amo.untitledskillsmod.net.AOESpinPacket;
import coffee.amo.untitledskillsmod.net.ArrowBoostPacket;
import coffee.amo.untitledskillsmod.net.ArrowRainPacket;
import coffee.amo.untitledskillsmod.net.EntityHurtPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CombatHandler {
    public static int spinTicks = 0;
    public static boolean isSpinning = false;
    public static void handleAttackRelease(LongPressController.ActionData data) {
        if(data.player.getMainHandItem().getItem() instanceof SwordItem && !data.player.isBlocking()){
            UntitledSkillsModNetworking.sendToServer(new AOESpinPacket(data.ticksDown));
            if(data.ticksDown > 20){
                data.player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 0.75f);
                isSpinning = true;
                double scalar = ((double) data.ticksDown / 20) * 2;
                // create a circle of particles around the player, with a radius of 0.5*scalar
                data.player.sendSystemMessage(Component.literal("SPIN TO WIN!").withStyle(ChatFormatting.GOLD));
                for (int i = 0; i < 360; i += 10) {
                    double x = data.player.getX() + (0.5 * scalar * Math.cos(Math.toRadians(i)));
                    double y = data.player.getY() + 1.25;
                    double z = data.player.getZ() + (0.5 * scalar * Math.sin(Math.toRadians(i)));
                    data.player.level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0, 0);
                }
            }
        }
        if(data.player.getOffhandItem().getItem() instanceof ShieldItem && data.player.isBlocking()) {
            if(data.ticksDown > 20){
                data.player.playSound(SoundEvents.VEX_CHARGE, 1.0f, 0.1f);
                double scalar = ((double) data.ticksDown / 20) * 2;
                // launch the player in the direction they're facing by 0.5*scalar
                Vec3 playerDir = data.player.getLookAngle().normalize();
                playerDir = new Vec3(playerDir.x, 0.0, playerDir.z);
                playerDir = playerDir.scale(scalar);
                data.player.setDeltaMovement(data.player.getDeltaMovement().add(playerDir.scale(0.5)));
                data.player.sendSystemMessage(Component.literal("CHAAAAAAAAARGE!").withStyle(ChatFormatting.GOLD));
                // get a list of entities in front of the player
                List<Entity> entities = data.player.level.getEntities(data.player, data.player.getBoundingBox().expandTowards(playerDir), (e) -> !(e instanceof Player) && e.isAlive() && e instanceof LivingEntity);
                entities.forEach((e) -> {
                    if (e != data.player) {
                        UntitledSkillsModNetworking.sendToServer(new EntityHurtPacket(e.getId()));
                    }
                });
            }
        }
        if(data.player.getMainHandItem().getItem() instanceof BowItem && data.player.isUsingItem() && data.player.getOffhandItem() == ItemStack.EMPTY) {
            // check if bow is charged
            BowItem bow = (BowItem) data.player.getMainHandItem().getItem();
            if(data.player.getUseItemRemainingTicks() <= bow.getUseDuration(data.player.getMainHandItem()) - 20) {
                // bow is charged
                // get a list of entities in front of the player
                // get the block the player is looking at, up to 5 * (ticksDown / 20) blocks away
                Vec3 playerDir = data.player.getLookAngle().normalize();
                BlockHitResult hitResult = data.player.level.clip(new ClipContext(data.player.getEyePosition(), data.player.position().add(playerDir.scale(5 * ((double) data.ticksDown / 20))), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, data.player));
                if(hitResult.getType() == HitResult.Type.BLOCK){
                    data.player.stopUsingItem();
                    UntitledSkillsModNetworking.sendToServer(new ArrowRainPacket(hitResult.getBlockPos().getX(), hitResult.getBlockPos().getY(), hitResult.getBlockPos().getZ()));
                    data.player.sendSystemMessage(Component.literal("Arrow Rain!").withStyle(ChatFormatting.GOLD));
                }
            }
        } else if (data.player.getMainHandItem().getItem() instanceof BowItem && data.player.isUsingItem() && data.player.getOffhandItem().getItem() instanceof ShieldItem) {
            BowItem bow = (BowItem) data.player.getMainHandItem().getItem();
            if(data.player.getUseItemRemainingTicks() <= bow.getUseDuration(data.player.getMainHandItem()) - 20) {
                data.player.stopUsingItem();
                data.player.playSound(SoundEvents.SHIELD_BREAK, 1.0f, 1.75f);
                UntitledSkillsModNetworking.sendToServer(new ArrowBoostPacket(0));
                data.player.sendSystemMessage(Component.literal("Arrow Boost!").withStyle(ChatFormatting.GOLD));
            }
        }
    }
    
    public static void handleAttackCharge(LongPressController.ActionData data) {
        if(data.player.getMainHandItem().getItem() instanceof SwordItem && !data.player.isBlocking()){
            if (data.ticksDown % 20 == 0) {
                data.player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0f, 1.0f + ((float) data.ticksDown / 20));
                double scalar = ((double) data.ticksDown / 20);
                for (int i = 0; i < 10 * (data.ticksDown / 20); i++) {
                    Vec3 particlePos = new Vec3(data.player.getX() + (Math.random()-0.5) * scalar, data.player.getY(), data.player.getZ() + (Math.random()-0.5) * scalar);
                    // get a direction from the particlePos to the player
                    Vec3 particleDir = data.player.position().subtract(particlePos).normalize();
                    particleDir = particleDir.scale(0.1*((double) data.ticksDown / 20));
                    data.player.level.addParticle(ParticleTypes.SMOKE, particlePos.x, particlePos.y, particlePos.z, particleDir.x, particleDir.y, particleDir.z);
                }
            }
        }
        if(data.player.getOffhandItem().getItem() instanceof ShieldItem && data.player.isBlocking()) {
            if (data.ticksDown % 20 == 0) {
                data.player.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0f, 1.0f + ((float) data.ticksDown / 20));
                double scalar = ((double) data.ticksDown / 20);
                for (int i = 0; i < 10 * (data.ticksDown / 20); i++) {
                    Vec3 particlePos = new Vec3(data.player.getX() + (Math.random()-0.5) * scalar, (data.player.getY() + data.player.getBbHeight()/2) + (Math.random()-0.5) * scalar, data.player.getZ() + (Math.random()-0.5) * scalar);
                    // get a direction from the particlePos to the player
                    Vec3 particleDir = data.player.position().subtract(particlePos).normalize();
                    particleDir = particleDir.scale(0.1*((double) data.ticksDown / 20));
                    data.player.level.addParticle(ParticleTypes.SCULK_CHARGE_POP, particlePos.x, particlePos.y, particlePos.z, particleDir.x, particleDir.y, particleDir.z);
                }
            }
        }
        if(data.player.getMainHandItem().getItem() instanceof BowItem && data.player.isUsingItem()) {
            if (data.ticksDown % 20 == 0) {
                data.player.playSound(SoundEvents.ARROW_HIT_PLAYER, 1.0f, 1.0f + ((float) data.ticksDown / 20));
                double scalar = ((double) data.ticksDown / 20);
                for (int i = 0; i < 10 * (data.ticksDown / 20); i++) {
                    Vec3 particlePos = new Vec3(data.player.getX() + (Math.random()-0.5) * scalar, (data.player.getY() + data.player.getBbHeight()/2) + (Math.random()-0.5) * scalar, data.player.getZ() + (Math.random()-0.5) * scalar);
                    // get a direction from the particlePos to the player
                    Vec3 particleDir = data.player.position().subtract(particlePos).normalize();
                    particleDir = particleDir.scale(0.1*((double) data.ticksDown / 20));
                    data.player.level.addParticle(ParticleTypes.ENCHANT, particlePos.x, particlePos.y, particlePos.z, particleDir.x, particleDir.y, particleDir.z);
                }
            }
        }
    }
}

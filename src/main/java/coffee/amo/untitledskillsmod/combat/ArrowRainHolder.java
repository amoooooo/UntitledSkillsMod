package coffee.amo.untitledskillsmod.combat;

import coffee.amo.untitledskillsmod.mixin.AbstractArrowAccessor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ArrowRainHolder {
    public int x;
    public int y;
    public int z;
    public int ticks;
    public int duration;
    public int damage;
    private Player owner;
    public Level level;
    private List<Arrow> arrowsToTick = new ArrayList<>();

    public static List<ArrowRainHolder> arrowRainHolders = new ArrayList<>();

    public ArrowRainHolder(int x, int y, int z, int ticks, int duration, int damage, Level level, Player owner) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ticks = ticks;
        this.duration = duration;
        this.damage = damage;
        this.level = level;
        this.owner = owner;
    }

    public static void addArrowRainHolder(ArrowRainHolder arrowRainHolder) {
        arrowRainHolders.add(arrowRainHolder);
    }

    public static void removeArrowRainHolder(ArrowRainHolder arrowRainHolder) {
        arrowRainHolders.remove(arrowRainHolder);
    }

    public static void tickArrowRainHolders() {
        List<ArrowRainHolder> toRemove = new ArrayList<>();
        for (ArrowRainHolder arrowRainHolder : arrowRainHolders) {
            arrowRainHolder.tick();
            if (arrowRainHolder.ticks >= arrowRainHolder.duration) {
                toRemove.add(arrowRainHolder);
            }
        }
        for (ArrowRainHolder arrowRainHolder : toRemove) {
            removeArrowRainHolder(arrowRainHolder);
        }
    }

    public void tick() {
        this.ticks++;
        if (this.ticks % 5 == 0 && this.ticks < this.duration - 20) {
            Arrow arrow = new Arrow(this.level, this.x + level.random.nextInt(5) * level.random.nextFloat()-0.5f, this.y + 24, this.z + level.random.nextInt(5) * level.random.nextFloat()-0.5f);
            arrow.setBaseDamage(this.damage);
            arrow.shoot(0, -damage, 0, 0.75f, 0);
            arrow.setOwner(this.owner);
            this.level.addFreshEntity(arrow);
            arrow.playSound(SoundEvents.ARROW_SHOOT, 1, 1);
            this.arrowsToTick.add(arrow);
        }
        for (Arrow arrow : this.arrowsToTick) {
            if(!((AbstractArrowAccessor)arrow).getInGround()){
                ((ServerLevel)arrow.level).sendParticles(ParticleTypes.FIREWORK, arrow.getX(), arrow.getY(), arrow.getZ(), 5, 0.1, 0.1, 0.1, 0.1);
            } else {
                arrow.kill();
            }
        }
    }
}

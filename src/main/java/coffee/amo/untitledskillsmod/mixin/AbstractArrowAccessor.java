package coffee.amo.untitledskillsmod.mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {

    @Accessor
    boolean getInGround();
}

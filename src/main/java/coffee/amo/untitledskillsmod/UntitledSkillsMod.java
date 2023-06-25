package coffee.amo.untitledskillsmod;

import coffee.amo.holdmeplease.controllers.LongPressController;
import coffee.amo.holdmeplease.event.RegisterControllersEvent;
import coffee.amo.untitledskillsmod.combat.ArrowRainHolder;
import coffee.amo.untitledskillsmod.combat.CombatHandler;
import coffee.amo.untitledskillsmod.net.AOESpinPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(UntitledSkillsMod.MODID)
public class UntitledSkillsMod {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "untitledskillsmod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public UntitledSkillsMod() {
        UntitledSkillsModNetworking.init();
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event){
            if(event.phase == TickEvent.Phase.END) return;
            ArrowRainHolder.tickArrowRainHolders();
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onButtonRegistry(RegisterControllersEvent event) {
            event.registerLongPressController(Minecraft.getInstance().options.keyAttack, new LongPressController(Minecraft.getInstance().options.keyAttack, event.getPlayer(),
                    CombatHandler::handleAttackRelease,
                    CombatHandler::handleAttackRelease, CombatHandler::handleAttackCharge, 60));
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event){
            if(event.phase == TickEvent.Phase.END) return;
            if(CombatHandler.isSpinning){
//                // spin the player around in a circle
//                Minecraft.getInstance().player.setYRot(Minecraft.getInstance().player.getYRot() + 30);
//
////                Minecraft.getInstance().player.setYHeadRot(Minecraft.getInstance().player.getYRot() - 30);
                CombatHandler.spinTicks++;
                if(CombatHandler.spinTicks > 60){
                    CombatHandler.isSpinning = false;
                    CombatHandler.spinTicks = 0;
                }
            }
        }
    }
}

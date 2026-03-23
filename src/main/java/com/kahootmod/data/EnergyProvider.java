package com.kahootmod.data;

import com.kahootmod.KahootMod;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = KahootMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnergyProvider implements ICapabilityProvider {
    private final EnergyData energy = new EnergyData();
    private final LazyOptional<EnergyData> optional = LazyOptional.of(() -> energy);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == EnergyCapability.INSTANCE) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(KahootMod.MODID, "energy"), new EnergyProvider());
        }
    }
}

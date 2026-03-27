package com.flashcardmod.data;

import com.flashcardmod.FlashcardMod;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = FlashcardMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnergyProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    private final EnergyData energy = new EnergyData();
    private final LazyOptional<EnergyData> optional = LazyOptional.of(() -> energy);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == EnergyCapability.INSTANCE) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        energy.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag nbt) {
        energy.loadNBTData(nbt);
    }

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(FlashcardMod.MODID, "energy"), new EnergyProvider());
        }
    }
}

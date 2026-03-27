package com.flashcardmod.network;

import com.flashcardmod.FlashcardMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = FlashcardMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(
        ResourceLocation.fromNamespaceAndPath(FlashcardMod.MODID, "main")
    ).networkProtocolVersion(1).simpleChannel();

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            int id = 0;
            CHANNEL.messageBuilder(SyncEnergyPacket.class, id++)
                .encoder(SyncEnergyPacket::encode)
                .decoder(SyncEnergyPacket::new)
                .consumerMainThread(SyncEnergyPacket::handle)
                .add();
                
            CHANNEL.messageBuilder(RewardEnergyPacket.class, id++)
                .encoder(RewardEnergyPacket::encode)
                .decoder(RewardEnergyPacket::new)
                .consumerMainThread(RewardEnergyPacket::handle)
                .add();
        });
    }
}

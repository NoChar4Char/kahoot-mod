package com.kahootmod.events;

import com.kahootmod.KahootMod;
import com.kahootmod.data.EnergyCapability;
import com.kahootmod.network.SyncEnergyPacket;
import com.kahootmod.network.NetworkHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = KahootMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EnergyEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            Player player = event.player;
            player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                boolean changed = false;
                
                if (player.isSprinting() && player.tickCount % 20 == 0) {
                    energy.consumeEnergy(2);
                    changed = true;
                } else if ((player.zza != 0 || player.xxa != 0) && player.tickCount % 40 == 0) {
                    energy.consumeEnergy(1);
                    changed = true;
                }

                if (energy.getEnergy() <= 0 && player.tickCount % 20 == 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 255, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 255, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, 200, false, false));
                }

                if (changed) {
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy()), PacketDistributor.PLAYER.with((ServerPlayer) player));
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (event.getEntity() != null) {
            event.getEntity().getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                if (energy.getEnergy() <= 0) {
                    event.setCanceled(true);
                    return;
                }
                if (!event.getEntity().level().isClientSide) {
                    energy.consumeEnergy(5);
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy()), PacketDistributor.PLAYER.with((ServerPlayer) event.getEntity()));
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getEntity() != null) {
            event.getEntity().getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                if (energy.getEnergy() <= 0) {
                    event.setCanceled(true);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null) {
            event.getPlayer().getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                if (energy.getEnergy() <= 0) {
                    event.setCanceled(true);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (event.getItem().getItem().components().has(net.minecraft.core.component.DataComponents.FOOD)) {
                player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                    energy.consumeEnergy(3);
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy()), PacketDistributor.PLAYER.with((ServerPlayer) player));
                });
            }
        }
    }
}

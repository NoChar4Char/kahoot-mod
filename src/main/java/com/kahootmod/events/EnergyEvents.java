package com.kahootmod.events;

import com.kahootmod.KahootMod;
import com.kahootmod.data.EnergyCapability;
import com.kahootmod.network.SyncEnergyPacket;
import com.kahootmod.network.NetworkHandler;
import com.kahootmod.config.ModConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.projectile.AbstractArrow;
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
                int before = energy.getEnergy();

                if (energy.getEnergy() == -1) {
                    energy.setEnergy(ModConfig.INITIAL_ENERGY.get());
                }

                if (player.tickCount % 20 == 0) {
                    if (player.isCrouching()) {
                        energy.consumeEnergy(ModConfig.DRAIN_CROUCH.get());
                    } else if (player.isSprinting() || player.isSwimming()) {
                        energy.consumeEnergy(ModConfig.DRAIN_SPRINT.get());
                    } else if (player.getDeltaMovement().lengthSqr() > 0.001 || player.isFallFlying() || player.isPassenger()) {
                        energy.consumeEnergy(ModConfig.DRAIN_MOVE.get());
                    } else {
                        energy.consumeEnergy(ModConfig.DRAIN_AFK.get());
                    }
                }
                
                if (energy.getEnergy() != before) {
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), ModConfig.MAX_ENERGY.get()), PacketDistributor.PLAYER.with((ServerPlayer) player));
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (event.getEntity() != null) {
            event.getEntity().getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                if (energy.getEnergy() == 0) {
                    event.setCanceled(true);
                    return;
                }
                if (!event.getEntity().level().isClientSide) {
                    energy.consumeEnergy(ModConfig.DRAIN_ATTACK.get());
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), ModConfig.MAX_ENERGY.get()), PacketDistributor.PLAYER.with((ServerPlayer) event.getEntity()));
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getEntity() != null) {
            event.getEntity().getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                if (energy.getEnergy() == 0 && event.isCancelable()) {
                    event.setCanceled(true);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onBlockInteractDrain(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity() != null && event.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND) {
            event.getEntity().getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                if (energy.getEnergy() > 0 && !event.getLevel().isClientSide) {
                    energy.consumeEnergy(ModConfig.DRAIN_BLOCK_INTERACT.get());
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), ModConfig.MAX_ENERGY.get()), PacketDistributor.PLAYER.with((ServerPlayer) event.getEntity()));
                }
            });
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null) {
            event.getPlayer().getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                if (energy.getEnergy() == 0) {
                    event.setCanceled(true);
                    return;
                }
                if (!event.getPlayer().level().isClientSide) {
                    energy.consumeEnergy(ModConfig.DRAIN_BLOCK_BREAK_PLACE.get());
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), ModConfig.MAX_ENERGY.get()), PacketDistributor.PLAYER.with((ServerPlayer) event.getPlayer()));
                }
            });
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                if (energy.getEnergy() == 0) {
                    event.setCanceled(true);
                    return;
                }
                if (!player.level().isClientSide) {
                    energy.consumeEnergy(ModConfig.DRAIN_BLOCK_BREAK_PLACE.get());
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), ModConfig.MAX_ENERGY.get()), PacketDistributor.PLAYER.with((ServerPlayer) player));
                }
            });
        }
    }
    
    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (event.getItem().getItem().components().has(net.minecraft.core.component.DataComponents.FOOD)) {
                player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                    energy.consumeEnergy(ModConfig.DRAIN_EAT.get());
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), ModConfig.MAX_ENERGY.get()), PacketDistributor.PLAYER.with((ServerPlayer) player));
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHeal(LivingHealEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                int drain = (int)(event.getAmount() / 2.0f * ModConfig.DRAIN_HEAL_HALF_HEART.get());
                if (drain > 0) {
                    energy.consumeEnergy(drain);
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), ModConfig.MAX_ENERGY.get()), PacketDistributor.PLAYER.with((ServerPlayer) player));
                }
            });
        }
    }

    @SubscribeEvent
    public static void onArrowShoot(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow && !arrow.level().isClientSide) {
            if (arrow.getOwner() instanceof Player player) {
                player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                    if (energy.getEnergy() > 0) {
                        energy.consumeEnergy(ModConfig.DRAIN_BOW.get());
                        NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), ModConfig.MAX_ENERGY.get()), PacketDistributor.PLAYER.with((ServerPlayer) player));
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
        }
        event.getOriginal().getCapability(EnergyCapability.INSTANCE).ifPresent(oldEnergy -> {
            event.getEntity().getCapability(EnergyCapability.INSTANCE).ifPresent(newEnergy -> {
                newEnergy.setEnergy(oldEnergy.getEnergy());
            });
        });
        if (event.isWasDeath()) {
            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), ModConfig.MAX_ENERGY.get()), PacketDistributor.PLAYER.with(player));
            });
        }
    }
    
    @SubscribeEvent
    public static void onPlayerLoginState(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy(), ModConfig.MAX_ENERGY.get()), PacketDistributor.PLAYER.with(player));
            });
        }
    }
}

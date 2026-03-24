package com.kahootmod.events;

import com.kahootmod.KahootMod;
import com.kahootmod.data.EnergyCapability;
import com.kahootmod.network.SyncEnergyPacket;
import com.kahootmod.network.NetworkHandler;
import com.kahootmod.config.ModGameRules;
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
                    energy.setEnergy(player.level().getGameRules().getInt(ModGameRules.INITIAL_ENERGY));
                }

                if (player.isCrouching()) {
                    if (player.tickCount % 20 == 0) {
                        energy.consumeEnergy(player.level().getGameRules().getInt(ModGameRules.DRAIN_CROUCH));
                    }
                } else if (player.isSprinting() || player.isSwimming()) {
                    if (player.tickCount % 20 == 0) {
                        energy.consumeEnergy(player.level().getGameRules().getInt(ModGameRules.DRAIN_SPRINT_SWIM));
                    }
                } else if (player.getDeltaMovement().lengthSqr() > 0.001 || player.isFallFlying() || player.isPassenger()) {
                    if (player.tickCount % 80 == 0) {
                        energy.consumeEnergy(player.level().getGameRules().getInt(ModGameRules.DRAIN_MOVE_OTHER));
                    }
                }
                
                if (energy.getEnergy() != before) {
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy()), PacketDistributor.PLAYER.with((ServerPlayer) player));
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
                    energy.consumeEnergy(event.getEntity().level().getGameRules().getInt(ModGameRules.DRAIN_ATTACK));
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy()), PacketDistributor.PLAYER.with((ServerPlayer) event.getEntity()));
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
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null) {
            event.getPlayer().getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                if (energy.getEnergy() == 0) {
                    event.setCanceled(true);
                    return;
                }
                if (!event.getPlayer().level().isClientSide) {
                    energy.consumeEnergy(event.getPlayer().level().getGameRules().getInt(ModGameRules.DRAIN_BLOCK));
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy()), PacketDistributor.PLAYER.with((ServerPlayer) event.getPlayer()));
                }
            });
        }
    }
    
    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            if (event.getItem().getItem().components().has(net.minecraft.core.component.DataComponents.FOOD)) {
                player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                    energy.consumeEnergy(player.level().getGameRules().getInt(ModGameRules.DRAIN_EAT));
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy()), PacketDistributor.PLAYER.with((ServerPlayer) player));
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHeal(LivingHealEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            player.getCapability(EnergyCapability.INSTANCE).ifPresent(energy -> {
                int drain = (int)(event.getAmount() / 2.0f * player.level().getGameRules().getInt(ModGameRules.DRAIN_HEAL));
                if (drain > 0) {
                    energy.consumeEnergy(drain);
                    NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy()), PacketDistributor.PLAYER.with((ServerPlayer) player));
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
                        energy.consumeEnergy(player.level().getGameRules().getInt(ModGameRules.DRAIN_BOW));
                        NetworkHandler.CHANNEL.send(new SyncEnergyPacket(energy.getEnergy()), PacketDistributor.PLAYER.with((ServerPlayer) player));
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(EnergyCapability.INSTANCE).ifPresent(oldEnergy -> {
            event.getEntity().getCapability(EnergyCapability.INSTANCE).ifPresent(newEnergy -> {
                newEnergy.setEnergy(oldEnergy.getEnergy());
            });
        });
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide) {
            if (com.kahootmod.QuestionManager.getQuestionCount() == 0) {
                if (event.getEntity() instanceof ServerPlayer player) {
                    player.connection.disconnect(Component.literal("You must have a Kahoot Pack loaded to join the game!"));
                }
            }
        }
    }
}

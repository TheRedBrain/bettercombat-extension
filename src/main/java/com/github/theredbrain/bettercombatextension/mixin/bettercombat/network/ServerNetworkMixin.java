package com.github.theredbrain.bettercombatextension.mixin.bettercombat.network;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.logic.PlayerAttackHelper;
import net.bettercombat.logic.PlayerAttackProperties;
import net.bettercombat.logic.TargetHelper;
import net.bettercombat.logic.knockback.ConfigurableKnockback;
import net.bettercombat.mixin.LivingEntityAccessor;
import net.bettercombat.network.Packets;
import net.bettercombat.network.ServerNetwork;
import net.bettercombat.utils.AttributeModifierHelper;
import net.bettercombat.utils.MathHelper;
import net.bettercombat.utils.SoundHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerNetwork.class)
public class ServerNetworkMixin {

	@Shadow
	@Final
	static Logger LOGGER;

	@Shadow
	public static Identifier TEMPORARY_ATTACK;

	/**
	 * @author TheRedBrain
	 * @reason integrate optional use of entity interaction attribute as attack range
	 */
	@Overwrite
	public static void handleAttackRequest(Packets.C2S_AttackRequest request, MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler) {
		ServerWorld world = (ServerWorld) Iterables.tryFind(server.getWorlds(), (element) -> {
			return element == player.getWorld();
		}).orNull();
		if (world != null && !world.isClient) {
			AttackHand hand = PlayerAttackHelper.getCurrentAttack(player, request.comboCount());
			if (hand == null) {
				LOGGER.error("Server handling Packets.C2S_AttackRequest - No current attack hand!");
				Logger var10000 = LOGGER;
				int var10001 = request.comboCount();
				var10000.error("Combo count: " + var10001 + " is dual wielding: " + PlayerAttackHelper.isDualWielding(player));
				LOGGER.error("Main-hand stack: " + String.valueOf(player.getMainHandStack()));
				LOGGER.error("Off-hand stack: " + String.valueOf(player.getOffHandStack()));
				var10000 = LOGGER;
				var10001 = player.getInventory().selectedSlot;
				var10000.error("Selected slot server: " + var10001 + " | client: " + request.selectedSlot());
			} else {
				WeaponAttributes.Attack attack = hand.attack();
				WeaponAttributes attributes = hand.attributes();
				boolean useVanillaPacket = Packets.C2S_AttackRequest.UseVanillaPacket;
				world.getServer().executeSync(() -> {
					((PlayerAttackProperties)player).setComboCount(request.comboCount());
					PlayerAttackHelper.swapHandAttributes(player, hand.isOffHand(), () -> {
						double damageBaseMultiplier = 0.0;
						double range = 18.0;
						float knockbackMultiplier;
						if (attributes != null && attack != null) {
							range = BetterCombatExtension.getAttackRange(player, attributes);
							double comboMultiplier = attack.damageMultiplier() - 1.0;
							damageBaseMultiplier += comboMultiplier;
							knockbackMultiplier = PlayerAttackHelper.getDualWieldingAttackDamageMultiplier(player, hand) - 1.0F;
							damageBaseMultiplier += (double)knockbackMultiplier;
							SoundHelper.playSound(world, player, attack.swingSound());
							if (BetterCombatMod.config.allow_reworked_sweeping && request.entityIds().length > 1) {
								double multiplier = (double)(0.0F - BetterCombatMod.config.reworked_sweeping_maximum_damage_penalty / (float)BetterCombatMod.config.reworked_sweeping_extra_target_count * (float)Math.min(BetterCombatMod.config.reworked_sweeping_extra_target_count, request.entityIds().length - 1));
								double sweepRatio = player.getAttributeValue(EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO);
								damageBaseMultiplier += multiplier + (double)BetterCombatMod.config.reworked_sweeping_maximum_damage_penalty * sweepRatio;
								boolean playEffects = !BetterCombatMod.config.reworked_sweeping_sound_and_particles_only_for_swords || hand.itemStack().getItem() instanceof SwordItem;
								if (BetterCombatMod.config.reworked_sweeping_plays_sound && playEffects) {
									world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
								}

								if (BetterCombatMod.config.reworked_sweeping_emits_particles && playEffects) {
									player.spawnSweepAttackParticles();
								}
							}
						}

						Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> damageModifier = null;
						if (damageBaseMultiplier != 0.0) {
							AttributeModifierHelper.fromModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, (EntityAttributeModifier)null);
							damageModifier = AttributeModifierHelper.fromModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(TEMPORARY_ATTACK, damageBaseMultiplier, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE));
							player.getAttributes().addTemporaryModifiers(damageModifier);
						}

						float attackCooldown = PlayerAttackHelper.getAttackCooldownTicksCapped(player);
						knockbackMultiplier = BetterCombatMod.config.knockback_reduced_for_fast_attacks ? MathHelper.clamp(attackCooldown / 12.5F, 0.1F, 1.0F) : 1.0F;
						int lastAttackedTicks = ((LivingEntityAccessor)player).getLastAttackedTicks();
						if (!useVanillaPacket) {
							player.setSneaking(request.isSneaking());
						}

						double validationRangeSquared = range * range * (double)BetterCombatMod.config.target_search_range_multiplier;
						int[] var18 = request.entityIds();
						int var27 = var18.length;

						for(int var20 = 0; var20 < var27; ++var20) {
							int entityId = var18[var20];
							boolean isBossPart = false;
							Entity entity = world.getEntityById(entityId);
							if (entity == null) {
								isBossPart = true;
								entity = world.getDragonPart(entityId);
							}

							if (entity != null && (!entity.equals(player.getVehicle()) || TargetHelper.isAttackableMount(entity)) && (!(entity instanceof ArmorStandEntity) || !((ArmorStandEntity)entity).isMarker())) {
								LivingEntity livingEntity;
								if (entity instanceof LivingEntity) {
									livingEntity = (LivingEntity)entity;
									if (BetterCombatMod.config.allow_fast_attacks) {
										livingEntity.timeUntilRegen = 0;
									}

									if (knockbackMultiplier != 1.0F) {
										((ConfigurableKnockback)livingEntity).setKnockbackMultiplier_BetterCombat(knockbackMultiplier);
									}
								}

								((LivingEntityAccessor)player).setLastAttackedTicks(lastAttackedTicks);
								if (!isBossPart && useVanillaPacket) {
									PlayerInteractEntityC2SPacket vanillaAttackPacket = PlayerInteractEntityC2SPacket.attack(entity, request.isSneaking());
									handler.onPlayerInteractEntity(vanillaAttackPacket);
								} else if (!BetterCombatMod.config.server_target_range_validation || player.squaredDistanceTo(entity) <= validationRangeSquared) {
									if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof PersistentProjectileEntity || entity == player) {
										handler.disconnect(Text.translatable("multiplayer.disconnect.invalid_entity_attacked"));
										LOGGER.warn("Player {} tried to attack an invalid entity", player.getName().getString());
										return;
									}

									player.attack(entity);
								}

								if (entity instanceof LivingEntity) {
									livingEntity = (LivingEntity)entity;
									if (knockbackMultiplier != 1.0F) {
										((ConfigurableKnockback)livingEntity).setKnockbackMultiplier_BetterCombat(1.0F);
									}
								}
							}
						}

						if (!useVanillaPacket) {
							player.updateLastActionTime();
						}

						if (damageModifier != null) {
							player.getAttributes().removeModifiers(damageModifier);
						}

						((PlayerAttackProperties)player).setComboCount(-1);
					});
				});
			}
		}
	}
}

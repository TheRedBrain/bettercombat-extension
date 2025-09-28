package com.github.theredbrain.bettercombatextension;

import com.github.theredbrain.bettercombatextension.compat.AttackRangeAttributeCompat;
import com.github.theredbrain.bettercombatextension.compat.RPGInventoryCompat;
import com.github.theredbrain.bettercombatextension.compat.StaminaAttributesCompat;
import com.github.theredbrain.bettercombatextension.config.ServerConfig;
import com.github.theredbrain.bettercombatextension.network.packet.AttackStaminaCostPacket;
import com.github.theredbrain.bettercombatextension.network.packet.AttackStaminaCostPacketReceiver;
import com.github.theredbrain.bettercombatextension.network.packet.CancelAttackPacket;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterCombatExtension implements ModInitializer {
	public static final String MOD_ID = "bettercombatextension";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ServerConfig SERVER_CONFIG;

	public static final boolean isAttackRangeAttributeLoaded = FabricLoader.getInstance().isModLoaded("minecrawl");
	public static final boolean isRPGInventoryLoaded = FabricLoader.getInstance().isModLoaded("rpginventory");
	public static final boolean isShoulderSurfingLoaded = FabricLoader.getInstance().isModLoaded("shouldersurfing");
	public static final boolean isStaminaAttributesLoaded = FabricLoader.getInstance().isModLoaded("staminaattributes");

	public static RegistryEntry<EntityAttribute> ATTACK_STAMINA_COST;

	public static double getAttackRange(PlayerEntity playerEntity) {
		if (isAttackRangeAttributeLoaded && SERVER_CONFIG.enable_attack_range_attribute_integration.get()) {
			return AttackRangeAttributeCompat.getAttackRange(playerEntity);
		} else {
			return playerEntity.getEntityInteractionRange();
		}
	}

	public static RegistryEntry<EntityAttribute> getAttackRangeAttribute() {
		if (isAttackRangeAttributeLoaded && SERVER_CONFIG.enable_attack_range_attribute_integration.get()) {
			return AttackRangeAttributeCompat.getAttackRangeAttribute();
		} else {
			return EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE;
		}
	}

	public static double getAttackRangeBaseValue(PlayerEntity playerEntity) {
		if (isAttackRangeAttributeLoaded && SERVER_CONFIG.enable_attack_range_attribute_integration.get()) {
			return AttackRangeAttributeCompat.getAttackRangeBaseValue(playerEntity);
		} else {
			return playerEntity.getAttributeBaseValue(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
		}
	}

	public static boolean isRPGInventoryHandSlotOverhaulActive() {
		if (isRPGInventoryLoaded) {
			return RPGInventoryCompat.isHandSlotOverhaulActive();
		}
		return false;
	}

	public static void setRPGInventoryMainHandSlot(PlayerInventory playerInventory, ItemStack itemStack) {
		if (isRPGInventoryLoaded) {
			RPGInventoryCompat.setMainHandSlot(playerInventory, itemStack);
		} else {
			playerInventory.main.set(playerInventory.selectedSlot, itemStack);
		}
	}

	public static boolean shouldAlternativeHandSwapAlgorithmBeEnabled() {
		return isRPGInventoryHandSlotOverhaulActive() && SERVER_CONFIG.enable_experimental_swap_hand_attributes_algorithm.get();
	}

	public static float getCurrentStamina(LivingEntity livingEntity) {
		float currentStamina = 0.0F;
		if (isStaminaAttributesLoaded) {
			currentStamina = StaminaAttributesCompat.getCurrentStamina(livingEntity);
		}
		return currentStamina;
	}

	public static void addStamina(LivingEntity livingEntity, float amount) {
		if (isStaminaAttributesLoaded) {
			StaminaAttributesCompat.addStamina(livingEntity, amount);
		}
	}

	@Override
	public void onInitialize() {
		LOGGER.info("BetterCombat was extended!");
		SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerConfig::new, RegisterType.BOTH);

		PayloadTypeRegistry.playS2C().register(CancelAttackPacket.PACKET_ID, CancelAttackPacket.PACKET_CODEC);

		PayloadTypeRegistry.playC2S().register(AttackStaminaCostPacket.PACKET_ID, AttackStaminaCostPacket.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(AttackStaminaCostPacket.PACKET_ID, new AttackStaminaCostPacketReceiver());

	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static final TagKey<EntityType<?>> DISABLES_JUMP_RESTRICTION_WHEN_RIDDEN = TagKey.of(RegistryKeys.ENTITY_TYPE, identifier("disables_jump_restriction_when_ridden"));
	public static final TagKey<EntityType<?>> DISABLES_MOVEMENT_LOCKING_WHEN_RIDDEN = TagKey.of(RegistryKeys.ENTITY_TYPE, identifier("disables_movement_locking_when_ridden"));
	public static final TagKey<Item> DISABLES_JUMP_RESTRICTION_DURING_ATTACK = TagKey.of(RegistryKeys.ITEM, identifier("disables_jump_restriction_during_attack"));
	public static final TagKey<Item> DISABLES_MOVEMENT_LOCKING_DURING_ATTACK = TagKey.of(RegistryKeys.ITEM, identifier("disables_movement_locking_during_attack"));
	public static final TagKey<Item> IGNORES_ATTACK_MOVEMENT_PENALTY = TagKey.of(RegistryKeys.ITEM, identifier("ignores_attack_movement_penalty"));
	public static final TagKey<Item> EMPTY_HAND_WEAPONS = TagKey.of(RegistryKeys.ITEM, identifier("empty_hand_weapons"));
}
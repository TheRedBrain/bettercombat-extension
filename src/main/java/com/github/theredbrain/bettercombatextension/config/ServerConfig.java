package com.github.theredbrain.bettercombatextension.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(
		name = "server"
)
public class ServerConfig implements ConfigData {
	@Comment("""
			WARNING: EXPERIMENTAL
			
			Better Combats attacking with weapons in the offhand slot is incompatible with RPG Inventory
			
			Setting this option to true, fixes the incompatibility. This may have unexpected consequences.
			Please report any issues you encounter when using this setting
			
			Default: false
			""")
	public boolean enable_experimental_swap_hand_attributes_algorithm = false;
	@Comment("""
			When set to true, changing the players orientation and position while attacking is disabled.
			It is recommended to install Shoulder Surfing Reloaded, play in the third person perspective and activate the decoupled camera setting.
			Since the movement_multiplier setting in the Better Combat server config has no effect when this is set to true, it is recommended to set it to its default of 1.0.
			Use the item tag "disables_movement_locking_during_attack" and the entity type tag "disables_movement_locking_when_ridden" to control when the movement locking is applied.
			
			Default: false
			""")
	public boolean enable_movement_locking_attacks = false;
	@Comment("""
			When set to true, jumping is disabled during attacks.
			Use the item tag "disables_jump_restriction_during_attack" and the entity type tag "disables_jump_restriction_when_ridden" to control when the jump restriction is applied.
			
			Default: false
			""")
	public boolean enable_jump_restriction_during_attacks = false;
	@Comment("""
			When set to true, the pitch of the players attack is restricted.
			
			Default: false
			""")
	public boolean restrict_attack_pitch = false;
	@Comment("""
			Only has an effect when 'restrict_attack_pitch' is set to 'true'.
			Describes the angle from 0, which means the final range is from -'attack_pitch_range' to 'attack_pitch_range'.
			
			Default: 15.0
			""")
	public float attack_pitch_range = 15.0F;
	@Comment("""
			Enables an alternative two_handed condition.
			If the offhand stack is empty and this is true, the mainhand stack is two_handed
			This is not active if the "two_handed" field in the weapon_attribute file is set to true.
			
			Default: false
			""")
	public boolean empty_offhand_equals_two_handing_mainhand = false;
	@Comment("""
			Disables Better Combat's formerly client feature of
			continuously attacking while holding down the attack key.
			
			Default: false
			""")
	public boolean disable_better_combat_hold_to_attack = false;
	@Comment("""
			When set to true, feinting an attack skips it.
			
			Default: false
			""")
	public boolean feinting_increases_combo_count = false;
	@Comment("""
			When set to true, the "minecraft:player.entity_interaction_range" entity attribute is used to determine the attack range.
			It is recommended to set the "isTooltipAttackRangeEnabled" value in the Better Combat client config to false,
			as it no longer displays the correct value.
			
			Default: false
			""")
	public boolean use_entity_interaction_range_attribute_as_attack_range = false;
	@Comment("""
			When set to false, weapon poses are not displayed while the player is sprinting.
			
			Default: true
			""")
	public boolean enable_poses_while_sprinting = true;
	@Comment("""
			
			
			The stamina cost of all attacks is multiplied with this value when the attack is feinted.
			
			Default: 1.0
			""")
	public float global_feint_stamina_cost_multiplier = 1.0F;
	@Comment("""
			The stamina cost of all attacks is multiplied with this value when the attack is executed.
			
			Default: 1.0
			""")
	public float global_attack_stamina_cost_multiplier = 1.0F;

	public ServerConfig() {

	}
}

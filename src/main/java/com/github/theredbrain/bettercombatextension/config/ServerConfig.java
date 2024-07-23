package com.github.theredbrain.bettercombatextension.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(
		name = "server"
)
public class ServerConfig implements ConfigData {
	@Comment("""
			When set to true, changing the players orientation and position while attacking is disabled.
			It is recommended to install Shoulder Surfing Reloaded, play in the third person perspective activate the decoupled camera setting.
			Since the movement_multiplier setting in the Better Combat server config has no effect when this is set to true, it is recommended to set it to its default of 1.0.
			Use the item tag "disables_movement_locking_during_attack" and the entity type tag "disables_movement_locking_when_ridden" to control when the movement locking is applied.
			
			Default: false
			""")
	public boolean enable_movement_locking_attacks = false;
	@Comment("""
			WIP
			When set to true, the pitch of the players attack is restricted.
			This currently only affects the animation.
			
			Default: false
			""")
	public boolean restrict_attack_pitch = false;
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

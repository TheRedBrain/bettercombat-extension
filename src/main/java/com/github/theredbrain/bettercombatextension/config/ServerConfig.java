package com.github.theredbrain.bettercombatextension.config;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;

@ConvertFrom(fileName = "server.json5", folder = "bettercombatextension")
public class ServerConfig extends Config {
	public ServerConfig() {
		super(BetterCombatExtension.identifier("server"));
	}
	@Comment("""
			When set to true, changing the players orientation and position while attacking is disabled.
			It is recommended to install Shoulder Surfing Reloaded, play in the third person perspective and activate the decoupled camera setting.
			Since the movement_multiplier setting in the Better Combat server config has no effect when this is set to true, it is recommended to set it to its default of 1.0.
			Use the item tag "disables_movement_locking_during_attack" and the entity type tag "disables_movement_locking_when_ridden" to control when the movement locking is applied.
			
			Default: false
			""")
	public ValidatedBoolean enable_movement_locking_attacks = new ValidatedBoolean(false);
	@Comment("""
			When set to true, jumping is disabled during attacks.
			Use the item tag "disables_jump_restriction_during_attack" and the entity type tag "disables_jump_restriction_when_ridden" to control when the jump restriction is applied.
			
			Default: false
			""")
	public ValidatedBoolean enable_jump_restriction_during_attacks = new ValidatedBoolean(false);
	@Comment("""
			When set to true, the pitch of the players attack is restricted.
			
			Default: false
			""")
	public ValidatedBoolean restrict_attack_pitch = new ValidatedBoolean(false);
	@Comment("""
			Only has an effect when 'restrict_attack_pitch' is set to 'true'.
			Describes the angle from 0, which means the final range is from -'attack_pitch_range' to 'attack_pitch_range'.
			
			Default: 15.0
			""")
	public ValidatedFloat attack_pitch_range = new ValidatedFloat(15.0F);
	@Comment("""
			Enables an alternative two_handed condition.
			If the offhand stack is empty and this is true, the mainhand stack is two_handed
			This is not active if the "two_handed" field in the weapon_attribute file is set to true.
			
			Default: false
			""")
	public ValidatedBoolean empty_offhand_equals_two_handing_mainhand = new ValidatedBoolean(false);
	@Comment("""
			Disables Better Combat's formerly client feature of continuously attacking while holding down the attack key.
			
			Default: false
			""")
	public ValidatedBoolean disable_better_combat_hold_to_attack = new ValidatedBoolean(false);
	@Comment("""
			When set to true, feinting an attack skips it.
			
			Default: false
			""")
	public ValidatedBoolean feinting_increases_combo_count = new ValidatedBoolean(false);
	@Comment("""
			
			
			The stamina cost of all attacks is multiplied with this value when the attack is feinted.
			
			Default: 1.0
			""")
	public ValidatedFloat global_feint_stamina_cost_multiplier = new ValidatedFloat(1.0F);
	@Comment("""
			The stamina cost of all attacks is multiplied with this value when the attack is executed.
			
			Default: 1.0
			""")
	public ValidatedFloat global_attack_stamina_cost_multiplier = new ValidatedFloat(1.0F);
	@Comment("""
			Better Combat Extension fixes an incompatibility between Better Combats offhand attacking and RPG Inventory's hand slot overhaul.
			
			This fix is only active when RPG Inventory is installed and the hand slot overhaul is enabled.
			
			This setting can disables the fix completely.
			
			WARNING:
			Setting this option to false will disable the RPG Inventory hand slot overhaul. This is done to prevent item duplication glitches.
			
			Default: true
			""")
	public ValidatedBoolean enable_experimental_swap_hand_attributes_algorithm = new ValidatedBoolean(true);
}

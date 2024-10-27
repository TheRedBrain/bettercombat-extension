# BetterCombat Extension

This is an extension to the [Better Combat](https://modrinth.com/mod/better-combat) mod by Daedelus.\
It adds several new features and settings which are all controlled from the server side.

## Additions to weapon_attribute files

In addition to the existing fields 'pose' and 'offhand_pose' there is an optional String field called 'two_handed_pose'.

Each attack element has two new optional fields:
- an int field called 'stamina_cost_multiplier'
- a String field called 'damage_type', when this is a valid identifier for a damage type, the attack deals damage using that damage type

### Example

```json
{
	"attributes": {
		"attack_range": 2.5,
		"category": "sword",
		"two_handed_pose": "bettercombat:pose_two_handed_sword",
		"attacks": [
			{
				"hitbox": "HORIZONTAL_PLANE",
				"damage_multiplier": 1,
				"stamina_cost_multiplier": 1,
				"angle": 120,
				"upswing": 0.5,
				"animation": "bettercombat:one_handed_slash_horizontal_right",
				"swing_sound": {
					"id": "bettercombat:sword_slash"
				}
			},
			{
				"hitbox": "HORIZONTAL_PLANE",
				"damage_multiplier": 1,
				"stamina_cost_multiplier": 1,
				"angle": 120,
				"upswing": 0.5,
				"animation": "bettercombat:one_handed_slash_horizontal_left",
				"swing_sound": {
					"id": "bettercombat:sword_slash"
				}
			},
			{
				"hitbox": "FORWARD_BOX",
				"damage_multiplier": 1,
				"stamina_cost_multiplier": 1.1,
				"angle": 0,
				"upswing": 0.5,
				"animation": "bettercombat:one_handed_stab",
				"swing_sound": {
					"id": "bettercombat:sword_slash",
					"pitch": 1.2
				}
			}
		]
	}
}
```

## Server Config

###  Better Combat x RPG Inventory Compatibility Experiment

Setting the "enable_experimental_swap_hand_attributes_algorithm" setting in the server config to true, enables this experimental compatibility.
This solves an item duplication glitch when using "Better Combat" and "RPG Inventory" together.

> This fix is very experimental!
>
> Please report any issues you encounter when using this setting.

### Movement Locking Attacks

Setting the "enable_movement_locking_attacks" setting in the server config to true, enables movement locking attacks.
This prevents the player from changing its orientation and position while a attack animation is active.

It is recommended to use the client-side mod [Shoulder Surfing Reloaded](https://modrinth.com/mod/shoulder-surfing-reloaded) and enable its "decoupled camera" setting when this setting is active.

The item tag "bettercombatextension:tags/items/disables_movement_locking_during_attack" and the entity type tag "bettercombatextension:tags/entity_types/disables_movement_locking_when_ridden" are used to control when the movement locking is applied.

Note that Better Combats "movement_speed_while_attacking" movement speed multiplier has no effect while this option is enabled.

### Restricting attack pitch

Setting the "restrict_attack_pitch" setting in the server config to true, restricts the attack pitch to a range defined by the "attack_pitch_range" server config setting.

This affects the animation and the attack hitbox.

### Exceptions to Better Combats "movement_speed_while_attacking" movement speed multiplier

While attacking with items in the item tag "bettercombatextension:tags/items/ignores_attack_movement_penalty" the multiplier is not applied.

### Attacks use "minecraft:player.entity_interaction_range" entity attribute instead of "attack_range" value in weapon_attribute file

Enabled when "use_entity_interaction_range_attribute_as_attack_range" setting in server config is true.

### Disabling Better Combats "Hold to attack" client setting

Enabled when "disable_better_combat_hold_to_attack" setting in server config is true.

### Toggleable Two-Handed Stance

Enabled when "empty_offhand_equals_two_handing_mainhand" setting in server config is true.

This is **not** active if the "two_handed" field in the weapon_attribute file is set to true.

If the offhand slot is empty, the idle pose defined via the "two_handed_pose" field in the weapon_attribute file is active.

This is designed to work together with the mod [RPG Inventory](https://modrinth.com/mod/rpg-inventory).

### Sprinting disables weapon poses

Enabled when "enable_poses_while_sprinting" setting in server config is false.

### Skipping attacks with feinting

Enabled when "feinting_increases_combo_count" setting in server config is true.

### Stamina Attributes Integration

When the [Stamina Attributes](https://modrinth.com/mod/stamina-attributes) mod is installed, every attack has a stamina cost. When the stamina cost is greater 0 and the player has less than 1 stamina, the attack is canceled.

The new entity attribute "generic:attack_stamina_cost" has a default base value of 1.0.

The cost is calculated like so:

```
stamina_cost = (value of "generic:attack_stamina_cost" entity attribute) * ("stamina_cost_multiplier" of the attack) * ("global_attack_stamina_cost_multiplier" setting in the server config")
```

If the attack is feinted, the cost is calculated like so:

```
stamina_cost = (value of "generic:attack_stamina_cost" entity attribute) * ("stamina_cost_multiplier" of the attack) * ("global_feint_stamina_cost_multiplier" setting in the server config")
```

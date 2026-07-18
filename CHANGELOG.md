# 2.11.1

now works with Better Combat 2.4.0

# 2.11.0

now works with Better Combat 2.3.2

## Additions

- added server config options to individually toggle position and orientation locking for movement locking attacks

# 2.10.1

- now works with Better Combat version 2.3.1

# 2.10.0

- now works with Better Combat version 2.3.0
- removed attack specific attack range and movement speed multipliers (these features are now included in Better Combat)

# 2.9.0

- added optional integration for "Attack Range Attribute"
- bumped Mixin Squared version, hopefully fixing crashes when used with Sinytra Connector

# 2.8.3

- now works with Better Combat version 2.2.5

# 2.8.2

- fixed all known issues with the Better Combat X Hand Slot Overhaul compatibility

# 2.8.1

- fixed an issue with the RPG Inventory integration, where 'empty hand weapons' could be added to normal hand slots

# 2.8.0

- added client config option to disable weapon poses while mounted
- fixed an issue with the RPG Inventory integration, where attacking with an empty main hand and a weapon in the offhand could duplicate items
- fixed an issue where the configs where initialized too early

# 2.7.0

- now works with Better Combat version 2.2.4
- added 3 new optional fields to attacks:
  - 'attack_range_multiplier', multiplies the attack range during the attack
  - 'attack_speed_multiplier', multiplies the attack speed during the attack
  - 'movement_speed_multiplier', multiplies the movement speed during the attack
- added new server config options that control the minimum and maximum values of movement speed modifiers

# 2.6.0

- added support for Better Combat version 2.2.2
- the item duplication fix for RPG Inventory x Better Combat is now automatically enabled if RPG Inventory is installed and the 'hand slot overhaul' is enabled. The existing server config overrides this. Note that disabling this setting also disables the 'hand slot overhaul'.

# 2.5.0

- re-added compat with 1.21
- added support for Better Combat version 2.1.3
- removed 'use_entity_interaction_range_attribute_as_attack_range' config option. This feature was implemented in Better Combat.

# 2.4.0

- added support for Better Combat version 2.0.4
- moved 'enable_poses_while_sprinting' from server to client config
- removed dependency on Cloth Config
- added dependency on Fzzy Config

# 2.3.0

- fixed a crash on game start
- added 'enable_poses_while_sprinting' config option

# 2.2.0

- added 'use_entity_interaction_range_attribute_as_attack_range' config option

# 2.1.0

Added an experimental config option that solves an item duplication glitch when using "Better Combat" and "RPG Inventory" together. This fix is very experimental!
Please report any issues you encounter when using this setting.

# 2.0.1

- fixed an issue where weapon_attributes would not inherit "damage_type" and "stamina_cost_multiplier" from their parent file

# 2.0.0

- updated to 1.21.1

# 1.3.0

- added 'attack_pitch_range' config option
- added 'disable_jumping_during_attacks' config option
- changed "restrict_attack_pitch" config option to also affect the attack hitbox
- fixed "enable_movement_locking_attacks" config option always preventing the player from moving

# 1.2.0

- Attack stamina cost calculation has been changed.

  Each attack now has a "attack_stamina_cost_multiplier" instead of a "attack_stamina_cost" float field. It defaults to 1.0. There is also a new entity attribute called "generic.attack_stamina_cost" which defaults to 1.0. The actual stamina cost is the product of these two values and the "global_attack_stamina_cost_multiplier" defined in the server config. This change allows the attack stamina cost to be changed in game.

- reworked the "disable_player_yaw_changes_during_attacks" setting

  renamed the option to "enable_movement_locking_attacks"

  the option now enables a proper movement lock during attacks

  it is recommended to use the "Shoulder Surfing Reloaded" mod and enable its "decoupled camera" setting when this setting is active

  the item tag "disables_movement_locking_during_attack" and the entity type tag "disables_movement_locking_when_ridden" are used to control when the movement locking is applied

# 1.1.1

- poses of two-handed weapons are now correctly displayed

# 1.1.0

- 'Stamina Attributes' is no longer a required dependency
- attack stamina_cost can now be a fractional value
- added global multipliers to attack stamina costs, one for attacks that are feinted, one for attacks that are actually executed. These multipliers can be set in the server config.
- added server config option to enable feinting to skip the current attack (Thank you, @ji.111 for the idea)
- fixed crash when attack damage_type field is not a valid damage type identifier

# 1.0.1

Fixed dependencies

# 1.0.0

First release!

#
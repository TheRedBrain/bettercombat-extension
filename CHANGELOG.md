# 1.2.0

- Attack stamina cost calculation has been changed.

  Each attack now has a "attack_stamina_cost_multiplier" instead of a "attack_stamina_cost" float field. It defaults to 1.0. There is also a new entity attribute called "generic.attack_stamina_cost" which defaults to 1.0. The actual stamina cost is the product of these two values and the "global_attack_stamina_cost_multiplier" defined in the server config. This change allows the attack stamina cost to be changed in game.

- added compatibility with Shoulder Surfing Reloaded

  When the "disable_player_yaw_changes_during_attacks" server config option is set to true, Shoulder Surfing's decoupled camera still functions and the player orientation can no longer be changed via the movement keys

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
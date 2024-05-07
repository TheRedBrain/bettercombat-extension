# BetterCombat Extension

This is an extension to the [Better Combat](https://modrinth.com/mod/better-combat) mod by Daedelus. It adds support for stamina provided by the mod [Stamina Attributes](https://modrinth.com/mod/stamina-attributes) and some additional config options.

## Server config

The server config has multiple options, which are disabled by default.

Notably, a alternative condition for two handing weapons can be enabled.

## Additions to weapon_attribute files

In addition to the existing fields 'pose' and 'offhand_pose' there is an optional String field called 'two_handed_pose'.

Each attack element has two new optional fields, an int field called 'stamina_cost' and a String field called 'damage_type'.

## Other changes

When an attack has a 'stamina_cost' > 0, the attack is canceled, if the player has less than 1 stamina.

Better Combat has a server config option to set a movement speed multiplier during an attack. All attacks with items in the new item tag 'ignores_attack_movement_penalty' ignore that penalty.
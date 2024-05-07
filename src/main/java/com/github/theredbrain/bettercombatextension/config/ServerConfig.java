package com.github.theredbrain.bettercombatextension.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(
        name = "server"
)
public class ServerConfig implements ConfigData {
    @Comment("""
            WIP
            When set to true, changing the players orientation while attacking is disabled.
            """)
    public boolean disable_player_yaw_changes_during_attacks = false;
    @Comment("""
            WIP
            When set to true, the pitch of the players attack is restricted.
            this currently only affects the animation
            """)
    public boolean restrict_attack_pitch = false;
    @Comment("""
            Enables an alternative two_handed condition.
            If the offhand stack is empty and this is true, the mainhand stack is two_handed
            """)
    public boolean empty_offhand_equals_two_handing_mainhand = false;
    @Comment("""
            Disables Better Combat's formerly client feature of
            continuously attacking while holding down the attack key.
            """)
    public boolean disable_better_combat_hold_to_attack = false;
    public ServerConfig() {

    }
}

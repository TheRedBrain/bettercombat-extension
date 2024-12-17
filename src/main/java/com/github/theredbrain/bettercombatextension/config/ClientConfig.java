package com.github.theredbrain.bettercombatextension.config;

import com.github.theredbrain.bettercombatextension.BetterCombatExtension;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;

public class ClientConfig extends Config {
	public ClientConfig() {
		super(BetterCombatExtension.identifier("client"));
	}

	public ValidatedBoolean enable_poses_while_sprinting = new ValidatedBoolean(true); // TODO move to client config
}

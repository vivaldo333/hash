package com.ks.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import static com.ks.constants.AppConstants.CONFIG_RESOURCE;

public final class Configs {

    private static Config CONFIG;

    private Configs() {
    }

    public static Config getConfig() {
        if (CONFIG == null) {
            CONFIG = ConfigFactory.parseResources(CONFIG_RESOURCE);
        }

        return CONFIG;
    }
}

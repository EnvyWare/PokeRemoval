package com.envyful.poke.removal.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.Maps;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
@ConfigPath("config/PokeRemoval/config.yml")
public class PokeRemovalConfig extends AbstractYamlConfig {

    private Map<String, RemovalSetting> removalSettings = Maps.newHashMap();


    @ConfigSerializable
    public static class RemovalSetting {

        private String name;
        private List<String> matchingRequirements;
        private boolean broadcastRemoval;
        private List<String> removalBroadcast;


    }
}

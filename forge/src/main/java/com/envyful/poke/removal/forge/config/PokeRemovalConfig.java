package com.envyful.poke.removal.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.envyful.api.reforged.pixelmon.PokemonSpec;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

@ConfigSerializable
@ConfigPath("config/PokeRemoval/config.yml")
public class PokeRemovalConfig extends AbstractYamlConfig {

    private Map<String, RemovalSetting> removalSettings = Maps.newHashMap();


    @ConfigSerializable
    public static class RemovalSetting {

        private String name;
        private RemovalType mode;
        private Set<String> removedEntities;
        private List<String> matchingRequirements;
        private boolean broadcastRemoval;
        private List<String> removalBroadcast;
        private int removalTime;
        private Map<String, WarningBroadcast> warningBroadcasts;

        public RemovalSetting() {
        }

        public RemovalSetting(String name, RemovalType mode, Set<String> removedEntities,
                              List<String> matchingRequirements, boolean broadcastRemoval, List<String> removalBroadcast, int removalTime, Map<String, WarningBroadcast> warningBroadcasts) {
            this.name = name;
            this.mode = mode;
            this.removedEntities = removedEntities;
            this.matchingRequirements = matchingRequirements;
            this.broadcastRemoval = broadcastRemoval;
            this.removalBroadcast = removalBroadcast;
            this.removalTime = removalTime;
            this.warningBroadcasts = warningBroadcasts;
        }

        public String getName() {
            return this.name;
        }

        public RemovalType getMode() {
            return this.mode;
        }

        public Set<String> getRemovedEntities() {
            return this.removedEntities;
        }

        public List<String> getMatchingRequirements() {
            return this.matchingRequirements;
        }

        public boolean isBroadcastRemoval() {
            return this.broadcastRemoval;
        }

        public List<String> getRemovalBroadcast() {
            return this.removalBroadcast;
        }

        public int getRemovalTime() {
            return this.removalTime;
        }

        public Map<String, WarningBroadcast> getWarningBroadcasts() {
            return this.warningBroadcasts;
        }
    }

    @ConfigSerializable
    public static class WarningBroadcast {

        private boolean enabled;
        private int timeBeforeRemoval;
        private List<String> broadcast;

        public WarningBroadcast() {
        }

        public WarningBroadcast(boolean enabled, int timeBeforeRemoval, List<String> broadcast) {
            this.enabled = enabled;
            this.timeBeforeRemoval = timeBeforeRemoval;
            this.broadcast = broadcast;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public int getTimeBeforeRemoval() {
            return this.timeBeforeRemoval;
        }

        public List<String> getBroadcast() {
            return this.broadcast;
        }
    }

    public enum RemovalType {

        WHITELIST((pokemonSpecs, pokemon) -> {
            for (PokemonSpec pokemonSpec : pokemonSpecs) {
                if (pokemonSpec.matches(pokemon)) {
                    return false;
                }
            }

            return true;
        }, (types, entity) -> {
            String entityString = EntityList.getEntityString(entity);

            for (String type : types) {
                if (type.equalsIgnoreCase(entityString)) {
                    return false;
                }
            }

            return true;
        }),
        BLACKLIST((pokemonSpecs, pokemon) -> {
            for (PokemonSpec pokemonSpec : pokemonSpecs) {
                if (pokemonSpec.matches(pokemon)) {
                    return true;
                }
            }

            return false;
        }, (types, entity) -> {
            String entityString = EntityList.getEntityString(entity);

            for (String type : types) {
                if (type.equalsIgnoreCase(entityString)) {
                    return true;
                }
            }

            return false;
        }),

        ;

        private final BiPredicate<List<PokemonSpec>, Pokemon> pokemonRemoval;
        private final BiPredicate<List<String>, Entity> entityRemoval;

        RemovalType(BiPredicate<List<PokemonSpec>, Pokemon> pokemonRemoval,
                    BiPredicate<List<String>, Entity> entityRemoval) {
            this.pokemonRemoval = pokemonRemoval;
            this.entityRemoval = entityRemoval;
        }

        public boolean shouldRemovePokemon(List<PokemonSpec> specs, Pokemon pokemon) {
            return this.pokemonRemoval.test(specs, pokemon);
        }

        public boolean shouldRemoveEntity(List<String> entities, Entity entity) {
            return this.entityRemoval.test(entities, entity);
        }
    }
}

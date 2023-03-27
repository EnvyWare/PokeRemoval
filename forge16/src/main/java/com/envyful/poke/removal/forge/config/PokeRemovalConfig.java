package com.envyful.poke.removal.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.pixelmonmod.api.pokemon.PokemonSpecification;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

@ConfigSerializable
@ConfigPath("config/PokeRemoval/config.yml")
public class PokeRemovalConfig extends AbstractYamlConfig {

    private Map<String, RemovalSetting> removalSettings = ImmutableMap.of(
            "one", new RemovalSetting(
                    "Pokemon", RemovalType.WHITELIST, Lists.newArrayList("pixelmon:pixelmon"),
                    Lists.newArrayList("!shiny", "boss:notboss"), true,
                    Lists.newArrayList(
                            " ",
                            "&c&l!!! INACTIVE POKEMON WERE JUST CLEARED FROM THE WORLD (%amount%) !!!",
                            " "
                    ),  30, 60,
                    ImmutableMap.of(
                            "first", new PokeRemovalConfig.WarningBroadcast(true, 1500, Lists.newArrayList("&e&l(!) " + "&eAll entities will be removed in 25 minutes")),
                            "second", new PokeRemovalConfig.WarningBroadcast(true, 600, Lists.newArrayList("&e&l(!) &eAll entities will be removed in 10 minutes")),
                            "third", new PokeRemovalConfig.WarningBroadcast(true, 30, Lists.newArrayList("&e&l(!) &eAll entities will be removed in 30 seconds"))
                    )
            ),
            "two", new RemovalSetting(
                    "Items", RemovalType.WHITELIST, Lists.newArrayList("minecraft:item"),
                    Collections.emptyList(), true,
                    Lists.newArrayList(
                            " ",
                            "&c&l!!! INACTIVE ITEMS WERE JUST CLEARED FROM THE WORLD (%amount%) !!!",
                            " "
                    ),  30, 60,
                    ImmutableMap.of(
                            "first", new PokeRemovalConfig.WarningBroadcast(true, 1500, Lists.newArrayList("&e&l(!) " + "&eAll entities will be removed in 25 minutes")),
                            "second", new PokeRemovalConfig.WarningBroadcast(true, 600, Lists.newArrayList("&e&l(!) &eAll entities will be removed in 10 minutes")),
                            "third", new PokeRemovalConfig.WarningBroadcast(true, 30, Lists.newArrayList("&e&l(!) &eAll entities will be removed in 30 seconds"))
                    )
            )
    );

    public PokeRemovalConfig() {
    }

    public Map<String, RemovalSetting> getRemovalSettings() {
        return this.removalSettings;
    }

    @ConfigSerializable
    public static class RemovalSetting {

        private String name;
        private RemovalType mode;
        private List<String> removedEntities;
        private List<String> matchingRequirements;
        private boolean broadcastRemoval;
        private List<String> removalBroadcast;
        private int removalTimeMinutes;
        private long ignoreEntitiesYoungerThan;
        private Map<String, WarningBroadcast> warningBroadcasts;
        private List<String> blacklistedWorlds = Lists.newArrayList();

        private transient boolean locked = false;

        private transient List<PokemonSpecification> matchingSpecs = null;

        public RemovalSetting() {
        }

        public RemovalSetting(String name, RemovalType mode, List<String> removedEntities,
                              List<String> matchingRequirements, boolean broadcastRemoval, List<String> removalBroadcast,
                              int removalTimeMinutes, long ignoreEntitiesYoungerThan, Map<String, WarningBroadcast> warningBroadcasts) {
            this.name = name;
            this.mode = mode;
            this.removedEntities = removedEntities;
            this.matchingRequirements = matchingRequirements;
            this.broadcastRemoval = broadcastRemoval;
            this.removalBroadcast = removalBroadcast;
            this.removalTimeMinutes = removalTimeMinutes;
            this.ignoreEntitiesYoungerThan = ignoreEntitiesYoungerThan;
            this.warningBroadcasts = warningBroadcasts;
        }

        public String getName() {
            return this.name;
        }

        public RemovalType getMode() {
            return this.mode;
        }

        public boolean shouldRemove(Entity entity) {
            if (this.ignoreEntitiesYoungerThan > 0 && entity.tickCount <= this.ignoreEntitiesYoungerThan) {
                return false;
            }

            if (!this.mode.shouldRemoveEntity(this.getRemovedEntities(), entity)) {
                return false;
            }

            if (entity instanceof PixelmonEntity) {
                PixelmonEntity pixelmon = (PixelmonEntity) entity;

                if (pixelmon.battleController != null) {
                    return false;
                }

                if (pixelmon.hasOwner()) {
                    return false;
                }

                return this.mode.shouldRemovePokemon(this.getMatchingRequirements(), pixelmon);
            }

            return true;
        }

        public List<String> getRemovedEntities() {
            return this.removedEntities;
        }

        public List<PokemonSpecification> getMatchingRequirements() {
            if (this.matchingSpecs == null) {
                List<PokemonSpecification> matchingSpecs = Lists.newArrayList();

                for (String matchingRequirement : this.matchingRequirements) {
                    matchingSpecs.add(PokemonSpecificationProxy.create(matchingRequirement));
                }

                this.matchingSpecs = matchingSpecs;
            }

            return this.matchingSpecs;
        }

        public boolean isBroadcastRemoval() {
            return this.broadcastRemoval;
        }

        public List<String> getRemovalBroadcast() {
            return this.removalBroadcast;
        }

        public int getRemovalTimeMinutes() {
            return this.removalTimeMinutes;
        }

        public long getIgnoreEntitiesYoungerThan() {
            return this.ignoreEntitiesYoungerThan;
        }

        public Map<String, WarningBroadcast> getWarningBroadcasts() {
            return this.warningBroadcasts;
        }

        public List<String> getBlacklistedWorlds() {
            return this.blacklistedWorlds;
        }

        public boolean isLocked() {
            return this.locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }
    }

    @ConfigSerializable
    public static class WarningBroadcast {

        private boolean enabled;
        private int timeBeforeRemovalSeconds;
        private List<String> broadcast;

        public WarningBroadcast() {
        }

        public WarningBroadcast(boolean enabled, int timeBeforeRemovalSeconds, List<String> broadcast) {
            this.enabled = enabled;
            this.timeBeforeRemovalSeconds = timeBeforeRemovalSeconds;
            this.broadcast = broadcast;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public int getTimeBeforeRemovalSeconds() {
            return this.timeBeforeRemovalSeconds;
        }

        public List<String> getBroadcast() {
            return this.broadcast;
        }
    }

    public enum RemovalType {

        WHITELIST((pokemonSpecs, pokemon) -> {
            for (PokemonSpecification pokemonSpec : pokemonSpecs) {
                if (pokemonSpec.matches(pokemon)) {
                    return true;
                }
            }

            return false;
        }, (types, entity) -> {
            if (entity.getType().getRegistryName() != null) {
                String entityString = entity.getType().getRegistryName().toString();

                for (String type : types) {
                    if (type.equalsIgnoreCase(entityString)) {
                        return true;
                    }
                }
            }
			
            return false;
        }),
        BLACKLIST((pokemonSpecs, pokemon) -> {
            for (PokemonSpecification pokemonSpec : pokemonSpecs) {
                if (pokemonSpec.matches(pokemon)) {
                    return false;
                }
            }

            return true;
        }, (types, entity) -> {
            if (entity.getType().getRegistryName() != null) {
                String entityString = entity.getType().getRegistryName().toString();

                for (String type : types) {
                    if (type.equalsIgnoreCase(entityString)) {
                        return false;
                    }
                }
            }

            return true;
        }),

        ;

        private final BiPredicate<List<PokemonSpecification>, PixelmonEntity> pokemonRemoval;
        private final BiPredicate<List<String>, Entity> entityRemoval;

        RemovalType(BiPredicate<List<PokemonSpecification>, PixelmonEntity> pokemonRemoval,
                    BiPredicate<List<String>, Entity> entityRemoval) {
            this.pokemonRemoval = pokemonRemoval;
            this.entityRemoval = entityRemoval;
        }

        public boolean shouldRemovePokemon(List<PokemonSpecification> specs, PixelmonEntity pokemon) {
            return this.pokemonRemoval.test(specs, pokemon);
        }

        public boolean shouldRemoveEntity(List<String> entities, Entity entity) {
            return this.entityRemoval.test(entities, entity);
        }
    }
}

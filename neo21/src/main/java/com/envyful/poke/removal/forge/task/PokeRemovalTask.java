package com.envyful.poke.removal.forge.task;

import com.envyful.api.neoforge.world.UtilWorld;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.api.text.Placeholder;
import com.envyful.poke.removal.forge.PokeRemovalForge;
import com.envyful.poke.removal.forge.config.PokeRemovalConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PokeRemovalTask implements Runnable {

    private final PokeRemovalForge mod;
    private final Map<String, Long> lastRemoval = Maps.newHashMap();

    public PokeRemovalTask(PokeRemovalForge mod) {
        this.mod = mod;
    }

    @Override
    public void run() {
        if (PlatformProxy.getPlayerManager().getOnlinePlayers().size() < this.mod.getConfig().getMinOnlinePlayers()) {
            return;
        }

        List<PokeRemovalConfig.RemovalSetting> toRemove = Lists.newArrayList();

        for (var value : this.mod.getConfig().getRemovalSettings().values()) {
            long time = TimeUnit.MINUTES.toMillis(value.getRemovalTimeMinutes());
            long lastRemovalDuration = (this.getLastRemoval(value) + time) - System.currentTimeMillis();

            if (lastRemovalDuration <= 0 && !value.isLocked()) {
                toRemove.add(value);
            }

            for (var warningBroadcast : value.getWarningBroadcasts().values()) {
                if (!warningBroadcast.isEnabled()) {
                    continue;
                }

                if (TimeUnit.MILLISECONDS.toSeconds(lastRemovalDuration) == warningBroadcast.getTimeBeforeRemovalSeconds()) {
                    PlatformProxy.broadcastMessage(warningBroadcast.getBroadcast());
                }
            }
        }

        if (toRemove.isEmpty()) {
            return;
        }

        for (var removalSetting : toRemove) {
            int removed = 0;
            removalSetting.setLocked(true);

            for (var world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
                if (removalSetting.getBlacklistedWorlds().contains(UtilWorld.getName(world))) {
                    continue;
                }

                for (Entity next : world.getEntities().getAll()) {
                    if (next != null && removalSetting.shouldRemove(next)) {
                        next.kill();
                        ++removed;
                    }
                }
            }

            this.lastRemoval.put(removalSetting.getName(), System.currentTimeMillis());

            if (!removalSetting.isBroadcastRemoval()) {
                continue;
            }

            PlatformProxy.broadcastMessage(removalSetting.getRemovalBroadcast(), Placeholder.simple("%amount%", removed + ""));
            removalSetting.setLocked(false);
        }
    }

    public long getLastRemoval(PokeRemovalConfig.RemovalSetting setting) {
        return this.lastRemoval.computeIfAbsent(setting.getName(), s -> System.currentTimeMillis());
    }
}

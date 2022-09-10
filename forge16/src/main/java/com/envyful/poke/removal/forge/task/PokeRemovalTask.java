package com.envyful.poke.removal.forge.task;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.world.UtilWorld;
import com.envyful.poke.removal.forge.PokeRemovalForge;
import com.envyful.poke.removal.forge.config.PokeRemovalConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PokeRemovalTask implements Runnable {

    private final PokeRemovalForge mod;
    private final Map<String, Long> lastRemoval = Maps.newHashMap();

    public PokeRemovalTask(PokeRemovalForge mod) {this.mod = mod;}

    @Override
    public void run() {
        List<PokeRemovalConfig.RemovalSetting> toRemove = Lists.newArrayList();

        for (PokeRemovalConfig.RemovalSetting value : this.mod.getConfig().getRemovalSettings().values()) {
            long time = TimeUnit.MINUTES.toMillis(value.getRemovalTimeMinutes());
            long lastRemovalDuration = (this.getLastRemoval(value) + time) - System.currentTimeMillis();

            if (lastRemovalDuration <= 0) {
                toRemove.add(value);
            }

            for (PokeRemovalConfig.WarningBroadcast warningBroadcast : value.getWarningBroadcasts().values()) {
                if (!warningBroadcast.isEnabled()) {
                    continue;
                }

                if (TimeUnit.MILLISECONDS.toSeconds(lastRemovalDuration) == warningBroadcast.getTimeBeforeRemovalSeconds()) {
                    for (String s : warningBroadcast.getBroadcast()) {
                        ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(UtilChatColour.colour(s), ChatType.CHAT, Util.NIL_UUID);
                    }
                }
            }
        }

        if (toRemove.isEmpty()) {
            return;
        }

        for (PokeRemovalConfig.RemovalSetting removalSetting : toRemove) {
            if (removalSetting.isLocked()) {
                continue;
            }

            int removed = 0;
            removalSetting.setLocked(true);

            for (ServerWorld world : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
                if (removalSetting.getBlacklistedWorlds().contains(UtilWorld.getName(world))) {
                    continue;
                }

                Iterator<Entity> iterator = world.getEntities().iterator();

                while (iterator.hasNext()) {
                    Entity next = iterator.next();

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

            for (String s : removalSetting.getRemovalBroadcast()) {
                ServerLifecycleHooks.getCurrentServer().getPlayerList().broadcastMessage(UtilChatColour.colour(s.replace(
                        "%amount%",
                        removed + ""
                )), ChatType.CHAT, Util.NIL_UUID);
            }

            removalSetting.setLocked(false);
        }

    }

    public long getLastRemoval(PokeRemovalConfig.RemovalSetting setting) {
        return this.lastRemoval.computeIfAbsent(setting.getName(), s -> System.currentTimeMillis());
    }
}

package com.envyful.poke.removal.forge.task;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.poke.removal.forge.PokeRemovalForge;
import com.envyful.poke.removal.forge.config.PokeRemovalConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

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
            long lastRemovalDuration = System.currentTimeMillis() - this.getLastRemoval(value);

            if (lastRemovalDuration >= TimeUnit.MINUTES.toMillis(value.getRemovalTimeMinutes())) {
                toRemove.add(value);
            }

            for (PokeRemovalConfig.WarningBroadcast warningBroadcast : value.getWarningBroadcasts().values()) {
                if (!warningBroadcast.isEnabled()) {
                    continue;
                }

                if (TimeUnit.MILLISECONDS.toSeconds(lastRemovalDuration) == warningBroadcast.getTimeBeforeRemovalSeconds()) {
                    for (String s : warningBroadcast.getBroadcast()) {
                        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(
                                new TextComponentString(UtilChatColour.translateColourCodes('&', s))
                        );
                    }
                }
            }
        }

        if (toRemove.isEmpty()) {
            return;
        }

        for (PokeRemovalConfig.RemovalSetting removalSetting : toRemove) {
            int removed = 0;

            for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds) {
                Iterator<Entity> iterator = world.loadedEntityList.iterator();

                while (iterator.hasNext()) {
                    Entity next = iterator.next();

                    if (removalSetting.shouldRemove(next)) {
                        next.setDead();
                        ++removed;
                    }
                }
            }

            for (String s : removalSetting.getRemovalBroadcast()) {
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendMessage(
                        new TextComponentString(UtilChatColour.translateColourCodes('&', s.replace(
                                "%amount%",
                                removed + ""
                        )))
                );
            }

            this.lastRemoval.put(removalSetting.getName(), System.currentTimeMillis());
        }

    }

    public long getLastRemoval(PokeRemovalConfig.RemovalSetting setting) {
        return this.lastRemoval.computeIfAbsent(setting.getName(), s -> System.currentTimeMillis());
    }

}

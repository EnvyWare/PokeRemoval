package com.envyful.poke.removal.forge;

import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.concurrency.ForgeTaskBuilder;
import com.envyful.poke.removal.forge.command.PokeRemovalCommand;
import com.envyful.poke.removal.forge.config.PokeRemovalConfig;
import com.envyful.poke.removal.forge.task.PokeRemovalTask;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.io.IOException;

@Mod("pokeremoval")
public class PokeRemovalForge {

    private static PokeRemovalForge instance;

    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private PokeRemovalConfig config;

    public PokeRemovalForge() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInit(FMLServerStartingEvent event) {
        instance = this;
        this.reloadConfig();

        new ForgeTaskBuilder()
                .task(new PokeRemovalTask(this))
                .interval(20L)
                .async(false)
                .delay(20L)
                .start();
    }

    public void reloadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(PokeRemovalConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onServerStart(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), new PokeRemovalCommand());
    }

    public static PokeRemovalForge getInstance() {
        return instance;
    }

    public PokeRemovalConfig getConfig() {
        return this.config;
    }
}

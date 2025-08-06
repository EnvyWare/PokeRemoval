package com.envyful.poke.removal.forge;

import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.neoforge.command.ForgeCommandFactory;
import com.envyful.api.neoforge.command.parser.ForgeAnnotationCommandParser;
import com.envyful.api.neoforge.concurrency.ForgeTaskBuilder;
import com.envyful.poke.removal.forge.command.PokeRemovalCommand;
import com.envyful.poke.removal.forge.config.PokeRemovalConfig;
import com.envyful.poke.removal.forge.task.PokeRemovalTask;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.io.IOException;

@Mod("pokeremoval")
public class PokeRemovalForge {

    private static PokeRemovalForge instance;

    private ForgeCommandFactory commandFactory = new ForgeCommandFactory(ForgeAnnotationCommandParser::new, null);

    private PokeRemovalConfig config;

    public PokeRemovalForge() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInit(ServerStartingEvent event) {
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
        this.commandFactory.registerCommand(event.getDispatcher(), this.commandFactory.parseCommand(new PokeRemovalCommand()));
    }

    public static PokeRemovalForge getInstance() {
        return instance;
    }

    public PokeRemovalConfig getConfig() {
        return this.config;
    }
}

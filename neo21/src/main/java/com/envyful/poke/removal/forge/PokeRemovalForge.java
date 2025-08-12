package com.envyful.poke.removal.forge;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.neoforge.chat.ComponentTextFormatter;
import com.envyful.api.neoforge.command.ForgeCommandFactory;
import com.envyful.api.neoforge.command.parser.ForgeAnnotationCommandParser;
import com.envyful.api.neoforge.concurrency.ForgeTaskBuilder;
import com.envyful.api.neoforge.gui.factory.ForgeGuiFactory;
import com.envyful.api.neoforge.platform.ForgePlatformHandler;
import com.envyful.api.neoforge.player.ForgePlayerManager;
import com.envyful.api.platform.PlatformProxy;
import com.envyful.poke.removal.forge.command.PokeRemovalCommand;
import com.envyful.poke.removal.forge.config.PokeRemovalConfig;
import com.envyful.poke.removal.forge.task.PokeRemovalTask;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod("pokeremoval")
public class PokeRemovalForge {

    private static final Logger LOGGER = LogManager.getLogger("EnvyGTS");
    private static PokeRemovalForge instance;

    private ForgePlayerManager playerManager = new ForgePlayerManager();
    private ForgeCommandFactory commandFactory = new ForgeCommandFactory(ForgeAnnotationCommandParser::new, playerManager);

    private PokeRemovalConfig config;

    public PokeRemovalForge() {
        instance = this;
        NeoForge.EVENT_BUS.register(this);
        UtilLogger.setLogger(LOGGER);

        GuiFactory.setPlatformFactory(new ForgeGuiFactory());
        PlatformProxy.setPlayerManager(playerManager);
        PlatformProxy.setHandler(ForgePlatformHandler.getInstance());
        PlatformProxy.setTextFormatter(ComponentTextFormatter.getInstance());
    }

    @SubscribeEvent
    public void onInit(ServerStartingEvent event) {
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

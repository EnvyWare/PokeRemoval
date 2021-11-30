package com.envyful.poke.removal.forge;

import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.concurrency.ForgeTaskBuilder;
import com.envyful.poke.removal.forge.command.PokeRemovalCommand;
import com.envyful.poke.removal.forge.config.PokeRemovalConfig;
import com.envyful.poke.removal.forge.task.PokeRemovalTask;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.io.IOException;

@Mod(
        modid = PokeRemovalForge.MOD_ID,
        name = "BetterDexRewards Forge",
        version = PokeRemovalForge.VERSION,
        acceptableRemoteVersions = "*",
        updateJSON = "" //TODO:
)
public class PokeRemovalForge {

    protected static final String MOD_ID = "pokeremoval";
    protected static final String VERSION = "0.0.1";

    @Mod.Instance(MOD_ID)
    private static PokeRemovalForge instance;

    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private PokeRemovalConfig config;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
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

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        this.commandFactory.registerCommand(event.getServer(), new PokeRemovalCommand());
    }

    public static PokeRemovalForge getInstance() {
        return instance;
    }

    public PokeRemovalConfig getConfig() {
        return this.config;
    }
}

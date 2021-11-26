package com.envyful.poke.removal.forge;

import com.envyful.api.forge.command.ForgeCommandFactory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

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

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {

    }

    public static PokeRemovalForge getInstance() {
        return instance;
    }
}

package com.envyful.poke.removal.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.poke.removal.forge.PokeRemovalForge;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "pokeremoval",
        description = "Reloads the config"
)
@Permissible("poke.removal.forge.command")
public class PokeRemovalCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSender sender, String[] args) {
        PokeRemovalForge.getInstance().reloadConfig();
        sender.sendMessage(new TextComponentString("Reloaded config!"));
    }
}

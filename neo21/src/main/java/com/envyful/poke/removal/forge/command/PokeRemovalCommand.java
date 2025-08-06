package com.envyful.poke.removal.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.command.annotate.permission.Permissible;
import com.envyful.poke.removal.forge.PokeRemovalForge;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;

@Command(
        value = "pokeremoval"
)
@Permissible("poke.removal.forge.command")
public class PokeRemovalCommand {

    @CommandProcessor
    public void onCommand(@Sender CommandSource sender, String[] args) {
        PokeRemovalForge.getInstance().reloadConfig();
        sender.sendSystemMessage(Component.literal("Reloaded config!"));
    }
}

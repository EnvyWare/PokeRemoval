package com.envyful.poke.removal.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.poke.removal.forge.PokeRemovalForge;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

@Command(
        value = "pokeremoval",
        description = "Reloads the config"
)
@Permissible("poke.removal.forge.command")
public class PokeRemovalCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender, String[] args) {
        PokeRemovalForge.getInstance().reloadConfig();
        sender.sendMessage(new StringTextComponent("Reloaded config!"), Util.NIL_UUID);
    }
}

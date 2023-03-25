package com.envyful.poke.removal.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.poke.removal.forge.PokeRemovalForge;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

@Command(
        value = "pokeremoval",
        description = "Reloads the config"
)
@Permissible("poke.removal.forge.command")
public class PokeRemovalCommand {

    @CommandProcessor
    public void onCommand(@Sender ICommandSource sender, String[] args) {
        if(args.length == 0){
           Reload(sender);
           return;
        }

        if(args[0].contains("reload") || args[0].isEmpty()){
            Reload(sender);
            return;
        }

        if (args[0].contains("specs")) {
            StringTextComponent text = (StringTextComponent) new StringTextComponent("Specs: ").withStyle(TextFormatting.GOLD);

            List<String> specs = PixelmonCommandUtils.SPEC_REQUIREMENTS;
            text.append(new StringTextComponent(specs.get(0)).withStyle(TextFormatting.DARK_AQUA));

            for( int i = 2; i != specs.size(); i += 2 ) {
                text.append(new StringTextComponent(", ").withStyle(TextFormatting.GOLD)).append(new StringTextComponent(specs.get(i + 1)).withStyle(TextFormatting.DARK_AQUA));
            }

            sender.sendMessage(text, Util.NIL_UUID);

            return;
        }

        sender.sendMessage(new StringTextComponent("/pokeremoval reload").withStyle(TextFormatting.RED), Util.NIL_UUID);
        sender.sendMessage(new StringTextComponent("/pokeremoval specs").withStyle(TextFormatting.RED), Util.NIL_UUID);
    }

    private void Reload(ICommandSource sender){
        PokeRemovalForge.getInstance().reloadConfig();
        sender.sendMessage(new StringTextComponent("Reloaded config!").withStyle(TextFormatting.GOLD), Util.NIL_UUID);
    }
}

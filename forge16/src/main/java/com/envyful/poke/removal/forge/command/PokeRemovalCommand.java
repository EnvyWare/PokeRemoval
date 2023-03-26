package com.envyful.poke.removal.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.poke.removal.forge.PokeRemovalForge;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
            Runnable specsTask = () -> {
                String search = null;
                int size = 3;

                if(args.length > 1){
                    try{
                        size = Math.max(Integer.parseInt(args[1]), 1);
                    }
                    catch (Exception e){
                        e.printStackTrace();;
                    }
                }

                if(args.length > 2){
                    search = args[2];
                }

                List<ITextComponent> components = getSpecListTC(search, size);

                sender.sendMessage(new StringTextComponent(TextFormatting.GOLD + "Specs: "), Util.NIL_UUID);
                int count = 0;
                for (ITextComponent component: components) {
                    count++;
                    sender.sendMessage(component, Util.NIL_UUID);
                    if (count > 40)
                    {
                        String fileName = "PokeRemoval/spec-" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".txt";
                        File specFile = new File(FMLPaths.CONFIGDIR.get().toFile(), fileName);
                        try {
                            Files.write(specFile.toPath(), getSpecListStr(search, Math.max(size, 4)), StandardCharsets.UTF_8);
                            sender.sendMessage(new StringTextComponent(TextFormatting.GOLD + "List was too big full list is saved in: " + fileName), Util.NIL_UUID);
                        } catch (IOException e) {
                            sender.sendMessage(new StringTextComponent(TextFormatting.RED + "List was too big full list didn't save due to an error: " + fileName), Util.NIL_UUID);
                            e.printStackTrace();
                        }

                        break;
                    }
                }
            };

            Thread thread = new Thread(specsTask);
            thread.setName("SpecsThread");
            thread.start();

            return;
        }

        sender.sendMessage(new StringTextComponent("/pokeremoval reload").withStyle(TextFormatting.RED), Util.NIL_UUID);
        sender.sendMessage(new StringTextComponent("/pokeremoval specs <items per row> <search>").withStyle(TextFormatting.RED), Util.NIL_UUID);
    }

    private List<ITextComponent> getSpecListTC(String search, int size){
        final List<ITextComponent> components = new ArrayList<>();

        for(String spec : getSpecListStr(search, size)) {
            components.add(new StringTextComponent(TextFormatting.DARK_AQUA + spec.replace(", ",TextFormatting.GOLD + ", " + TextFormatting.DARK_AQUA)));
        }

        return components;
    }

    private List<String> getSpecListStr(String search, int size){
        final AtomicInteger atomicInteger = new AtomicInteger(0);

        Collection<List<String>> data;

        if(search != null && !search.isEmpty()){
            data = PixelmonCommandUtils.SPEC_REQUIREMENTS.stream().filter(s ->  s.contains(search)).collect(Collectors.groupingBy(it -> atomicInteger.getAndIncrement() / size)).values();
        }
        else {
            data =PixelmonCommandUtils.SPEC_REQUIREMENTS.stream().collect(Collectors.groupingBy(str -> atomicInteger.getAndIncrement() / size)).values();
        }

        return data.stream().map(row -> String.join(", ", row)).collect(Collectors.toList());
    }

    private void Reload(ICommandSource sender){
        PokeRemovalForge.getInstance().reloadConfig();
        sender.sendMessage(new StringTextComponent("Reloaded config!").withStyle(TextFormatting.GOLD), Util.NIL_UUID);
    }
}

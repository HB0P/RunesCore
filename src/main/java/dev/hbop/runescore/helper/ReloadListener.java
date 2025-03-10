package dev.hbop.runescore.helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.hbop.runescore.RunesCore;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReloadListener implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return RunesCore.identifier("");
    }

    @Override
    public void reload(ResourceManager manager) {
        Map<Identifier, RuneTemplate> templates = new HashMap<>();
        Map<Identifier, Resource> resources = manager.findResources("runes", identifier -> identifier.getPath().endsWith(".json"));
        for (Identifier identifier : resources.keySet()) {
            Resource resource = resources.get(identifier);
            try {
                JsonElement element = JsonParser.parseReader(resource.getReader());
                DataResult<RuneTemplate> result = RuneTemplate.CODEC.parse(JsonOps.INSTANCE, element);
                Optional<RuneTemplate> optionalTemplate = result.resultOrPartial(str -> RunesCore.LOGGER.error("Couldn't parse rune template '" + identifier + "': " + str));
                optionalTemplate.ifPresent(template -> {
                    if (templates.containsKey(template.identifier())) {
                        if (template.priority() > templates.get(template.identifier()).priority()) {
                            templates.put(template.identifier(), template);
                        }
                        else if (template.priority() == templates.get(template.identifier()).priority()) {
                            RunesCore.LOGGER.warn("Found multiple runes with identifier '" + template.identifier() + "' and equal priority " + template.priority() + " - only one will be loaded");
                        }
                    }
                    else {
                        templates.put(template.identifier(), template);
                    }
                });
            } catch (IOException | JsonSyntaxException e) {
                RunesCore.LOGGER.error("Couldn't parse rune template '" + identifier + "': invalid json");
            }
        }
        RuneHelper.RUNE_TEMPLATES = templates.values().stream().toList();
    }
}
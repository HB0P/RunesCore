package dev.hbop.runescore.helper;

import com.google.gson.Gson;
import dev.hbop.runescore.RunesCore;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

class ReloadListener implements SimpleSynchronousResourceReloadListener {

    private final Map<Identifier, Integer> priorities = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return RunesCore.identifier("");
    }

    @Override
    public void reload(ResourceManager manager) {
        for (String namespace : manager.getAllNamespaces()) {
            Optional<Resource> resource = manager.getResource(Identifier.of(namespace, "rune/runes.json"));
            if (resource.isEmpty()) continue;
            try (InputStream stream = resource.get().getInputStream()) {
                processStream(stream);
            } catch (IOException e) {
                RunesCore.LOGGER.error("Failed to read runes.json from datapack " + namespace);
            }
        }
    }

    private void processStream(InputStream stream) {
        // read json and filter to ensure all required fields are present
        RuneJson[] runeJsons = Arrays.stream(new Gson().fromJson(new InputStreamReader(stream), RuneJson[].class)).filter((runeJson) -> {
            if (runeJson.id == null) {
                RunesCore.LOGGER.warn("Found rune with no identifier - skipping");
                return false;
            }
            if (runeJson.max_level == 0) {
                RunesCore.LOGGER.warn("Rune [" + runeJson.id + "] has no max_level - skipping");
                return false;
            }
            if (runeJson.enchantments == null) {
                RunesCore.LOGGER.warn("Rune [" + runeJson.id + "] has no enchantments - skipping");
                return false;
            }
            return true;
        }).toArray(RuneJson[]::new);

        // create rune infos
        for (RuneJson runeJson : runeJsons) {
            Identifier id = Identifier.of(runeJson.id);
            // handle runes with same priority
            if (priorities.containsKey(id)) {
                if (runeJson.priority < priorities.get(id)) {
                    continue;
                }
                if (runeJson.priority > priorities.get(id)) {
                    RuneHelper.RUNE_INFOS.removeIf(runeInfo -> runeInfo.getIdentifier().equals(id));
                }
                else {
                    RunesCore.LOGGER.warn("Found multiple runes with identifier [" + runeJson.id + "] and equal priority " + runeJson.priority + " - only one will be loaded");
                    continue;
                }
            }
            priorities.put(id, runeJson.priority);

            // create enchantments map
            Map<Supplier<TagKey<Item>>, Function<RegistryWrapper.Impl<Enchantment>, List<RegistryEntry<Enchantment>>>> enchantments = new HashMap<>();
            for (String strTag : runeJson.enchantments.keySet()) {
                String strEnchantment = runeJson.enchantments.get(strTag);
                enchantments.put(
                        () -> Registries.ITEM.streamTagKeys().filter((tag) -> strTag.equals("#" + tag.id().toString())).findAny().orElseThrow(),
                        (registryWrapper) -> List.of(registryWrapper.streamEntries().filter((reference) -> reference.getIdAsString().equals(strEnchantment)).findAny().orElseThrow())
                );
            }

            RuneHelper.RUNE_INFOS.add(new RuneInfo(
                    id,
                    runeJson.max_level,
                    runeJson.base_size,
                    runeJson.bonus_size,
                    enchantments
            ));
        }
    }

    private static class RuneJson {
        String id;
        int max_level;
        int base_size;
        int bonus_size;
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        Map<String, String> enchantments;
        int priority;
    }
}
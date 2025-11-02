import json

corrupt_color = 16733525 | 0xFF000000
colors = [i | 0xFF000000 for i in [16777215, 16777045, 5635925, 5636095, 16733695]]

with open("list.txt") as f:
    for line in f.readlines():
        id = line.replace("\n", "")
        # item model
        with open("models/item/rune_" + id + ".json", "w") as g:
            g.write('{"parent": "item/generated", "textures": {"layer0": "runescore:item/rune", "layer1": "enchantmentrunes:item/rune_' + id + '"}}')
        # item definition
        with open("items/rune_" + id + ".json", "w") as g:
            with open("../../../../saves/Runes Core Test World/datapacks/enchantmentrunes/data/enchantmentrunes/runes/" + id + ".json") as h:
                max_level = json.load(h)["max_level"]
            colors_subset = [corrupt_color] + colors[-max_level:]
            g.write('{"model": {"type": "minecraft:model", "model": "enchantmentrunes:item/rune_' + id + '", "tints": [{"type": "minecraft:constant", "value": -1}, {"type": "runescore:rune", "colors": ' + str(colors_subset) + '}]}}')
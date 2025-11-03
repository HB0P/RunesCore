import os

path = "../../../../../../saves/testworld/datapacks/vanillarunes/data/vanillarunes/runescore/rune"
for name in os.listdir(path):
    with open(name, "w") as f:
        f.write("""{
    "model": {
        "type": "minecraft:model",
        "model": "vanillarunes:item/rune/""" + name[:-5] + """"
    }
}
        """)

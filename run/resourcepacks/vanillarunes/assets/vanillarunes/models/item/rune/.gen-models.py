import os

path = "../../../../../../../saves/testworld/datapacks/vanillarunes/data/vanillarunes/runescore/rune"
for name in os.listdir(path):
    with open(name, "w") as f:
        f.write("""{
  "parent": "item/generated",
  "textures": {
    "layer0": "runescore:item/rune"
  }
}
        """)

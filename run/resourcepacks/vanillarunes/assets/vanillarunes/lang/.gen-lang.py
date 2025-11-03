import os, json

lang = {}
levels = ["I", "II", "III", "IV", "V"]
for f in os.listdir("../../../../../saves/testworld/datapacks/vanillarunes/data/vanillarunes/runescore/rune"):
    lang["item.vanillarunes.rune." + f[:-5]] = "Rune of " + f[:-7].capitalize() + " " + levels[int(f[-6]) - 1]

with open("en_us.json", "w") as f:
    json.dump(lang, f)
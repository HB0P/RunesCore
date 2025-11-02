import os, json

for path in os.listdir("rune_template"):
    print(path)
    with open("rune_template/" + path) as f:
        data = json.load(f)
    data["item_model"] = "enchantmentrunes:rune_" + path[:-5]
    with open("rune_template/" + path, "w") as f:
        json.dump(data, f)

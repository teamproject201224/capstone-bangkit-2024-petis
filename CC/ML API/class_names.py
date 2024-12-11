class_names = {
    0: "Cashew anthracnose",
    1: "Cashew gumosis",
    2: "Cashew healthy",
    3: "Cashew leaf miner",
    4: "Cashew red rust",
    5: "Cassava bacterial blight",
    6: "Cassava brown spot",
    7: "Cassava green mite",
    8: "Cassava healthy",
    9: "Cassava mosaic"
}

def get_class_name(predicted_class):
    return class_names.get(predicted_class, "Unknown class")

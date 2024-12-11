import urllib.parse

def generate_link(product_name):
    base_url = "https://plant-info-api-petiss-977686857428.asia-southeast2.run.app/products/"
    product_slug = urllib.parse.quote(product_name.lower())
    return base_url + product_slug

solutions = {
    0: ["Symbush 50 EC", "Actara"],
    1: ["Fipronil", "Klorpirifos"],
    2: [],
    3: ["Imidakloprid", "Fipronil"],
    4: ["Symbush 50 EC", "Klorpirifos"],
    5: ["Pumicidin", "Vertimec"],
    6: ["Beauveria bassiana", "Symbush 50 EC"],
    7: ["Vertimec", "Beauveria bassiana"],
    8: [],
    9: ["Neem Oil", "Diazinon"]
}

def get_solution(predicted_class):
    return [{"name": product, "link": generate_link(product)} for product in solutions.get(predicted_class, ["No solution available"])]

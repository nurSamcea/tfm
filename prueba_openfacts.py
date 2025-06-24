import requests

# Código de barras de ejemplo (puedes poner cualquier código real)
barcode = "3017620422003"  # Nutella

# URL de la API de OpenFoodFacts
url = f"https://world.openfoodfacts.org/api/v0/product/{barcode}.json"

# Hacemos la consulta
response = requests.get(url)

# Comprobamos si existe
if response.status_code == 200:
    data = response.json()
    if data.get("status") == 1:
        product = data["product"]
        print("Nombre:", product.get("product_name", "Desconocido"))
        print("Marca:", product.get("brands", ""))
        print("Nutri-Score:", product.get("nutriscore_grade", ""))
        print("Ingredientes:", product.get("ingredients_text", ""))
    else:
        print("Producto no encontrado.")
else:
    print("Error en la API.")

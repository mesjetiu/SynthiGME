import requests

# URL de la API para obtener los tags del repositorio
url = "https://api.github.com/repos/mesjetiu/SynthiGME/tags"

# Realizar la solicitud GET a la API
response = requests.get(url)

# Comprobar si la solicitud fue exitosa
if response.status_code == 200:
    tags = response.json()
    # Extraer y mostrar los nombres de los tags
    tag_names = [tag['name'] for tag in tags]
    print(tag_names)
else:
    print("Error al obtener los tags:", response.status_code)

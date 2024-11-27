import sys
import os
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import load_img, img_to_array
import numpy as np
import json
import logging

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'
os.environ["CUDA_VISIBLE_DEVICES"] = ""
logging.basicConfig(filename="debug.log", level=logging.DEBUG)

def classify_image(image_path, model_path):
    logging.debug(f"Imagem recebida: {image_path}")
    logging.debug(f"Modelo recebido: {model_path}")

    if not os.path.exists(model_path):
        error_message = f"Modelo não encontrado no caminho especificado: {model_path}"
        logging.error(error_message)
        return json.dumps({"error": error_message})

    try:
        model = load_model(model_path)
        logging.debug("Modelo carregado com sucesso.")

        img = load_img(image_path, target_size=(224, 224))
        img_array = img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0)
        img_array = img_array / 255.0

        logging.debug("Imagem processada com sucesso.")

        prediction = model.predict(img_array)
        predicted_class = np.argmax(prediction)
        prediction_percentages = (prediction * 100).round(2)
        probabilities = [int(i) for i in prediction_percentages[0]]

        result = {
            "Classe Prevista": str(predicted_class),
            "Probabilidades": probabilities
        }
        logging.debug(f"Resultado: {result}")
        return json.dumps(result)

    except Exception as e:
        error_message = f"Erro ao classificar a imagem: {str(e)}"
        logging.error(error_message)
        return json.dumps({"error": error_message})

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print(json.dumps({"error": "Uso incorreto: python color_classifier_model.py <caminho_da_imagem> <caminho_do_modelo>"}))
        sys.exit(1)

    image_path = sys.argv[1]
    model_path = sys.argv[2]
    result = classify_image(image_path, model_path)

    if isinstance(result, str):
        try:
            json_result = json.loads(result)
            print(json.dumps(json_result))
        except json.JSONDecodeError:
            print(json.dumps({"error": result}))
    else:
        print(json.dumps({"error": "Resultado inválido do script"}))

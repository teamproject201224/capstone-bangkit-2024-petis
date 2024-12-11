from flask import Flask, request, jsonify
import tensorflow as tf
import numpy as np
import cv2
import os
import urllib.parse
import requests

from class_names import get_class_name
from solutions import get_solution, generate_link

app = Flask(__name__)

def load_model():
    interpreter = tf.lite.Interpreter(model_path="petis.tflite")
    interpreter.allocate_tensors()
    return interpreter

interpreter = load_model()


def preprocess_image(image):
    # Resize image 
    image_resized = cv2.resize(image, (224, 224))
    # convert pixel values to float32 between 0 and 1
    image_resized = image_resized.astype(np.float32) / 255.0
    image_resized = np.expand_dims(image_resized, axis=0)
    return image_resized

def predict(image):
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    interpreter.set_tensor(input_details[0]['index'], image)
    interpreter.invoke()

    output_data = interpreter.get_tensor(output_details[0]['index'])
    return output_data

def fetch_product_details(product_link):
    try:
        response = requests.get(product_link)
        # Check the API response
        if response.status_code == 200:
            product_data = response.json()  
            return {
                "image": product_data.get("image"),
                "name": product_data.get("name"),
                "type": product_data.get("type"),
                "description": product_data.get("description"),
                "link": product_data.get("link")
            }
        else:
            return {"error": "Failed to fetch product details."}
    except requests.exceptions.RequestException as e:
        return {"error": str(e)}

@app.route('/predict', methods=['POST'])
def predict_plant_disease():
    if 'image' not in request.files:
        return jsonify({'error': 'No image file provided'}), 400

    image_file = request.files['image']
    image_np = np.frombuffer(image_file.read(), np.uint8)
    image = cv2.imdecode(image_np, cv2.IMREAD_COLOR)

    preprocessed_image = preprocess_image(image)

    prediction = predict(preprocessed_image)

    predicted_class = np.argmax(prediction)
    predicted_confidence = np.max(prediction)

    class_name = get_class_name(predicted_class)
    solutions = get_solution(predicted_class)

    products_with_details = []
    for product in solutions:
        product_details = fetch_product_details(product["link"])
        products_with_details.append(product_details)

    return jsonify({
        'predicted_class': class_name,
        'confidence': float(predicted_confidence),
        'products': products_with_details
    })

@app.route('/')
def home():
    return "Welcome to Petis API! SIUUUU!!!"

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=int(os.environ.get('PORT', 8080)))

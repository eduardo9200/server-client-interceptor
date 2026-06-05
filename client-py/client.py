import requests
import json
import os
import base64
import random

from datetime import date, timedelta

from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.primitives.asymmetric import padding
from cryptography.hazmat.primitives import serialization, hashes, hmac

# Solicita a chave pública do RSA ao servidor
resp = requests.get("http://localhost:8080/public-key")
public_key_base64 = resp.text

public_key_bytes = base64.b64decode(public_key_base64)

public_key = serialization.load_der_public_key(public_key_bytes)

# Gera chave privada do AES
aes_key = os.urandom(32)
iv = os.urandom(16)

# Calcula data de nascimento aleatória até 130 anos atrás
today = date.today()
start_date = today.replace(year=today.year - 130)
days_between = (today - start_date).days
random_date = start_date + timedelta(days=random.randint(0, days_between))

# Simula JSON de dados IOMT do paciente monitorado
data = {
    "patientId": f"pat{random.randint(100, 999)}",
    "doctorId": f"doc{random.randint(100, 999)}",
    "pulse": random.randint(60, 200),
    "dateOfBirth": random_date.isoformat() # yyyy-MM-dd
}

# Criptografa o JSON usando AES
plaintext = json.dumps(data).encode()

cipher = Cipher(algorithms.AES(aes_key), modes.CBC(iv))
encryptor = cipher.encryptor()

pad_len = 16 - (len(plaintext) % 16)
plaintext += bytes([pad_len]) * pad_len

ciphertext = encryptor.update(plaintext) + encryptor.finalize()

# Criptografa a chave privada do AES usando RSA
encrypted_key = public_key.encrypt(
    aes_key,
    padding.PKCS1v15()
)

# Gera código HMAC para verificação de integridade dos dados do JSON
h = hmac.HMAC(aes_key, hashes.SHA256())
h.update(ciphertext)
hmac_value = h.finalize()

# Envia os dados ao servidor para processamento e aguarda a resposta deste
payload = {
    "data": base64.b64encode(ciphertext).decode(),
    "key": base64.b64encode(encrypted_key).decode(),
    "iv": base64.b64encode(iv).decode(),
    "hmac": base64.b64encode(hmac_value).decode()
}

response = requests.post(
    "http://localhost:9090/data",
    json=payload,
    timeout=5
)

print("Resposta do servidor:", response.text)

import os
import random
import signal
import sys

import requests
from flask import Flask, jsonify, request

app = Flask(__name__)

@app.get('/health')
def health():
    return jsonify({'status': True})

@app.get('/api/movies')
def get_movies():
    gradual_migration: bool = os.getenv('GRADUAL_MIGRATION', False)
    monolith_url: str = os.getenv('MONOLITH_URL', 'http://localhost:8080')
    movies_service_url: str = os.getenv('MOVIES_SERVICE_URL', 'http://localhost:8081')

    if not gradual_migration:
        return proxy_get_request(monolith_url, '/api/movies')

    movies_migration_percent: int = int(os.getenv('MOVIES_MIGRATION_PERCENT', '0'))
    rnd: float = random.uniform(0, 100)
    if rnd <= movies_migration_percent:
        return proxy_get_request(movies_service_url, '/api/movies')

    return proxy_get_request(monolith_url, '/api/movies')

@app.post('/api/movies')
def create_movie():
    return proxy_post_request(
        os.getenv('MONOLITH_URL', 'http://localhost:8080'),
        '/api/movies'
    )

@app.get('/api/users')
def get_users():
    return proxy_get_request(
        os.getenv('MONOLITH_URL', 'http://localhost:8080'),
        '/api/users'
    )

@app.post('/api/users')
def create_user():
    return proxy_post_request(
        os.getenv('MONOLITH_URL', 'http://localhost:8080'),
        '/api/users'
    )

@app.get('/api/subscriptions')
def get_subscriptions():
    return proxy_get_request(
        os.getenv('MONOLITH_URL', 'http://localhost:8080'),
        '/api/subscriptions'
    )

@app.post('/api/subscriptions')
def create_subscription():
    return proxy_post_request(
        os.getenv('MONOLITH_URL', 'http://localhost:8080'),
        '/api/subscriptions'
    )

# Payments endpoints
@app.get('/api/payments')
def get_payments():
    return proxy_get_request(
        os.getenv('MONOLITH_URL', 'http://localhost:8080'),
        '/api/payments'
    )

@app.post('/api/payments')
def create_payment():
    return proxy_post_request(
        os.getenv('MONOLITH_URL', 'http://localhost:8080'),
        '/api/payments'
    )

def proxy_get_request(base_url: str, endpoint: str):
    print(f'sending get request to {base_url}{endpoint}')

    params = request.args.to_dict()

    r = requests.get(
        f'{base_url}{endpoint}',
        params=params,
        headers={'Content-Type': 'application/json'}
    )

    if r.status_code == 200:
        return jsonify(r.json())
    else:
        return jsonify(r.json()), r.status_code

def proxy_post_request(base_url: str, endpoint: str):
    print(f'sending post request to {base_url}{endpoint}')

    r = requests.post(
        f'{base_url}{endpoint}',
        json=request.get_json(),
        headers={'Content-Type': 'application/json'}
    )

    return jsonify(r.json()), r.status_code

def handle_signal(signum, frame):
    sys.exit(0)

signal.signal(signal.SIGINT, handle_signal)   # Ctrl+C / docker stop -> SIGINT/SIGTERM
signal.signal(signal.SIGTERM, handle_signal)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000)
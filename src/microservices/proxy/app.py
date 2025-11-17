import os
import random
import signal
import sys

import requests
from flask import Flask, jsonify

app = Flask(__name__)

@app.get('/health')
def health():
    return jsonify({'status': True})

@app.get('/api/users')
def get_users():
    monolith_url: str = os.getenv('MONOLITH_URL', 'http://localhost:8080')
    r = requests.get(f'{monolith_url}/api/users', headers={'Content-Type': 'application/json'})
    if r.status_code != 200:
        return r.json()
    else:
        return jsonify([])

@app.get('/api/movies')
def get_movies():
    gradual_migration: bool = os.getenv('GRADUAL_MIGRATION', False)
    monolith_url: str = os.getenv('MONOLITH_URL', 'http://localhost:8080')
    movies_service_url: str = os.getenv('MOVIES_SERVICE_URL', 'http://localhost:8081')

    if not gradual_migration:
        return proxy_get_movies(monolith_url)

    movies_migration_percent: int = int(os.getenv('MOVIES_MIGRATION_PERCENT', '0'))
    rnd: float = random.uniform(0, 100)
    if rnd <= movies_migration_percent:
        return proxy_get_movies(movies_service_url)

    return proxy_get_movies(monolith_url)

def proxy_get_movies(base_url: str):
    print(f'sending request to {base_url}')
    r = requests.get(f'{base_url}/api/movies', headers={'Content-Type': 'application/json'})
    if r.status_code == 200:
        return r.json()
    else:
        return jsonify({})

def handle_signal(signum, frame):
    sys.exit(0)

signal.signal(signal.SIGINT, handle_signal)   # Ctrl+C / docker stop -> SIGINT/SIGTERM
signal.signal(signal.SIGTERM, handle_signal)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000)
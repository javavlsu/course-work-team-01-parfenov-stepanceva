#!/usr/bin/env python3
"""
Простой HTTP сервер для разработки Frontend
Запустите этот скрипт и откройте http://localhost:8000 в браузере
"""

import http.server
import socketserver
import os
import webbrowser
from pathlib import Path

PORT = 8000

class MyHTTPRequestHandler(http.server.SimpleHTTPRequestHandler):
    def end_headers(self):
        # Добавляем CORS заголовки для разрешения загрузки JSON
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        super().end_headers()

    def log_message(self, format, *args):
        # Упрощаем логи
        pass

if __name__ == "__main__":
    # Переходим в директорию скрипта
    os.chdir(Path(__file__).parent)
    
    Handler = MyHTTPRequestHandler
    
    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        url = f"http://localhost:{PORT}/index.html"
        print("=" * 60)
        print(f"Сервер запущен на http://localhost:{PORT}")
        print(f"Откройте в браузере: {url}")
        print("=" * 60)
        print("Нажмите Ctrl+C для остановки сервера")
        print("=" * 60)
        
        # Автоматически открываем браузер
        try:
            webbrowser.open(url)
        except:
            pass
        
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nСервер остановлен")


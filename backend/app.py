from flask import Flask, request, jsonify
from flask_cors import CORS
import jwt
import datetime
import uuid
import time
import os

app = Flask(__name__)
CORS(app)  # Android uygulamasından istekler için CORS

# Gemini API import ve yapılandırma
try:
    import google.generativeai as genai
    GEMINI_API_KEY = os.getenv('GEMINI_API_KEY', '')
    
    if GEMINI_API_KEY:
        genai.configure(api_key=GEMINI_API_KEY)
        gemini_model = genai.GenerativeModel('gemini-2.5-flash')
        GEMINI_AVAILABLE = True
    else:
        gemini_model = None
        GEMINI_AVAILABLE = False
        print("⚠️  GEMINI_API_KEY bulunamadı. Fallback özet algoritması kullanılacak.")
except ImportError:
    gemini_model = None
    GEMINI_AVAILABLE = False
    print("⚠️  google-generativeai paketi yüklü değil. 'pip install google-generativeai' ile yükleyin.")
except Exception as e:
    gemini_model = None
    GEMINI_AVAILABLE = False
    print(f"⚠️  Gemini API yapılandırma hatası: {str(e)}")

# Secret key (gerçek projede environment variable kullanın)
SECRET_KEY = "notmobil-secret-key-2024"

# Basit veritabanı (gerçek projede SQLite/PostgreSQL kullanın)
notes_db = []
users_db = [
    {
        "id": "user1",
        "email": "test@test.com",
        "password": "123456",
        "name": "Test User"
    }
]

# ========== HELPER FUNCTIONS ==========

def create_token(user_id, expires_in=3600):
    """JWT token oluştur"""
    payload = {
        'user_id': user_id,
        'exp': datetime.datetime.utcnow() + datetime.timedelta(seconds=expires_in)
    }
    return jwt.encode(payload, SECRET_KEY, algorithm='HS256')

def verify_token(token):
    """JWT token doğrula"""
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=['HS256'])
        return payload['user_id']
    except:
        return None

def require_auth():
    """Authorization header'dan token al ve doğrula"""
    auth_header = request.headers.get('Authorization')
    if not auth_header or not auth_header.startswith('Bearer '):
        return None
    
    token = auth_header.split(' ')[1]
    user_id = verify_token(token)
    return user_id

# ========== AUTH ENDPOINTS ==========

@app.route('/api/auth/login', methods=['POST'])
def login():
    """Kullanıcı girişi"""
    try:
        data = request.json
        email = data.get('email')
        password = data.get('password')
        
        if not email or not password:
            return jsonify({"error": "Email and password required"}), 400
        
        # Kullanıcıyı bul
        user = next((u for u in users_db if u['email'] == email and u['password'] == password), None)
        
        if not user:
            return jsonify({"error": "Invalid credentials"}), 401
        
        # Token oluştur
        access_token = create_token(user['id'], expires_in=3600)  # 1 saat
        refresh_token = create_token(user['id'], expires_in=86400)  # 24 saat
        
        return jsonify({
            "accessToken": access_token,
            "refreshToken": refresh_token,
            "user": {
                "id": user['id'],
                "email": user['email'],
                "name": user.get('name')
            }
        }), 200
        
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/auth/refresh', methods=['POST'])
def refresh_token_endpoint():
    """Token yenileme"""
    try:
        data = request.json
        refresh_token = data.get('refreshToken')
        
        if not refresh_token:
            return jsonify({"error": "Refresh token required"}), 400
        
        user_id = verify_token(refresh_token)
        if not user_id:
            return jsonify({"error": "Invalid token"}), 401
        
        new_access_token = create_token(user_id, expires_in=3600)
        
        return jsonify({
            "accessToken": new_access_token
        }), 200
        
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# ========== NOTES ENDPOINTS (CRUD) ==========

@app.route('/api/notes', methods=['GET'])
def get_all_notes():
    """Tüm notları getir (READ)"""
    user_id = require_auth()
    if not user_id:
        return jsonify({"error": "Unauthorized"}), 401
    
    # Kullanıcının notlarını filtrele
    user_notes = [n for n in notes_db if n.get('userId') == user_id]
    
    return jsonify(user_notes), 200

@app.route('/api/notes/<note_id>', methods=['GET'])
def get_note(note_id):
    """Tek bir notu getir (READ)"""
    user_id = require_auth()
    if not user_id:
        return jsonify({"error": "Unauthorized"}), 401
    
    note = next((n for n in notes_db if n['id'] == note_id and n.get('userId') == user_id), None)
    
    if not note:
        return jsonify({"error": "Note not found"}), 404
    
    return jsonify(note), 200

@app.route('/api/notes', methods=['POST'])
def create_note():
    """Yeni not oluştur (CREATE)"""
    user_id = require_auth()
    if not user_id:
        return jsonify({"error": "Unauthorized"}), 401
    
    try:
        data = request.json
        
        new_note = {
            "id": str(uuid.uuid4()),
            "title": data.get('title', ''),
            "content": data.get('content', ''),
            "createdAt": int(time.time() * 1000),
            "updatedAt": int(time.time() * 1000),
            "isSynced": True,
            "tags": data.get('tags', []),
            "location": data.get('location'),
            "sensorData": data.get('sensorData'),
            "userId": user_id
        }
        
        notes_db.append(new_note)
        
        return jsonify(new_note), 201
        
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/notes/<note_id>', methods=['PUT'])
def update_note(note_id):
    """Notu güncelle (UPDATE)"""
    user_id = require_auth()
    if not user_id:
        return jsonify({"error": "Unauthorized"}), 401
    
    try:
        note = next((n for n in notes_db if n['id'] == note_id and n.get('userId') == user_id), None)
        
        if not note:
            return jsonify({"error": "Note not found"}), 404
        
        data = request.json
        
        # Notu güncelle
        note['title'] = data.get('title', note['title'])
        note['content'] = data.get('content', note['content'])
        note['updatedAt'] = int(time.time() * 1000)
        note['tags'] = data.get('tags', note.get('tags', []))
        note['location'] = data.get('location', note.get('location'))
        note['sensorData'] = data.get('sensorData', note.get('sensorData'))
        
        return jsonify(note), 200
        
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/notes/<note_id>', methods=['DELETE'])
def delete_note(note_id):
    """Notu sil (DELETE)"""
    user_id = require_auth()
    if not user_id:
        return jsonify({"error": "Unauthorized"}), 401
    
    try:
        global notes_db
        initial_length = len(notes_db)
        notes_db = [n for n in notes_db if not (n['id'] == note_id and n.get('userId') == user_id)]
        
        if len(notes_db) == initial_length:
            return jsonify({"error": "Note not found"}), 404
        
        return jsonify({}), 200
        
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# ========== AI ENDPOINTS ==========

@app.route('/api/ai/summarize', methods=['POST'])
def summarize():
    """Metin özetleme - Gemini API ile"""
    user_id = require_auth()
    if not user_id:
        return jsonify({"error": "Unauthorized"}), 401
    
    try:
        data = request.json
        text = data.get('text', '')
        user_api_key = data.get('geminiApiKey', '')
        
        if not text:
            return jsonify({"error": "Text required"}), 400
        
        # Kullanıcının API key'ini kullan veya global API key'i kullan
        api_key_to_use = user_api_key if user_api_key else GEMINI_API_KEY
        
        # API key varsa Gemini API'yi yapılandır
        if api_key_to_use:
            try:
                import google.generativeai as genai
                genai.configure(api_key=api_key_to_use)
                user_gemini_model = genai.GenerativeModel('gemini-2.5-flash')
                use_gemini = True
            except Exception as e:
                print(f"⚠️  User API key ile Gemini yapılandırma hatası: {str(e)}")
                use_gemini = False
                user_gemini_model = None
        else:
            use_gemini = GEMINI_AVAILABLE
            user_gemini_model = gemini_model
        
        # Gemini API kullanılabilirse onu kullan
        if use_gemini and user_gemini_model:
            try:
                prompt = f"""Aşağıdaki metni Türkçe olarak özetle. Özet, metnin ana fikirlerini içermeli ve kısa olmalı (maksimum 3-4 cümle). Özeti doğrudan ver, başka açıklama ekleme.

Metin:
{text}

Özet:"""
                
                response = user_gemini_model.generate_content(prompt)
                summary = response.text.strip()
                
                # Gemini'nin bazen ek açıklamalar eklediğini temizle
                if "Özet:" in summary:
                    summary = summary.split("Özet:")[-1].strip()
                
                return jsonify({
                    "summary": summary
                }), 200
            except Exception as e:
                # Gemini API hatası durumunda fallback algoritma kullan
                print(f"⚠️  Gemini API error: {str(e)}")
                return fallback_summarize(text)
        else:
            # Gemini API kullanılamazsa fallback algoritma kullan
            print("⚠️  Gemini API kullanılamıyor, fallback algoritma kullanılıyor")
            return fallback_summarize(text)
        
    except Exception as e:
        return jsonify({"error": str(e)}), 500

def fallback_summarize(text):
    """Gemini API kullanılamazsa eski algoritma - İyileştirilmiş versiyon"""
    # Noktalama işaretlerine göre cümleleri ayır
    import re
    sentences = re.split(r'[.!?]+', text)
    sentences = [s.strip() for s in sentences if s.strip()]
    words = text.split()
    
    # Metin çok kısaysa direkt döndür
    if len(words) <= 30:
        summary = text
    elif len(sentences) <= 2:
        # Çok az cümle varsa direkt döndür
        summary = text
    else:
        # Cümleleri uzunluklarına göre sırala (en uzunlar önemli olabilir)
        sentence_lengths = [(len(s), s) for s in sentences]
        sentence_lengths.sort(reverse=True, key=lambda x: x[0])
        
        # En uzun 3-4 cümleyi al, ama orijinal sıralamalarını koru
        important_sentences = []
        for orig_sentence in sentences:
            if any(orig_sentence == long_sent for _, long_sent in sentence_lengths[:4]):
                if orig_sentence not in important_sentences:
                    important_sentences.append(orig_sentence)
        
        # Eğer önemli cümle bulamazsak, ilk ve son cümleleri al
        if not important_sentences or len(important_sentences) < 2:
            important_sentences = []
            if len(sentences) >= 2:
                important_sentences.append(sentences[0])  # İlk cümle
                if len(sentences) > 2:
                    important_sentences.append(sentences[-1])  # Son cümle
            else:
                important_sentences = sentences[:3]
        
        # Özeti oluştur
        summary = '. '.join(important_sentences[:4])
        if len(words) > 100:
            summary += "..."
    
    return jsonify({
        "summary": summary
    }), 200

@app.route('/api/ai/generate-tags', methods=['POST'])
def generate_tags():
    """Etiket oluşturma"""
    user_id = require_auth()
    if not user_id:
        return jsonify({"error": "Unauthorized"}), 401
    
    try:
        data = request.json
        text = data.get('text', '').lower()
        
        if not text:
            return jsonify({"error": "Text required"}), 400
        
        # Basit etiket oluşturma (gerçek projede AI servisi kullanın)
        keywords = {
            'iş': ['iş', 'toplantı', 'proje', 'çalışma', 'ofis'],
            'kişisel': ['kişisel', 'özel', 'aile', 'arkadaş'],
            'alışveriş': ['alışveriş', 'satın', 'liste', 'market'],
            'önemli': ['önemli', 'acil', 'dikkat', 'hatırlat']
        }
        
        generated_tags = []
        for category, words in keywords.items():
            if any(word in text for word in words):
                generated_tags.append(category)
        
        if not generated_tags:
            generated_tags = ['genel']
        
        return jsonify({
            "tags": generated_tags[:5]  # Maksimum 5 etiket
        }), 200
        
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/ai/classify', methods=['POST'])
def classify():
    """Metin sınıflandırma"""
    user_id = require_auth()
    if not user_id:
        return jsonify({"error": "Unauthorized"}), 401
    
    try:
        data = request.json
        text = data.get('text', '').lower()
        
        if not text:
            return jsonify({"error": "Text required"}), 400
        
        # Basit sınıflandırma (gerçek projede AI servisi kullanın)
        if any(word in text for word in ['iş', 'toplantı', 'proje', 'çalışma']):
            category = "iş"
        elif any(word in text for word in ['alışveriş', 'satın', 'liste', 'market']):
            category = "alışveriş"
        elif any(word in text for word in ['kişisel', 'özel', 'aile']):
            category = "kişisel"
        else:
            category = "genel"
        
        return jsonify({
            "category": category
        }), 200
        
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# ========== HEALTH CHECK ==========

@app.route('/api/health', methods=['GET'])
def health_check():
    """API sağlık kontrolü"""
    return jsonify({
        "status": "ok",
        "message": "NotMobil API is running"
    }), 200

# ========== ERROR HANDLERS ==========

@app.errorhandler(404)
def not_found(error):
    return jsonify({"error": "Endpoint not found"}), 404

@app.errorhandler(500)
def internal_error(error):
    return jsonify({"error": "Internal server error"}), 500

# ========== MAIN ==========

if __name__ == '__main__':
    print("=" * 50)
    print("NotMobil REST API Server")
    print("=" * 50)
    print("Server starting on http://0.0.0.0:8080")
    print("Test user: test@test.com / 123456")
    if GEMINI_AVAILABLE:
        print("✅ Gemini API aktif - Gelişmiş özet özelliği kullanılabilir")
    else:
        print("⚠️  Gemini API aktif değil - Fallback özet algoritması kullanılacak")
        print("   Gemini API için: pip install google-generativeai")
        print("   Ve GEMINI_API_KEY environment variable'ı ayarlayın")
    print("=" * 50)
    
    app.run(host='0.0.0.0', port=8080, debug=True)


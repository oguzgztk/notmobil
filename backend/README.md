# NotMobil Backend API

Flask tabanlı REST API sunucusu.

## Kurulum

1. Python 3.8+ yüklü olmalı
2. Gerekli paketleri yükleyin:
```bash
pip install -r requirements.txt
```

## Çalıştırma

```bash
python app.py
```

Server `http://0.0.0.0:8080` adresinde çalışacaktır.

## Test Kullanıcı

- Email: `test@test.com`
- Password: `123456`

## Gemini API (Opsiyonel)

Gemini API kullanmak için:
1. `pip install google-generativeai`
2. Environment variable olarak `GEMINI_API_KEY` ayarlayın:
```bash
export GEMINI_API_KEY="your-api-key"
```

veya Windows'ta:
```bash
set GEMINI_API_KEY=your-api-key
```

## API Endpoints

- `POST /api/auth/login` - Kullanıcı girişi
- `POST /api/auth/refresh` - Token yenileme
- `GET /api/notes` - Tüm notları getir
- `GET /api/notes/<id>` - Tek not getir
- `POST /api/notes` - Yeni not oluştur
- `PUT /api/notes/<id>` - Not güncelle
- `DELETE /api/notes/<id>` - Not sil
- `POST /api/ai/summarize` - Metin özetleme
- `POST /api/ai/generate-tags` - Etiket oluşturma
- `POST /api/ai/classify` - Metin sınıflandırma
- `GET /api/health` - Sağlık kontrolü


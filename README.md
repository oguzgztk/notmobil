# NotMobil - Mobil Not Uygulaması

Kapsamlı bir mobil not alma uygulaması. Android Studio, Kotlin ve Jetpack Compose kullanılarak geliştirilmiştir.

## 🚀 Özellikler

1. **Storage/Basic Data** - DataStore PreferencesManager ile temel veri saklama
2. **Local Database** - Room database ile yerel veri kalıcılığı
3. **RESTful API** - Retrofit ile CRUD işlemleri
4. **Modern UI** - Jetpack Compose ile modern kullanıcı arayüzü
5. **Background Process/Task** - WorkManager ile arka plan görevleri
6. **Broadcast Receiver/Notification** - Sistem olayları ve bildirimler
7. **Sensor APIs** - Konum, hareket ve ortam sensörleri
8. **Connectivity APIs** - WiFi, Bluetooth LE ve NFC bağlantıları
9. **Authorization** - OAuth 2.0, JWT token yönetimi
10. **Cloud Service** - AI (Gemini API) ile metin özetleme

## 📁 Proje Yapısı

```
NotMobil/
├── app/                          # Android uygulaması
│   ├── src/main/java/com/example/notuygulamasi/
│   │   ├── data/                 # Data katmanı
│   │   │   ├── local/           # Room database, DataStore
│   │   │   ├── remote/          # Retrofit API, DTOs
│   │   │   ├── repository/      # Repository implementasyonları
│   │   │   ├── work/            # WorkManager workers
│   │   │   ├── receiver/        # BroadcastReceiver
│   │   │   └── notification/   # NotificationManager
│   │   ├── domain/              # Domain katmanı
│   │   │   ├── model/           # Domain modelleri
│   │   │   └── repository/     # Repository interface'leri
│   │   ├── presentation/         # Presentation katmanı
│   │   │   ├── screen/          # Compose ekranları
│   │   │   ├── viewmodel/      # ViewModels
│   │   │   └── navigation/      # Navigation
│   │   └── di/                  # Dependency Injection (Hilt)
│   └── build.gradle.kts
├── backend/                      # Flask REST API
│   ├── app.py                   # Ana API dosyası
│   ├── requirements.txt         # Python bağımlılıkları
│   └── README.md                # Backend dokümantasyonu
└── README.md                     # Bu dosya
```

## 🛠️ Teknolojiler

### Android
- **Kotlin** - Programlama dili
- **Jetpack Compose** - Modern UI framework
- **Room** - Local database
- **DataStore** - Preferences storage
- **Retrofit** - REST API client
- **Hilt** - Dependency Injection
- **WorkManager** - Background tasks
- **Navigation Compose** - Navigation
- **Coroutines & Flow** - Asynchronous programming

### Backend
- **Flask** - Python web framework
- **JWT** - Token authentication
- **Google Gemini API** - AI text summarization

## 📦 Kurulum

### Android Uygulaması

1. Android Studio'da projeyi açın
2. Gradle sync yapın
3. Emulator veya fiziksel cihazda çalıştırın

### Backend API

1. Backend klasörüne gidin:
```bash
cd backend
```

2. Python bağımlılıklarını yükleyin:
```bash
pip install -r requirements.txt
```

3. (Opsiyonel) Gemini API key ayarlayın:
```bash
export GEMINI_API_KEY="your-api-key"  # Linux/Mac
set GEMINI_API_KEY=your-api-key       # Windows
```

4. Backend'i başlatın:
```bash
python app.py
```

Backend `http://0.0.0.0:8080` adresinde çalışacaktır.

## 🔐 Test Kullanıcı

- **Email:** `test@test.com`
- **Password:** `123456`

## 🌿 Git Branch Yapısı

Proje Git Flow stratejisi ile yönetilmektedir:

- `main` - Production branch (tüm özellikler birleştirilmiş)
- `develop` - Development branch
- Feature branches:
  - `storage-basic`
  - `local-database`
  - `restful-api`
  - `ui`
  - `background-process-task`
  - `broadcast-receiver-notification-center`
  - `sensor`
  - `connectivity`
  - `authorization`
  - `cloud-service`

## 📱 Kullanım

1. Uygulamayı başlatın
2. Test kullanıcı bilgileri ile giriş yapın
3. Notlar oluşturun, düzenleyin ve silin
4. Konum bilgisi otomatik olarak kaydedilir
5. AI özet özelliğini kullanmak için Settings'ten Gemini API key girin

## 🔧 Yapılandırma

### NetworkModule.kt

Emülatör için:
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/api/"
```

Fiziksel cihaz için (bilgisayarınızın IP adresi):
```kotlin
private const val BASE_URL = "http://192.168.1.100:8080/api/"
```

## 📄 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.

## 👥 Geliştirici

NotMobil projesi - 2024

# 🛡️ IDS Home Network - Intrusion Detection System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2.0-blue.svg)](https://reactjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue.svg)](https://www.postgresql.org/)
[![Suricata](https://img.shields.io/badge/Suricata-Latest-orange.svg)](https://suricata.io/)

Uy tarmoqlari uchun real-time Intrusion Detection System (IDS). MacBook Air M2 da Suricata, Spring Boot, PostgreSQL va React yordamida qurilgan.

## 📋 Loyiha Haqida

Bu loyiha uy tarmoqlaridagi xavflarni aniqlash uchun mo'ljallangan web-based IDS tizimi. Foydalanuvchilar ro'yxatdan o'tib, real-time ravishda o'z tarmoqlarini monitoring qilishlari mumkin.

### ✨ Asosiy Imkoniyatlar

- 👤 **User Authentication** - Login/Register sistemi (JWT)
- 🚨 **Real-time Alert Detection** - Suricata orqali xavflarni aniqlash
- 📊 **Dashboard** - Statistika va vizualizatsiya
- 🔴 **Start/Stop Monitoring** - Bitta tugma bilan IDS ni boshqarish
- 💾 **Database Storage** - Barcha alertlar PostgreSQL da saqlanadi
- 🌐 **Web Interface** - React SPA
- 🔔 **Real-time Notifications** - WebSocket orqali yangi alertlar

## 🏗️ Texnologiyalar

### Backend
- **Spring Boot 3.2.1** - Java framework
- **Suricata** - Network IDS/IPS
- **PostgreSQL 14** - Database
- **JWT** - Authentication
- **WebSocket** - Real-time communication

### Frontend
- **React 18** - UI library
- **Axios** - HTTP client
- **React Router** - Navigation
- **Chart.js** - Grafik uchun
- **WebSocket (SockJS + STOMP)** - Real-time updates

## 📦 O'rnatish

### 1. Talablar

- macOS (MacBook)
- Homebrew
- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Suricata
- Maven

### 2. Dependencies o'rnatish

```bash
# Homebrew (agar yo'q bo'lsa)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# PostgreSQL
brew install postgresql@14
brew services start postgresql@14

# Suricata
brew install suricata

# Java
brew install openjdk@17
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Node.js
brew install node

# Maven
brew install maven
```

### 3. Database yaratish

```bash
# PostgreSQL ga kirish
psql postgres

# Database yaratish
CREATE DATABASE ids_home_network;

# User yaratish (agar kerak bo'lsa)
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE ids_home_network TO postgres;

# Chiqish
\q
```

### 4. Suricata sozlash

```bash
# Suricata rules yangilash
sudo suricata-update

# Emerging Threats rules qo'shish
sudo suricata-update update-sources
sudo suricata-update enable-source et/open
sudo suricata-update

# Suricata test
sudo suricata -T -c /opt/homebrew/etc/suricata/suricata.yaml

# Suricata log papkasiga ruxsat berish
sudo mkdir -p /var/log/suricata
sudo chown -R $(whoami) /var/log/suricata
```

### 5. Proyektni clone qilish

```bash
cd ~/Desktop
git clone https://github.com/YOUR_USERNAME/ids-home-network.git
cd ids-home-network
```

### 6. Backend ishga tushirish

```bash
cd backend

# Dependencies yuklab olish va build qilish
mvn clean install

# Ishga tushirish
mvn spring-boot:run

# Yoki JAR fayldan
# java -jar target/homenetwork-1.0.0.jar
```

Backend `http://localhost:8080` da ishga tushadi.

### 7. Frontend ishga tushirish

Yangi terminal oching:

```bash
cd frontend

# Dependencies o'rnatish
npm install

# Ishga tushirish
npm start
```

Frontend `http://localhost:3000` da ochiladi va brauzer avtomatik ochiladi.

## 🚀 Foydalanish

### 1. Ro'yxatdan o'tish
1. Brauzerni ochib `http://localhost:3000` ga o'ting
2. "Register" tugmasini bosing
3. Username, email va parol kiriting
4. "Register" tugmasini bosing

### 2. Login
1. Username va parolingizni kiriting
2. "Login" tugmasini bosing
3. Dashboard ga o'tasiz

### 3. Monitoring boshlash
1. Dashboard da **"▶️ Start Monitoring"** tugmasini bosing
2. Suricata ishga tushadi va WiFi trafikni monitoring qiladi
3. Real-time alertlar paydo bo'ladi

### 4. Alertlarni ko'rish
- Dashboard da barcha alertlar ro'yxati
- Har bir alertga bosib batafsil ma'lumot olish
- Severity (Critical, High, Medium, Low) bo'yicha ranglar
- Source IP, Destination IP, Protocol va boshqalar

### 5. Statistika
- "📊 Statistics" tab ga o'ting
- Alertlar soni (jami, bugun, shu hafta)
- Severity bo'yicha taqsimot (Pie chart)
- Kategoriya bo'yicha top 10 (Bar chart)
- Timeline (Line chart)
- Top source IP manzillar

### 6. Monitoring to'xtatish
- **"🛑 Stop Monitoring"** tugmasini bosing
- Suricata to'xtaydi

## 🌐 Ngrok orqali Internetga ochish

Loyihangizni butun dunyoga ko'rsatish uchun:

```bash
# Ngrok o'rnatish
brew install ngrok

# Authentikatsiya (ngrok.com dan token oling)
ngrok config add-authtoken YOUR_TOKEN

# Backend ni ochish
ngrok http 8080
```

Ngrok sizga public URL beradi, masalan: `https://abc123.ngrok.io`

Frontend da API URL ni yangilang:

```javascript
// frontend/src/services/api.js
const API_URL = 'https://YOUR_NGROK_URL.ngrok.io/api';
```

Frontend ni qayta ishga tushiring:

```bash
npm start
```

Endi har kim sizning IDS ga kirishi mumkin! 🌍

## 📊 API Endpoints

### Authentication
```
POST /api/auth/register - Ro'yxatdan o'tish
POST /api/auth/login    - Login
GET  /api/auth/me       - Current user info
```

### Suricata Control
```
POST /api/suricata/start  - Monitoring boshlash
POST /api/suricata/stop   - Monitoring to'xtatish
GET  /api/suricata/status - Status olish
```

### Alerts
```
GET    /api/alerts              - Barcha alertlar
GET    /api/alerts/unread       - O'qilmagan alertlar
GET    /api/alerts/{id}         - Bitta alert
PUT    /api/alerts/{id}/read    - Alert o'qilgan deb belgilash
PUT    /api/alerts/read-all     - Hammasini o'qilgan deb belgilash
DELETE /api/alerts/{id}         - Alert o'chirish
```

### Statistics
```
GET /api/statistics - Umumiy statistika
```

## 🔒 Security

- JWT token authentication (24 soat)
- Password encryption (BCrypt)
- CORS configuration
- SQL injection protection (JPA)
- XSS protection
- HTTPS support (Ngrok orqali)

## 🧪 Test qilish

### Backend Test
```bash
cd backend
mvn test
```

### Hujum simulyatsiya

Terminal da:

```bash
# Port scanning (Suricata aniqlaydi!)
nmap -p 1-1000 localhost

# SYN flood
sudo hping3 -c 1000 -d 120 -S -p 80 --flood localhost

# Telnet scan
nmap -sV -p 23 localhost
```

Bu hujumlar Suricata tomonidan aniqlanadi va dashboard da ko'rinadi!

## 📁 Proyekt Strukturasi

```
ids-home-network/
├── backend/
│   ├── src/main/java/uz/ids/homenetwork/
│   │   ├── HomeNetworkApplication.java   # Main class
│   │   ├── controller/                    # REST controllers
│   │   │   ├── AuthController.java
│   │   │   ├── SuricataController.java
│   │   │   ├── AlertController.java
│   │   │   └── StatisticsController.java
│   │   ├── service/                       # Business logic
│   │   │   ├── AuthService.java
│   │   │   ├── SuricataService.java
│   │   │   ├── LogMonitorService.java
│   │   │   └── AlertService.java
│   │   ├── repository/                    # Database access
│   │   │   ├── UserRepository.java
│   │   │   ├── AlertRepository.java
│   │   │   └── SuricataSessionRepository.java
│   │   ├── model/                         # JPA entities
│   │   │   ├── User.java
│   │   │   ├── Alert.java
│   │   │   └── SuricataSession.java
│   │   ├── config/                        # Configuration
│   │   │   ├── SecurityConfig.java
│   │   │   ├── WebSocketConfig.java
│   │   │   └── JwtUtils.java
│   │   ├── dto/                           # Data transfer objects
│   │   └── security/                      # Security components
│   └── src/main/resources/
│       └── application.properties
├── frontend/
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── App.js                         # Main component
│   │   ├── index.js                       # Entry point
│   │   ├── components/                    # React components
│   │   │   ├── AlertList.js
│   │   │   └── Statistics.js
│   │   ├── pages/                         # Page components
│   │   │   ├── Login.js
│   │   │   ├── Register.js
│   │   │   └── Dashboard.js
│   │   ├── services/                      # API services
│   │   │   ├── api.js
│   │   │   └── websocket.js
│   │   └── styles/                        # CSS files
│   └── package.json
└── README.md
```

## 🎓 Canvas/Moodle ga topshirish

### Kerakli fayllar:

1. **GitHub Repository** 
   - Public repository havolasini bering
   - Yaxshi README bilan

2. **Video Demo** (5-10 daqiqa)
   - Loyihani ishga tushirish
   - Register/Login
   - Monitoring boshlash
   - Test hujum (nmap)
   - Alertlarni ko'rsatish
   - Statistics

3. **Documentation** (PDF)
   - Loyiha haqida
   - Arxitektura diagrammasi
   - Screenshots
   - API dokumentatsiyasi

4. **Presentation** (PowerPoint/PDF)
   - Problem statement
   - Solution
   - Technologies
   - Demo screenshots
   - Conclusion

## 🐛 Troubleshooting

### Suricata ishlamasa:

```bash
# Permission berish
sudo chown -R $(whoami) /var/log/suricata/

# Config tekshirish
sudo suricata -T -c /opt/homebrew/etc/suricata/suricata.yaml

# Rules yangilash
sudo suricata-update
```

### Database xatolik:

```bash
# PostgreSQL restart
brew services restart postgresql@14

# Database qayta yaratish
dropdb ids_home_network
createdb ids_home_network
```

### Port band bo'lsa:

```bash
# 8080 portni bo'shatish
lsof -ti:8080 | xargs kill -9

# 3000 portni bo'shatish
lsof -ti:3000 | xargs kill -9
```

### Frontend backend ga ulanmasa:

`frontend/src/services/api.js` da `API_URL` to'g'ri ekanligini tekshiring:

```javascript
const API_URL = 'http://localhost:8080/api';
```

## 📞 Muammolar

Agar muammo bo'lsa:
- GitHub Issues ga yozing
- Pull request yuboring
- Email: your@email.com

## 📄 License

MIT License - Educational purposes

## 👨‍💻 Muallif

**Sizning Ismingiz**  
University Diploma Project - 2026  
IDS for Home Networks

---

⭐ Agar foydali bo'lsa, GitHub da star bering!

🌟 Contributors welcome!

📧 Feedback: your@email.com

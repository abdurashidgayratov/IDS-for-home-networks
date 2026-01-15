
# 🛡️ IDS Home Network - Intrusion Detection System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2.0-blue.svg)](https://reactjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue.svg)](https://www.postgresql.org/)
[![Suricata](https://img.shields.io/badge/Suricata-8.0.3-orange.svg)](https://suricata.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A real-time Intrusion Detection System (IDS) for home networks, built with Suricata, Spring Boot, PostgreSQL, and React on MacBook Air M2.

## 📋 About

This project is a web-based IDS system designed to detect threats in home networks. Users can register, login, and monitor their networks in real-time with an intuitive dashboard.

### ✨ Key Features

- 👤 **User Authentication** - Secure Login/Register system with JWT
- 🚨 **Real-time Alert Detection** - Threat detection via Suricata IDS
- 📊 **Interactive Dashboard** - Statistics and data visualization
- 🔴 **One-Click Monitoring** - Start/Stop IDS with a single button
- 💾 **Persistent Storage** - All alerts stored in PostgreSQL database
- 🌐 **Modern Web Interface** - Responsive React SPA
- 🔔 **Real-time Notifications** - Live alerts via WebSocket

## 🗂️ Technologies

### Backend
- **Spring Boot 3.2.1** - Java application framework
- **Suricata 8.0.3** - Network IDS/IPS engine
- **PostgreSQL 14** - Relational database
- **JWT** - Secure authentication
- **WebSocket** - Real-time bidirectional communication

### Frontend
- **React 18** - UI library
- **Axios** - HTTP client
- **React Router** - Client-side routing
- **Chart.js** - Data visualization
- **WebSocket (SockJS + STOMP)** - Real-time updates

## 📦 Installation

### 1. Prerequisites

- macOS (tested on MacBook Air M2)
- Homebrew package manager
- Java 17 or higher
- Node.js 18 or higher
- PostgreSQL 14 or higher
- Suricata IDS
- Maven

### 2. Install Dependencies

```bash
# Install Homebrew (if not already installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install PostgreSQL
brew install postgresql@14
brew services start postgresql@14

# Install Suricata
brew install suricata

# Install Java
brew install openjdk@17
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Install Node.js
brew install node

# Install Maven
brew install maven
```

### 3. Setup Database

```bash
# Connect to PostgreSQL
psql postgres

# Create database
CREATE DATABASE ids_home_network;

# Create user (if needed)
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE ids_home_network TO postgres;

# Exit
\q
```

### 4. Configure Suricata

```bash
# Update Suricata rules
sudo suricata-update

# Add Emerging Threats rules
sudo suricata-update update-sources
sudo suricata-update enable-source et/open
sudo suricata-update

# Test Suricata configuration
sudo suricata -T -c /opt/homebrew/etc/suricata/suricata.yaml

# Set up Suricata log directory permissions
sudo mkdir -p /var/log/suricata
sudo chown -R $(whoami) /var/log/suricata
```

### 5. Clone Repository

```bash
cd ~/Desktop
git clone https://github.com/abdurashidgayratov/IDS-for-home-networks.git
cd IDS-for-home-networks
```

### 6. Start Backend

```bash
cd backend

# Install dependencies and build
mvn clean install

# Run application
mvn spring-boot:run

# Or run from JAR
# java -jar target/homenetwork-1.0.0.jar
```

Backend will start at `http://localhost:8080`

### 7. Start Frontend

Open a new terminal:

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

Frontend will open automatically at `http://localhost:3000`

## 🚀 Usage

### 1. Register
1. Open browser and navigate to `http://localhost:3000`
2. Click "Register" button
3. Enter username, email, and password
4. Click "Register" to create account

### 2. Login
1. Enter your username and password
2. Click "Login"
3. You'll be redirected to the Dashboard

### 3. Start Monitoring
1. On the Dashboard, click **"▶️ Start Monitoring"**
2. Suricata will start and begin monitoring WiFi traffic
3. Real-time alerts will appear automatically

### 4. View Alerts
- All alerts are listed on the Dashboard
- Click on any alert to view detailed information
- Alerts are color-coded by severity (Critical, High, Medium, Low)
- View Source IP, Destination IP, Protocol, and more

### 5. Statistics
- Navigate to "📊 Statistics" tab
- View alert counts (total, today, this week)
- Severity distribution (Pie chart)
- Top 10 categories (Bar chart)
- Alert timeline (Line chart)
- Top source IP addresses

### 6. Stop Monitoring
- Click **"🛑 Stop Monitoring"** button
- Suricata will stop monitoring

## 🌐 Deploy with Ngrok

Share your project with the world:

```bash
# Install Ngrok
brew install ngrok

# Authenticate (get token from ngrok.com)
ngrok config add-authtoken YOUR_TOKEN

# Expose backend
ngrok http 8080
```

Ngrok will provide a public URL like: `https://abc123.ngrok.io`

Update the API URL in frontend:

```javascript
// frontend/src/services/api.js
const API_URL = 'https://YOUR_NGROK_URL.ngrok.io/api';
```

Restart frontend:

```bash
npm start
```

Now anyone can access your IDS! 🌐

## 📊 API Endpoints

### Authentication
```
POST /api/auth/register - User registration
POST /api/auth/login    - User login
GET  /api/auth/me       - Get current user info
```

### Suricata Control
```
POST /api/suricata/start  - Start monitoring
POST /api/suricata/stop   - Stop monitoring
GET  /api/suricata/status - Get monitoring status
```

### Alerts
```
GET    /api/alerts              - Get all alerts
GET    /api/alerts/unread       - Get unread alerts
GET    /api/alerts/{id}         - Get specific alert
PUT    /api/alerts/{id}/read    - Mark alert as read
PUT    /api/alerts/read-all     - Mark all as read
DELETE /api/alerts/{id}         - Delete alert
```

### Statistics
```
GET /api/statistics - Get overall statistics
```

## 🔒 Security

- JWT token authentication (24-hour expiration)
- Password encryption using BCrypt
- CORS configuration for cross-origin requests
- SQL injection protection via JPA
- XSS protection
- HTTPS support (via Ngrok)

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Attack Simulation

In terminal:

```bash
# Port scanning (Suricata will detect this!)
nmap -p 1-1000 localhost

# SYN flood
sudo hping3 -c 1000 -d 120 -S -p 80 --flood localhost

# Telnet scan
nmap -sV -p 23 localhost
```

These attacks will be detected by Suricata and appear on the dashboard!

## 📁 Project Structure

```
ids-home-network/
├── backend/
│   ├── src/main/java/uz/ids/homenetwork/
│   │   ├── HomeNetworkApplication.java   # Main application
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
│   │   ├── repository/                    # Data access layer
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

## 🎓 Academic Submission

### Required Files:

1. **GitHub Repository** 
   - Public repository link
   - Well-documented README

2. **Video Demo** (5-10 minutes)
   - Project setup and launch
   - User registration/login
   - Start monitoring
   - Simulate attacks (nmap)
   - Show detected alerts
   - Display statistics

3. **Documentation** (PDF)
   - Project overview
   - System architecture diagram
   - Screenshots
   - API documentation
   - Testing results

4. **Presentation** (PowerPoint/PDF)
   - Problem statement
   - Proposed solution
   - Technology stack
   - Demo screenshots
   - Results and conclusion

## 🛠 Troubleshooting

### Suricata not working:

```bash
# Grant permissions
sudo chown -R $(whoami) /var/log/suricata/

# Test configuration
sudo suricata -T -c /opt/homebrew/etc/suricata/suricata.yaml

# Update rules
sudo suricata-update
```

### Database errors:

```bash
# Restart PostgreSQL
brew services restart postgresql@14

# Recreate database
dropdb ids_home_network
createdb ids_home_network
```

### Port already in use:

```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9

# Kill process on port 3000
lsof -ti:3000 | xargs kill -9
```

### Frontend can't connect to backend:

Check `API_URL` in `frontend/src/services/api.js`:

```javascript
const API_URL = 'http://localhost:8080/api';
```

## 📞 Support

If you encounter any issues:
- Open a GitHub Issue
- Submit a Pull Request
- Email: abdurashidgayratov0@gmail.com

## 📄 License

MIT License - Educational purposes

## 👨‍💻 Author

**Abdurashid G'ayratov**  
Student ID: 2428264  
Email: abdurashidgayratov0@gmail.com  
University Diploma Project - 2026  
IDS for Home Networks

---

⭐ If you find this project useful, please give it a star on GitHub!

🌟 Contributors welcome!

📧 Feedback: abdurashidgayratov0@gmail.com



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


## 📄 License

MIT License - Educational purposes

## 👨‍💻 Muallif

University Diploma Project - 2026  
IDS for Home Networks
**Abdurashid G'ayratov**  
Student ID: 2428264  
Email: abdurashidgayratov0@gmail.com  
University Diploma Project - 2026  
IDS for Home Networks

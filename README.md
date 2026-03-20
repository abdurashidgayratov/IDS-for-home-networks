<div align="center">

# 🛡️ IDS Home Network

**Real-time Intrusion Detection System for Home Networks**

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2.0-61DAFB?style=flat-square&logo=react&logoColor=black)](https://reactjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-4169E1?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Suricata](https://img.shields.io/badge/Suricata-8.0.3-EF3B2D?style=flat-square)](https://suricata.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-macOS-black?style=flat-square&logo=apple)](https://www.apple.com/macos/)

A web-based IDS system that monitors your home network in real-time, detects intrusions via Suricata, and displays live alerts on an interactive dashboard.

[Features](#-features) • [Quick Start](#-quick-start) • [Usage](#-usage) • [Attacks Detected](#-attacks-detected) • [API](#-api-endpoints) • [Troubleshooting](#-troubleshooting)

</div>

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🔐 **JWT Authentication** | Secure login & registration with 24-hour tokens |
| 🚨 **Real-time Alerts** | Live threat detection via Suricata IDS engine |
| 📊 **Interactive Dashboard** | Statistics, charts, and alert management |
| 📄 **Pagination** | Alerts displayed 10 per page with navigation |
| 🔴 **Severity Levels** | Critical / High / Medium / Low classification |
| 🌐 **WebSocket** | Instant alert delivery without page refresh |
| 💾 **Persistent Storage** | All alerts saved to PostgreSQL |
| 🌑 **Dark Theme UI** | Cyber-style dark interface |

---

## 🖥️ Screenshots

<div align="center">

| Dashboard | Alert Details | Statistics |
|-----------|--------------|------------|
| Real-time alert feed | IP, port, protocol info | Charts & top source IPs |

</div>

---

## 🗂️ Tech Stack

```
Frontend          Backend           Infrastructure
─────────         ─────────         ──────────────
React 18          Spring Boot 3.2   Suricata 8.0.3
Chart.js          JWT Auth          PostgreSQL 14
WebSocket         Hibernate JPA     Homebrew (macOS)
Dark CSS Theme    REST API
```

---

## 🚀 Quick Start

### Prerequisites

- macOS (tested on MacBook Air M2)
- [Homebrew](https://brew.sh/)
- Java 17+, Node.js 18+, Maven 3.6+

### 1. Install Dependencies

```bash
brew install postgresql@14 suricata openjdk@17 node maven

echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### 2. Setup Database

```bash
brew services start postgresql@14

psql postgres -c "CREATE DATABASE ids_home_network;"
psql postgres -c "CREATE USER postgres WITH PASSWORD '123';"
psql postgres -c "GRANT ALL PRIVILEGES ON DATABASE ids_home_network TO postgres;"
```

### 3. Setup Suricata

```bash
# Update rules
sudo suricata-update

# Set log directory permissions
sudo mkdir -p /var/log/suricata
sudo chown -R $(whoami) /var/log/suricata

# Allow passwordless execution (add to sudoers)
sudo visudo
# Add this line:
# YOUR_USERNAME ALL=(ALL) NOPASSWD: /opt/homebrew/bin/suricata
```

### 4. Clone & Run

```bash
git clone https://github.com/abdurashidgayratov/IDS-for-home-networks.git
cd IDS-for-home-networks

# Terminal 1 — Backend
cd backend
export MAVEN_OPTS="-Xmx512m -Xms256m"
mvn spring-boot:run

# Terminal 2 — Frontend
cd frontend
npm install
npm start
```

### 5. Open Browser

```
http://localhost:3000
```

Register an account → Login → Click **▶️ Start Monitoring**

---

## 📖 Usage

### Start Monitoring

1. Open `http://localhost:3000`
2. Register / Login
3. Click **▶️ Start Monitoring** on the Dashboard
4. Suricata begins monitoring your network interface (`en0`)

### Simulate Attacks

```bash
# Find your WiFi IP
ipconfig getifaddr en0

# Port scan (generates MEDIUM alerts)
nmap -Pn -p 1-1000 $(ipconfig getifaddr en0)

# Aggressive scan (generates HIGH alerts)
nmap -A $(ipconfig getifaddr en0)

# Add test alerts directly to DB (all severity levels)
./test_attacks.sh
```

Alerts appear on the dashboard instantly via WebSocket. Use **Refresh** to reload manually.

---

## 🎯 Attacks Detected

IDS detects **27 attack types** across 4 severity levels using Suricata + Emerging Threats rules:

### 🔴 Critical
| Attack | Signature |
|--------|-----------|
| EternalBlue SMB Exploit | `ET EXPLOIT EternalBlue SMB Remote Code Execution` |
| Trojan CnC Connection | `MALWARE-CNC Win.Trojan.Zeus Outbound` |
| SQL Injection | `ET WEB_SPECIFIC_APPS SQL Injection Attempt` |
| Remote Code Execution | `ET EXPLOIT Apache Struts RCE` |
| Ransomware Traffic | `MALWARE-CNC Ransomware Beacon` |
| Botnet C2 Communication | `ET TROJAN Generic Bot C2 Traffic` |

### 🟠 High
| Attack | Signature |
|--------|-----------|
| SSH Brute Force | `ET SCAN SSH Brute Force Attempt` |
| DDoS UDP Flood | `ET DOS Potential DDoS UDP Flood` |
| RDP Brute Force | `ET SCAN RDP Brute Force Attempt` |
| FTP Brute Force | `ET SCAN FTP Brute Force Attempt` |
| Aggressive Port Scan | `ET SCAN Nmap Scripting Engine Detected` |
| XSS Attack | `ET WEB Cross-Site Scripting Attempt` |
| Directory Traversal | `ET WEB Directory Traversal Attempt` |

### 🟡 Medium
Port scans, DNS suspicious lookups, HTTP floods, ICMP floods, Telnet scans, Tor traffic, SMB scans, suspicious user-agents

### 🔵 Low
ICMP ping sweeps, TLS handshake failures, NTP requests, cleartext passwords, suspicious TLS certificates

---

## 📁 Project Structure

```
IDS-for-home-networks/
├── backend/
│   └── src/main/java/uz/ids/homenetwork/
│       ├── controller/          # REST endpoints
│       │   ├── AuthController.java
│       │   ├── AlertController.java
│       │   ├── SuricataController.java
│       │   └── StatisticsController.java
│       ├── service/             # Business logic
│       │   ├── LogMonitorService.java   # ← Reads eve.json (RandomAccessFile)
│       │   ├── SuricataService.java
│       │   ├── AlertService.java
│       │   └── AuthService.java
│       ├── model/               # JPA entities
│       ├── repository/          # Database access
│       ├── config/              # Security, JWT, WebSocket
│       └── dto/                 # Data transfer objects
├── frontend/
│   └── src/
│       ├── pages/
│       │   ├── Dashboard.js     # Main dashboard with pagination
│       │   ├── Login.js
│       │   └── Register.js
│       ├── components/
│       │   ├── AlertList.js     # Alert feed (10/page)
│       │   └── Statistics.js    # Charts
│       ├── services/
│       │   ├── api.js           # Axios HTTP client
│       │   └── websocket.js     # SockJS + STOMP
│       └── styles/              # Dark cyber theme CSS
├── test_attacks.sh              # Test alert simulator
└── README.md
```

---

## 🌐 API Endpoints

### Authentication
```
POST  /api/auth/register     Register new user
POST  /api/auth/login        Login & receive JWT token
GET   /api/auth/me           Get current user info
```

### Suricata Control
```
POST  /api/suricata/start    Start monitoring
POST  /api/suricata/stop     Stop monitoring
GET   /api/suricata/status   Get current status
```

### Alerts
```
GET    /api/alerts              Get all alerts
GET    /api/alerts/unread       Get unread alerts
GET    /api/alerts/{id}         Get alert by ID
PUT    /api/alerts/{id}/read    Mark as read
PUT    /api/alerts/read-all     Mark all as read
DELETE /api/alerts/{id}         Delete alert
```

### Statistics
```
GET   /api/statistics        Total, today, weekly counts + charts data
```

---

## 🔧 Configuration

`backend/src/main/resources/application.properties`

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/ids_home_network
spring.datasource.username=postgres
spring.datasource.password=123

# Suricata
suricata.log.path=/opt/homebrew/var/log/suricata/eve.json
suricata.executable.path=/opt/homebrew/bin/suricata
suricata.config.path=/opt/homebrew/etc/suricata/suricata.yaml
suricata.interface=en0

# Timezone
spring.jackson.time-zone=Asia/Tashkent
```

> **Note:** Change `suricata.interface` to match your network interface. Run `networksetup -listallhardwareports` to find your WiFi device name.

---

## 🐛 Troubleshooting

<details>
<summary><b>OutOfMemoryError: Java heap space</b></summary>

```bash
# Clear large log file
sudo truncate -s 0 /opt/homebrew/var/log/suricata/eve.json

# Run with more memory
export MAVEN_OPTS="-Xmx512m -Xms256m"
mvn spring-boot:run
```
</details>

<details>
<summary><b>"Query did not return unique result" on Start Monitoring</b></summary>

```bash
PGPASSWORD="123" psql -U postgres -d ids_home_network \
  -c "UPDATE suricata_sessions SET status='STOPPED' WHERE status='RUNNING';"
```
</details>

<details>
<summary><b>Port already in use</b></summary>

```bash
lsof -ti:8080 | xargs kill -9   # Backend
lsof -ti:3000 | xargs kill -9   # Frontend
```
</details>

<details>
<summary><b>Alerts showing "unknown" IP/port</b></summary>

Suricata is listening on the wrong interface. Check your WiFi interface:

```bash
networksetup -listallhardwareports | grep -A1 "Wi-Fi"
# Update suricata.interface= in application.properties
```
</details>

<details>
<summary><b>Frontend "Operation timed out"</b></summary>

```bash
cd frontend
rm -rf node_modules
npm cache clean --force
npm install && npm start
```
</details>

---

## 🔒 Security

- JWT token authentication (24-hour expiration)
- BCrypt password hashing
- CORS configured for `localhost:3000`
- SQL injection protection via JPA/Hibernate
- XSS protection headers

---

## 👨‍💻 Author

<div align="center">

**Abdurashid G'ayratov**

Student ID: `2428264`

📧 abdurashidgayratov0@gmail.com

*University Diploma Project — 2026*

---

⭐ **Star this repo if you find it useful!**

</div>

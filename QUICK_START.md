

# 🚀 Quick Start Guide

## Get running in 5 minutes!

### 1. Clone the Project

```bash
git clone https://github.com/abdurashidgayratov/IDS-for-home-networks.git
cd IDS-for-home-networks
```

### 2. Install Everything (One Command!)

```bash
./setup.sh
```

This script automatically:
- ✅ Installs and configures PostgreSQL
- ✅ Installs Suricata IDS
- ✅ Installs Java, Node.js, and Maven
- ✅ Creates database
- ✅ Installs backend and frontend dependencies

### 3. Start the Application

**Option A: Separate Terminals**

Terminal 1 - Backend:
```bash
cd backend
mvn spring-boot:run
```

Terminal 2 - Frontend:
```bash
cd frontend
npm start
```

**Option B: Single Command**

```bash
./start.sh
```

### 4. Open Browser

Automatically opens at: `http://localhost:3000`

### 5. Register and Login

1. Click "Register" button
2. Enter your details
3. Login with credentials

### 6. Start Monitoring!

Click "▶️ Start Monitoring" on the Dashboard!

---

## Test Attack Simulation

Open a new terminal:

```bash
# Port scan
nmap -p 1-1000 localhost

# SYN flood
sudo hping3 -c 100 -S -p 80 localhost
```

Alerts will appear on the dashboard! 🚨

---

## Troubleshooting

### Port already in use:
```bash
lsof -ti:8080 | xargs kill -9
lsof -ti:3000 | xargs kill -9
```

### Database error:
```bash
brew services restart postgresql@14
psql postgres -c "CREATE DATABASE ids_home_network;"
```

### Suricata not working:
```bash
sudo chown -R $(whoami) /var/log/suricata/
sudo suricata -T -c /opt/homebrew/etc/suricata/suricata.yaml
```

---

## Video Tutorial

Check the full demo video in the README or on YouTube

---

## Need Help?

- GitHub Issues: https://github.com/abdurashidgayratov/IDS-for-home-networks/issues
- Email: abdurashidgayratov0@gmail.com

---

⭐ Star the project if you find it useful!



# 🚀 Quick Start Guide

## 5 daqiqada ishga tushirish!

### 1. Proyektni yuklab olish

```bash
git clone https://github.com/YOUR_USERNAME/ids-home-network.git
cd ids-home-network
```

### 2. Hammasini o'rnatish (bitta buyruq!)

```bash
./setup.sh
```

Bu skript avtomatik ravishda:
- ✅ PostgreSQL o'rnatadi va sozlaydi
- ✅ Suricata o'rnatadi
- ✅ Java, Node.js, Maven o'rnatadi
- ✅ Database yaratadi
- ✅ Backend va frontend dependencies o'rnatadi

### 3. Ishga tushirish

**Variant A: Alohida terminal larda**

Terminal 1 - Backend:
```bash
cd backend
mvn spring-boot:run
```

Terminal 2 - Frontend:
```bash
cd frontend
npm start
```

**Variant B: Bitta buyruq**

```bash
./start.sh
```

### 4. Brauzer

Avtomatik ochiladi: `http://localhost:3000`

### 5. Register va Login

1. "Register" tugmasini bosing
2. Ma'lumotlarni kiriting
3. Login qiling

### 6. Start Monitoring!

Dashboard da "▶️ Start Monitoring" tugmasini bosing!

---

## Test hujum

Yangi terminal:

```bash
# Port scan
nmap -p 1-1000 localhost

# SYN flood
sudo hping3 -c 100 -S -p 80 localhost
```

Alertlar dashboard da ko'rinadi! 🚨

---

## Muammolar?

### Port band:
```bash
lsof -ti:8080 | xargs kill -9
lsof -ti:3000 | xargs kill -9
```

### Database xatolik:
```bash
brew services restart postgresql@14
psql postgres -c "CREATE DATABASE ids_home_network;"
```

### Suricata ishlamasa:
```bash
sudo chown -R $(whoami) /var/log/suricata/
sudo suricata -T -c /opt/homebrew/etc/suricata/suricata.yaml
```

---

## Video Tutorial

1. YouTube da qidiring: "IDS Home Network Setup"
2. Yoki README.md ni o'qing

---

## Yordam

- GitHub Issues: https://github.com/YOUR_USERNAME/ids-home-network/issues
- Email: your@email.com

---

⭐ Agar foydali bo'lsa, star bering!

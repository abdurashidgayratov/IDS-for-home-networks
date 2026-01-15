
#!/bin/bash

echo "🚀 Setting up IDS Home Network..."
echo ""
echo "Author: Abdurashid G'ayratov (Student ID: 2428264)"
echo "Project: Intrusion Detection System for Home Networks"
echo ""

# Check if Homebrew is installed
if ! command -v brew &> /dev/null; then
    echo "❌ Homebrew not found. Installing..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
else
    echo "✅ Homebrew already installed"
fi

# Install PostgreSQL
echo ""
echo "📦 Installing PostgreSQL..."
if ! command -v psql &> /dev/null; then
    brew install postgresql@14
    brew services start postgresql@14
    echo "✅ PostgreSQL installed and started"
else
    echo "✅ PostgreSQL already installed"
    brew services start postgresql@14
fi

# Install Suricata
echo ""
echo "📦 Installing Suricata IDS..."
if ! command -v suricata &> /dev/null; then
    brew install suricata
    echo "✅ Suricata installed"
else
    echo "✅ Suricata already installed"
fi

# Install Java
echo ""
echo "📦 Installing Java 17..."
if ! command -v java &> /dev/null; then
    brew install openjdk@17
    echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
    echo "✅ Java 17 installed"
else
    echo "✅ Java already installed"
fi

# Install Node.js
echo ""
echo "📦 Installing Node.js..."
if ! command -v node &> /dev/null; then
    brew install node
    echo "✅ Node.js installed"
else
    echo "✅ Node.js already installed"
fi

# Install Maven
echo ""
echo "📦 Installing Maven..."
if ! command -v mvn &> /dev/null; then
    brew install maven
    echo "✅ Maven installed"
else
    echo "✅ Maven already installed"
fi

# Setup PostgreSQL database
echo ""
echo "💾 Setting up database..."
psql postgres -c "CREATE DATABASE ids_home_network;" 2>/dev/null && echo "✅ Database created" || echo "⚠️  Database already exists"

# Setup Suricata
echo ""
echo "🔧 Setting up Suricata..."
sudo suricata-update
sudo mkdir -p /var/log/suricata
sudo chown -R $(whoami) /var/log/suricata
echo "✅ Suricata configured"

# Install backend dependencies
echo ""
echo "📦 Installing backend dependencies..."
cd backend
mvn clean install -DskipTests
cd ..
echo "✅ Backend dependencies installed"

# Install frontend dependencies
echo ""
echo "📦 Installing frontend dependencies..."
cd frontend
npm install
cd ..
echo "✅ Frontend dependencies installed"

echo ""
echo "========================================="
echo "✅ Setup complete!"
echo "========================================="
echo ""
echo "To start the application:"
echo "1. Backend:  cd backend && mvn spring-boot:run"
echo "2. Frontend: cd frontend && npm start"
echo ""
echo "Or use the quick start script:"
echo "./start.sh"
echo ""
echo "For help: abdurashidgayratov0@gmail.com"
echo ""



#!/bin/bash

echo "🚀 Setting up IDS Home Network..."
echo ""

# Check if Homebrew is installed
if ! command -v brew &> /dev/null; then
    echo "❌ Homebrew not found. Installing..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
fi

# Install PostgreSQL
echo "📦 Installing PostgreSQL..."
brew install postgresql@14
brew services start postgresql@14

# Install Suricata
echo "📦 Installing Suricata..."
brew install suricata

# Install Java
echo "📦 Installing Java 17..."
brew install openjdk@17

# Install Node.js
echo "📦 Installing Node.js..."
brew install node

# Install Maven
echo "📦 Installing Maven..."
brew install maven

# Setup PostgreSQL database
echo "💾 Setting up database..."
psql postgres -c "CREATE DATABASE ids_home_network;" 2>/dev/null || echo "Database already exists"

# Setup Suricata
echo "🔧 Setting up Suricata..."
sudo suricata-update
sudo mkdir -p /var/log/suricata
sudo chown -R $(whoami) /var/log/suricata

# Install backend dependencies
echo "📦 Installing backend dependencies..."
cd backend
mvn clean install -DskipTests
cd ..

# Install frontend dependencies
echo "📦 Installing frontend dependencies..."
cd frontend
npm install
cd ..

echo ""
echo "✅ Setup complete!"
echo ""
echo "To start the application:"
echo "1. Backend:  cd backend && mvn spring-boot:run"
echo "2. Frontend: cd frontend && npm start"
echo ""

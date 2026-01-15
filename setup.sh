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

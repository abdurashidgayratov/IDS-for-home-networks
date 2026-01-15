#!/bin/bash

echo "🚀 Starting IDS Home Network..."

# Start backend in background
echo "▶️  Starting backend..."
cd backend
mvn spring-boot:run &
BACKEND_PID=$!

# Wait for backend to start
sleep 10

# Start frontend
echo "▶️  Starting frontend..."
cd ../frontend
npm start &
FRONTEND_PID=$!

echo ""
echo "✅ Application started!"
echo "   Backend PID: $BACKEND_PID"
echo "   Frontend PID: $FRONTEND_PID"
echo ""
echo "📝 Access the application at: http://localhost:3000"
echo ""
echo "To stop: kill $BACKEND_PID $FRONTEND_PID"
echo ""

# Wait for user input
read -p "Press Enter to stop the application..."

# Kill processes
kill $BACKEND_PID $FRONTEND_PID
echo "🛑 Application stopped"

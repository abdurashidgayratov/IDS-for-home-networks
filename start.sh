#!/bin/bash

echo "🚀 Starting IDS Home Network..."
echo ""
echo "Author: Abdurashid G'ayratov"
echo "Email: abdurashidgayratov0@gmail.com"
echo ""

# Start backend in background
echo "▶️  Starting backend server..."
cd backend
mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
cd ..

echo "   Backend PID: $BACKEND_PID"
echo "   Waiting for backend to start..."

# Wait for backend to be ready (check for port 8080)
for i in {1..30}; do
    if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null ; then
        echo "   ✅ Backend is ready!"
        break
    fi
    echo -n "."
    sleep 2
done

# Start frontend
echo ""
echo "▶️  Starting frontend server..."
cd frontend
npm start &
FRONTEND_PID=$!
cd ..

echo "   Frontend PID: $FRONTEND_PID"
echo ""
echo "========================================="
echo "✅ Application started successfully!"
echo "========================================="
echo ""
echo "📝 Access points:"
echo "   Frontend: http://localhost:3000"
echo "   Backend:  http://localhost:8080"
echo ""
echo "🔍 Process IDs:"
echo "   Backend:  $BACKEND_PID"
echo "   Frontend: $FRONTEND_PID"
echo ""
echo "📋 Logs:"
echo "   Backend:  tail -f backend.log"
echo "   Frontend: Check terminal output"
echo ""
echo "🛑 To stop the application:"
echo "   kill $BACKEND_PID $FRONTEND_PID"
echo "   Or press Ctrl+C in frontend terminal"
echo ""

# Wait for user input
echo "Press Enter to stop all services..."
read

# Kill processes
echo ""
echo "🛑 Stopping services..."
kill $BACKEND_PID 2>/dev/null
kill $FRONTEND_PID 2>/dev/null
echo "✅ Application stopped"
echo ""
echo "Thank you for using IDS Home Network!"
echo "For support: abdurashidgayratov0@gmail.com"
echo ""



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

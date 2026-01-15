import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
  }

  connect(userId, onAlertReceived) {
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect(
      {},
      () => {
        console.log('WebSocket Connected');
        this.connected = true;

        // Subscribe to user-specific alerts
        this.stompClient.subscribe(`/topic/alerts/${userId}`, (message) => {
          const alert = JSON.parse(message.body);
          console.log('New alert received:', alert);
          if (onAlertReceived) {
            onAlertReceived(alert);
          }
        });
      },
      (error) => {
        console.error('WebSocket Connection Error:', error);
        this.connected = false;
        
        // Retry connection after 5 seconds
        setTimeout(() => {
          console.log('Retrying WebSocket connection...');
          this.connect(userId, onAlertReceived);
        }, 5000);
      }
    );
  }

  disconnect() {
    if (this.stompClient && this.connected) {
      this.stompClient.disconnect();
      this.connected = false;
      console.log('WebSocket Disconnected');
    }
  }

  isConnected() {
    return this.connected;
  }
}

export default new WebSocketService();

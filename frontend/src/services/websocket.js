import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';

class WebSocketService {
  constructor() {
    this.stompClient = null;
    this.connected = false;
    this.userId = null;
    this.onAlertReceived = null;
  }

  connect(userId, onAlertReceived) {
    this.userId = userId;
    this.onAlertReceived = onAlertReceived;

    try {
      const socket = new SockJS('http://localhost:8080/ws');
      this.stompClient = Stomp.over(socket);
      this.stompClient.debug = () => {};

      const headers = {};
      const token = localStorage.getItem('token');
      if (token) headers['Authorization'] = `Bearer ${token}`;

      this.stompClient.connect(
        headers,
        (frame) => {
          this.connected = true;
          console.log('WebSocket Connected');

          setTimeout(() => {
            try {
              if (this.stompClient && this.connected) {
                this.stompClient.subscribe(
                  `/topic/alerts/${userId}`,
                  (message) => {
                    try {
                      const alert = JSON.parse(message.body);
                      if (this.onAlertReceived) this.onAlertReceived(alert);
                    } catch (e) {
                      console.error('Parse error:', e);
                    }
                  }
                );
              }
            } catch (e) {
              console.error('Subscribe error:', e);
            }
          }, 500);
        },
        (error) => {
          this.connected = false;
          setTimeout(() => {
            if (this.userId) this.connect(this.userId, this.onAlertReceived);
          }, 5000);
        }
      );
    } catch (e) {
      console.error('WebSocket init error:', e);
    }
  }

  disconnect() {
    if (this.stompClient && this.connected) {
      try {
        this.stompClient.disconnect();
      } catch (e) {}
      this.connected = false;
    }
    this.userId = null;
    this.onAlertReceived = null;
  }

  isConnected() {
    return this.connected;
  }
}

export default new WebSocketService();
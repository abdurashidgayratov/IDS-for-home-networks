import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { suricataAPI, alertAPI, statisticsAPI, authAPI } from '../services/api';
import websocketService from '../services/websocket';
import AlertList from '../components/AlertList';
import Statistics from '../components/Statistics';
import '../styles/Dashboard.css';

function Dashboard() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [isMonitoring, setIsMonitoring] = useState(false);
  const [alerts, setAlerts] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [tab, setTab] = useState('alerts');

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      // Get current user
      const userResponse = await authAPI.getCurrentUser();
      setUser(userResponse.data);

      // Get Suricata status
      const statusResponse = await suricataAPI.getStatus();
      setIsMonitoring(statusResponse.data.isRunning);

      // Get alerts
      const alertsResponse = await alertAPI.getAll();
      setAlerts(alertsResponse.data);

      // Get statistics
      const statsResponse = await statisticsAPI.get();
      setStatistics(statsResponse.data);

      // Connect WebSocket
      if (userResponse.data.id) {
        websocketService.connect(userResponse.data.id, handleNewAlert);
      }

      setLoading(false);
    } catch (error) {
      console.error('Failed to load dashboard', error);
      setLoading(false);
    }
  };

  const handleNewAlert = (newAlert) => {
    setAlerts(prevAlerts => [newAlert, ...prevAlerts]);
    
    // Update statistics
    if (statistics) {
      setStatistics({
        ...statistics,
        totalAlerts: statistics.totalAlerts + 1,
        unreadAlerts: statistics.unreadAlerts + 1
      });
    }

    // Show notification
    if (Notification.permission === 'granted') {
      new Notification('New Security Alert!', {
        body: `${newAlert.signature} from ${newAlert.sourceIp}`,
        icon: '/alert-icon.png'
      });
    }
  };

  const handleStartMonitoring = async () => {
    try {
      await suricataAPI.start();
      setIsMonitoring(true);
      alert('Suricata monitoring started!');
      
      // Request notification permission
      if (Notification.permission === 'default') {
        Notification.requestPermission();
      }
    } catch (error) {
      console.error('Failed to start monitoring', error);
      alert('Failed to start monitoring: ' + (error.response?.data?.error || error.message));
    }
  };

  const handleStopMonitoring = async () => {
    try {
      await suricataAPI.stop();
      setIsMonitoring(false);
      alert('Suricata monitoring stopped!');
    } catch (error) {
      console.error('Failed to stop monitoring', error);
      alert('Failed to stop monitoring: ' + (error.response?.data?.error || error.message));
    }
  };

  const handleLogout = () => {
    websocketService.disconnect();
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const refreshData = async () => {
    try {
      const alertsResponse = await alertAPI.getAll();
      setAlerts(alertsResponse.data);

      const statsResponse = await statisticsAPI.get();
      setStatistics(statsResponse.data);
    } catch (error) {
      console.error('Failed to refresh data', error);
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <div className="header-left">
          <h1>🛡️ IDS Home Network</h1>
          <p className="subtitle">Real-time Intrusion Detection</p>
        </div>
        <div className="header-right">
          <span className="user-info">👤 {user?.username}</span>
          <button onClick={handleLogout} className="btn-secondary">
            Logout
          </button>
        </div>
      </header>

      <div className="dashboard-content">
        <div className="control-panel">
          <div className="monitoring-status">
            <div className={`status-indicator ${isMonitoring ? 'active' : 'inactive'}`}>
              <span className="status-dot"></span>
              <span className="status-text">
                {isMonitoring ? 'Monitoring Active' : 'Monitoring Inactive'}
              </span>
            </div>
            
            {isMonitoring ? (
              <button onClick={handleStopMonitoring} className="btn-danger">
                🛑 Stop Monitoring
              </button>
            ) : (
              <button onClick={handleStartMonitoring} className="btn-success">
                ▶️ Start Monitoring
              </button>
            )}
            
            <button onClick={refreshData} className="btn-secondary">
              🔄 Refresh
            </button>
          </div>
        </div>

        <div className="stats-overview">
          {statistics && (
            <>
              <div className="stat-card">
                <div className="stat-value">{statistics.totalAlerts}</div>
                <div className="stat-label">Total Alerts</div>
              </div>
              <div className="stat-card">
                <div className="stat-value">{statistics.unreadAlerts}</div>
                <div className="stat-label">Unread</div>
              </div>
              <div className="stat-card">
                <div className="stat-value">{statistics.todayAlerts}</div>
                <div className="stat-label">Today</div>
              </div>
              <div className="stat-card">
                <div className="stat-value">{statistics.thisWeekAlerts}</div>
                <div className="stat-label">This Week</div>
              </div>
            </>
          )}
        </div>

        <div className="dashboard-tabs">
          <button 
            className={`tab ${tab === 'alerts' ? 'active' : ''}`}
            onClick={() => setTab('alerts')}
          >
            🚨 Alerts ({alerts.length})
          </button>
          <button 
            className={`tab ${tab === 'statistics' ? 'active' : ''}`}
            onClick={() => setTab('statistics')}
          >
            📊 Statistics
          </button>
        </div>

        <div className="dashboard-main">
          {tab === 'alerts' && (
            <AlertList alerts={alerts} onRefresh={refreshData} />
          )}
          {tab === 'statistics' && statistics && (
            <Statistics data={statistics} />
          )}
        </div>
      </div>
    </div>
  );
}

export default Dashboard;

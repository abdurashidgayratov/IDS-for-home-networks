import React, { useState } from 'react';
import { alertAPI } from '../services/api';
import '../styles/AlertList.css';

function AlertList({ alerts, onRefresh }) {
  const [selectedAlert, setSelectedAlert] = useState(null);

  const getSeverityClass = (severity) => {
    switch(severity) {
      case 1: return 'critical';
      case 2: return 'high';
      case 3: return 'medium';
      default: return 'low';
    }
  };

  const getSeverityText = (severity) => {
    switch(severity) {
      case 1: return 'Critical';
      case 2: return 'High';
      case 3: return 'Medium';
      default: return 'Low';
    }
  };

  const handleMarkAsRead = async (id) => {
    try {
      await alertAPI.markAsRead(id);
      onRefresh();
    } catch (error) {
      console.error('Failed to mark as read', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await alertAPI.markAllAsRead();
      onRefresh();
    } catch (error) {
      console.error('Failed to mark all as read', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this alert?')) {
      try {
        await alertAPI.delete(id);
        onRefresh();
        setSelectedAlert(null);
      } catch (error) {
        console.error('Failed to delete alert', error);
      }
    }
  };

  const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleString();
  };

  if (alerts.length === 0) {
    return (
      <div className="alert-list-empty">
        <p>No alerts yet. Start monitoring to detect intrusions!</p>
      </div>
    );
  }

  return (
    <div className="alert-list-container">
      <div className="alert-list-header">
        <h2>Security Alerts</h2>
        <button onClick={handleMarkAllAsRead} className="btn-secondary">
          Mark All as Read
        </button>
      </div>

      <div className="alert-list">
        {alerts.map((alert) => (
          <div 
            key={alert.id}
            className={`alert-item ${!alert.isRead ? 'unread' : ''} ${getSeverityClass(alert.severity)}`}
            onClick={() => setSelectedAlert(alert)}
          >
            <div className="alert-header">
              <span className={`severity-badge ${getSeverityClass(alert.severity)}`}>
                {getSeverityText(alert.severity)}
              </span>
              <span className="alert-time">{formatTimestamp(alert.timestamp)}</span>
            </div>
            
            <div className="alert-signature">{alert.signature}</div>
            
            <div className="alert-details">
              <span>📍 {alert.sourceIp}:{alert.sourcePort || '?'}</span>
              <span>→</span>
              <span>🎯 {alert.destinationIp}:{alert.destinationPort || '?'}</span>
              <span className="protocol-badge">{alert.protocol}</span>
            </div>
          </div>
        ))}
      </div>

      {selectedAlert && (
        <div className="alert-modal" onClick={() => setSelectedAlert(null)}>
          <div className="alert-modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="alert-modal-header">
              <h3>Alert Details</h3>
              <button onClick={() => setSelectedAlert(null)} className="close-btn">×</button>
            </div>
            
            <div className="alert-modal-body">
              <div className="detail-row">
                <strong>Severity:</strong>
                <span className={`severity-badge ${getSeverityClass(selectedAlert.severity)}`}>
                  {getSeverityText(selectedAlert.severity)}
                </span>
              </div>
              
              <div className="detail-row">
                <strong>Signature:</strong>
                <span>{selectedAlert.signature}</span>
              </div>
              
              <div className="detail-row">
                <strong>Category:</strong>
                <span>{selectedAlert.category}</span>
              </div>
              
              <div className="detail-row">
                <strong>Time:</strong>
                <span>{formatTimestamp(selectedAlert.timestamp)}</span>
              </div>
              
              <div className="detail-row">
                <strong>Source:</strong>
                <span>{selectedAlert.sourceIp}:{selectedAlert.sourcePort || 'N/A'}</span>
              </div>
              
              <div className="detail-row">
                <strong>Destination:</strong>
                <span>{selectedAlert.destinationIp}:{selectedAlert.destinationPort || 'N/A'}</span>
              </div>
              
              <div className="detail-row">
                <strong>Protocol:</strong>
                <span>{selectedAlert.protocol}</span>
              </div>
            </div>
            
            <div className="alert-modal-footer">
              {!selectedAlert.isRead && (
                <button 
                  onClick={() => handleMarkAsRead(selectedAlert.id)}
                  className="btn-primary"
                >
                  Mark as Read
                </button>
              )}
              <button 
                onClick={() => handleDelete(selectedAlert.id)}
                className="btn-danger"
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AlertList;

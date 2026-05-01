import React, { useState } from 'react';
import { alertAPI } from '../services/api';
import '../styles/AlertList.css';

const ITEMS_PER_PAGE = 10;

function AlertList({ alerts, onRefresh }) {
  const [selectedAlert, setSelectedAlert] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [blockingId, setBlockingId] = useState(null);

  const totalPages = Math.ceil(alerts.length / ITEMS_PER_PAGE);
  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
  const endIndex = startIndex + ITEMS_PER_PAGE;
  const currentAlerts = alerts.slice(startIndex, endIndex);

  // Sahifa raqamlarini hisoblash (1 ... 4 5 6 ... 10)
  const getPageNumbers = () => {
    const pages = [];
    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) pages.push(i);
    } else {
      if (currentPage <= 4) {
        pages.push(1, 2, 3, 4, 5, '...', totalPages);
      } else if (currentPage >= totalPages - 3) {
        pages.push(1, '...', totalPages-4, totalPages-3, totalPages-2, totalPages-1, totalPages);
      } else {
        pages.push(1, '...', currentPage-1, currentPage, currentPage+1, '...', totalPages);
      }
    }
    return pages;
  };

  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

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
        // Agar sahifada 1 ta qolgan bo'lsa oldingi sahifaga qaytish
        if (currentAlerts.length === 1 && currentPage > 1) {
          setCurrentPage(currentPage - 1);
        }
      } catch (error) {
        console.error('Failed to delete alert', error);
      }
    }
  };

  const handleBlockIp = async (alertId, sourceIp, e) => {
    e && e.stopPropagation();
    if (!window.confirm(`Block IP: ${sourceIp}?`)) return;
    setBlockingId(alertId);
    try {
      await alertAPI.blockIp(alertId);
      alert(`✅ ${sourceIp} bloklandi!`);
      onRefresh();
    } catch (error) {
      alert(`❌ Xato: ${error.response?.data?.error || error.message}`);
    } finally {
      setBlockingId(null);
    }
  };

  const formatTimestamp = (timestamp) => {
    return new Date(timestamp).toLocaleString();
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
        {currentAlerts.map((alert) => (
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
            <div className="alert-actions" onClick={e => e.stopPropagation()}>
              <button
                className="btn-block-ip"
                onClick={(e) => handleBlockIp(alert.id, alert.sourceIp, e)}
                disabled={blockingId === alert.id}
              >
                {blockingId === alert.id ? '⏳...' : '🚫 Block IP'}
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* ===== PAGINATION ===== */}
      {totalPages > 1 && (
        <div className="pagination">
          <div className="pagination-info">
            <span>{startIndex + 1}–{Math.min(endIndex, alerts.length)}</span>
            {' '}/ {alerts.length} alert
          </div>

          <div className="pagination-controls">
            {/* Previous */}
            <button
              className="page-btn"
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 1}
              title="Previous"
            >
              ‹
            </button>

            {/* Page numbers */}
            {getPageNumbers().map((page, index) =>
              page === '...' ? (
                <span key={`dots-${index}`} className="page-dots">…</span>
              ) : (
                <button
                  key={page}
                  className={`page-number ${currentPage === page ? 'active' : ''}`}
                  onClick={() => handlePageChange(page)}
                >
                  {page}
                </button>
              )
            )}

            {/* Next */}
            <button
              className="page-btn"
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage === totalPages}
              title="Next"
            >
              ›
            </button>
          </div>
        </div>
      )}

      {/* ===== MODAL ===== */}
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
                <button onClick={() => handleMarkAsRead(selectedAlert.id)} className="btn-primary">
                  Mark as Read
                </button>
              )}
              <button
                onClick={(e) => handleBlockIp(selectedAlert.id, selectedAlert.sourceIp, e)}
                className="btn-block-ip"
                disabled={blockingId === selectedAlert.id}
              >
                {blockingId === selectedAlert.id ? '⏳...' : '🚫 Block IP'}
              </button>
              <button onClick={() => handleDelete(selectedAlert.id)} className="btn-danger">
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

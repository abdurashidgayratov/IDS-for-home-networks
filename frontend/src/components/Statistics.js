import React from 'react';
import { Chart as ChartJS, ArcElement, CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend } from 'chart.js';
import { Pie, Bar, Line } from 'react-chartjs-2';
import '../styles/Statistics.css';

ChartJS.register(ArcElement, CategoryScale, LinearScale, BarElement, LineElement, PointElement, Title, Tooltip, Legend);

function Statistics({ data }) {
  const severityChartData = {
    labels: Object.keys(data.alertsBySeverity || {}),
    datasets: [{
      label: 'Alerts by Severity',
      data: Object.values(data.alertsBySeverity || {}),
      backgroundColor: [
        '#ff4444',
        '#ff8800',
        '#ffbb33',
        '#00C851'
      ]
    }]
  };

  const categoryChartData = {
    labels: Object.keys(data.alertsByCategory || {}).slice(0, 10),
    datasets: [{
      label: 'Alerts by Category',
      data: Object.values(data.alertsByCategory || {}).slice(0, 10),
      backgroundColor: '#4285f4'
    }]
  };

  const timelineChartData = {
    labels: (data.timeline || []).map(point => point.date),
    datasets: [{
      label: 'Alerts Over Time',
      data: (data.timeline || []).map(point => point.count),
      borderColor: '#4285f4',
      backgroundColor: 'rgba(66, 133, 244, 0.1)',
      tension: 0.4
    }]
  };

  return (
    <div className="statistics-container">
      <h2>Statistics & Analytics</h2>

      <div className="stats-grid">
        <div className="stat-chart">
          <h3>Alerts by Severity</h3>
          {Object.keys(data.alertsBySeverity || {}).length > 0 ? (
            <Pie data={severityChartData} />
          ) : (
            <p className="no-data">No data available</p>
          )}
        </div>

        <div className="stat-chart">
          <h3>Top 10 Categories</h3>
          {Object.keys(data.alertsByCategory || {}).length > 0 ? (
            <Bar data={categoryChartData} options={{ indexAxis: 'y' }} />
          ) : (
            <p className="no-data">No data available</p>
          )}
        </div>

        <div className="stat-chart full-width">
          <h3>Timeline (Last 7 Days)</h3>
          {data.timeline && data.timeline.length > 0 ? (
            <Line data={timelineChartData} />
          ) : (
            <p className="no-data">No data available</p>
          )}
        </div>

        <div className="stat-chart">
          <h3>Top Source IPs</h3>
          <div className="ip-list">
            {data.topSourceIps && data.topSourceIps.length > 0 ? (
              data.topSourceIps.map((item, index) => (
                <div key={index} className="ip-item">
                  <span className="ip-address">{item.ip}</span>
                  <span className="ip-count">{item.count} alerts</span>
                </div>
              ))
            ) : (
              <p className="no-data">No data available</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Statistics;

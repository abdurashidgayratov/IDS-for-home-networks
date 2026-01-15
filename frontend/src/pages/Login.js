import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authAPI } from '../services/api';
import '../styles/Auth.css';

function Login() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authAPI.login(formData);
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify({
        id: response.data.id,
        username: response.data.username,
        email: response.data.email
      }));
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-box">
        <div className="auth-header">
          <h1>🛡️ IDS Home Network</h1>
          <p>Intrusion Detection System</p>
        </div>
        
        <form onSubmit={handleSubmit} className="auth-form">
          <h2>Login</h2>
          
          {error && <div className="error-message">{error}</div>}
          
          <div className="form-group">
            <label>Username</label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
              placeholder="Enter your username"
            />
          </div>
          
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              placeholder="Enter your password"
            />
          </div>
          
          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
          
          <div className="auth-footer">
            Don't have an account? <Link to="/register">Register</Link>
          </div>
        </form>
      </div>
    </div>
  );
}

export default Login;

import React, { useState } from 'react'

const Settings = () => {
  const [address, setAddress] = useState('123 Main St')
  const [email, setEmail] = useState('user@example.com')
  const [password, setPassword] = useState('')

  const handleAddressSave = () => {
    alert(`Address updated: ${address} (placeholder)`)
  }

  const handleEmailSave = () => {
    alert(`Email updated: ${email} (placeholder)`)
  }

  const handlePasswordSave = () => {
    alert('Password updated (placeholder)')
  }

  return (
    <div className="page">
      <div className="page-header">
        <h2>Account Settings</h2>
        <p>Manage your account preferences</p>
      </div>
      
      <div className="settings-section">
        <h4>Shipping Address</h4>
        <div className="settings-input-group">
          <div className="settings-input">
            <input 
              className="form-input"
              value={address} 
              onChange={(e) => setAddress(e.target.value)} 
            />
          </div>
          <button className="btn" onClick={handleAddressSave}>Save Address</button>
        </div>
      </div>

      <div className="settings-section">
        <h4>Email Address</h4>
        <div className="settings-input-group">
          <div className="settings-input">
            <input 
              className="form-input"
              value={email} 
              onChange={(e) => setEmail(e.target.value)} 
            />
          </div>
          <button className="btn" onClick={handleEmailSave}>Save Email</button>
        </div>
      </div>

      <div className="settings-section">
        <h4>Change Password</h4>
        <div className="form-group">
          <input
            type="password"
            className="form-input"
            placeholder="Enter new password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button className="btn" onClick={handlePasswordSave}>Update Password</button>
      </div>
    </div>
  )
}

export default Settings
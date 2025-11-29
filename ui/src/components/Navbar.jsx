import React from 'react'
import { Link } from 'react-router-dom'

const Navbar = ({ onLogout }) => {
  return (
    <nav className="navbar">
      <div className="nav-content">
        <div className="nav-links">
          <Link to="/products" className="nav-link">Products</Link>
          <Link to="/cart" className="nav-link">Cart</Link>
          <Link to="/orders" className="nav-link">Orders</Link>
          <Link to="/settings" className="nav-link">Settings</Link>
        </div>
        <button onClick={onLogout} className="logout-btn">Logout</button>
      </div>
    </nav>
  )
}

export default Navbar
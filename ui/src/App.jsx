import React, { useEffect, useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import api from "./api/axios";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Navbar from "./components/Navbar";
import Products from "./pages/Products";
import Cart from "./pages/Cart";
import Orders from "./pages/Orders";
import Settings from "./pages/Settings";
import { apiUrl } from "./api/api";
import { Service } from "./api/Service";
import PaymentSuccess from "./pages/PaymentSuccess";
import PaymentCancel from "./pages/PaymentCancel";


const App = () => {
  const [user, setUser] = useState(false);
  const [loading, setLoading] = useState(true);

  const handleLogout = () => {
    localStorage.removeItem("token");
    setUser(false);
    window.location.href = "/";
  };

  useEffect(() => {
    const refresh = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        setLoading(false);
        return;
      }

      try {
        const { data } = await api.post(apiUrl(Service.USERS, "/token/refresh"));
        localStorage.setItem("token", data.token);
        setUser(true);
      } catch {
        handleLogout();
      } finally {
        setLoading(false);
      }
    };

    refresh();
  }, []);

  if (loading) return <div>Loading...</div>;

  return (
    <Router>
      {user && <Navbar onLogout={handleLogout} />}
      <div className="container">
        <Routes>
          <Route path="/" element={!user ? <Login onLogin={() => setUser(true)} /> : <Navigate to="/products" />} />
          <Route path="/signup" element={!user ? <Signup onSignup={() => setUser(true)} /> : <Navigate to="/settings" />} />
          <Route path="/products" element={user ? <Products /> : <Navigate to="/" />} />
          <Route path="/cart" element={user ? <Cart /> : <Navigate to="/" />} />
          <Route path="/orders" element={user ? <Orders /> : <Navigate to="/" />} />
          <Route path="/settings" element={user ? <Settings /> : <Navigate to="/" />} />
          <Route path="/payment/success" element={<PaymentSuccess />} />
          <Route path="/payment/cancel" element={<PaymentCancel />} />
        </Routes>
      </div>
    </Router>
  );
};

export default App;

import React, { useState, useEffect } from "react";
import apiClient from '../api/axios';
import { apiUrl } from '../api/api';
import { Service } from '../api/Service';
import { useNavigate } from "react-router-dom";


const Cart = () => {
  const [cart, setCart] = useState([]);
  const [total, setTotal] = useState(0);
  const [checkoutView, setCheckoutView] = useState(false);
  const [shippingAddress, setShippingAddress] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("PayPal");
  const [contact, setContact] = useState("");
  const [paymentUrl, setPaymentUrl] = useState(null);
  const [orderId, setOrderId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCart = async () => {
      try {
        const { data } = await apiClient.get(apiUrl(Service.CART, "/all"));
        setCart(data.items || []);
        setTotal(data.totalCartPrice || 0);
      } catch (err) {
        console.error("Failed to fetch cart", err);
      }
    };
    fetchCart();
  }, []);

  const handleQuantity = async (productId, qty) => {
    if (qty < 1) return;
    try {
      const { data } = await apiClient.put(
        apiUrl(Service.CART, `/items/${productId}`),
        null, { params: { quantity: qty } }
      );
      setCart(data.items || []);
      setTotal(data.totalCartPrice || 0);
    } catch (err) {
      alert(`Unable to Update at the moment.Please try later.`)
      console.error("Update failed", err);
    }
  };

  const removeItem = async (productId) => {
    try {
      const { data } = await apiClient.delete(
        apiUrl(Service.CART, `/items/${productId}`)
      );
      setCart(data.items || []);
      setTotal(data.totalCartPrice || 0);
    } catch (err) {
      console.error("Remove failed", err);
    }
  };

  const placeOrder = async () => {
   if (!shippingAddress || !contact) {
    alert("Please fill all required fields");
    return;
  } 
  const checkoutPayload = {
    cartItems: cart,
    shippingAddress,
    paymentMethod,
    contact
  }; 
  try {
    const { data } = await apiClient.post(
      apiUrl(Service.ORDERS, "/checkout"),
      checkoutPayload
    );

    setOrderId(data.orderId);
    setPaymentUrl(data.paymentApprovedUrl || null);
    setCheckoutView(false);
    setCart([]);

  } catch (err) {
    console.error("Order failed", err);
    alert("Unable to place order right now. Try again later.");
  }
};


  const clearCart = async () => {
    try {
      await apiClient.delete(apiUrl(Service.CART, "/clear"));
      setCart([]);
    } catch (err) {
      console.error("Failed to clear cart", err);
    }
  };

  // ================= RENDER ==================
  return (
    <div className="cart-container">
      {paymentUrl ? (
      <>
        <h2 className="cart-title">Payment Required</h2>

        <div className="payment-box">
          <p><strong>Order Number:</strong> {orderId}</p>
          <p>Please complete your payment to confirm your order.</p>

          <div className="payment-actions">
            <button
              className="btn-pay"
              onClick={() => window.open(paymentUrl, "_blank")}
            >
              Pay Now
            </button>

            <button
              className="btn-cancel"
              onClick={async () => {
                try {
                  await apiClient.put(apiUrl(Service.ORDERS, `/${orderId}/cancel`));
                  alert("Order cancelled.");
                  setPaymentUrl(null);
                  setOrderId(null);
                  setCheckoutView(false);
                  navigate("/orders");
                } catch (err) {
                  console.log(err)
                  alert("Unable to cancel order right now.");
                }
              }}
            >
              Cancel Order
            </button>
          </div>
        </div>
      </>
    ) : !checkoutView ? (
      <>
        <h2 className="cart-title">🛒 Your Cart</h2>

        {cart.length === 0 ? (
          <p className="empty-msg">Your cart is empty.</p>
        ) : (
          <>
            <ul className="cart-list">
              {cart.map(item => (
                <li key={item.productId} className="cart-item">

                  <div className="item-info">
                    <span className="item-name">{item.productName}</span>
                  </div>

                  <div className="quantity-controls">
                    <input
                      type="number"
                      min="1"
                      value={item.quantity}
                      onChange={(e) =>
                        handleQuantity(item.productId, Number(e.target.value))
                      }
                    />
                  </div>

                  <span className="item-subtotal">
                    ${item.totalPrice.toFixed(2)}
                  </span>

                  <button className="remove-btn" onClick={() => removeItem(item.productId)}>
                    ✖
                  </button>
                </li>
              ))}
            </ul>

            <div className="cart-summary">
              <h3>Total: ${total.toFixed(2)}</h3>
              <button className="checkout-btn" onClick={() => setCheckoutView(true)}>
                Proceed to Checkout
              </button>

              <button className="btn-clear" onClick={clearCart}>
                Clear Cart
              </button>
            </div>
          </>
        )}
      </>
    ) : (
      <>
        {/* CHECKOUT VIEW */}
        <h2 className="cart-title">Checkout</h2>

        <div className="checkout-info-box">
          <p><strong>Items:</strong> {cart.length}</p>
          <p><strong>Total Amount:</strong> ${total.toFixed(2)}</p>
        </div>

        <div className="checkout-form">
          <label>
            Shipping Address
            <input
              type="text"
              placeholder="Enter delivery address"
              value={shippingAddress}
              onChange={(e) => setShippingAddress(e.target.value)}
            />
          </label>

          <label>
            Landmark (optional)
            <input type="text" placeholder="Landmark" />
          </label>

          <label>
            Phone
            <input
              type="text"
              placeholder="Phone number"
              value={contact}
              onChange={(e) => setContact(e.target.value)}
            />
          </label>

          <label>
            Payment Type
            <select value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)}>
              <option>PayPal</option>
            </select>
          </label>
        </div>

        <div className="checkout-actions">
          <button className="confirm-btn" onClick={placeOrder}>Confirm Order</button>
          <button className="cancel-btn" onClick={() => setCheckoutView(false)}>Back to Cart</button>
        </div>
      </>
    )}
  </div>
);
};

export default Cart;

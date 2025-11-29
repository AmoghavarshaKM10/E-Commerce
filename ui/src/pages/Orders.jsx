import React, { useEffect, useState } from 'react'
import apiClient from '../api/axios'
import { apiUrl } from '../api/api'
import { Service } from '../api/Service'
import { useNavigate } from 'react-router-dom'

const Orders = () => {
  const [orders, setOrders] = useState([])
  const [selectedOrder, setSelectedOrder] = useState(null) // for modal
  const navigate = useNavigate()

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const { data } = await apiClient.get(apiUrl(Service.ORDERS, '/all'))
        setOrders(data)
      } catch (err) {
        console.error("Failed to fetch orders", err)
        setOrders([])
      }
    }
    fetchOrders()
  }, [])

  const cancelOrder = async (orderId) => {
  try {
    const response = await apiClient.put(apiUrl(Service.ORDERS, `/${orderId}/cancel`));

    if (response.status === 200) {
      const updatedOrder = response.data; 
      setOrders(prev =>
        prev.map(o =>
          o.orderId === updatedOrder.orderId
            ? { ...o, ...updatedOrder }   
            : o
        )
      );
      setSelectedOrder(prev =>
        prev && prev.orderId === updatedOrder.orderId
          ? { ...prev, ...updatedOrder }
          : prev
      );
    } else {
      alert(`Cancellation failed`);
    }

  } catch (err) {
    console.error(err);
    alert("Unable to cancel order right now");
  }
};

  const getStatusColor = (status) => {
    switch (status) {
      case 'CONFIRMED': return '#28a745'
      case 'PENDING': return '#ffc107'
      case 'CANCELLED': return '#dc3545'
      default: return '#6c757d'
    }
  }

  return (
    <div className="page">
      <div className="page-header">
        <h2>My Orders</h2>
        <p>Track and manage your orders</p>
      </div>

      <div className="orders-list">
        {orders.map(order => (
          <div key={order.orderId} className="order-card">
            <div className="order-header">
              <div className="order-info">
                <h3>Order Number:#{order.orderNumber}</h3>
                <p>Ordered on {new Date(order.createdDate).toLocaleString()}</p>
              </div>
              <div 
                className="order-status"
                style={{ backgroundColor: getStatusColor(order.status) }}
              >
                {order.status}
              </div>
            </div>

            <div className="order-total">
              <strong>Total:</strong> ${order.totalAmount.toFixed(2)}
            </div>

            <div className="order-actions">
              <button className="btn btn-details" onClick={() => setSelectedOrder(order)}>
                View Details
              </button>

              {order.status !== 'PENDING' && order.status !== 'CANCELLED' && (
                <button className="btn btn-cancel" onClick={() => cancelOrder(order.orderId)}>
                  Cancel Order
                </button>
              )}
            </div>
          </div>
        ))}
      </div>

      {orders.length === 0 && (
        <div className="empty-state">
          <div className="empty-icon">📦</div>
          <h3>No Orders Yet</h3>
          <p>Your orders will appear here once you make a purchase.</p>
          <button className="btn" onClick={() => navigate("/products")}>Start Shopping</button>
        </div>
      )}

      {/* DETAILS MODAL */}
{selectedOrder && (
  <div className="modal-overlay">
    <div className="modal">
      <div className="modal-header">
        <h3>Order Details</h3>
        <button className="close-btn" onClick={() => setSelectedOrder(null)}>✖</button>
      </div>

      <div className="modal-body">
        <div className="order-info">
          <p><strong>Payment Status:</strong> {selectedOrder.paymentStatus}</p>
          <p><strong>Payment Method:</strong> {selectedOrder.paymentMethod}</p>
          <p><strong>Shipping Address:</strong> {selectedOrder.shippingAddress}</p>
        </div>

        <hr />

        <div className="modal-items">
          {selectedOrder.items.map(item => (
            <div key={item.productId} className="modal-item">
              <span>{item.productName} x {item.quantity}</span>
              <span>${item.totalPrice.toFixed(2)}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="modal-footer">
        <strong>Total:</strong> ${selectedOrder.totalAmount.toFixed(2)}
      </div>
    </div>
  </div>
)}


      <style jsx>{`
        .modal-overlay {
          position: fixed;
          top: 0; left: 0; right: 0; bottom: 0;
          background: rgba(0,0,0,0.5);
          display: flex;
          justify-content: center;
          align-items: center;
          z-index: 1000;
        }
        .modal {
          background: white;
          padding: 1.5rem;
          border-radius: 8px;
          width: 400px;
          max-width: 90%;
        }
        .modal-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
        }
        .close-btn {
          background: none;
          border: none;
          font-size: 1.2rem;
          cursor: pointer;
        }
        .modal-body {
          margin-top: 1rem;
          max-height: 300px;
          overflow-y: auto;
        }
        .modal-item {
          display: flex;
          justify-content: space-between;
          margin-bottom: 0.5rem;
        }
        .modal-footer {
          margin-top: 1rem;
          text-align: right;
        }
      `}</style>
    </div>
  )
}

export default Orders

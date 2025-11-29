import { useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import apiClient from "../api/axios";
import { apiUrl } from "../api/api";
import { Service } from "../api/Service";


export default function PaymentCancel() {
 const [params] = useSearchParams();
  console.log(params);
  const paymentId = params.get("token");
  const navigate = useNavigate();


  useEffect(() => {
  const cancelOrder = async () => {
    if (!paymentId) return;

    try {
      const response = await apiClient.get(apiUrl(Service.ORDERS, "/payments/cancel"), {
        params: { token: paymentId },
      });
      if(response.status == 200) {
      alert("Payment Cancelled");
      navigate("/orders");
      } else {
        alert("Payment cancellation failed");
        navigate("/orders");
      }
    } catch (err) {
      alert("Payment cancellation failed");
      navigate("/orders");
    }
  };

  cancelOrder();
}, [paymentId, navigate]);

  return <p>Cancelling order, please wait...</p>;
}

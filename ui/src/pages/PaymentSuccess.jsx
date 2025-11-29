import { useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import apiClient from "../api/axios";
import { apiUrl } from "../api/api";
import { Service } from "../api/Service";
//import { useNavigate } from "react-router-dom";


export default function PaymentSuccess() {
  const [params] = useSearchParams();
  console.log(params);
  const paymentId = params.get("token");
  const navigate = useNavigate();

  useEffect(() => {
    const confirmPayment = async () => {
      if (!paymentId) return;

    try {
      const response = await apiClient.get(apiUrl(Service.ORDERS, "/payments/success"), {
        params: { token: paymentId },
      });
      if(response.status == 200) {
      alert("Payment Success");
      navigate("/orders");
      } else {
        alert("Payment Failed");
        navigate("/orders");
      }
    } catch (err) {
      alert("Payment Failed");
      navigate("/orders");
    }
  };
    confirmPayment(paymentId, navigate);
  }, []); 



  return <p>Validating payment, please wait...</p>;
}

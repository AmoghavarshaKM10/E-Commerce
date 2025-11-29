import apiClient from "./axios";
import { apiUrl } from "./api";
import { Service } from "./Service";

export const loginApi = async (email, password) => {
  const { data } = await apiClient.post(
    apiUrl(Service.USERS, "/login"),
    { email, password }
  );
  localStorage.setItem("token", data.token);
  return data;
};

export const signupApi = async (name, email, password) => {
  const { data } = await apiClient.post(
    apiUrl(Service.USERS, "/signUp"),
    { name, email, password },
    {
    headers: {
      "Content-Type": "application/json"
    }
  }
  );
  localStorage.setItem("token", data.token);
  return data;
};

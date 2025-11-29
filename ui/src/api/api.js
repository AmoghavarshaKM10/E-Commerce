import { Service } from "./Service";

export const BASE_URL = {
  [Service.USERS]: import.meta.env.VITE_USER_SERVICE_URL,
  [Service.PRODUCTS]: import.meta.env.VITE_PRODUCTS_URL,
  [Service.CART]: import.meta.env.VITE_CART_URL,
  [Service.ORDERS]: import.meta.env.VITE_ORDERS_URL,
};

/**
 * Builds full path: <service base> + <path>
 */
export const apiUrl = (service, path) => `${BASE_URL[service]}${path}`;

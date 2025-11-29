import axios from "axios";

const apiClient = axios.create({
  withCredentials: true,
});

// REQUEST
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (err) => Promise.reject(err)
);

let isRefreshing = false;
let queue = [];

const processQueue = (error, token = null) => {
  queue.forEach((p) => (error ? p.reject(error) : p.resolve(token)));
  queue = [];
};

// RESPONSE (refresh)
apiClient.interceptors.response.use(
  (res) => res,
  async (err) => {
    const original = err.config;

    if (err.response?.status === 401 && !original._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          queue.push({ resolve, reject });
        }).then((token) => {
          original.headers.Authorization = `Bearer ${token}`;
          return apiClient(original);
        });
      }

      original._retry = true;
      isRefreshing = true;

      try {
        const refreshRes = await apiClient.post(
          `${import.meta.env.VITE_USER_SERVICE_URL}/refresh-token`
        );

        localStorage.setItem("token", refreshRes.data.token);
        apiClient.defaults.headers.Authorization = `Bearer ${refreshRes.data.token}`;
        processQueue(null, refreshRes.data.token);

        return apiClient(original);
      } catch (error) {
        processQueue(error, null);
        localStorage.removeItem("token");
        window.location.href = "/";
        return Promise.reject(error);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(err);
  }
);

export default apiClient;

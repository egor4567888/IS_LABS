import axios from "axios";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

// WebSocket (для автообновлений)
export const stompClient = new Client({
  webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
  reconnectDelay: 5000,
});

import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

// Типы для сообщений WebSocket
export interface WebSocketMessage {
  action: 'create' | 'update' | 'delete';
  id: number;
  type?: 'marine' | 'chapter'; // Добавляем тип для различения сущностей
}

// Типы для подписчиков
type MessageCallback = (message: WebSocketMessage) => void;
type ConnectionCallback = (connected: boolean) => void;

class WebSocketService {
  private client: Client | null = null;
  private messageSubscribers: Map<string, MessageCallback[]> = new Map();
  private connectionSubscribers: ConnectionCallback[] = [];
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 10;
  private _isConnected = false;
  private connectionCheckInterval: NodeJS.Timeout | null = null;

  constructor() {
    this.initializeClient();
  }

  private initializeClient() {
    this.client = new Client({
      // Используем SockJS для совместимости с Spring WebSocket
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      
      // Настройки отладки
      debug: (str) => {
        if (process.env.NODE_ENV === 'development') {
          console.log('STOMP Debug:', str);
        }
      },
      
      // Настройки переподключения
      reconnectDelay: 3000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      
      // Обработчик успешного подключения
      onConnect: this.handleConnect.bind(this),
      
      // Обработчик ошибок STOMP
      onStompError: this.handleStompError.bind(this),
      
      // Обработчик отключения
      onDisconnect: this.handleDisconnect.bind(this),
      
      // Обработчик ошибок WebSocket
      onWebSocketError: this.handleWebSocketError.bind(this),
    });
  }

  // Подключение к WebSocket
  public connect(): void {
    if (!this.client) {
      this.initializeClient();
    }

    if (this.client && !this.client.active) {
      try {
        this.client.activate();
        console.log('WebSocket: Attempting to connect...');
        
        // Запускаем проверку соединения
        this.startConnectionCheck();
      } catch (error) {
        console.error('WebSocket: Connection error:', error);
        this.handleConnectionError(error);
      }
    }
  }

  // Отключение от WebSocket
  public disconnect(): void {
    this.stopConnectionCheck();
    
    if (this.client && this.client.active) {
      this.client.deactivate();
      console.log('WebSocket: Disconnected');
    }
    this._isConnected = false;
    this.notifyConnectionSubscribers(false);
  }

  // Подписка на сообщения определенного топика
  public subscribe(topic: string, callback: MessageCallback): void {
    if (!this.messageSubscribers.has(topic)) {
      this.messageSubscribers.set(topic, []);
    }
    
    const subscribers = this.messageSubscribers.get(topic)!;
    if (!subscribers.includes(callback)) {
      subscribers.push(callback);
    }

    console.log(`WebSocket: Subscribed to ${topic}, total subscribers: ${subscribers.length}`);
  }

  // Отписка от сообщений топика
  public unsubscribe(topic: string, callback?: MessageCallback): void {
    const subscribers = this.messageSubscribers.get(topic);
    if (!subscribers) return;

    if (callback) {
      const index = subscribers.indexOf(callback);
      if (index > -1) {
        subscribers.splice(index, 1);
      }
    } else {
      // Если callback не указан, отписываем всех
      this.messageSubscribers.set(topic, []);
    }

    console.log(`WebSocket: Unsubscribed from ${topic}, remaining subscribers: ${subscribers.length}`);
  }

  // Подписка на изменения состояния подключения
  public onConnectionChange(callback: ConnectionCallback): void {
    this.connectionSubscribers.push(callback);
    // Немедленно вызываем callback с текущим статусом
    callback(this._isConnected);
  }

  // Отписка от изменений состояния подключения
  public offConnectionChange(callback: ConnectionCallback): void {
    const index = this.connectionSubscribers.indexOf(callback);
    if (index > -1) {
      this.connectionSubscribers.splice(index, 1);
    }
  }

  // Отправка сообщения
  public send(destination: string, body: any): void {
    if (this._isConnected && this.client) {
      this.client.publish({
        destination,
        body: JSON.stringify(body)
      });
    } else {
      console.warn('WebSocket: Cannot send message - not connected');
    }
  }

  // Проверка подключения
  public getConnectionStatus(): boolean {
    return this._isConnected;
  }

  // Для совместимости с вашим кодом
  public getIsConnected(): boolean {
    return this._isConnected;
  }

  // Приватные методы

  private startConnectionCheck() {
    this.stopConnectionCheck();
    this.connectionCheckInterval = setInterval(() => {
      const wasConnected = this._isConnected;
      this._isConnected = this.client?.connected || false;
      
      // Уведомляем только при изменении статуса
      if (wasConnected !== this._isConnected) {
        this.notifyConnectionSubscribers(this._isConnected);
      }
    }, 1000);
  }

  private stopConnectionCheck() {
    if (this.connectionCheckInterval) {
      clearInterval(this.connectionCheckInterval);
      this.connectionCheckInterval = null;
    }
  }

  private handleConnect() {
    console.log('WebSocket: Successfully connected to server');
    this._isConnected = true;
    this.reconnectAttempts = 0;
    this.notifyConnectionSubscribers(true);
    
    // Подписываемся на все зарегистрированные топики
    this.messageSubscribers.forEach((_, topic) => {
      this.subscribeToTopic(topic);
    });
  }

  private handleStompError(frame: any) {
    console.error('WebSocket: STOMP error:', frame);
    this._isConnected = false;
    this.notifyConnectionSubscribers(false);
  }

  private handleDisconnect() {
    console.log('WebSocket: Disconnected from server');
    this._isConnected = false;
    this.notifyConnectionSubscribers(false);
    
    // Автоматическое переподключение
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`WebSocket: Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
      setTimeout(() => this.connect(), 3000);
    } else {
      console.error('WebSocket: Max reconnection attempts reached');
    }
  }

  private handleWebSocketError(error: any) {
    console.error('WebSocket: WebSocket connection error:', error);
    this._isConnected = false;
    this.notifyConnectionSubscribers(false);
  }

  private handleConnectionError(error: any) {
    console.error('WebSocket: Connection setup error:', error);
    this._isConnected = false;
    this.notifyConnectionSubscribers(false);
  }

  private subscribeToTopic(topic: string) {
    if (!this.client) return;

    console.log(`WebSocket: Subscribing to topic: ${topic}`);
    
    this.client.subscribe(topic, (message: IMessage) => {
      try {
        const parsedMessage: WebSocketMessage = JSON.parse(message.body);
        console.log('WebSocket: Received message:', parsedMessage);
        this.notifyMessageSubscribers(topic, parsedMessage);
      } catch (error) {
        console.error('WebSocket: Error parsing message:', error, 'Body:', message.body);
      }
    });
  }

  private notifyMessageSubscribers(topic: string, message: WebSocketMessage) {
    const subscribers = this.messageSubscribers.get(topic);
    if (subscribers && subscribers.length > 0) {
      subscribers.forEach(callback => {
        try {
          callback(message);
        } catch (error) {
          console.error('WebSocket: Error in subscriber callback:', error);
        }
      });
    }
  }

  private notifyConnectionSubscribers(connected: boolean) {
    this.connectionSubscribers.forEach(callback => {
      try {
        callback(connected);
      } catch (error) {
        console.error('WebSocket: Error in connection callback:', error);
      }
    });
  }

  // Очистка ресурсов
  public destroy() {
    this.stopConnectionCheck();
    this.disconnect();
    this.messageSubscribers.clear();
    this.connectionSubscribers = [];
    this.client = null;
  }
}

// Создаем и экспортируем singleton экземпляр
export const webSocketService = new WebSocketService();
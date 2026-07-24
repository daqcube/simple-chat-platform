import {inject, Injectable, signal} from '@angular/core';
import {Client, StompSubscription} from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {ChatRoomState} from '../../features/chat/state/chat-room.state';
import {JoinResponse} from '../models/join-response.model';
import {JoinRequest} from '../models/join-request.model';
import {ChatMessageResponse} from '../models/chat-message-response.model';
import {ChatMessageRequest} from '../models/chat-message-request.model';
import {ChatRoom} from '../models/chat-room.model';

@Injectable({providedIn: 'root'})
export class ChatService {
  private client?: Client;
  subscriptions = new Map<string, StompSubscription>();
  readonly connected = signal(false);
  readonly chatState = inject(ChatRoomState)

  constructor(private readonly http: HttpClient) {
  }

  join(request: JoinRequest): Observable<JoinResponse> {
    return this.http.post<JoinResponse>(`${environment.api.chat}/join`, request);
  }

  getChatRooms(): Observable<ChatRoom[]> {
    return this.http.get<ChatRoom[]>(
      `${environment.api.chat}/rooms`
    );
  }

  connect(): Promise<void> {
    if (this.client?.active) {
      return Promise.resolve();
    }
    return new Promise((resolve) => {
      this.client = new Client({
        webSocketFactory: () =>
          new SockJS(environment.websocket.endpoint),
        debug: message => console.log(message),
        reconnectDelay: 3000,
        onConnect: () => {
          this.connected.set(true);
          resolve();
        },
        onDisconnect: () => {
          this.connected.set(false);
        },
        onWebSocketClose: () => {
          this.connected.set(false);
        }
      });
      this.client.activate();
    });
  }

  subscribeToRoom(roomId: string) {
    this.subscribeToUsers(roomId)
    this.subscribeToMessages(roomId)
  }

  private subscribeToMessages(roomId: string) {
    if (!this.client?.connected) {
      throw new Error('STOMP connection not established');
    }
    const messageKey = this.messageKey(roomId);
    this.unsubscribe(messageKey)

    const subscription = this.client?.subscribe(
      `${environment.websocket.subscription.rooms}/${roomId}/messages`,
      message => {
        const response: ChatMessageResponse = JSON.parse(message.body);
        console.log('Message received:', response);
        this.chatState.addMessage(roomId, response);
      }
    );
    this.subscriptions.set(messageKey, subscription);
  }

  private subscribeToUsers(roomId: string) {
    if (!this.client?.connected) {
      throw new Error('STOMP connection not established');
    }
    const key = this.usersKey(roomId);
    this.unsubscribe(this.usersKey(roomId))
    const subscription = this.client?.subscribe(
      `${environment.websocket.subscription.rooms}/${roomId}/users`,
      message => {
        const users: string[] = JSON.parse(message.body);
        this.chatState.setUsers(roomId, users);
        this.subscriptions.set(key, subscription);

      }
    );

  }

  unsubscribeFromRoom(roomId: string) {
    this.unsubscribe(this.usersKey(roomId));
    this.unsubscribe(this.messageKey(roomId));
  }

  sendMessage(request: ChatMessageRequest): void {
    if (!this.client?.active) {
      throw new Error(
        'Cannot send a message before the socket is connected'
      );
    }

    let body = JSON.stringify(request);
    console.log('Sending message::' + body)
    this.client.publish({
      destination: environment.websocket.destination.sendMessage,
      body: body
    });
  }


  leaveRoom(roomId: string, username: string): void {
    this.client?.publish({
      destination:
      environment.websocket.destination.leave,

      body: JSON.stringify({
        roomId,
        username
      })
    });
  }

  unsubscribe(key: string): void {
    const subscription = this.subscriptions.get(key);

    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(key);
    }
  }

  disconnect(): void {
    this.client?.deactivate();

    this.connected.set(false);
  }


  private messageKey(roomId: string) {
    return `messages:${roomId}`;
  }

  private usersKey(roomId: string) {
    return `users:${roomId}`;
  }
}

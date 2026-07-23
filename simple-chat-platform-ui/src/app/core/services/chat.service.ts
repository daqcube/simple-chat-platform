import {inject, Injectable, signal} from '@angular/core';
import {Client, StompSubscription} from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {ChatMessageRequest, ChatMessageResponse, ChatRoom} from '../models/chat.model';
import {environment} from '../../../environments/environment';
import {Observable, of} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {ChatState} from '../../features/chat/state/chat.state';
import {JoinResponse} from '../models/join-response.model';
import {JoinRequest} from '../models/join-request.model';

@Injectable({providedIn: 'root'})
export class ChatService {
  private client?: Client;
  subscriptions = new Map<string, StompSubscription>();
  readonly connected = signal(false);
  readonly chatState = inject(ChatState)

  constructor(private readonly http: HttpClient) {
  }

  join(request: JoinRequest): Observable<JoinResponse> {
    return this.http.post<JoinResponse>(`${environment.api.chat}/join`, request);
  }

  connect(): Promise<void> {
    if (this.client?.active) {
      return Promise.resolve();
    }
    return new Promise((resolve, reject) => {
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
    const key = `messages:${roomId}`;
    this.unsubscribe(key)

    const subscription = this.client?.subscribe(
      `${environment.websocket.subscription.rooms}/${roomId}/messages`,
      message => {
        console.log(
          'Message received:',

          message.body
        );
        const response: ChatMessageResponse = JSON.parse(message.body);
        this.chatState.addMessage(roomId, response);
      }
    );
    this.subscriptions.set(key, subscription);

  }

  private subscribeToUsers(roomId: string) {
    if (!this.client?.connected) {
      throw new Error('STOMP connection not established');
    }
    const key = `users:${roomId}`;
    this.unsubscribe(key)
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
    this.unsubscribe(`users:${roomId}`);
    this.unsubscribe(`messages:${roomId}`);
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


  leave(roomId: string, username: string): void {

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

  getRooms(): Observable<ChatRoom[]> {
    return of([
        {
          id: 'general',
          name: 'General',
          onlineUsers: 0
        },
        {
          id: 'java',
          name: 'Java Developers',
          onlineUsers: 0
        },
        {
          id: 'spring',
          name: 'Spring Boot',
          onlineUsers: 0
        }
      ]
    )
      ;
  }
}

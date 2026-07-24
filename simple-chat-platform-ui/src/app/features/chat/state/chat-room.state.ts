import {computed, Injectable, signal, WritableSignal} from '@angular/core';
import {ChatRoom} from '../../../core/models/chat-room.model';
import {ChatMessageResponse} from '../../../core/models/chat-message-response.model';

@Injectable({
  providedIn: 'root'
})
export class ChatRoomState {
  private readonly _username = signal('');
  private readonly _chatRooms = signal<ChatRoom[]>([]);
  private readonly _activeChatRoom = signal<ChatRoom | null>(null);

  private readonly _chatMessages =
    signal<Record<string, ChatMessageResponse[]>>({});

  private readonly _chatRoomUsers =
    signal<Record<string, string[]>>({});

  readonly username = computed(() => this._username());
  readonly chatRooms = computed(() => this._chatRooms());
  readonly activeRoom = computed(() => this._activeChatRoom());

  setUsername(username: string): void {
    this._username.set(username);
  }

  setRooms(rooms: ChatRoom[]): void {
    this._chatRooms.set(rooms);
  }

  selectRoomById(roomId: string): void {
    this.selectRoom(this.getChatRoomOrDefault(roomId));
  }

  selectRoom(room: ChatRoom): void {
    this._activeChatRoom.set(room);

    this.ensureRoomExists(this._chatMessages, room.id);
    this.ensureRoomExists(this._chatRoomUsers, room.id);
  }

  addMessage(roomId: string, message: ChatMessageResponse): void {
    this._chatMessages.update(current => ({
      ...current,
      [roomId]: [
        ...(current[roomId] ?? []),
        message
      ]
    }));
  }

  getMessages(roomId: string): ChatMessageResponse[] {
    return this._chatMessages()[roomId] ?? [];
  }

  setUsers(roomId: string, users: string[]): void {
    this.updateMap(this._chatRoomUsers, roomId, users);
  }

  getRoomUsers(roomId: string): string[] {
    return this._chatRoomUsers()[roomId] ?? [];
  }

  clear(): void {
    this._username.set('');
    this._chatRooms.set([]);
    this._activeChatRoom.set(null);
    this._chatMessages.set({});
    this._chatRoomUsers.set({});
  }

  private getChatRoomOrDefault(roomId: string): ChatRoom {
    return this._chatRooms().find(room => room.id === roomId) ?? {
      id: roomId,
      name: roomId
    };
  }

  private ensureRoomExists<T>(state: WritableSignal<Record<string, T[]>>,
                              roomId: string
  ): void {
    state.update(current => {
      if (current[roomId]) {
        return current;
      }

      return {
        ...current,
        [roomId]: []
      };
    });
  }

  private updateMap<T>(
    state: WritableSignal<Record<string, T>>,
    key: string,
    value: T
  ): void {
    state.update(current => ({
      ...current,
      [key]: value
    }));
  }

}

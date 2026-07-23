import {computed, Injectable, signal} from '@angular/core';
import {ChatRoom} from '../../../core/models/chat-room.model';
import {ChatMessageResponse} from '../../../core/models/chat-message-response.model';


@Injectable({
  providedIn: 'root'
})
export class ChatState {
  private readonly _username = signal<string>('');
  private readonly _rooms = signal<ChatRoom[]>([]);
  private readonly _activeRoom = signal<ChatRoom | null>(null);

  private readonly _messages =
    signal<Record<string, ChatMessageResponse[]>>({});

  private readonly _users =
    signal<Record<string, string[]>>({});

  readonly username = computed(() =>
    this._username()
  );

  readonly rooms = computed(() =>
    this._rooms()
  );

  readonly activeRoom = computed(() =>
    this._activeRoom()
  );

  readonly messages = computed(() =>
    this._messages()
  );

  readonly users = computed(() =>
    this._users()
  );

  setUsername(username: string) {
    this._username.set(username);
  }

  clearUsername() {
    this._username.set('');
  }

  setRooms(rooms: ChatRoom[]) {
    this._rooms.set(rooms);
  }

  addRoom(room: ChatRoom) {
    this._rooms.update(current => [
      ...current,
      room

    ]);

  }

  selectRoomById(roomId: string) {

    const existing = this._rooms()
      .find(room => room.id === roomId);

    if (existing) {
      this._activeRoom.set(existing);

    } else {
      this._activeRoom.set({
        id: roomId,
        name: roomId,
      });

    }


    if (!this._messages()[roomId]) {

      this._messages.update(current => ({

        ...current,

        [roomId]: []

      }));

    }


    if (!this._users()[roomId]) {

      this._users.update(current => ({

        ...current,

        [roomId]: []

      }));

    }

  }

  selectRoom(room: ChatRoom) {
    this._activeRoom.set(room);
    if (!this._messages()[room.id]) {

      this._messages.update(current => ({
        ...current,
        [room.id]: []
      }));
    }

    if (!this._users()[room.id]) {
      this._users.update(current => ({

        ...current,

        [room.id]: []

      }));
    }

  }

  clearRoom() {
    this._activeRoom.set(null);
  }

  addMessage(
    roomId: string,
    message: ChatMessageResponse
  ) {

    this._messages.update(current => ({
      ...current,
      [roomId]: [
        ...(current[roomId] ?? []),
        message
      ]
    }));

  }


  setMessages(
    roomId: string,
    messages: ChatMessageResponse[]
  ) {

    this._messages.update(current => ({

      ...current,

      [roomId]: messages

    }));

  }


  getMessages(roomId: string) {
    return this._messages()[roomId] ?? [];
  }


  clearMessages(roomId: string) {
    this._messages.update(current => {

      const copy = {...current};

      delete copy[roomId];

      return copy;

    });

  }

  setUsers(
    roomId: string,
    users: string[]
  ) {

    this._users.update(current => ({

      ...current,

      [roomId]: users

    }));

  }

  addUser(
    roomId: string,
    username: string
  ) {

    this._users.update(current => ({

      ...current,

      [roomId]: [

        ...(current[roomId] ?? []),

        username

      ]

    }));

  }

  removeUser(
    roomId: string,
    username: string
  ) {

    this._users.update(current => ({
      ...current,

      [roomId]:
        (current[roomId] ?? [])
          .filter(user => user !== username)

    }));

  }

  getRoomUsers(roomId: string): string[] {
    return this._users()[roomId] ?? [];
  }

  clear() {
    this._username.set('');
    this._rooms.set([]);
    this._activeRoom.set(null);
    this._messages.set({});
    this._users.set({});
  }

}

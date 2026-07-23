import {Component, computed, inject} from '@angular/core';
import {Router} from '@angular/router';
import {ChatRoom} from '../../../../core/models/chat-room.model';
import {ChatState} from '../../state/chat.state';
import {ChatService} from '../../../../core/services/chat.service';

@Component({
  selector: 'app-room-list',
  standalone: true,
  templateUrl: './room-list.html',
  styleUrl: './room-list.css'
})
export class RoomList {
  private readonly router = inject(Router);
  private readonly chatService = inject(ChatService);
  private readonly chatState = inject(ChatState);

  readonly rooms = this.chatState.rooms;
  readonly activeRoom = this.chatState.activeRoom;
  readonly username = this.chatState.username

  readonly users = computed(() =>
    this.chatState.getRoomUsers(<string>this.activeRoom()?.id)
  );

  select(room: ChatRoom): void {
    if (this.activeRoom()?.id === room.id) {
      return;
    }

    const previous = this.activeRoom();
    if (previous) {
      this.chatService.leaveRoom(previous.id, this.chatState.username());
      this.chatService.unsubscribeFromRoom(previous.id);
    }

    this.chatService.subscribeToRoom(room.id);

    this.chatState.selectRoom(room);

    this.chatService.join({
      roomId: room.id,
      username: this.chatState.username()
    }).subscribe({
      next: () => {
        this.router.navigate(['/chat/rooms', room.id]).then(r => r);
      },
      error: err => {
        console.error('Failed to join room', err);
      }

    });
  }

  getUsers(room: ChatRoom) {
    return this.chatState.getRoomUsers(room.id)
  }
}

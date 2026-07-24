import {Component, computed, inject} from '@angular/core';
import {Router} from '@angular/router';
import {ChatRoom} from '../../../../core/models/chat-room.model';
import {ChatRoomState} from '../../state/chat-room.state';
import {ChatService} from '../../../../core/services/chat.service';
import {JoinRequest} from '../../../../core/models/join-request.model';

@Component({
  selector: 'app-room-list',
  standalone: true,
  templateUrl: './room-list.html',
  styleUrl: './room-list.css'
})
export class RoomList {
  private readonly router = inject(Router);
  private readonly chatService = inject(ChatService);
  private readonly chatRoomState = inject(ChatRoomState);

  readonly rooms = this.chatRoomState.chatRooms;
  readonly activeRoom = this.chatRoomState.activeRoom;
  readonly username = this.chatRoomState.username

  readonly users = computed(() =>
    this.chatRoomState.getRoomUsers(<string>this.activeRoom()?.id)
  );

  selectRoom(room: ChatRoom): void {
    if (this.activeRoom()?.id === room.id) {
      return;
    }

    const previous = this.activeRoom();
    if (previous) {
      this.chatService.leaveRoom(previous.id, this.chatRoomState.username());
      this.chatService.unsubscribeFromRoom(previous.id);
    }

    this.chatService.subscribeToRoom(room.id);
    this.chatRoomState.selectRoom(room);

    const request: JoinRequest = {
      roomId: room.id,
      username: this.chatRoomState.username()
    }

    this.chatService.join(request).subscribe({
      next: () => {
        this.router.navigate(['/chat/rooms', room.id]).then(r => r);
      },
      error: err => {
        console.error('Failed to join room', err);
      }
    });
  }

  getUsers(room: ChatRoom) {
    return this.chatRoomState.getRoomUsers(room.id)
  }
}

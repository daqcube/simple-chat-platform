import {Component, computed, inject, OnInit} from '@angular/core';
import {ChatService} from '../../../../core/services/chat.service';
import {RoomList} from '../../components/room-list/room-list';
import {Users} from '../../components/users/users';
import {ChatState} from '../../state/chat.state';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-chat-layout',
  imports: [
    RoomList,
    Users,
    RouterOutlet
  ],
  templateUrl: './chat-layout.html',
  styleUrl: './chat-layout.css',
})
export class ChatLayout implements OnInit {

  readonly chatState = inject(ChatState);
  readonly chatService = inject(ChatService);

  readonly username = this.chatState.username;
  readonly activeRoom = this.chatState.activeRoom;

  readonly users = computed(() => {

    const roomId = this.activeRoom()?.id;

    return roomId

      ? this.chatState.getRoomUsers(roomId)

      : [];

  });


  ngOnInit(): void {
    this.loadRooms();
  }

  private loadRooms() {
    this.chatService.getRooms()
      .subscribe(rooms => {
        this.chatState.setRooms(rooms);
      });

  }
}

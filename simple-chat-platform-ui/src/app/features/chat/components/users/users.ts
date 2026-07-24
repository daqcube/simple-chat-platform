import {Component, inject, input} from '@angular/core';
import {ChatRoomState} from '../../state/chat-room.state';

@Component({
  selector: 'app-room-users',
  imports: [],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users {
  readonly roomId = input.required<string>();
  private readonly chatState = inject(ChatRoomState);

  readonly users = () => this.chatState.getRoomUsers(this.roomId());
}

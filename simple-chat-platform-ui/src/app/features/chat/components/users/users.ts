import {Component, inject, input} from '@angular/core';
import {ChatState} from '../../state/chat.state';

@Component({
  selector: 'app-room-users',
  imports: [],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users {
  readonly roomId = input.required<string>();
  private readonly chatState = inject(ChatState);

  readonly users = () => this.chatState.getRoomUsers(this.roomId());
}

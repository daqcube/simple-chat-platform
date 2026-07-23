import {Component, computed, input} from '@angular/core';
import {ChatMessageResponse, MessageType} from '../../../../core/models/chat.model';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-message-item',
  imports: [
    DatePipe
  ],
  templateUrl: './message-item.html',
  styleUrl: './message-item.css',
})
export class MessageItem {
  readonly message = input.required<ChatMessageResponse>();

  readonly showAvatar = computed(() => {
    const type = this.message().type;
    return type === MessageType.CHAT
  });
  readonly initials = computed(() => this.message().username
    .charAt(0)
    .toUpperCase()
  );

}

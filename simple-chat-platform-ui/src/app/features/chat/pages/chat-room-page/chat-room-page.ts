import {Component, computed, inject, input, OnDestroy, OnInit} from '@angular/core';
import {MessageComposer} from '../../components/message-composer/message-composer';
import {MessageList} from '../../components/message-list/message-list';
import {ChatState} from '../../state/chat.state';
import {ChatService} from '../../../../core/services/chat.service';
import {ChatMessageRequest} from '../../../../core/models/chat.model';

@Component({
  selector: 'app-chat-room',
  imports: [
    MessageList,
    MessageComposer
  ],
  templateUrl: './chat-room-page.html',
  styleUrl: './chat-room-page.css'
})
export class ChatRoomPage implements OnInit, OnDestroy {
  private readonly chatState = inject(ChatState);
  private readonly chatService = inject(ChatService);

  readonly roomId = input.required<string>();

  readonly room = computed(() =>
    this.chatState.activeRoom()
  );

  readonly users = computed(() =>
    this.chatState.getRoomUsers(this.roomId())
  );
  readonly username = computed(() => this.chatState.username() || '');

  readonly messages = computed(() =>
    this.chatState.getMessages(this.roomId())
  );

  ngOnInit(): void {
    const roomId = this.roomId();
    this.chatService.subscribeToRoom(roomId);
  }

  ngOnDestroy(): void {
    this.chatService.unsubscribeFromRoom(this.roomId());
  }

  onSend(message: ChatMessageRequest): void {
    this.chatService.sendMessage(message);
  }

}

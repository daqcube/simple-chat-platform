import {Component, computed, effect, inject, input, OnDestroy} from '@angular/core';
import {MessageComposer} from '../../components/message-composer/message-composer';
import {MessageList} from '../../components/message-list/message-list';
import {ChatRoomState} from '../../state/chat-room.state';
import {ChatService} from '../../../../core/services/chat.service';
import {ChatMessageRequest} from '../../../../core/models/chat-message-request.model';

@Component({
  selector: 'app-chat-room',
  standalone: true,
  imports: [
    MessageList,
    MessageComposer
  ],
  templateUrl: './chat-room-page.html',
  styleUrl: './chat-room-page.css'
})
export class ChatRoomPage implements OnDestroy {
  private readonly chatState = inject(ChatRoomState);
  private readonly chatService = inject(ChatService);

  readonly roomId = input.required<string>();

  readonly activeRoom = computed(() =>
    this.chatState.activeRoom()
  );

  readonly roomUsers = computed(() =>
    this.chatState.getRoomUsers(this.roomId())
  );
  readonly username = computed(() => this.chatState.username() || '');

  readonly chatMessages = computed(() =>
    this.chatState.getMessages(this.roomId())
  );

  constructor() {
    effect(() => {
      this.chatService.subscribeToRoom(
        this.roomId()
      );
    });

  }

  ngOnDestroy(): void {
    this.chatService.unsubscribeFromRoom(this.roomId());
  }

  onSend(message: ChatMessageRequest): void {
    this.chatService.sendMessage(message);
  }

}

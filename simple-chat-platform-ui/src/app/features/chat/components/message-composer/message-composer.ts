import {Component, EventEmitter, input, Output, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ChatMessageRequest, ChatRoom} from '../../../../core/models/chat.model';

@Component({
  selector: 'app-message-composer',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './message-composer.html',
  styleUrl: './message-composer.css'
})
export class MessageComposer {
  readonly username = input.required<string | null>();
  readonly room = input.required<ChatRoom>();

  @Output()
  send = new EventEmitter<ChatMessageRequest>();

  message = signal('');

  submit(): void {
    const content = this.message().trim();
    const usernameValue = this.username()
    if (!content || !usernameValue) {
      return;
    }

    this.send.emit({
      username: usernameValue,
      roomId: this.room().id,
      content: this.message()
    });
    this.message.set('');
  }

  updateMessage(value: string): void {
    this.message.set(value);
  }

}

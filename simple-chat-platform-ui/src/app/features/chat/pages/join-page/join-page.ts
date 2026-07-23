import {Component, inject, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {ChatService} from '../../../../core/services/chat.service';
import {ChatState} from '../../state/chat.state';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-join',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './join-page.html',
  styleUrl: './join-page.css'
})
export class JoinPage {
  private readonly router = inject(Router);
  private readonly chatService = inject(ChatService)
  private readonly chatState = inject(ChatState)

  readonly submitting = signal(false);
  username = signal('');
  error = signal<string | null>(null);

  join(): void {
    const name = this.username().trim();
    if (!name) {
      this.error.set(
        'Username is required'
      );

      return;
    }

    this.chatService.connect()
      .then(() => {
        this.chatService.subscribeToRoom('general')
        return firstValueFrom(this.chatService.join({username: name}));
      })
      .then((response) => {
        this.chatState.setUsername(name);
        this.chatState.selectRoomById(response.roomId);
        this.submitting.set(false);
        this.router.navigate([`/chat/rooms/${response.roomId}`]).then(r => r);
      })
      .catch(err => {
        this.submitting.set(false);
        this.error.set(err.message);
        console.error('Chat connection failed', err);
      });


  }


  updateUsername(value: string): void {

    this.username.set(value);

    this.error.set(null);

  }

}

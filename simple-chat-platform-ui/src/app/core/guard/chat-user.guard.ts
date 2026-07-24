import {inject} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';
import {ChatRoomState} from '../../features/chat/state/chat-room.state';

export const chatUserGuard: CanActivateFn = () => {
  const chatState = inject(ChatRoomState);
  const router = inject(Router);
  const username = chatState.username();

  if (username) {
    return true;
  }

  return router.createUrlTree(['/join']);

};

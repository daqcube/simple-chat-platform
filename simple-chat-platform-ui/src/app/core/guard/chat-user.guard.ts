import {inject} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';
import {ChatState} from '../../features/chat/state/chat.state';

export const chatUserGuard: CanActivateFn = () => {
  const chatState = inject(ChatState);
  const router = inject(Router);
  const username = chatState.username();

  if (username) {
    return true;
  }

  return router.createUrlTree(['/join']);

};

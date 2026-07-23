import {Routes} from '@angular/router';
import {chatUserGuard} from './core/guard/chat-user.guard';

export const routes: Routes = [
  {
    path: 'join',
    loadComponent: () =>
      import('./features/chat/pages/join-page/join-page')
        .then(m => m.JoinPage)
  },
  {
    path: 'chat',
    canActivate: [
      chatUserGuard
    ],
    loadComponent: () =>
      import('./features/chat/layouts/chat-layout/chat-layout')
        .then(m => m.ChatLayout),
    children: [
      {
        path: 'rooms/:roomId',
        loadComponent: () =>
          import('./features/chat/pages/chat-room-page/chat-room-page')
            .then(m => m.ChatRoomPage)
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'rooms/general'
      }

    ]
  },
  {
    path: '**',
    redirectTo: 'chat'
  }

];

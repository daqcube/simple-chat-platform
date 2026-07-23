import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {catchError, throwError} from 'rxjs';
import {ApiErrorResponse} from '../models/chat.model';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const backendError = error.error as ApiErrorResponse | undefined;
      const message =
        backendError?.message ??
        (error.status === 0
          ? 'Unable to reach the chat server. Is the backend running?'
          : `Request failed (${error.status})`);

      console.error('[HTTP error]', message, backendError?.details ?? '');
      return throwError(() => new Error(message));
    }),
  );
};

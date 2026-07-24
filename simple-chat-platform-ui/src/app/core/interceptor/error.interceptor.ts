import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {catchError, throwError} from 'rxjs';
import {ApiErrorResponse} from '../models/api-error-response.model';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const backendError = error.error as ApiErrorResponse | undefined;
      const message =
        backendError?.message ??
        (error.status === 0
          ? 'We are having trouble connecting to our chat platform right now.'
          : `Request failed (${error.status})`);

      console.error('[HTTP error]', message, backendError?.details ?? '');
      return throwError(() => new Error(message));
    }),
  );
};

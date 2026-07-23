import {AfterViewChecked, Component, ElementRef, input, ViewChild} from '@angular/core';
import {ChatMessageResponse} from '../../../../core/models/chat.model';
import {MessageItem} from '../message-item/message-item';


@Component({
  selector: 'app-message-list',
  standalone: true,
  imports: [
    MessageItem
  ],
  templateUrl: './message-list.html',
  styleUrl: './message-list.css',
})
export class MessageList implements AfterViewChecked {
  readonly messages = input.required<ChatMessageResponse[]>();

  @ViewChild('scrollAnchor')
  private readonly scrollAnchor?: ElementRef<HTMLDivElement>;

  ngAfterViewChecked(): void {
    this.scrollAnchor?.nativeElement.scrollIntoView({block: 'end'});
  }
}

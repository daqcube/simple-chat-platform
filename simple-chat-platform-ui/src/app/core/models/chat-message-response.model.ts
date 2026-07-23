export enum MessageType {
  CHAT = 'CHAT',
  JOINED = 'JOINED',
  LEFT = 'LEFT'
}

export interface ChatMessageResponse {
  id: string;
  type: MessageType;
  username: string;
  content: string;
  timestamp: string;
  roomId: string
}

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

export interface ChatRoom {
  id: string;
  name: string;
}

export interface ChatMessageRequest {
  roomId: string;
  username: string;
  content: string;
}


export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  details: string[];
}

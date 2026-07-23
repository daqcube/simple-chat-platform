export const environment = {
  production: false,

  websocket: {
    endpoint: 'http://localhost:8080/ws',
    destination: {
      sendMessage: '/app/chat.send',
      leave: '/app/chat.leave',
    },
    subscription: {
      messages: '/topic/messages',
      rooms: '/topic/rooms',
    }
  },
  api: {
    chat: 'http://localhost:8080/api/chat'
  }
};

export const environment = {
  production: true,

  websocket: {
    endpoint: 'http://localhost:8080/ws',
    destination: {
      sendMessage: '/app/chat.send',
      leave: '/app/chat.leave',
    },
    subscription: {
      users: '/topic/users',
      rooms: '/topic/rooms',
    }
  },
  api: {
    chat: 'http://localhost:8080/api/chat'
  }
};

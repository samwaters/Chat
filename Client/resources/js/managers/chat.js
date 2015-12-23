/**
 * Created by sam on 17/12/2015.
 */
if(typeof window.appManager == "undefined")
{
  throw new Error("App Manager must be included first");
}
window.appManager.chatManager = {
  socket: null,
  connect: function() {
    this.socket = new WebSocket("ws://localhost:9001");
    this.socket.onclose = function() { };
    this.socket.onerror = function(e) { this.error("socket", e.message); };
    this.socket.onopen = function() {
      this.getFavouriteRoomList();
      this.getFriendsList();
      this.getRoomList();
      this.getUserList();
    };
    this.socket.onmessage = function(d) { this.processMessage(d); };
  },
  disconnect: function() {
    if(this.isConnected()) {
      this.socket.close();
    }
  },
  error: function(type, message) {

  },
  getFavouriteRoomList: function() {
    this.send("command", "favourite_room_list");
  },
  getFriendsList: function() {
    this.send("command", "friend_list");
  },
  getRoomList: function() {
    this.send("command", "room_list");
  },
  getUserList: function() {
    this.send("command", "user_list");
  },
  isConnected: function() {
    return this.socket instanceof WebSocket && this.socket.readyState == 1;
  },
  processMessage: function(message) {
    var messageData = JSON.parse(message);
    if(!messageData) {
      this.error("receive", "Failed to decode message");
      return;
    }
    switch(messageData.command) {
      case "favourite_room_list":
        window.appManager.chatUIManager.setFavouriteRoomList(messageData.rooms);
        break;
      case "friend_list":
        window.appManager.chatUIManager.setFriendsList(messageData.friends);
        break;
      case "room_list":
        window.appManager.chatUIManager.setRoomList(messageData.rooms);
        break;
      case "user_list":
        window.appManager.chatUIManager.setUserList(messageData.users);
        break;
      case "text":
        window.appManager.chatUIManager.newMessage(messageData.message, messageData.from);
        break;
    }
  },
  send: function(type, message) {
    if(!this.isConnected()) {
      this.error("send", "Not Connected");
      return false;
    }
    var messageData = {
      "command": type,
      "message": message,
      "room": window.appManager.chatUIManager.getCurrentRoom()
    };
    this.socket.send(JSON.stringify(messageData));
    return true;
  }
};

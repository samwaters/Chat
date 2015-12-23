/**
 * Created by sam on 17/12/2015.
 */
if(typeof window.appManager == "undefined")
{
  throw new Error("App Manager must be included first");
}
window.appManager.chatUIManager = {
  getCurrentRoom: function() {
    return "public";
  },
  newMessage: function(message, from) {

  },
  setFavouriteRoomList: function(rooms) {

  },
  setFriendsList: function(friends) {

  },
  setRoomList: function(rooms) {

  },
  setUserList: function(users) {

  }
};

/**
 * Created by sam on 11/10/2015.
 * Login manager for chat
 */
if(typeof window.appManager == "undefined")
{
  throw new Error("App Manager must be included first");
}
window.appManager.loginManager = {
  doLogin: function(username, token, callback) {
    $.ajax({
      "data": "username=" + username + "&token=" + token,
      "error": function() {
        callback({"success": false, "error": "Server Error"});
      },
      "success": function(data) {
        if(data.success)
        {
          callback({"success": true});
        }
        else
        {
          callback({"success": false, "error": "Invalid Username / Password"});
        }
      },
      "type": "post",
      "url": "login"
    })
  },
  getLoginToken: function(username, password, callback) {
    $.ajax({
      "error": function() {
        callback({"success": false, "error": "Could not get token"});
      },
      "loginData": {"username":username, "password": password},
      "success": function(data) {
        if(typeof data.token != "undefined")
        {
          var encrypyedPassword = CryptoJS.HmacSHA512(this.loginData.password, data.token);
          window.appManager.loginManager.doLogin(this.loginData.username, encrypyedPassword, callback);
        }
        else
        {
          callback({"success": false, "error": "Could not get token"});
        }
      },
      "url": "login/token?ts=" + new Date().getTime()
    });
  },
  login: function(username, password, callback) {
    window.appManager.loginManager.getLoginToken(username, password, callback);
  }
};

/**
 * Created by sam on 12/10/2015.
 */
//Login
$("#btnLogin").on("click", function(e) {
  e.preventDefault();
  window.appManager.loginManager.showValidatingPane();
  window.appManager.loginManager.login($("#loginUsername").val(), $("#loginPassword").val(), function(result) {
    if(result.code == 200 && result.result == "ok") {
      var settings = {
        "token": result.token,
        "tokenExpiry": result.tokenExpiry,
        "username": result.username
      };
      window.appManager.settingsManager.saveSettings(settings);
      $("#mdlLogin").modal("hide");
    } else {
      window.appManager.loginManager.showLoginPane();
    }
  });
});
//Font modal
$("#btnFontSettings").on("click", function(e) {
  e.preventDefault();
  $("#mdlFontSettings").modal();
});

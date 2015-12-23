<?php
/**
 * Created by Sam.
 * At: 19/11/2015 02:37
 */
include_once("partials/header_open.php");
?>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" type="text/css" href="resources/css/plugins/loader.css" />
<style type="text/css">
  body { padding-top:30px; }
  #messageArea .message { background-color:#FFBABA; border:1px solid #D8000C; border-radius:5px; color:#D8000C; font-weight:bold; margin-bottom:10px; padding:10px; }
  .login-form label, .login-form input { width:100%; }
  .login-form input { border-radius:5px; }
  .login-form .row { margin:0 0 10px 0; }
  .login-form .row:last-child { margin-bottom:0; }
  .login-form .col-label { padding-left:0; }
  .login-form .col-input { padding:0; }
  .row { margin:0; }
</style>
<?php include_once("partials/header_close.php"); ?>
  <div class="row">
    <div class="col-md-4 col-md-offset-4 col-sm-10 col-sm-offset-1 col-xs-10 col-xs-offset-1">
      <div id="messageArea"></div>
      <div class="panel panel-default">
        <div class="panel-heading">
          <h1 class="panel-title">Login</h1>
        </div>
        <div class="panel-body login-form">
          <div class="row">
            <div class="col-md-3 col-label"><label for="username">Username:</label></div>
            <div class="col-md-9 col-input"><input type="text" id="username" /></div>
          </div>
          <div class="row">
            <div class="col-md-3 col-label"><label for="password">Password:</label></div>
            <div class="col-md-9 col-input"><input type="password" id="password" /></div>
          </div>
        </div>
        <div class="panel-footer">
          <button class="btn btn-warning" id="forgottenButton">Forgotten Password?</button>
          <button class="btn btn-success pull-right" id="loginButton">Log In</button>
          <div class="clearfix"></div>
        </div>
      </div>
    </div>
  </div>
  <script type="text/javascript" src="resources/js/crypto/hmac-sha512.js"></script>
  <script type="text/javascript" src="resources/js/managers/app.js"></script>
  <script type="text/javascript" src="resources/js/managers/login.js"></script>
  <script type="text/javascript" src="resources/js/plugins/loader.js"></script>
  <script type="text/javascript">
    $("#loginButton").on("click", function() {
      var username = $("#username").val();
      var password = $("#password").val();
      $("#loginButton").elementLoader();
      window.appManager.loginManager.login(username, password, function(result) {
        if(result.success)
        {
          var loc = window.location.pathname.split("/");
          loc.pop();
          loc = loc.join("/");
          window.location.href = loc;
        }
        else
        {
          $("#messageArea").empty().append("<div class='message'>Login Failed: " + result.error + "</div>");
          $("#loginButton").elementLoader("stop");
        }
      });
    });
    $("#password").on("keyup", function(e) {
      var $lb = $("#loginButton");
      if(!$lb.is(".elementLoading") && (e.keyCode == 10 || e.keyCode == 13)) {
        $lb.trigger("click");
      }
    });
    $("#username").focus();
  </script>
<?php
include_once("partials/footer.php");

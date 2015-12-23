<?php
/**
 * Created by Sam.
 * At: 10/10/2015 20:45
 */
$forceHTTPS = false;
if($forceHTTPS && (!isset($_SERVER["HTTPS"]) || $_SERVER["HTTPS"] != "on"))
{
  header("Location: https://" . $_SERVER["SERVER_NAME"] . "/" . trim($_SERVER["REQUEST_URI"], "/"));
  exit;
}
require_once("vendor/autoload.php");
if(!\ChatUI\Helpers\ConfigHelper::loadConfig("config.ini"))
{
  echo "<h1>Error</h1><p>Cannot find or parse config.ini</p>";
  die;
}
session_cache_limiter(false);
session_start();
$app = new \Slim\Slim(\ChatUI\Helpers\ConfigHelper::getConfig("slim"));
$app->get("/login", function() use($app) {
  $app->render("login.php");
});
$app->post("/login", function() use($app) {
  $app->contentType("text/json");
  $username = $_POST["username"];
  $token = $_POST["token"];
  $verified = \ChatUI\Helpers\LoginHelper::validateLogin($username, $token);
  echo json_encode([
    "username" => $username,
    "token" => $token,
    "success" => $verified
  ]);
});
$app->get("/login/token", function() use($app) {
  $app->contentType("text/json");
  $token = \ChatUI\Helpers\LoginHelper::generateLoginToken();
  echo json_encode($token);
});
$app->get("/", function() use($app) {
  if(\ChatUI\Helpers\LoginHelper::validateCookie())
  {
    $app->render("chat.php");
  }
  else
  {
    $app->redirect("login");
  }
});
$app->run();

<?php
/**
 * Created by Sam.
 * At: 19/11/2015 08:30
 */
namespace ChatUI\Helpers;

class CookieHelper
{
  public static function decryptCookie($name)
  {
    if(isset($_COOKIE[$name]))
    {
      $authConfig = ConfigHelper::getConfig("auth");
      $encrypted = $_COOKIE[$name];
      $key = hash("sha256", $authConfig["cookiekey"]);
      $iv = substr(md5($authConfig["cookiekey"]), 4, 16);
      return openssl_decrypt($encrypted, "aes256", $key, false, $iv);
    }
    else
    {
      return false;
    }
  }
  public static function setEncryptedCookie($name, $value, $expires)
  {
    $authConfig = ConfigHelper::getConfig("auth");
    $key = hash("sha256", $authConfig["cookiekey"]);
    $iv = substr(md5($authConfig["cookiekey"]), 4, 16);
    $encrypted = openssl_encrypt($value, "aes256", $key, false, $iv);
    setcookie($name, $encrypted, $expires);
  }
}

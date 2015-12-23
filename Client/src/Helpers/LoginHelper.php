<?php
/**
 * Created by Sam.
 * At: 19/11/2015 04:02
 */
namespace ChatUI\Helpers;

class LoginHelper
{
  public static function generateLoginToken()
  {
    $token = md5(session_id());
    $_SESSION["login_token"] = $token;
    $_SESSION["login_token_expiry"] = strtotime("+1 hour");
    return [
      "expires" => $_SESSION["login_token_expiry"],
      "token" => $token
    ];
  }

  public static function validateCookie()
  {
    if(isset($_COOKIE["chatsession"]))
    {
      try
      {
        $decrypted = json_decode(CookieHelper::decryptCookie("chatsession"));
      }
      catch(\Exception $e)
      {
        setcookie("chatsession", null, -3600);
        return false;
      }
      if($decrypted)
      {
        if(!isset($decrypted->username) || !isset($decrypted->token) || !isset($decrypted->ip) || !isset($decrypted->generated) || !isset($decrypted->expiry))
        {
          return false;
        }
        if($decrypted->ip != $_SERVER["REMOTE_ADDR"] || $decrypted->expiry < time())
        {
          return false;
        }
        $authConfig = ConfigHelper::getConfig("auth");
        /** @var \ChatUI\AuthProviders\IAuthProvider $provider */
        $provider = new $authConfig["provider"];
        return $provider->auth($decrypted->username, $decrypted->token);
      }
      else
      {
        setcookie("chatsession", null, -3600);
        return false;
      }
    }
    else
    {
      return false;
    }
  }

  public static function validateLogin($username, $token)
  {
    $authConfig = ConfigHelper::getConfig("auth");
    /** @var \ChatUI\AuthProviders\IAuthProvider $provider */
    $provider = new $authConfig["provider"];
    if($provider->auth($username, $token))
    {
      $data = [
        "username" => $username,
        "token" => $token,
        "ip" => $_SERVER["REMOTE_ADDR"],
        "generated" => time(),
        "expiry" => strtotime("+1 hour")
      ];
      CookieHelper::setEncryptedCookie("chatsession", json_encode($data), strtotime("+1 hour"));
      return true;
    }
    else
    {
      return false;
    }
  }
}

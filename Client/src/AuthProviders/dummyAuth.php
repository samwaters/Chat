<?php
/**
 * Created by Sam.
 * At: 11/10/2015 20:44
 */
namespace ChatUI\AuthProviders;

class dummyAuth implements IAuthProvider
{
  public function auth($username, $token)
  {
    if(!isset($_SESSION["login_token"]))
    {
      return false;
    }
    $password = "pass1234";
    $calculated = hash_hmac("sha512", $password, $_SESSION["login_token"]);
    return $calculated == $token;
  }
}

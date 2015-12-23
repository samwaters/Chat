<?php
/**
 * Created by Sam.
 * At: 11/10/2015 20:44
 */
namespace ChatUI\AuthProviders;

interface IAuthProvider
{
  public function auth($username, $token);
}

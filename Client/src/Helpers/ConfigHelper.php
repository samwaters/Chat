<?php
/**
 * Created by Sam.
 * At: 19/11/2015 07:53
 */
namespace ChatUI\Helpers;

class ConfigHelper
{
  private static $_config = [];

  public static function loadConfig($configFile)
  {
    $config = parse_ini_file($configFile, true);
    if(!$config)
    {
      return false;
    }
    self::$_config = $config;
    return true;
  }

  public static function getConfig($key)
  {
    return !empty(self::$_config[$key]) ? self::$_config[$key] : false;
  }
}

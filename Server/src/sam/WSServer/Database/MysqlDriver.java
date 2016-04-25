package sam.WSServer.Database;

import sam.WSServer.Utils;

import java.sql.*;
/**
 * @author sam@samwaters.com
 * @version 1.0.0
 * @date 25/04/2016
 */
public class MysqlDriver
{
  private String _host;
  private String _username;
  private String _password;
  private Connection _connection;

  public MysqlDriver(String host, String username, String password) throws ClassNotFoundException
  {
    Class.forName("com.mysql.jdbc.Driver");
    this._host = "jdbc:mysql://" + host;
    this._username = username;
    this._password = password;
    this._refreshConnection();
  }

  private boolean _refreshConnection()
  {
    try
    {
      this._connection = DriverManager.getConnection(this._host, this._username, this._password);
      return true;
    }
    catch(Exception e)
    {
      Utils.logMessage("Failed to connect to mysql : " + e.getMessage());
      return false;
    }
  }

  private void _checkConnection() throws Exception
  {
    if(!this.isConnected() && !this._refreshConnection())
    {
      throw new Exception("Failed to connect to MySQL");
    }
  }

  public boolean isConnected()
  {
    try
    {
      return this._connection.isValid(5);
    }
    catch(Exception e)
    {
      Utils.logMessage("Failed to validate connection : " + e.getMessage());
      return false;
    }
  }

  public ResultSet query(String query)
  {
    try
    {
      this._checkConnection();
      Statement statement = this._connection.createStatement();
      return statement.executeQuery(query);
    }
    catch(Exception e)
    {
      Utils.logMessage("Failed to execute query : " + e.getMessage());
      return null;
    }
  }

  public int update(String query)
  {
    try
    {
      this._checkConnection();
      Statement statement = this._connection.createStatement();
      return statement.executeUpdate(query);
    }
    catch(Exception e)
    {
      Utils.logMessage("Failed to execute query : " + e.getMessage());
      return -1;
    }
  }
}

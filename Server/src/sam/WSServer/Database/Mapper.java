package sam.WSServer.Database;

import sam.WSServer.Utils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author sam@samwaters.com
 * @version 1.0.0
 * @date 25/04/2016
 */
abstract public class Mapper<T> implements Iterator
{
  private Class<T> _class;
  private MysqlDriver _driver;
  private int _pointer = 0;
  private ArrayList<T> _rows = new ArrayList<T>();

  public Mapper()
  {
    this._class = (Class<T>)this.getClass();
  }

  abstract protected String getDatabaseName();
  abstract protected String getTableName();

  public final boolean hasNext()
  {
    return this._rows.size() > this._pointer;
  }

  public final boolean loadAll()
  {
    this.loadDriver();
    Field[] fields = this._class.getFields();
    String fieldList = "";
    for(Field f : fields)
    {
      fieldList += f.getName() + ",";
    }
    fieldList = fieldList.substring(0, fieldList.length() - 1);
    ResultSet resultSet = this._driver.query("SELECT " + fieldList + " FROM " + this.getTableName());
    try
    {
      while(resultSet.next())
      {
        T instance = (T)this.getClass().getDeclaredConstructors()[0].newInstance();
        for(Field f : fields)
        {
          String value = resultSet.getString(f.getName());
          f.set(instance, value);
        }
        this._rows.add(instance);
      }
      return true;
    }
    catch(Exception e)
    {
      Utils.logMessage("Error loading records : " + e.getMessage());
      return false;
    }
  }

  protected final boolean loadDriver()
  {
    if(this._driver == null)
    {
      try
      {
        this._driver = new MysqlDriver("localhost", "test", "test");
        this._driver.query("USE " + this.getDatabaseName());
      }
      catch(Exception e)
      {
        Utils.logMessage("Cannot connect to mysql : " + e.getMessage() );
        return false;
      }
    }
    return true;
  }

  public final T loadOneWhere(String where)
  {
    this.loadDriver();
    Field[] fields = this._class.getFields();
    String fieldList = "";
    for(Field f : fields)
    {
      fieldList += f.getName() + ",";
    }
    fieldList = fieldList.substring(0, fieldList.length() - 1);
    ResultSet resultSet = this._driver.query("SELECT " + fieldList + " FROM " + this.getTableName() + " WHERE " + where + " LIMIT 1");
    try
    {
      if(resultSet.next())
      {
        for(Field f : fields)
        {
          String value = resultSet.getString(f.getName());
          f.set(this, value);
        }
        return (T)this;
      }
      return null;
    }
    catch(Exception e)
    {
      Utils.logMessage("Error loading record : " + e.getMessage());
      return null;
    }
  }

  public final boolean loadWhere(String where)
  {
    this.loadDriver();
    Field[] fields = this._class.getFields();
    String fieldList = "";
    for(Field f : fields)
    {
      fieldList += f.getName() + ",";
    }
    fieldList = fieldList.substring(0, fieldList.length() - 1);
    ResultSet resultSet = this._driver.query("SELECT " + fieldList + " FROM " + this.getTableName() + " WHERE " + where);
    try
    {
      while(resultSet.next())
      {
        T instance = (T)this.getClass().getDeclaredConstructors()[0].newInstance();
        for(Field f : fields)
        {
          String value = resultSet.getString(f.getName());
          f.set(instance, value);
        }
        this._rows.add(instance);
      }
      return true;
    }
    catch(Exception e)
    {
      Utils.logMessage("Error loading records : " + e.getMessage());
      return false;
    }
  }

  public final boolean save()
  {
    this.loadDriver();
    try
    {
      Field[] fields = this._class.getFields();
      String fieldList = "";
      String valuesList = "";
      for(Field f : fields)
      {
        fieldList += f.getName() + ",";
        valuesList += "'" + f.get(this) + "',";
      }
      fieldList = fieldList.substring(0, fieldList.length() - 1);
      valuesList = valuesList.substring(0, valuesList.length() - 1);
      String query = "INSERT INTO " + this.getTableName() + "(" + fieldList + ") VALUES(" + valuesList + ")";
      Utils.logMessage("Query = " + query);
      this._driver.update(query);
      return true;
    }
    catch(Exception e)
    {
      Utils.logMessage("Cannot save record : " + e.getMessage());
      return false;
    }
  }

  public final T next()
  {
    T instance = this._rows.get(this._pointer);
    this._pointer++;
    return instance;
  }

  public final void reset()
  {
    this._pointer = 0;
  }
}

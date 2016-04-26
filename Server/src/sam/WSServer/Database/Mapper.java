package sam.WSServer.Database;

import sam.WSServer.Database.Drivers.Mysql;
import sam.WSServer.Database.Enums.QueryType;
import sam.WSServer.Database.Enums.WhereOperator;
import sam.WSServer.Database.Lib.Query;
import sam.WSServer.Database.Lib.Where;
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
  private Mysql _driver;
  private int _pointer = 0;
  private ArrayList<T> _rows = new ArrayList<T>();

  public Mapper()
  {
    this._class = (Class<T>)this.getClass();
  }

  abstract protected String getDatabaseName();
  abstract protected String getTableName();

  public String getIdField()
  {
    return "id";
  }

  public final boolean hasNext()
  {
    return this._rows.size() > this._pointer;
  }

  public final boolean loadAll()
  {
    this.loadDriver();
    Query query = new Query(QueryType.SELECT, this.getTableName());
    Field[] fields = this._class.getFields();
    for(Field f : fields)
    {
      query.addSelect(f.getName());
    }
    ResultSet resultSet = this._driver.query(query);
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
        this._driver = new Mysql("localhost", "test", "test");
        Query query = new Query(QueryType.USE, this.getTableName());
        query.setDatabaseName(this.getDatabaseName());
        this._driver.query(query);
      }
      catch(Exception e)
      {
        Utils.logMessage("Cannot connect to mysql : " + e.getMessage() );
        return false;
      }
    }
    return true;
  }

  public final T loadOneWhere(ArrayList<Where> where)
  {
    this.loadDriver();
    Query query = new Query(QueryType.SELECT, this.getTableName());
    Field[] fields = this._class.getFields();
    for(Field f : fields)
    {
      query.addSelect(f.getName());
    }
    for(Where w : where)
    {
      query.addWhere(w);
    }
    ResultSet resultSet = this._driver.query(query);
    try
    {
      if(resultSet.next())
      {
        T instance = (T)this.getClass().getDeclaredConstructors()[0].newInstance();
        for(Field f : fields)
        {
          String value = resultSet.getString(f.getName());
          f.set(instance, value);
        }
        return instance;
      }
      return null;
    }
    catch(Exception e)
    {
      Utils.logMessage("Error loading record : " + e.getMessage());
      return null;
    }
  }

  public final boolean loadWhere(ArrayList<Where> where)
  {
    this.loadDriver();
    Query query = new Query(QueryType.SELECT, this.getTableName());
    Field[] fields = this._class.getFields();
    for(Field f : fields)
    {
      query.addSelect(f.getName());
    }
    for(Where w : where)
    {
      query.addWhere(w);
    }
    ResultSet resultSet = this._driver.query(query);
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

  public final T next()
  {
    T instance = this._rows.get(this._pointer);
    this._pointer++;
    return instance;
  }

  public final boolean save()
  {
    this.loadDriver();
    try
    {
      String idFieldValue = this._class.getField(this.getIdField()).get(this).toString();
      //Do we need to do an insert or update?
      ArrayList<Where> whereList = new ArrayList<Where>();
      Where where = new Where(this.getIdField(), WhereOperator.EQUALS, idFieldValue);
      whereList.add(where);
      if(this.loadOneWhere(whereList) != null)
      {
        return this._saveUpdate();
      }
      else
      {
        return this._saveInsert();
      }
    }
    catch(Exception e)
    {
      Utils.logMessage("Cannot save record : " + e.getMessage());
      return false;
    }
  }

  private boolean _saveInsert()
  {
    try
    {
      Query query = new Query(QueryType.INSERT, this.getTableName());
      Field[] fields = this._class.getFields();
      for(Field f : fields)
      {
        query.addInsert(f.getName(), (String)f.get(this));
      }
      int insertId = this._driver.insert(query);
      if(insertId > 0)
      {
        this._class.getField(this.getIdField()).set(this, "" + insertId);
        return true;
      }
      return false;
    }
    catch(Exception e)
    {
      Utils.logMessage("Cannot save insert record : " + e.getMessage());
      return false;
    }
  }

  private boolean _saveUpdate()
  {
    try
    {
      Query query = new Query(QueryType.UPDATE, this.getTableName());
      Field[] fields = this._class.getFields();
      for(Field f : fields)
      {
        query.addUpdate(f.getName(), (String)f.get(this));
      }
      Where where = new Where(this.getIdField(), WhereOperator.EQUALS, (String)this._class.getField(this.getIdField()).get(this));
      query.addWhere(where);
      return this._driver.update(query) >= 0;
    }
    catch(Exception e)
    {
      Utils.logMessage("Cannot save update record : " + e.getMessage());
      return false;
    }
  }

  public final void reset()
  {
    this._pointer = 0;
  }
}

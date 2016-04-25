package sam.WSServer;

import sam.WSServer.Database.Mappers.TestTableMapper;

/**
 * @author sam@samwaters.com
 * @version 1.0.0
 * @date 25/04/2016
 */
public class DBTester
{
  public DBTester()
  {

  }

  public static void main(String args[])
  {
    TestTableMapper n = new TestTableMapper();
    n.id = "0";
    n.name = "Sam 7";
    n.email = "sam7@samwaters.com";
    n.save();
    TestTableMapper m = new TestTableMapper();
    m.loadAll();
    while(m.hasNext())
    {
      TestTableMapper row = (TestTableMapper)m.next();
      Utils.logMessage("Row : " + row.id + " , " + row.name + " , " + row.email);
    }
    Utils.logMessage("---------------------------");
    TestTableMapper o = new TestTableMapper();
    o.loadWhere("id > 3 AND id < 6");
    while(o.hasNext())
    {
      TestTableMapper row = (TestTableMapper)o.next();
      Utils.logMessage("Row : " + row.id + " , " + row.name + " , " + row.email);
    }
    Utils.logMessage("---------------------------");
    TestTableMapper row = (TestTableMapper)new TestTableMapper().loadOneWhere("id = 4");
    if(row != null)
    {
      Utils.logMessage("Row : " + row.id + " , " + row.name + " , " + row.email);
    }
  }
}

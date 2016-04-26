package sam.WSServer;

import sam.WSServer.Mappers.TestTableMapper;
import sam.WSServer.Database.Lib.Where;
import sam.WSServer.Database.Enums.WhereOperator;

import java.util.ArrayList;

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
    n.name = "Sam 12";
    n.email = "sam7@samwaters.com";
    n.save();
    Utils.logMessage("Insert ID " + n.id);
    n.name = "TESTT User";
    n.email = "sam.test@samwaters.com";
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
    ArrayList<Where> where = new ArrayList<Where>();
    where.add(new Where("id", WhereOperator.GREATER_THAN_OR_EQUAL, "5"));
    where.add(new Where("id", WhereOperator.LESS_THAN, "10"));
    o.loadWhere(where);
    while(o.hasNext())
    {
      TestTableMapper row = (TestTableMapper)o.next();
      Utils.logMessage("Row : " + row.id + " , " + row.name + " , " + row.email);
    }
  }
}

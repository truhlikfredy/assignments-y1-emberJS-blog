import java.util.List;

import play.*;
import play.jobs.*;
import play.test.*;
import models.*;
import utils.Helpers;

@OnApplicationStart
public class Bootstrap extends Job {
  public void doJob() {
    Fixtures.deleteDatabase();
    Logger.info("Timestamp is:"+System.currentTimeMillis());
    // Fixtures.loadModels("data.yml");
    Helpers.fillData();
  }
}

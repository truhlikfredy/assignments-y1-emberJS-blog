import java.util.ArrayList;
import java.util.List;

import models.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Logger;
import play.test.Fixtures;
import play.test.UnitTest;
import utils.Helpers;

public class LoggedTest extends UnitTest {

  @BeforeClass
  public static void loadDB() {
    Fixtures.deleteAllModels();
  }

  @Before
  public void setup() {
    // Testing on whole mockup database, more realistic data and even easier
    // tests. Still testing properly because these data could contain situations
    // which could be forgoten when setuping test data. Downside of this
    // approach is that when the mockup database changes these tests need to be
    // adjusted.
    Helpers.fillData();
  }

  @After
  public void teardown() {
    Fixtures.deleteAllModels();
  }

  @Test
  public void testCreating() {
    User user = User.findByEmail("skyler@white.com");
    
    assertEquals(1, Logged.findAll().size());
    Logged log = new Logged(user); 
    log.save();
    assertEquals(2, Logged.findAll().size());

  }

  @Test
  public void testCheatSession() {
    assertEquals("Skyler White", Logged.getSession("111111-2222-333-44-5").user.name);
  }

  @Test
  public void testDeletion() {
    Logged.cleanSessions();
    assertEquals(0, Logged.findAll().size());    
  }

}
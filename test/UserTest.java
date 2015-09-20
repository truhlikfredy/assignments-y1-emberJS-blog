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

public class UserTest extends UnitTest {

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
  public void testCreation() {
    assertEquals(9, User.findAll().size());

    User user = new User("Name named", "donNot@spam.me", "iDonTkn0w", true);
    user.save();

    assertEquals(10, User.findAll().size());
  }

  @Test
  public void testDeletion() {
    User user = User.findByEmail("skyler@white.com");

    // number blogs present in database is 5
    assertEquals(5, Blog.findAll().size());

    assertEquals(9, User.findAll().size());

    List<User> all = User.findAll();

    Logger.info("Deleting user " + user);

    for (Blog myBlog : user.blogs) {
      for (User someBody : all) {
        if (someBody != user) {
          // Logger.info("Checking user " + someBody + " if he is subscribed.");
          if (someBody.subs.contains(myBlog)) {
            Logger.info("Found user " + someBody + " subscribed to this blog (he is subscribed to " + someBody.subs.size() + " blogs).");

            someBody.subs.remove(myBlog);
            someBody.save();
            Logger.info("He is subscribed to " + someBody.subs.size() + " blogs now.");
          }
        }
      }
      user.blogs.remove(myBlog);
      myBlog.delete();
    }
    user.save();

    // delete all users comments from other blog posts which are not in this
    // object, but would reference back to this object
    Logger.info("Deleting comments of user " + user);
    List<Comment> comments = Comment.findAll();
    for (Comment comment : comments) {
      if (comment.user == user) {
        comment.delete();
      }
    }

    // delete all his friendships with anybody else again this is outside this
    // object referencing back to it
    // all subchilds object of this object will be destroyed automaticly, but we
    // need to ensura nothing outside this object is referencing back to it
    for (User someBody : all) {
      if (someBody != user) {
        if (someBody.friends.contains(user)) {
          Logger.info("User has it as friend "+someBody.friends.size());
          someBody.friends.remove(user);
          someBody.save();
          Logger.info("Updated number of friends "+someBody.friends.size());
        }
      }
    }

    // delete this user from sessions
    Logged.cleanSessions();

    user.delete();

    assertEquals(8, User.findAll().size());

    // number of posts should drop automaticly from 5 to 3
    assertEquals(3, Blog.findAll().size());
  }

}

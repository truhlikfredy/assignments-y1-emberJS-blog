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

public class BlogTest extends UnitTest {

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
    assertEquals(5, Blog.findAll().size());

    User user = User.findByEmail("skyler@white.com");
    Blog blog = new Blog(user, "New test blog", true, 5, true, true);
    blog.save();

    assertEquals(6, Blog.findAll().size());
  }

  @Test
  public void testDeletion() {
    User user = User.findByEmail("skyler@white.com");

    // number posts present in database is 11
    assertEquals(11, Post.findAll().size());

    assertEquals(5, Blog.findAll().size());
    Blog blog = user.blogs.get(0);
    Logger.info("Deleting blog: " + blog + " blog id " + blog.id);

    List<User> all = User.findAll();
    for (User someBody : all) {
      if (someBody != user) {
        Logger.info("Checking user " + someBody+ " if he is subscribed.");
        if (someBody.subs.contains(blog)) {
          Logger.info("Found user " + someBody + " subscribed to this blog (he is subscribed to " + someBody.subs.size() + " blogs).");

          someBody.subs.remove(blog);
          someBody.save();
          Logger.info("He is subscribed to " + someBody.subs.size() + " blogs now.");
        }
      }
    }

    blog.delete();

    assertEquals(4, Blog.findAll().size());

    // number of posts should drop automaticly from 11 to 10
    assertEquals(10, Post.findAll().size());
  }

  @Test
  public void testUserBlogs() {
    //feetch list of somebody blogs by suplying his user instance
    assertEquals(2, Blog.userBlogs(User.findByEmail("walter@white.com")).size());
  }
}
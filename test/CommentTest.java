import java.util.ArrayList;
import java.util.List;

import utils.Helpers;
import models.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class CommentTest extends UnitTest {
//  private User bob;
//  private Post post1, post2;
//  private Comment comment1, comment2, comment3;

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
    assertEquals(4, Comment.findAll().size());
    User user = User.findByEmail("skyler@white.com");
    Comment comment = new Comment(user.blogs.get(0).posts.get(0), user, "Adding comment", "Hi");
    comment.save();
    assertEquals(5, Comment.findAll().size());
    assertTrue(comment.time <= System.currentTimeMillis());
  }

  @Test
  public void testDeletion() {
    User user = User.findByEmail("skyler@white.com");
    for (Blog blog : user.blogs) {
      for (Post post : blog.posts) {
        for (Comment comment : post.comments) {
          comment.delete();
        }
      }
    }
    assertEquals(3, Comment.findAll().size());
  }

  @Test
  public void testToString() {
    User user = User.findByEmail("skyler@white.com");
    assertEquals("**No**! You have to try more. I command **YOU**!", user.blogs.get(1).posts.get(0).comments.get(0).contentText);
  }

}
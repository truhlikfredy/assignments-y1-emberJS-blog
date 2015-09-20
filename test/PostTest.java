import models.Comment;
import models.Post;
import models.User;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



//import antlr.collections.List;
import play.test.Fixtures;
import play.test.UnitTest;
import utils.Helpers;

public class PostTest extends UnitTest {

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
    assertEquals(11, Post.findAll().size());
    User user = User.findByEmail("skyler@white.com");
    Post post = new Post(user.blogs.get(0),"Carwash post","Washy washy wash.");
    post.save();
    assertEquals(12, Post.findAll().size());
    assertTrue(post.timeShow <= System.currentTimeMillis());
  }
  
  @Test
  public void testDeletion() {
    User user = User.findByEmail("skyler@white.com");

    //number comments present in database is 4
    assertEquals(4, Comment.findAll().size());
    
    for (Post post:user.blogs.get(0).posts) {
      post.delete();
    }
    assertEquals(10, Post.findAll().size());
    
    for (Post post:user.blogs.get(1).posts) {
      post.delete();
    }
    assertEquals(8, Post.findAll().size());
    
    //number of comments should drop automaticly from 4 to 3 
    assertEquals(3, Comment.findAll().size());
  }
  
  @Test
  public void testFindPost() {
    Post foundPost = Post.find("title", "Caballero del Febo").first();
    assertNotNull(foundPost);
  }

}

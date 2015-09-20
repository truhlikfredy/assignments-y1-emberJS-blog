package controllers;

import play.*;
import play.db.jpa.Blob;
import play.mvc.*;
import play.mvc.results.RenderText;

import java.util.*;

import models.*;
import utils.Parse;
//import utils.UserComparator;

import java.util.*;

import com.google.gson.JsonElement;

/**
 * This class contains majority of the server side methods. Decided to keep in 1
 * medium sized class file, instead of many tiny files. This size is ideal size,
 * not too big, not too small. Easy to find methods (don't have to open 5-10
 * tabs to find what I'm looking for). Thankfuly all methods are very small and
 * effective (if it wasn't this case I would split them into multiple files).
 * 
 * Tiny methods feel more organized when they are grouped together and easier to
 * orient in.
 * 
 * Size of the methods is caused by 6 factors:
 * 
 * 1) My own implementation of sessions returns directly User object in 1 line.
 * 
 * 2) All objects in model are cascaded together, this means that deleting
 * something doesn't require to iterate everything and can be done in 1 line.
 * 
 * 3) Side effect of cascaded model is that childs have to point to parents.
 * This means that getting any kind of object anywhere in the chain means you
 * get access to everything:
 */

// public static void removeComment(Long id, String sid) {
//   User user = Logged.getUser(sid);
//   Comment comment = Comment.findById(id);
//   if (comment.user == user || comment.post.blog.user == user) {

/**
 * This sniplet requires only 2 parameters, id of comment, and session. From
 * session we can get user User object, from id the comment object.
 * 
 * And in third line we can check if the logged in user is owner of the comment,
 * or he is owner of the whole blog on which this comment was made. And
 * therefore he has permissions to delete it.
 * 
 * 4) These methods send raw data, parsing is done by separate class. And these
 * methods do not have to worry to what template it should render. Often they
 * are called from different places in ember (which would require workarounds if
 * templating would be needed on server side). So again simplification on server
 * side.
 * 
 * 5) Because of ember all Api calls require just parameters needed for given
 * action. They do not pass trough any unecesarry information which would be
 * required without ember. State of the application is conserved on client side
 * so you don't need to supply state parameters.
 * 
 * 6) Because user interaction is handled on client side all error messages are
 * displayed there. This is not public Api, it's just meant to work with our
 * website client. In case of error these messages are displayed in browser by
 * ember, or even ember doesn't allow you to make request which would fail (like
 * deleting record which you have no access). So if this happens on Api side and
 * somebody tries on purpose break Api then you don't owe him appology. No
 * redirection, no error explanations. The methods now are littlied just to
 * request processing and do not waste time with all the other "fuzz". On some
 * parts even no error checking wasn't required, if logged user is needed,
 * session will return null and other actions will just fail.
 * 
 * Thankful these factors I managed to squeeze all methods into one class and
 * still it is pretty well organized.
 * 
 * @author Anton Krug
 * 
 */
public class Api extends Controller {

  public static void index() {
    render();
  }

  /********** members, member listings *********/

  public static void members(String sid) {
    Logger.info("API members sid=" + sid);

    User user = Logged.getUser(sid);
    List<User> users = User.findAll();

    // remove myself
    users.remove(user);

    // tag friends
    for (User u : users) {
      if (user.friends.contains(u)) u.isFriend = true;

      // information permissions, chceked on server side and I went double
      // measures and did client side as well, on client if it gets the
      // information
      // he shoudn't know at least it will not display it
      if (!u.showOnline) u.online = false;
    }

    // sort alphabeticaly by name
//    Collections.sort(users, User.Comparators.NAME);
//    Collections.sort(users,new UserComparator());
    renderText(Parse.usersSend(users));
  }

  public static void member(Long id) {
    renderText(Parse.userSend(User.findById(id)));
  }

  public static void myProfile(String sid) {
    renderText(Parse.userSend(Logged.getUser(sid)));
  }

  /********** friendships *********/

  public static void friendsAdd(Long id, String sid) {
    User user = Logged.getUser(sid);
    User friend = User.findById(id);
    user.friends.add(friend);
    user.save();
    members(sid);
  }

  public static void friendsRemove(Long id, String sid) {
    User user = Logged.getUser(sid);
    User friend = User.findById(id);
    user.friends.remove(friend);
    user.save();
    members(sid);
  }

  /********* images ***********/

  public static void avatar(Long id) {
    User user = User.findById(id);
    Blob avatar = user.avatar;
    if (avatar.exists()) {
      response.setContentTypeIfNotSet(avatar.type());
      renderBinary(avatar.get());
    }
  }

  public static void bgImage(Long id) {
    Blog blog = Blog.findById(id);
    Blob image = blog.bgImage;
    // Logger.info("Background for "+id+" "+image);
    if (image.exists()) {
      response.setContentTypeIfNotSet(image.type());
      renderBinary(image.get());
    }
  }

  public static void uploadBg(Long id, String sid, Blob picture) {
    User user = Logged.getUser(sid);
    Blog blog = Blog.findById(id);
    if (blog.user.id == user.id) {
      blog.bgImage = picture;
      blog.save();
      Logger.info("saving blog picture");
    }
    Api.index();
  }

  /**************** CRUD for blog,post comment *************** */

  /******************* list **************************/

  public static void blogs(String sid) {
    User user = Logged.getUser(sid);

    List<Blog> blogy = Blog.findAll();
    for (Blog blog : blogy) {

      // checking ownership on mine objects
      if (blog.user.id == user.id) {
        blog.isMine = true;
      }

      // checking ownership if there are in the rest
      for (Post post : blog.posts) {
        for (Comment comment : post.comments) {
          if (comment.user.id == user.id) comment.isMine = true;
        }
      }

    }
    // Logger.info("Blogs " + Parse.blogsSend(blogy));
    renderText(Parse.blogsSend(blogy));
  }

  public static void blog(Long id, String sid) {
    User user = Logged.getUser(sid);

    Blog blog = Blog.findById(id);

    if (blog.user.id == user.id) {
      blog.isMine = true;
    }

    // checking ownership if there are none in the rest
    for (Post post : blog.posts) {
      for (Comment comment : post.comments) {
        if (comment.user.id == user.id) comment.isMine = true;
      }
    }

    // Logger.info("Blog " + id + ": " + Parse.blogsSend(Blog.findAll()));
    renderText(Parse.blogSend(blog));
  }

  public static void post(Long id, String sid) {
    Logger.info("Blogs " + Parse.postSend(Post.findById(id)));
    renderText(Parse.postSend(Post.findById(id)));
  }

  /******************* add **************************/

  public static void addBlog(String title, String sid) {
    User user = Logged.getUser(sid);
    Blog blog = new Blog(user, title, true, 5, true, false);
    blog.save();
    user.blogs.add(blog);
    user.save();
    // renderText("");
    Api.blogs(sid);
  }

  public static void addPost(Long id, String title, String contentText, boolean allowComments, String sid) {
    User user = Logged.getUser(sid);
    Blog blog = Blog.findById(id);
    if (blog.user.id == user.id) {
      Post post = new Post(blog, title, contentText);
      post.allowComments = allowComments;
      post.save();

      blog.posts.add(post);
      blog.save();

      Logger.info("Added post " + contentText);
      Api.blog(blog.id, sid);
    } else {
      renderText("");
    }
  }

  public static void addComment(Long id, String title, String contentText, String sid) {
    User user = Logged.getUser(sid);
    Post post = Post.findById(id);
    if (post.allowComments) {
      Comment comment = new Comment(post, user, title, contentText);
      comment.save();
      post.comments.add(comment);
      post.save();

      Logger.info("Added comment " + contentText);
      Api.blog(post.blog.id, sid);
    } else {
      renderText("");
    }
  }

  /******************* update **************************/

  public static void updateBlog(Long id, String title, String sid) {
    User user = Logged.getUser(sid);
    Blog blog = Blog.findById(id);
    if (blog.user.id == user.id) {
      blog.title = title;
      blog.save();
      Api.blog(blog.id, sid);
    } else {
      renderText("");
    }
  }

  public static void updatePost(Long id, String title, String contentText, boolean allowComments, String sid) {
    User user = Logged.getUser(sid);
    Post post = Post.findById(id);
    if (post.blog.user.id == user.id) {
      post.title = title;
      post.contentText = contentText;
      post.allowComments = allowComments;
      post.save();
      Logger.info("Post content" + contentText);
      Api.blog(post.blog.id, sid);
    } else {
      renderText("");
    }
  }

  /******************* remove **************************/

  public static void removeBlog(Long id, String sid) {
    User user = Logged.getUser(sid);
    Blog blog = Blog.findById(id);

    if (blog.user.id == user.id) {

      // remove it from subscribtion from all users
      List<User> users = User.findAll();
      for (User anyUser : users) {
        if (anyUser.subs.contains(blog)) {
          anyUser.subs.remove(blog);
          anyUser.save();
        }
      }

      blog.delete();
      renderText("{}");
      // Api.blog(blog_id, sid);
    } else {
      renderText("");
    }
  }

  public static void removePost(Long id, String sid) {
    User user = Logged.getUser(sid);
    Post post = Post.findById(id);

    if (post.blog.user.id == user.id) {
      Long blog_id = post.blog.id;
      post.delete();
      Api.blog(blog_id, sid);
    } else {
      renderText("");
    }
  }

  public static void removeComment(Long id, String sid) {
    User user = Logged.getUser(sid);
    Comment comment = Comment.findById(id);

    // only owner of the comment or owner of the blog is allowed to delete the
    // comment
    if (comment.user.id == user.id || comment.post.blog.user.id == user.id) {
      Long blog_id = comment.post.blog.id;
      comment.delete();
      Api.blog(blog_id, sid);
    } else {
      renderText("");
    }
  }

}

/**
 * *********** junk and leftovers *******************
 * 
 * 
 * // verbose debug informations
 * 
 * // Logger.info("blogs session "+sid+" user "+user.name); //
 * Logger.info("json sending "+Parse.blogsSend(Post.findAll())); //
 * Logger.info("First post:",Post.find("id",1L).first()); //
 * Logger.info(Parse.blogsSend(Post.find("id",1L).first())); //
 * renderJSON(Post.findAll()); // List<Post> posts = blog.posts;
 * 
 * // debug lines, helping to ease parsing and serialization:
 * 
 * // Logger.info("Posts inside "+blog.title+" blog : "+ blog.posts.size()); //
 * blog.user=null; // blog.posts = null; // blog.bgImage=null;
 * 
 * 
 * // Logger.info("show blog "+id+" "+Post.find("id", id).first()); //
 * Logger.info("all: "+Post.findAll());
 * 
 * //remove it from listed blogs of the owner // user.blogs.remove(blog); //
 * user.save();
 * 
 * 
 * 
 * ********** Desperate attemnts to delete that cursed blog
 * 
 * public static void removeBlog(Long id,String sid) { User user =
 * Logged.getUser(sid); Blog blog = Blog.findById(id);
 * 
 * // Iterator<Post> it = blog.posts.iterator(); // while (it.hasNext()) { //
 * Post post = it.next(); // // Logger.info("Post "+ post.title ); //
 * blog.posts.remove(post); // }
 * 
 * // blog.user=null; // blog.posts=null; blog.save();
 * 
 * user.blogs.remove(blog); user.save();
 * 
 * Logger.info("Blog " + id + ": " + Parse.blogSend(blog)); blog.delete();
 * 
 * renderText("{}");
 * 
 * if (blog.user.id == user.id) {
 * 
 * // for (Post post:blog.posts) { // post.blog=null; // post.save(); //
 * post.delete(); // } // // blog.posts.clear(); // blog.save();
 * 
 * user.blogs.remove(blog); List<User> users = User.findAll(); for (User
 * anyUser:users) { if (anyUser.subs.contains(blog)) {
 * anyUser.subs.remove(blog); anyUser.save(); } }
 * 
 * 
 * // Iterator<Post> it = blog.posts.iterator(); // while (it.hasNext()) { //
 * Post post = it.next(); // for (Comment comment:post.comments) { //
 * post.comments.remove(comment); // post.save(); // comment.delete(); // } //
 * //blog.posts.remove(post); // post.delete(); // } // blog.save();
 * 
 * // blog.user.blogs.remove(blog); // blog.user.save(); blog.posts=null;
 * blog.save(); blog.delete(); renderText("{}"); //Api.blog(blog_id, sid); }
 * else { renderText(""); } }
 */

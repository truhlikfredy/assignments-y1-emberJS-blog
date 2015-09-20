package utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import models.*;

public class Parse {
  
  public static JSONSerializer serializer = new JSONSerializer().exclude("class").exclude("persistent").exclude("entityId").exclude("*.class").exclude("*.*.file").exclude("*.avatar").exclude("*.password").exclude("password");
  public static JSONSerializer serBlogs = new JSONSerializer().include("posts").include("*.*.comments").exclude("*.class").exclude("*.entityId").exclude("*.persistent").exclude("*.password").exclude("class").exclude("bgImage").exclude("persistent").exclude("entityId").exclude("*.avatar").exclude("*.lastActivity");
//  public static JSONSerializer serializer = new JSONSerializer().include("title").exclude("*");

  /*
   * public static JSONSerializer userSerializer = new
   * JSONSerializer().exclude("class").exclude("persistent").exclude("entityId")
   * .include("donations"); public static JSONSerializer donationSerializer =
   * new
   * JSONSerializer().exclude("class").exclude("persistent").exclude("entityId"
   * );
   */

  // user
  public static User userGet(String json) {
    return new JSONDeserializer<User>().deserialize(json, User.class);
  }

  public static String userSend(Object obj) {
    return serializer.serialize(obj);
  }

  // users
  public static List<User> usersGet(String json) {   
    return new JSONDeserializer<ArrayList<User>>().use("values", User.class).deserialize(json);
  }

  public static String usersSend(Object obj) {
    // return "{\"users\":" + userSerializer.serialize(obj) + "}";
    return serializer.serialize(obj);
  }

  // blog
  public static Blog blogGet(String json) {
    return new JSONDeserializer<Blog>().deserialize(json, User.class);
  }

  public static String blogSend(Object obj) {
    return serBlogs.serialize(obj);
  }

  // blogs
  public static List<Blog> blogsGet(String json) {
    return new JSONDeserializer<ArrayList<Blog>>().use("values", User.class).deserialize(json);
  }

  public static String blogsSend(Object obj) {
//    JSONSerializer ser = new JSONSerializer().include("title").include("embeddedPosts").include("embeddedPostsPerPage").include("bgImage").include("visibleMembers").include("visiblePublic").exclude("*");
//    JSONSerializer ser = new JSONSerializer().exclude("posts.*").exclude("*.user").exclude("user.*");
//    return "{\"blogs\":" + ser.serialize(obj) + "}";

//    JSONSerializer ser = new JSONSerializer().include("user.id").include("id").include("title").include("embeddedPosts").include("embeddedPostsPerPage").include("bgImage").include("visibleMembers").include("visiblePublic").exclude("*");
    return serBlogs.serialize(obj);
  }
  
  //post
  public static Post postGet(String json) {
    return new JSONDeserializer<Post>().deserialize(json, User.class);
  }

  public static String postSend(Object obj) {
    return serializer.serialize(obj);
  }
  
  // posts
  public static List<Post> postsGet(String json) {
    return new JSONDeserializer<ArrayList<Post>>().use("values", User.class).deserialize(json);
  }
  
  public static String postsSend(Object obj) {
    // return "{\"users\":" + userSerializer.serialize(obj) + "}";
    return serializer.serialize(obj);
  }

  //comment
  public static Comment commentGet(String json) {
    return new JSONDeserializer<Comment>().deserialize(json, User.class);
  }
  
  public static String commentSend(Object obj) {
    return serializer.serialize(obj);
  }
  
  // comments
  public static List<Comment> commentsGet(String json) {
    return new JSONDeserializer<ArrayList<Comment>>().use("values", User.class).deserialize(json);
  }
  
  public static String commentsSend(Object obj) {
    // return "{\"users\":" + userSerializer.serialize(obj) + "}";
    return serializer.serialize(obj);
  }  

}

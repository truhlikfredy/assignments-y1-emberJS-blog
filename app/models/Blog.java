package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import java.util.ArrayList;
import java.util.List;

import play.db.jpa.Model;
import play.db.jpa.Blob;
import play.Logger;

@Entity
public class Blog extends Model {
  
  @ManyToOne
  public User       user;
  public String     title;
  public boolean    embeddedPosts=true;
  public int        embeddedPostsPerPage=5;
  public Blob       bgImage;
  public boolean    visibleMembers=true;
  public boolean    visiblePublic=true;

//  @ManyToMany(mappedBy = "blog", cascade = CascadeType.ALL)
  @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
  public List<Post> posts = new ArrayList<Post>();
  
  //not meant as for storage, more as helper object for ember
  public boolean    isMine=false;
  

  public Blog(User user, String title, boolean embeddedPosts, int embeddedPostsPerPage, boolean visibleMembers, boolean visiblePublic) {
    this.user = user;
    this.title = title;
    this.embeddedPosts=embeddedPosts;
    this.embeddedPostsPerPage=embeddedPostsPerPage;
    this.visibleMembers=visibleMembers;
    this.visiblePublic=visiblePublic;
  }
  
  public static List<Blog> userBlogs(User user) {
    return Blog.find("user",user).fetch();
  }
  
  public String toString() {
    return this.title;
  }

}

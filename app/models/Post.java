package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import play.Logger;
import play.db.jpa.Model;

@Entity
public class Post extends Model {

  @ManyToOne
  public Blog          blog;

  public String        title;

  @Lob
  public String        contentText;

  public Long          timeShow;
  public Long          timeSort;
  public boolean       allowComments;
  public boolean       streamPost;
  public boolean       visible;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  public List<Comment> comments = new ArrayList<Comment>();
  
  public Post(Blog blog, String title, String contentText) {
    this.blog = blog;
    this.title = title;
    this.contentText = contentText;
    this.timeShow = System.currentTimeMillis();

    // if we want later add feature allowing: show different date and be in
    // different position in hierarchy. allowing edit this values independly can
    // allow sticky notes etc...
    this.timeSort = this.timeShow;
  }

  public String toString() {
    return title;
  }
}

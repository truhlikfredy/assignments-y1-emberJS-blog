package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import play.Logger;
import play.db.jpa.Model;

@Entity
public class Comment extends Model {
  
  @ManyToOne()
  public Post  post;

  @ManyToOne()
  public User   user;
  
  public String title;
  
  @Lob
  public String contentText;
  
  public Long   time;

  //not meant as for storage, more as helper object for ember
  public boolean    isMine;
  
  public Comment(Post post, User user, String title, String contentText) {
    this.post=post;
    this.user=user;
    this.title = title;
    this.contentText = contentText;
    this.time=System.currentTimeMillis();
  }

  public String toString() {
    return contentText;
  }
}

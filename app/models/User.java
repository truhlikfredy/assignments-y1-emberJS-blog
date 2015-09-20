package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.Lob;
import javax.persistence.OneToMany;


//import controllers.Users;
import play.Logger;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class User extends Model {

  @OneToMany(mappedBy="user", cascade=CascadeType.ALL)
  public List<Blog> blogs = new ArrayList<Blog>();

  @ManyToMany
  public List<Blog> subs = new ArrayList<Blog>();

  @ManyToMany
  public List<User> friends = new ArrayList<User>();
  
  public String     name;
  public String     email;
  public String     password;

  public String     status;
  public Blob       avatar;
  public boolean    online = false;
  public boolean    showOnline = true;
  public Long       lastActivity = 0L;

  //not meant as for storage, more as helper object for ember
  public boolean    isFriend = false;
  
  public User(String name, String email, String password, boolean showOnline) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.online = false;
    this.showOnline = showOnline;
    this.lastActivity = System.currentTimeMillis();
  }

  public String toString() {
    return name;
  }

  public static User findByEmail(String email) {
    return find("email", email).first();
  }

  public boolean checkPassword(String password) {
    return this.password.equals(password);
  }

  public void subAdd(Blog add) {
    if (subs.contains(add)) {
      Logger.info("Using old link or breaking website. Trying to create duplicates.");
    } else {
      subs.add(add);
      save();
    }
  }

  public void subRemove(Blog remove) {
    subs.remove(remove);
    save();
  }  

  public void friendsAdd(User add) {
    if (friends.contains(add)) {
      Logger.info("Using old link or breaking website. Trying to create duplicates.");
    } else {
      friends.add(add);
      save();
    }
  }
  
  public void friendsRemove(User remove) {
    friends.remove(remove);
    save();
  }  
  
//  public static class Comparators {
//     public static Comparator<User> NAME = new Comparator<User>() {
//        @Override
//        public int compare(User o1,User o2){
//          return o1.name.compareTo(o2.name);
//        }
//    };
//  }
}

package models;

import java.util.UUID;
import java.util.List;

import javax.persistence.*;

import com.jamonapi.utils.Logger;

import play.db.jpa.Model;

/*
 * More user friendly implementation of sessions?
 * 
 * Time will tell. But it has 2 nice features:
 * 
 *  1) Can get session information by suplying my own session ID by any means I like,
 *     doesn't have to be with cookies.
 * 
 *  2) Returning full object of user which is ready to be used. 
 *     (less lines in code which is using these sessions) 
 * 
 */

@Entity
public class Logged extends Model {
  public String session;
  @OneToOne
  public User   user;

  public Logged(User user) {
    this.session = UUID.randomUUID().toString();
    this.user = user;
  }

  public String toString() {
    return session;
  }

  public static Logged getSession(String session) {
    return Logged.find("session", session).first();
  }

  public static User getUser(String session) {
    if (Logged.getSession(session) == null) {
      Logger.log("session " + session + " gone.");
      return null;
    } else {
      return Logged.getSession(session).user;
    }
  }
  
  public static void cleanSessions() {
    List<Logged> all = Logged.findAll();
    for (Logged item: all) {
      item.delete();
    }
  }

}

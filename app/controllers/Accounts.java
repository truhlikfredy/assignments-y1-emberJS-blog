package controllers;

import play.*;
import play.db.jpa.Blob;
import play.mvc.*;

import java.util.*;

import models.User;
import models.Logged;
import utils.Helpers;

public class Accounts extends Controller {
  /* helpers */

  public static String getLoggedUserIdRaw() {
    return (session.get("logged_in_userid"));
  }

  public static void clearSessionAndShowIndex() {
    session.clear();
//    accountsIndex();
  }

  /* regular controller methods */


  public static void register(String name, String email, String password, boolean showonline) {
    //email not case sensitive
//    profile_email = profile_email.toLowerCase();

    Logger.info(name + " " + email + " " + " " + password);

    User user = new User(name, email, password, showonline);
    // no duplicates by my own email is allowed to use again
    if (User.findByEmail(email)!=null && name.length()>0  && email.length()>0) {
      user.save();
      Logged logged = new Logged(user).save();
      Logger.info("User registered");
      renderText(logged);
    } else {
      Logger.info("Some other inputs are not filled in properly. Not registering.");
      renderText("");
    }
  }

  public static void update(String sid, String name, String email, String status, boolean showonline) {
    
    Logger.info("Updating profile :" + sid + " "+ name + " " + email + " " + status );
    
    if (status.length()>0 && name.length()>0  && email.length()>0) {
      User user = Logged.getUser(sid);
      user.name=name;
      user.email=email;
      user.status=status;
      user.showOnline=showonline;
      user.save();

      Logger.info("User updated");
      renderText(user);
    } else {
      Logger.info("Some other inputs are not filled in properly. Not updating.");
      renderText("");
    }
  }

  public static void login(String email, String password) {
    // email is case insensitive
    email = email.toLowerCase();
    Logger.info("Attempting to authenticate with " + email + ":" + password);

    User user = User.findByEmail(email);
    if ((user != null) && (user.checkPassword(password) == true)) {
      Logger.info("Authentication successful");
      Logged logged = new Logged(user).save();
      
      user.online = true;
      user.save();
//      MyHome.myHomeIndex();
      Logger.info("Created session id:"+logged);
      renderText(logged);
    } else {
      Logger.info("Authentication failed");
//      accountsLogin();
      renderText("");
    }
  }

  public static void logout(String session) {
    Logger.info("Logout session id:"+session);
    if (session.length()>0) {
      User user = Logged.getUser(session);
      user.online = false;
      user.save();
      Logged logged = Logged.getSession(session);
      logged.delete();
    } else {
      Logger.info("No session yet, we can continue.");
    }
    renderText("");
  }
  
  public static void uploadAvatar(String sid, Blob picture) {
      User user = Logged.getUser(sid);   
      user.avatar = picture;
      user.save();
      Logger.info("saving picture");
      Api.index();
  }
  

}
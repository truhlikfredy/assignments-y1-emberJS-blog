package utils;

import models.Logged;
import models.User;
import play.Logger;
import controllers.Accounts;
import controllers.Api;
import models.*;
import play.db.DB;

import java.util.*;

import javax.mail.Session;

public class Helpers {

  public static long getLoggedUserId() {
    return (Long.parseLong(Accounts.getLoggedUserIdRaw()));
  }

  public static void timeoutCheck() {
    List<User> users = User.findAll();

    for (User user : users) {
      // timeout set for 1 hour
      if (user.online && ((user.lastActivity + (60L * 60L * 1000L)) < System.currentTimeMillis())) {
        user.online = false;
        user.save();
        Logger.info("User " + user.id + " (" + user.name + ") got online timeout.");
      }
    }
  }

  public static User getLoggedUser() {
    String userId = Accounts.getLoggedUserIdRaw();
    if (userId != null) {
      User user = User.findById(Long.parseLong(userId));

      if (user.online) {
        // user.lastActivity = System.currentTimeMillis() / 1000L;
        user.lastActivity = System.currentTimeMillis();
        user.save();

        timeoutCheck();

        return (user);
      } else {
        /*
         * here we can have 2 behaviours timeouted user be forced to relogin and
         * redirected without any clue what is happening or have light timeout
         * and heavy timeout, light timeout would consider you offline but when
         * you get back you would still be able to use the webpage and hard
         * timeout which would signout you as well.
         */
        /*
         * // this one will log you out every single time
         * Logger.info("Timeout user "
         * +user.id+" ( "+user.firstName+" "+user.lastName
         * +" ) tried to use the webpage"); Accounts.clearSessionAndShowIndex();
         */
        // this will allow you use it after light timeout and reactivate your
        // status as online
        user.online = true;
        user.lastActivity = System.currentTimeMillis();
        user.save();

        timeoutCheck();
        return (user);

      }
    } else {
      // Accounts.accountsLogin();
    }
    // just to avoid eclipse warrnings because some if statements won't return
    return (null);
  }

  public static boolean notLoggedIn() {
    String userId = Accounts.getLoggedUserIdRaw();
    if (userId == null) {
      return (true);
    } else {
      return (false);
    }
  }

  public static void redirectIfNotLoggedIn(String sid) {
    User user = Logged.getUser(sid);
    if (user==null) Api.index();
  }

  public static boolean fieldsCorrect(User user) {
    if (user.name.isEmpty() || user.email.isEmpty() || !user.email.contains("@") || user.password.isEmpty()) {
      return (false);
    } else {
      return (true);
    }
  }

  public static void fillData() {
    // I know not the most elegant way, but at least I'm sure what ID is
    // what and no monkey bussiness behind my back. And even less lines to write
    // than entities or data.yml bootstraping. It's not flexible but easier to
    // fill richers data with full control.

    // put autoincrement into its place where it belongs, but cloudbees doesn't like it
    
    DB.execute("alter table blog alter column id restart with 1;");
    DB.execute("alter table comment alter column id restart with 1;");
    DB.execute("alter table post alter column id restart with 1;");
    DB.execute("alter table user alter column id restart with 1;");
    DB.execute("alter table logged alter column id restart with 1;");
    
    //cloudbees friendly version
    /*
    DB.execute("alter table blog AUTO_INCREMENT = 1;");
    DB.execute("alter table comment AUTO_INCREMENT = 1;");
    DB.execute("alter table post AUTO_INCREMENT = 1;");
    DB.execute("alter table user AUTO_INCREMENT = 1;");
    DB.execute("alter table logged AUTO_INCREMENT = 1;");
    */

    // 1 water white
    DB.execute("insert into user set name='Walter White', email='walter@white.com', password='empirebussiness',status='SAY MY NAME!', avatar='avatar_1.jpg|image/jpeg', showOnline=0, online=0, isFriend=0;");

    DB.execute("insert into blog set title='Mr White Blog', user_id=1, visiblemembers=1, visiblepublic=1, embeddedposts=0, embeddedpostsperpage=5, isMine = 0;");
    DB.execute("insert into post set blog_id=1, visible=1, streampost=1, allowcomments=1, timeShow=1398555822403, timeSort=1398555822403, title='Health and family', contenttext='Is very britle thing.';");
    DB.execute("insert into post set blog_id=1, visible=1, streampost=1, allowcomments=1, timeShow=1398545822403, timeSort=1398545822403, title='How to secure your family', contenttext='You will work hard in lucrative bussines. That will do.';");

    DB.execute("insert into blog set title='Heinserberg', user_id=1, visiblemembers=1, visiblepublic=0, embeddedposts=1, embeddedpostsperpage=5, bgImage='blog_2.jpg|image/jpeg', isMine = 0;");
    DB.execute("insert into post set blog_id=2, visible=1, streampost=0, allowcomments=0, timeShow=1398807025900, timeSort=1398807025900, title='How to cook', contenttext='You will first preheat the oven **@ 200C** and then let it bake for 17minutes on ***greased paper***.';");
    DB.execute("insert into post set blog_id=2, visible=1, streampost=1, allowcomments=1, timeShow=1398545922403, timeSort=1398545922403, title='What bussiness to choose', contenttext='This is easy one, **EMPIRE** bussiness.\n\n### Conclusion \n\n You always go for what? **Empire** bussiness! ***You''re goddam right.*** ';");
    DB.execute("insert into post set blog_id=2, visible=1, streampost=1, allowcomments=0, timeShow=1380545922403, timeSort=1380545922403, title='Who is this blog aimed to', contenttext='The standard chunk of Lorem Ipsum used since the **1500s** is reproduced below for those interested.\n\n###Sections\n\n Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the **1914** translation by H. Rackham.';");

    // 2 Skyler White
    DB.execute("insert into user set name='Skyler White', email='skyler@white.com', password='hope',status='Family first', avatar='avatar_2.jpg|image/jpeg', showOnline=1, online=1, isFriend=0;");

    DB.execute("insert into blog set title='CarWash News', user_id=2, visiblemembers=1, visiblepublic=1, bgImage='blog_3.jpg|image/jpeg', embeddedposts=0, embeddedpostsperpage=5, isMine = 0;");
    DB.execute("insert into post set blog_id=3, visible=1, streampost=1, allowcomments=0, timeShow=1393555822403, timeSort=1393555822403, title='Deals', contenttext='We offer for limited time period discounts.';");

    DB.execute("insert into blog set title='Household troubles', user_id=2, visiblemembers=1, visiblepublic=0, bgImage='blog_4.jpg|image/jpeg', embeddedposts=0, embeddedpostsperpage=5, isMine = 0;");
    DB.execute("insert into post set blog_id=4, visible=1, streampost=1, allowcomments=1, timeShow=1397545922403, timeSort=1397545922403, title='How to keep family together', contenttext='You can''t. Tried it. Nope. Not gonna happen';");
    DB.execute("insert into post set blog_id=4, visible=1, streampost=1, allowcomments=1, timeShow=1398545922403, timeSort=1398545922403, title='How to cook', contenttext='Have a look at my husbans [blog]. He is explaining it in ***exhausing detail***.\n\n [blog]:/#/blogs/2\n';");

    // this session will be logged in as skyler from the app start, easier for
    // debuging, no anoying loggining in etc...
    // remove on production version and remove it from ember javascripts as
    // well.
    DB.execute("insert into logged set user_id=2, session='111111-2222-333-44-5';");

    // 3 Jesse Pinkman
    DB.execute("insert into user set name='Jesse Pinkman', email='jesse@aol.com', password='science',status='THECAPN', avatar='avatar_3.jpg|image/jpeg', showOnline=1, online=1, isFriend=0;");

    // 4 Hank Schrader
    DB.execute("insert into user set name='Hank Schrader', email='hank@asac.mechico.gov', password='marie',status='Do what you''re gonna do.', avatar='avatar_4.jpg|image/jpeg', showOnline=1, online=1, isFriend=0;");

    // 5 Gustavo Fring
    DB.execute("insert into user set name='Gustavo Fring', email='gus@polos.net', password='LosPollosHermanos',status='Never make the same mistake twice.', avatar='avatar_5.jpg|image/jpeg', showOnline=0, online=1, isFriend=0;");
    DB.execute("insert into blog set title='Los Pollos Hermanos', embeddedposts=1, embeddedpostsperpage=5, user_id=5, visiblemembers=1, visiblepublic=1, bgImage='blog_5.jpg|image/jpeg', isMine = 0;");
    DB.execute("insert into post set blog_id=5, visible=1, streampost=1, allowcomments=0, timeShow=1392545922403, timeSort=1392545922403, title='Caballero del Febo', contenttext='En resolución, él se enfrascó tanto en su **letura**, que se le pasaban las noches leyendo de claro en claro, y los días de **turbio** en turbio';");
    DB.execute("insert into post set blog_id=5, visible=1, streampost=1, allowcomments=0, timeShow=1390545922403, timeSort=1390545922403, title='Ei has ***dico*** dolorum', contenttext='Mel atqui labores voluptua ei, pri ut ferri principes consequat. Est ne aliquid salutandi, intellegat intellegam duo ea. An mel putent delenit consectetuer. Id tantas deserunt inciderint eos. At eos idque nominavi pertinax, est ea altera laoreet, an appareat theophrastus pro. Ea eam ipsum albucius. Ex nec meis mentitum abhorreant, te etiam saperet accusam qui, cu putent lobortis delicata mei.';");
    DB.execute("insert into post set blog_id=5, visible=1, streampost=1, allowcomments=1, timeShow=1380545922403, timeSort=1380545922403, title='***dico*** dico ***dico***', contenttext='Est ne aliquid salutandi, intellegat intellegam duo ea. An mel putent delenit consectetuer. Id tantas deserunt inciderint eos. At eos idque nominavi pertinax, est ea altera laoreet, an appareat theophrastus pro. Ea eam ipsum albucius. Ex nec meis mentitum abhorreant, te etiam saperet accusam qui, cu putent lobortis delicata mei.\n\n###Caramba\n\n Mel atqui labores voluptua ei, pri ut ferri principes consequat. Est ne aliquid salutandi, intellegat intellegam duo ea. An mel putent delenit consectetuer. Id tantas deserunt inciderint eos. At eos idque nominavi pertinax, est ea altera laoreet, an appareat theophrastus pro. Ea eam ipsum albucius. Ex nec meis mentitum abhorreant, te etiam saperet accusam qui, cu putent lobortis delicata mei.';");

    // 6 Mike Ehrmantraut
    DB.execute("insert into user set name='Mike Ehrmantraut', email='mike@hotmail.net', password='kaylee',status='Thanks, but no thanks.', avatar='avatar_6.jpg|image/jpeg', showOnline=1, online=0, isFriend=0;");

    // 7 Saul Goodman McGill
    DB.execute("insert into user set name='Saul Goodman McGill', email='saul@goodman.org', password='betterCallMe',status='Faith and begorrah! A fellow potato eater!', avatar='avatar_7.jpg|image/jpeg', showOnline=1, online=1, isFriend=0;");

    // 8 Gale Boetticher
    DB.execute("insert into user set name='Gale Boetticher', email='gale@gmail.com', password='q''pla',status='I can guarantee you a purity of ninety-six percent', avatar='avatar_8.jpg|image/jpeg', showOnline=1, online=0, isFriend=0;");

    // 9 Tuco Salamanca
    DB.execute("insert into user set name='Tuco Salamanca', email='tuco@new-mechico-mail.com', password='booyah',status='Blue, yellow, pink, whatever man', avatar='avatar_9.jpg|image/jpeg', showOnline=1, online=0, isFriend=0;");

    // friendships
//    DB.execute("insert into user_user set user_id=1, friends_id=2;");
//    DB.execute("insert into user_user set user_id=1, friends_id=3;");
//    DB.execute("insert into user_user set user_id=1, friends_id=4;");
//    DB.execute("insert into user_user set user_id=1, friends_id=7;");
//    DB.execute("insert into user_user set user_id=1, friends_id=8;");

    //some subscription data ready if we want implement subscriptions
//    DB.execute("insert into user_blog set user_id=1, subs_id=2;");
//    DB.execute("insert into user_blog set user_id=1, subs_id=3;");
//    DB.execute("insert into user_blog set user_id=1, subs_id=4;");
//    DB.execute("insert into user_blog set user_id=1, subs_id=5;");

    DB.execute("insert into user_user set user_id=2, friends_id=1;");
    DB.execute("insert into user_user set user_id=2, friends_id=4;");

//    DB.execute("insert into user_blog set user_id=2, subs_id=1;");
//    DB.execute("insert into user_blog set user_id=2, subs_id=4;");
//    DB.execute("insert into user_blog set user_id=2, subs_id=5;");

//    DB.execute("insert into user_user set user_id=3, friends_id=1;");
//    DB.execute("insert into user_user set user_id=3, friends_id=7;");
//
//    DB.execute("insert into user_user set user_id=4, friends_id=1;");
//
//    DB.execute("insert into user_user set user_id=5, friends_id=6;");
//    DB.execute("insert into user_user set user_id=5, friends_id=8;");
//
//    DB.execute("insert into user_user set user_id=6, friends_id=1;");
//    DB.execute("insert into user_user set user_id=6, friends_id=3;");
//    DB.execute("insert into user_user set user_id=6, friends_id=5;");
//    DB.execute("insert into user_user set user_id=6, friends_id=7;");
//
//    DB.execute("insert into user_user set user_id=7, friends_id=1;");
//    DB.execute("insert into user_user set user_id=7, friends_id=2;");
//    DB.execute("insert into user_user set user_id=7, friends_id=3;");
//    DB.execute("insert into user_user set user_id=7, friends_id=6;");
//
//    DB.execute("insert into user_user set user_id=8, friends_id=1;");
//    DB.execute("insert into user_user set user_id=8, friends_id=5;");
    // tuco has no friends :) he is alone

    DB.execute("insert into comment set user_id=3, post_id=4, time=1396810424091, isMine = 0, title='Yo Mr White', contentText='Yes. **Science**!';");
    DB.execute("insert into comment set user_id=1, post_id=4, time=1398810424091, isMine = 0, title='Chemistry', contentText='**No** Jesse. Knowledge.';");
    DB.execute("insert into comment set user_id=2, post_id=4, time=1399910424091, isMine = 0, title='Explanation', contentText='What is going on **here**?';");

    DB.execute("insert into comment set user_id=1, post_id=7, time=1398810424091, isMine = 0, title='With love', contentText='**No**! You have to try more. I command **YOU**!';");
    

  }
}

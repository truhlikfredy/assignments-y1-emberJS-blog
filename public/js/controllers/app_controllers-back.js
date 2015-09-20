//controllers

//index
App.IndexController = Ember.Controller.extend({
  
  // *** login ***
  loginFailed: false,
  isProcessing: false,
  isSlowConnection: false,
  timeout: null,
 
  login: function() {
    this.setProperties({
      loginFailed: false,
      isProcessing: true
    });

    this.set("timeout", setTimeout(this.slowConnection.bind(this), 5000));
    
    var request = $.post("/login", this.getProperties("email", "password"));
    request.then(this.success.bind(this), this.failure.bind(this));
  },

  
  success: function() {
    this.reset();
    if (event.target.response.length==0) {
      console.log('got empty');
      this.failure();
    } else {
      session=event.target.response;
      console.log(session);
      document.location = "/#/home";
    }    
  },

  failure: function() {
    this.reset();
    console.log("failed");
    this.set("loginFailed", true);
  },

  slowConnection: function() {
    console.log("slow");
    this.set("isSlowConnection", true);
  },

  reset: function() {
    console.log("reset");
    clearTimeout(this.get("timeout"));
    this.setProperties({
      isProcessing: false,
      isSlowConnection: false
    });
  },  
  
});

// *** signup *** 
App.SignupController = Ember.ObjectController.extend({
  signupFailed: false,
  signupisProcessing: false,
  signupisSlowConnection: false,
  signuptimeout: null,

  register: function() {
    this.setProperties({
      signupFailed: false,
      signupisProcessing: true
    });

    this.set("signuptimeout", setTimeout(this.signupSlowConnection.bind(this), 5000));
//    console.log(this);
//    console.log($('#register_email').val());
//    console.log(this.getProperties("register_name", "register_email", "register_password","register_showOnline"));
    var request = $.post("/register", {name: $('#register_name').val(), email: $('#register_email').val(), password: $('#register_password').val(), showonline: $('#register_showonline').val()});
    request.then(this.signupSuccess.bind(this), this.signupFailure.bind(this));
  },

  signupSuccess: function() {
    this.signupReset();
    console.log('signed up response:');
    if (event.target.response.length==0) {
      console.log('got empty');
      this.signupFailure();
    } else {
      session=event.target.response;
      console.log(session);
      document.location = "/#/home";
    }    
  },

  signupFailure: function() {
    this.signupReset();
    this.set("signupFailed", true);
  },

  signupSlowConnection: function() {
    this.set("signupisSlowConnection", true);
  },

  signupReset: function() {
    clearTimeout(this.get("signuptimeout"));
    this.setProperties({
      signupisProcessing: false,
      signupisSlowConnection: false
    });
  }  
});



App.MembersController = Ember.ArrayController.extend({
  content: [],
  addmem: function(obj) {
//    obj.isFriend = true;
    console.log(obj);
    $.getJSON('/api/members/friendsAdd/'+obj.id+'/'+ session, function(data) {
      console.log(data);
//      App.MembersRoute.set('content',data);
    });
  },
  removemem: function(obj) {
//    obj.isFriend = false;
    var that=this;
    
    $.getJSON('/api/members/friendsRemove/'+obj.id+'/'+ session,$.proxy(function(data) {
      console.log(this);
      console.log(this.get('content'));
      this.set('content',data);
      console.log(App.MembersController.content);
    },this));
        
  }
});

App.PostController = Ember.ObjectController.extend({
  isEditing: false,

  edit: function() {
    this.set('isEditing', true);
  },

  doneEditing: function() {
    this.set('isEditing', false);
    this.get('store').commit();
  }
});

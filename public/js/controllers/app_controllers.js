//controllers

//index  ************************************************** login ********************
App.IndexController = Ember.Controller.extend({
  loginFailed : false,
  isProcessing : false,
  isSlowConnection : false,
  timeout : null,

  actions : {
    login : function() {
      this.setProperties({
        loginFailed : false,
        isProcessing : true
      });

      this.set("timeout", setTimeout(this.slowConnection.bind(this), 5000));

      var request = $.post("/login", this.getProperties("email", "password"));
      request.then(this.success.bind(this), this.failure.bind(this));
    }
  },

  success : function() {
    this.reset();
    if (event.target.response.length == 0) {
      console.log('got empty');
      this.failure();
    } else {
      session = event.target.response;
      console.log(session);
      document.location = "/#/home";
    }
  },

  failure : function() {
    this.reset();
    console.log("failed");
    this.set("loginFailed", true);
  },

  slowConnection : function() {
    console.log("slow");
    this.set("isSlowConnection", true);
  },

  reset : function() {
    console.log("reset");
    clearTimeout(this.get("timeout"));
    this.setProperties({
      isProcessing : false,
      isSlowConnection : false
    });
  },

});

// ************************************************** signup ********************
App.SignupController = Ember.ObjectController.extend({
  signupFailed : false,
  signupisProcessing : false,
  signupisSlowConnection : false,
  signuptimeout : null,

  actions : {
    register : function() {
      this.setProperties({
        signupFailed : false,
        signupisProcessing : true
      });

      this.set("signuptimeout", setTimeout(this.signupSlowConnection.bind(this), 5000));
      var request = $.post("/register", {
        name : $('#register_name').val(),
        email : $('#register_email').val(),
        password : $('#register_password').val(),
        showonline : $('#register_showonline').val()
      });
      request.then(this.signupSuccess.bind(this), this.signupFailure.bind(this));
    }
  },

  signupSuccess : function() {
    this.signupReset();
    console.log('signed up response:');
    if (event.target.response.length == 0) {
      console.log('got empty');
      this.signupFailure();
    } else {
      session = event.target.response;
      console.log(session);
      document.location = "/#/home";
    }
  },

  signupFailure : function() {
    this.signupReset();
    this.set("signupFailed", true);
  },

  signupSlowConnection : function() {
    this.set("signupisSlowConnection", true);
  },

  signupReset : function() {
    clearTimeout(this.get("signuptimeout"));
    this.setProperties({
      signupisProcessing : false,
      signupisSlowConnection : false
    });
  }
});

// ************************************************** home ********************
App.HomeController = Ember.ObjectController.extend({
  signupFailed : false,
  signupisProcessing : false,
  signupisSlowConnection : false,
  signuptimeout : null,

  actions : {
    update : function() {
      this.setProperties({
        signupFailed : false,
        signupisProcessing : true
      });

      this.set("signuptimeout", setTimeout(this.signupSlowConnection.bind(this), 5000));
      var request = $.post("/update", {
        sid : session,
        name : $('#profile_name').val(),
        email : $('#profile_email').val(),
        status : $('#profile_status').val(),
        showonline : $('#profile_showonline').val()
      });
      request.then(this.signupSuccess.bind(this), this.signupFailure.bind(this));
    }
  },

  signupSuccess : function() {
    this.signupReset();
    console.log('signed up response:');
    if (event.target.response.length == 0) {
      console.log('got empty');
      this.signupFailure();
    } else {
      console.log(session);
      document.location = "/#/home";
    }
  },

  signupFailure : function() {
    this.signupReset();
    this.set("signupFailed", true);
  },

  signupSlowConnection : function() {
    this.set("signupisSlowConnection", true);
  },

  signupReset : function() {
    clearTimeout(this.get("signuptimeout"));
    this.setProperties({
      signupisProcessing : false,
      signupisSlowConnection : false
    });
  }
});

// ************************************************** Members ********************
App.MembersController = Ember.ArrayController.extend({
  // mailtoContactEmail: function() { console.log('aa'); },

  countfriends : function() {
    var members = this.get('content');
    var count = 0;
    for (var i = 0; i < members.length; i++) {
      if (members[i].isFriend) count++
    }
    return '%@'.fmt(count);
  }.property('content'),

  countmembers : function() {
    var members = this.get('content');
    var count = 0;
    for (var i = 0; i < members.length; i++) {
      if (!members[i].isFriend) count++
    }
    return '%@'.fmt(count);
  }.property('content'),

  actions : {
    addmem : function(obj) {
      $.getJSON('/api/members/friendsAdd/' + obj.id + '/' + session, $.proxy(function(data) {
        this.set('content', data);
      }, this));
    },
    removemem : function(obj) {
      $.getJSON('/api/members/friendsRemove/' + obj.id + '/' + session, $.proxy(function(data) {
        this.set('content', data);
      }, this));
    }
  }
});

// ************************************************** Blogs ********************
App.BlogsController = Ember.ArrayController.extend({
  blogsFailed : false,
  blogsProcessing : false,
  blogsSlowConnection : false,
  blogstimeout : null,

  actions : {
    addblogpost : function() {
      this.setProperties({
        blogsFailed : false,
        blogsProcessing : true
      });

      this.set("signuptimeout", setTimeout(this.blogsSlowConnection.bind(this), 5000));
      var request = $.post("/api/addblog", {
        title : $('#blog_title').val(),
        sid : session
      });
      request.then(this.blogsSuccess.bind(this), this.blogsFailure.bind(this));
    },
    
    addblogget : function(obj) {
      $.getJSON('/api/addBlog/' + encodeURIComponent($('#blog_title').val()) + '/' + session, $.proxy(function(data) {
        this.set('content', data);
      }, this));
    },
    
  },

  blogsSuccess : function() {
    this.blogsReset();
    if (event.target.response.length == 0) {
      console.log('got empty');
      this.blogsFailure();
    } else {
      session = event.target.response;
      console.log(session);
      // document.location = "/#/home";
      // this.get('content').reload();
      this.get('store').commit();
    }
  },

  blogsFailure : function() {
    this.blogsReset();
    this.set("blogsFailed", true);
  },

  blogsSlowConnection : function() {
    this.set("blogsSlowConnection", true);
  },

  blogsReset : function() {
    clearTimeout(this.get("blogstimeout"));
    this.setProperties({
      blogsProcessing : false,
      blogsSlowConnection : false
    });
  }
});

// ************************************************** Blog ********************
App.BlogController = Ember.ObjectController.extend({
  isEditing : false,
  isEditingBlog : false,
  color : 'ffeeaa',
  style : function() {
    return 'background-color:%@%@'.fmt('#', this.get('color'));
  }.property('color'),

  bgstyle : function() {
    // console.log('changin background');
    return 'background-image: url(/api/bgImage/%@)'.fmt(this.get('content.id'));
  }.property('content.id'),

  actions : {

    addpost : function(obj) {
      $.getJSON('/api/addPost/' + encodeURIComponent(obj.id) + '/' + encodeURIComponent(obj.posttitle) + '/'
          + encodeURIComponent(obj.postcontenttext) + '/' + obj.postallowcomments + '/' + session, $.proxy(function(data) {
        this.set('content', data);
      }, this));
    },

    addcomment : function(obj) {
      $.getJSON('/api/addComment/' + encodeURIComponent(obj.id) + '/' + encodeURIComponent(obj.commenttitle) + '/'
          + encodeURIComponent(obj.commentbody) + '/' + session, $.proxy(function(data) {
            this.set('content', data);
          }, this));
    },

    removecomment : function(obj) {
      $.getJSON('/api/removeComment/' + obj.id + '/' + session, $.proxy(function(data) {
        this.set('content', data);
      }, this));
    },

    removepost : function(obj) {
      $.getJSON('/api/removePost/' + obj.id + '/' + session, $.proxy(function(data) {
        this.set('content', data);
      }, this));
    },

    removeblog : function(obj) {
      $.getJSON('/api/removeBlog/' + obj.id + '/' + session, $.proxy(function(data) {
        this.set('content', data);
        document.location = "/#/blogs";
      }, this));
    },

    editpost : function() {
      this.set('isEditing', true);
      // ember_semantic();
    },

    editblog : function() {
      this.set('isEditingBlog', true);
    },

    doneeditingpost : function(obj) {
      this.set('isEditing', false);
      // console.log(obj);
      $.getJSON('/api/updatePost/' + obj.id + '/' + encodeURIComponent(obj.title) + '/' + encodeURIComponent(obj.contentText) + '/'
          + obj.allowComments + '/' + session, $.proxy(function(data) {
        this.set('content', data);
      }, this));

      /*
       * //can be exchanged with POST, even route can be swapped and action works as it should and
       * return same result, but in post the result is interpreted as text, and it didn't want to
       * deserialize into object
       * 
       * $.post("/api/updatePost", { id : obj.id, title : obj.title, contentText : obj.contentText,
       * allowComments : obj.allowComments, sid : session }, $.proxy(function(data) {
       * console.log('got data back'); console.log(data); this.set('content', data); }, this));
       */
    },

    doneeditingblog : function(obj) {
      this.set('isEditingBlog', false);
      $.getJSON('/api/updateBlog/' + obj.id + '/' + encodeURIComponent(obj.title) + '/' + session, $.proxy(function(data) {
        this.set('content', data);
      }, this));
    }
  }
});

// ****************************************************** Post ********************
App.PostController = Ember.ObjectController.extend({
  isEditing : false,

  edit : function() {
    this.set('isEditing', true);
  },

  doneEditing : function() {
    this.set('isEditing', false);
    this.get('store').commit();
  }
});

// *********************************** rubish and lefovers ***********************************/

/*
// App.MemberController = Ember.ArrayController.extend({
// mailtoContactEmail: mailto('email')
// });
// var mailto = App.computed.mailto;
// var mailto = function(property) {
// return function() {
// console.log(property);
// return "mailto:" + this.get(property);
// }.property(property);
// };
// App.computed.mailto('dsdsa@sdas.com');
// mailto('dsdsa@sdas.com');
/*
 * var link = '<div style="background-repeat: repeat; background-image: url(/api/bgImage/' + id +
 * ')">';
 * 
 */

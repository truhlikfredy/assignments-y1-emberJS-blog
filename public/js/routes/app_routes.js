//routes
App.Router.map(function() {
  this.resource('signup');
  this.resource('home');
  this.resource('members', function() {
    this.resource('member',{ path: ':user_id'});    
  });
  this.resource('about');
  this.resource('blogs', function() {
    this.resource('blog',{ path: ':blog_id'}, function() {
      this.resource('post', {path: ':post_id'});      
    });
  });
});

App.HomeRoute = Ember.Route.extend({
  beforeModel: function() { redirect_if_not_logged_in(); },
  model: function() {
    return $.getJSON("/api/myProfile/"+session).then(function(data){
      console.log(data);
      return data;    
    });
  }
});

App.MembersRoute = Ember.Route.extend({
  beforeModel: function() { redirect_if_not_logged_in(); },
  model: function() {
    return $.getJSON("/api/members/"+session).then(function(data){
//      console.log(data);
      return data;    
    });
  }
});

App.MemberRoute = Ember.Route.extend({
  beforeModel: function() { redirect_if_not_logged_in(); },
  model: function(params) {
    return $.getJSON("/api/member/"+params.user_id).then(function(data){
      console.log(data);
      return data;    
    });
  }
});

App.BlogsRoute = Ember.Route.extend({
  beforeModel: function() { redirect_if_not_logged_in(); },
  model: function() {
    console.log('blogs');
//    return this.store.find('blogs');
    return $.getJSON("/api/blogs/"+session).then(function(data){
      console.log(data);
      return data;    
    });
  }
});

App.BlogRoute = Ember.Route.extend({
  beforeModel: function() { redirect_if_not_logged_in(); },
  model: function(params) {
//    return posts.findBy('id',params.post_id);
      console.log(params.blog_id);
      return $.getJSON("/api/blog/"+params.blog_id+"/"+session).then(function(data){
          console.log(data);
          return data;    
        });   
  }
});

App.PostRoute = Ember.Route.extend({
  beforeModel: function() { redirect_if_not_logged_in(); },
  model: function(params) {
    return $.getJSON("/api/post/"+params.post_id+"/"+session).then(function(data){
      console.log(data);
      return data;    
    });   
  }
});

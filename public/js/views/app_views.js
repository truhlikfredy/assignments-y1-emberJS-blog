/*
App.ApplicationView = Ember.View.extend({
  didInsertElement: function() {
    Ember.run.scheduleOnce('afterRender', this, this._childViewsRendered);
  },
  _childViewsRendered: function() {
    mine_semantic();
  }
});
*/

App.SignupView = Ember.View.extend({
  didInsertElement: function() { 
    ember_semantic();
    var request = $.post("/logout", { session: session });
    request.then(this.success.bind(this));    
  },
  success: function() {
    session="";
  }
});

App.HomeView = Ember.View.extend({
  didInsertElement: function() { 
    ember_semantic();
  }
});

App.BlogView = Ember.View.extend({
  didInsertElement: function() { 
//    ember_semantic();
  }
});

App.MembersView = Ember.View.extend({
//  willInsertElement: function() { redirect_if_not_logged_in(); }
});

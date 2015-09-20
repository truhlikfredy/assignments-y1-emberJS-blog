function mine_semantic() {
  $('.ui.checkbox').checkbox();
}

function ember_semantic() {
  Ember.run.scheduleOnce('afterRender', this, function() { mine_semantic() } );
  console.log('Should call my semantic tweeks anytime now.');
}

function redirect_if_not_logged_in() {
  if (session=="")  {
    if (local_version) {
      session="111111-2222-333-44-5";
      console.log("session cheat "+session);
    } else {
      document.location = "/#/";
    }
  }
}


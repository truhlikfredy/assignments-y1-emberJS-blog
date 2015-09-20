var session = "";
var local_version = false;

if (document.location.hostname == "localhost") {
  console.log("Local server! Enabling my sessions cheats.");
  local_version = true;
}

// ember
App = Ember.Application.create({
  ready : function() {
    /*
     * $('.ui.checkbox').checkbox(); alert('aaa');
     */
  }
});

/********************** handlebars helpers ****************************/

Ember.Handlebars.helper('format-date', function(date) {
  return moment(date).fromNow();
});

Ember.Handlebars.helper('verbose', function(context) {
  return JSON.stringify(context);
});

var showdown = new Showdown.converter();

Ember.Handlebars.helper('format-markdown', function(input) {
  return new Handlebars.SafeString(showdown.makeHtml(input));
});

Ember.Handlebars.helper('plain-value', function(value) {
  console.log(value);
  return value;
});

Ember.Handlebars.helper('disabled-if-offline', function(online, showOnline) {
  if (!online || !showOnline) {
    return new Handlebars.SafeString('disabled');
  } else {
    return '';
  }
});

Ember.Handlebars.helper('hello', function(a, b, c, options) {
  return '%@ - %@ - %@'.fmt(a, b, c);
});

Ember.Handlebars.helper('show-session', function() {
  return session;
});

// I will use this for ocations where the friend statement will return true/false as the value, but
// with fav I can switch the behaviour to return exactly the opposite
Ember.Handlebars.registerHelper('ifswitch', function(friend, fav, options) {
  // console.log(Ember.get(this,friend));
  if (fav) {
    return (Ember.get(this, friend)) ? options.fn(this) : options.inverse(this);
  } else {
    return (Ember.get(this, friend)) ? options.inverse(this) : options.fn(this);
  }
});

Ember.Handlebars.registerHelper('count', function(obj) {
  return Ember.get(this, obj).length;
});

Ember.Handlebars.registerHelper('countFav', function(obj) {
  var list = Ember.get(this, obj);
  var ret = 0;

  for (var i = 0; i < list.length; i++)
    if (list[i].isFriend) ret++;

  return ret;
});

Ember.Handlebars.registerHelper('countNFav', function(obj) {
  var ret = 0;

  for (var i = 0; i < Ember.get(this, obj).length; i++)
    if (!Ember.get(this, obj)[i].isFriend) ret++;

  return ret;
});

// mail to hyper link as button for semantic (it needs some house keeping after usage like </a> but
// it allow much more freedom than regular handlebars things
Ember.Handlebars.registerBoundHelper('mailToButton', function(emailAddress, label) {
  emailAddress = Em.Handlebars.Utils.escapeExpression(emailAddress);

  var link = '<a class="ui animated fade tiny button" href="mailto:' + emailAddress + '">';
  return new Em.Handlebars.SafeString(link);
  // return link;
});

//easy helper to display quickly and easy avatars in bound way
Ember.Handlebars.registerBoundHelper('avatarImage', function(id) {
  var link = '<img src="/api/avatar/' + id + '">';
  return new Em.Handlebars.SafeString(link);
});

//setting style for blog backgrounds
Ember.Handlebars.registerBoundHelper('bgImageDiv', function(id) {
  var link = '<div style="background-repeat: repeat; background-image: url(/api/bgImage/' + id + ')">';
  return new Em.Handlebars.SafeString(link);
});

//more usefull if condtiion
Handlebars.registerHelper('ifCond', function(v1, operator, v2, options) {
  switch (operator) {
    case '==':
      return (v1 == v2) ? options.fn(this) : options.inverse(this);
    case '===':
      return (v1 === v2) ? options.fn(this) : options.inverse(this);
    case '<':
      return (v1 < v2) ? options.fn(this) : options.inverse(this);
    case '<=':
      return (v1 <= v2) ? options.fn(this) : options.inverse(this);
    case '>':
      return (v1 > v2) ? options.fn(this) : options.inverse(this);
    case '>=':
      return (v1 >= v2) ? options.fn(this) : options.inverse(this);
    case '&&':
      return (v1 && v2) ? options.fn(this) : options.inverse(this);
    case '||':
      return (v1 || v2) ? options.fn(this) : options.inverse(this);
    default:
      return options.inverse(this);
  }
});

// ************ desperate attemts and snippets

// App.Store = DS.Store.extend();

// App.computed.mailto = function(property) {
// return function() {
// console.log(property);
// return "mailto:" + this.get(property);
// }.property(property);
// };

/*
 * {{#ifCond var1 '==' var2}} {{#ifCond v1 v2}} {{v1}} is equal to {{v2}} {{else}} {{v1}} is not
 * equal to {{v2}} {{/ifCond}}
 */

/*
 * Ember.Handlebars.registerHelper('unbound', function unboundHelper(property, fn) { var options =
 * arguments[arguments.length - 1], helper, context, out;
 * 
 * if (arguments.length > 2) { // Unbound helper call. options.data.isUnbound = true; helper =
 * Ember.Handlebars.helpers[arguments[0]] || Ember.Handlebars.helpers.helperMissing; out =
 * helper.apply(this, Array.prototype.slice.call(arguments, 1)); delete options.data.isUnbound;
 * return out; }
 * 
 * context = (fn.contexts && fn.contexts.length) ? fn.contexts[0] : this; return
 * handlebarsGet(context, property, fn); });
 */

/*
 * App.IfEqualComponent = Ember.Component.extend({ isSwitch: function() { if (this.get('par2')) {
 * console.log(this.get('par1')); return (this.get('par1'))?true:false; } else { return
 * (this.get('par1'))?false:true; }
 * 
 * }.property('par1','par2') })
 */

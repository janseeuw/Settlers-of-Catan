/*
 * Contains cookie methods
 */
javaRest.cookie = {}

/*
 * Get the value of a cookie.
 */
javaRest.cookie.get = function(name) {
  var pairs = document.cookie.split(/\; /g);
  var cookie = {};
  for (var i in pairs) {
    var parts = pairs[i].split(/\=/);
    cookie[parts[0]] = unescape(parts[1]);
  }
  return cookie[name];
};

/*
 * Set a cookie.
 */
javaRest.cookie.set = function(name, value) {
  document.cookie = name + '=' + value;
};

/*
 * Remove a cookie.
 */
javaRest.cookie.remove = function(name) {
  document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
};
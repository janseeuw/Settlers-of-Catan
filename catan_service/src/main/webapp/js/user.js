javaRest.user = {};

/**
 * @description Log the user in.
 * @param {string} username
 * @param {string} password
 * @param {function} Callback. First parameter is error, if any.
 */
javaRest.user.login = function(username, password, callback) {
  javaRest.post(
          '/catan/players/player/login',
          {
            "username": username,
            "password": password
          },
  function(data, textStatus, jqXHR) {
    // Base64 encode the username and password
    var credentials = username + ';' + password;
    var wordArray = CryptoJS.enc.Utf16.parse(credentials);
    var base64 = CryptoJS.enc.Base64.stringify(wordArray);
    javaRest.cookie.set('credentials', base64);
    javaRest.cookie.set('userid', data.id);
    javaRest.cookie.set('username', data.name);
    javaRest.cookie.set('admin', data.admin);
    callback();
  },
          function(jqXHR, textStatus, errorThrown) {
            callback(jqXHR);
          });
};

/*
 * Login using facebook
 * @param {string} accesstoken. Accesstoken from Facebook
 * @param {function} callback. Contains user object
 */
javaRest.user.loginSocial = function(accesstoken, callback) {
  javaRest.post(
          '/catan/players/player/login/facebook',
          {
            "accesstoken": accesstoken
          },
  function(data, textStatus, jqXHR) {
    javaRest.cookie.set('credentials', data.id);
    javaRest.cookie.set('userid', data.id);
    javaRest.cookie.set('username', data.name);
    javaRest.cookie.set('admin', data.admin);
    callback();
  },
          function(jqXHR, textStatus, errorThrown) {
            callback(jqXHR);
          });
};

/**
 * @description Logs the user out.
 */
javaRest.user.logout = function() {
  javaRest.cookie.remove('userid');
  javaRest.cookie.remove('username');
  javaRest.cookie.remove('credentials');
  javaRest.cookie.remove('admin');
  window.location = "index.jsp";
};

/**
 * Creates the user
 * @param {string} username
 * @param {string} password
 * @param {string} email 
 * @param {string} avatar. Base64 encoded data
 * @param {function} callback. First parameter is error, if any.
 */
javaRest.user.create = function(username, password, email, avatar, callback) {
  javaRest.post(
          '/catan/players/player/create',
          {
            "username": username,
            "password": password,
            "email": email,
            "avatar": avatar
          },
  function(data, textStatus, jqXHR) {
    callback();
  },
          function(jqXHR, textStatus, errorThrown) {
            callback(jqXHR);
          });
};

/**
 * @description Activates the user
 * @param {string} token
 * @param {function} callback. First parameter is error, if any.
 */
javaRest.user.validate = function(token, callback) {
  javaRest.post(
          '/catan/players/player/activate/' + token,
          {},
          function(data, textStatus, jqXHR) {
            callback();
          },
          function(jqXHR, textStatus, errorThrown) {
            callback(jqXHR);
          });
};

/*
 * @description Sends an e-mail to reset the password.
 * @param username
 * @callback callback. First parameter is error, if any.
 */
javaRest.user.sendResetEmail = function(username, callback) {
  javaRest.post(
          '/catan/players/player/recover',
          {
            "username": username
          },
  function(data, textStatus, jqXHR) {
    callback();
  },
          function(jqXHR, textStatus, errorThrown) {
            callback(jqXHR);
          });
};

/**
 * @description Reset the user's password.
 * @param {string} username
 * @param {string} password
 * @param {function} callback. First parameter is error, if any.
 */
javaRest.user.resetPassword = function(token, password, callback) {
  javaRest.post(
          '/catan/players/player/recover/' + token,
          {
            "password": password
          },
  function(data, textStatus, jqXHR) {
    callback();
  },
          function(jqXHR, textStatus, errorThrown) {
            callback(jqXHR);
          });
};

/**
 * @description Checks if the user is logged in.
 */
javaRest.user.isLoggedIn = function() {
  // controleer of er een cookie is 
  return !!javaRest.cookie.get('credentials');
};

/**
 * @description Join's the user to a game.
 * @param {string} userid
 * @param {string} gameid
 * @param {function} callback. First parameter is error, if any.
 */
javaRest.user.joinGame = function(userid, gameid, callback) {
  javaRest.postAuth(
          '/catan/games/game/join',
          {
            "gameid": gameid,
            "userid": userid
          },
  function(data, textStatus, jqXHR) {
    callback();
  },
          function(jqXHR, textStatus, errorThrown) {
            callback(jqXHR);
          });
};

/**
 * @description Creates a new game with the user as first player.
 * @param {string} userid
 * @param {function} callback. First parameter is error, if any.
 */
javaRest.user.createGame = function(userid, callback) {
  javaRest.postAuth(
          '/catan/games/game/create',
          {
            "userid": userid
          },
  function(data, textStatus, jqXHR) {
    callback(data);
  },
          function(jqXHR, textStatus, errorThrown) {
            callback();
          });
};

/**
 * @description Disables a user.
 * @param {string} userid
 * @param {function} callback. First parameter is error, if any.
 */
javaRest.user.disable = function(userid, callback) {
  javaRest.post(
          '/catan/players/player/disable',
          {"userid": userid},
  function(data, textStatus, jqXHR) {
    callback();
  },
          function(jqXHR, textStatus, errorThrown) {
            callback(jqXHR);
          });
};

/**
 * @description Gets all the users
 * @param {function} callback. First parameter is JSON containing users.
 */
javaRest.user.getAll = function(callback) {
  javaRest.get(
          '/catan/players/player/all',
          {},
          function(data, textStatus, jqXHR) {
            callback(data);
          },
          function(jqXHR, textStatus, errorThrown) {
            callback();
          });
};


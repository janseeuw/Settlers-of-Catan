/*
 * Join a user to a game
 * @param gameid the gameid from the game you want to join
 */
var join = function(gameid) {
  if (javaRest.user.isLoggedIn())
  {
    var userid = javaRest.cookie.get('userid');
    javaRest.user.joinGame(userid, gameid, function(error) {
      if (!error) {
        var url = "http://stable.team10.vop.tiwi.be/catan/jnlp/create/" + gameid + "/0/" + userid;
        deployJava.launchWebStartApplication(url);
      }
    });
  } else {
    window.location = "login.jsp";
  }
}

/*
 * Re-joins a user to a game.
 * @param gameid the gameid from to game
 */
var rejoin = function(gameid) {
  var userid = javaRest.cookie.get('userid');
  var url = "http://stable.team10.vop.tiwi.be/catan/jnlp/create/" + gameid + "/0/" + userid;
  deployJava.launchWebStartApplication(url);
}

/*
 * Spectate a game without playing
 */
var spectate = function(gameid) {
  var userid = 0;
  var url = "http://stable.team10.vop.tiwi.be/catan/jnlp/create/" + gameid + "/0/" + userid;
  deployJava.launchWebStartApplication(url);
}

/*
 * Rebuild the DOM with data
 */
var refreshGameList = function(data) {
  $('tbody').empty();
  $.each(data, function(i, item) {
    var p1 = (item.players[0] !== undefined) ? item.players[0] : "";
    var p2 = (item.players[1] !== undefined) ? item.players[1] : "";
    var p3 = (item.players[2] !== undefined) ? item.players[2] : "";
    var p4 = (item.players[3] !== undefined) ? item.players[3] : "";
    var tr = $('<tr/>');
    $(tr).append('<td>' + p1 + '</td>');
    $(tr).append('<td>' + p2 + '</td>');
    $(tr).append('<td>' + p3 + '</td>');
    $(tr).append('<td>' + p4 + '</td>');
    $(tr).append('<td>' + item.status + '</td>');
    if (item.status !== "running") {
      if (javaRest.user.isLoggedIn())
      {
        var username = javaRest.cookie.get('username');
        if (item.players.indexOf(username) != -1)
        {
          $(tr).append('<td><button class=\"btn btn-success\" onclick=\"rejoin(' + item.gameId + ');\">Rejoin</button></td>');
        } else {
          $(tr).append('<td><button class=\"btn btn-primary\" onclick=\"join(' + item.gameId + ');\">Join</button></td>');
        }
      } else {
        $(tr).append('<td><button class=\"btn btn-primary\" onclick=\"join(' + item.gameId + ');\">Join</button></td>');
      }
    } else {
      if (javaRest.user.isLoggedIn())
      {
        var username = javaRest.cookie.get('username');
        if (item.players.indexOf(username) != -1)
        {
          $(tr).append('<td><button class=\"btn btn-success\" onclick=\"rejoin(' + item.gameId + ');\">Rejoin</button></td>');
        } else {
          $(tr).append('<td></td>');
        }
      } else {
        $(tr).append('<td></td>');
      }
    }
    $(tr).append('<td><button class=\"btn\" onclick=\"spectate(' + item.gameId + ');\">Spectate</button></td>');

    if (item.status === "running")
    {
      tr.addClass("error");
    } else {
      tr.addClass("success");
    }
    $('tbody').append(tr);
  });
}

$(function() {
  // Default parameters
  var page = "1";
  var maxPage = "";
  var itemsPerPage = "20";
  var column = "3";

  // WebWorker to make http requests on the background
  var worker = new Worker('js/lobbyWorker.js');
  worker.onmessage = function(event) {
    maxPage = Math.ceil(event.data[0].max / itemsPerPage);
    refreshGameList(event.data);
  };
  worker.postMessage(JSON.stringify({
    "page": page,
    "itemsPerPage": itemsPerPage,
    "column": column
  })
          );

  // Changes menu based login status.
  if (!!javaRest.user.isLoggedIn())
  {
    $('#loggedIn').show();
    $('#notLoggedIn').hide();
  } else {
    $('#notLoggedIn').show();
    $('#loggedIn').hide();
  }


// Listeners
  $('#createGame').on('click', function() {
    if (javaRest.user.isLoggedIn())
    {
      var userid = javaRest.cookie.get('userid');
      javaRest.user.createGame(userid, function(data) {
        if (data) {
          var url = "http://stable.team10.vop.tiwi.be/catan/jnlp/create/" + data.gameId + "/0/" + userid;
          deployJava.launchWebStartApplication(url);
        }
      });
    } else {
      window.location = "login.jsp";
    }
  });

  $('#players').on('click', function() {
    if (column !== 2) {
      column = 2;
      worker.postMessage(JSON.stringify({
        "page": page,
        "itemsPerPage": itemsPerPage,
        "column": column
      })
              );
    }
  });

  $('#status').on('click', function() {
    if (column !== 3) {
      column = 3;
      worker.postMessage(JSON.stringify({
        "page": page,
        "itemsPerPage": itemsPerPage,
        "column": column
      })
              );
    }
  });

  $('#itemsPerPage').on('change', function() {
    page = 1;
    itemsPerPage = $("#itemsPerPage option:selected").text();
    worker.postMessage(JSON.stringify({
      "page": page,
      "itemsPerPage": itemsPerPage,
      "column": column
    })
            );
  });

  $('#next').on('click', function() {
    if (page < maxPage)
    {
      page++;
      worker.postMessage(JSON.stringify({
        "page": page,
        "itemsPerPage": itemsPerPage,
        "column": column
      })
              );
    }
  });

  $('#prev').on('click', function() {
    if (page > 1) {
      page--;
      worker.postMessage(JSON.stringify({
        "page": page,
        "itemsPerPage": itemsPerPage,
        "column": column
      })
              );
    }
  });
});

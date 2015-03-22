/*
 * Web worker
 * Used to make http requests to the service and fetch the games list.
 * 
 */

var page = "";
var itemsPerPage = "";
var column = "";

refreshInformation = function() {
  var fullUrl = "/catan/games/game/paged/page/" + page + "/" + itemsPerPage + "/sort/" + column;

  function infoReceived()
  {
    var output = JSON.parse(httpRequest.responseText);
    if (output) {
      postMessage(output);
    }
    httpRequest = null;
  }

  var httpRequest = new XMLHttpRequest();
  httpRequest.open("GET", fullUrl, true);
  httpRequest.onload = infoReceived;
  httpRequest.send(null);
}

setInterval(function() {
    refreshInformation();
}, 2000);
 
onmessage = function(event) {
  if (event.data) {
    var data = JSON.parse(event.data);
    itemsPerPage = data.itemsPerPage;
    page = data.page;
    column = data.column;
  }
    refreshInformation();
};
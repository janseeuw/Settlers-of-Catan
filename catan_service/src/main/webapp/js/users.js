var pageSize = 25;
var numOfPlayers;

/*
 * Uses handlebars to generate a table with users.
 */
var loadUsersTable = function() {
  var source = $("#row-template").html();
  var template = Handlebars.compile(source);

  javaRest.user.getAll(function(data) {
    if (data) {
      numOfPlayers = data.length;
      var players = {players: data};
      var html = template(players);
      $("#usertable tbody").html(html);
      if(javaRest.cookie.get('admin')==='true'){
        $('.user-entry').find('button').show();
      }
    }
  });
}
;

/*
 * @description Searches the table with users for given inputVal
 * @param {string} inputVal
 */
var searchTable = function(inputVal)
{
  var table = $('#usertable');
  table.find('tr').each(function(index, row)
  {
    var allCells = $(row).find('td');
    if (allCells.length > 0)
    {
      var found = false;
      allCells.each(function(index, td)
      {
        var regExp = new RegExp(inputVal, 'i');
        if (regExp.test($(td).text()))
        {
          found = true;
          return false;
        }
      });
      if (found === true)
        $(row).show();
      else
        $(row).hide();
    }
  });
}

/*
 * Shows only a selection from the usertable.
 */
var showPage = function(page) {
  $(".user-entry").hide();
  $(".user-entry").each(function(n) {
    if (n >= pageSize * (page - 1) && n < pageSize * page)
      $(this).show();
  });
}
;

$(function() {
  if (!!javaRest.user.isLoggedIn())
  {
    $('#loggedIn').show();
    $('#notLoggedIn').hide();
  } else {
    $('#notLoggedIn').show();
    $('#loggedIn').hide();
  }
  
  loadUsersTable();
  showPage(1);

  var numOfPages = numOfPlayers / pageSize;

  $('.pagination').bootpag({
    total: numOfPages,
    page: 1,
    maxVisible: 10
  }).on('page', function(event, num) {
    showPage(num);
  });

  $('#search').on('input', function() {
    if ($(this).val() !== '') {
      $('.pagination').hide();
      searchTable($(this).val());
    } else {
      $('.pagination').show();
      showPage(1);
    }
  });

  $('.user-entry').on('click', 'button', function() {
    var userEntry = $(this).closest('.user-entry');
    var id = userEntry.data('id');
    javaRest.user.disable(id, function(error) {
      if (!error) {
        userEntry.find('#status').text('B');
        userEntry.find('button').hide();
      }
    });
  });
});
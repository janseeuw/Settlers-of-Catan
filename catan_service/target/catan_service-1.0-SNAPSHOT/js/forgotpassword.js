$(function() {

  /*
   * Check if user is logged in.
   */
  if (javaRest.user.isLoggedIn()) {
    window.location = "index.jsp";
  }

  /*
   * Listeners
   */
  $('#forgotPasswordForm').on('submit', function() {
    var username = $('#username').val();
    javaRest.user.sendResetEmail(username, function(error) {
      $('#forgotPasswordForm').hide();
      if (!error) {
        $('.success').show();
      } else {
        $('.error').show();
      }
    });
    return false;
  });
});
$(function() {

  if (javaRest.user.isLoggedIn()) {
    window.location = "index.jsp";
  }

  // Listeners
  $('#resetPasswordForm').on('submit', function() {
    var token = window.location.search.substring(1);
    javaRest.user.resetPassword(token, $('#newPassword').val(), function(error) {
      if (!error) {
        $('#resetPasswordForm').hide();
        $('#success').show();
      } else {
        $('#error').html('error').show();
      }
    });
    return false;
  });

  $('#repeatNewPassword').on('input', function() {
    if (this.value !== document.getElementById('newPassword').value) {
      this.setCustomValidity('The two passwords must match.');
    } else {
      // input is valid -- reset the error message
      this.setCustomValidity('');
    }
  });
});
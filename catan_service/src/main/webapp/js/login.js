$(function() {
  window.fbAsyncInit = function() {
    FB.init({
      appId: '332714630184955',
      status: true,
      cookie: true,
      xfbml: true
    });

    $('#fb').on('click', function() {
      FB.login(function(response) {
        if (response.authResponse) {
          javaRest.user.loginSocial(response.authResponse.accessToken, function(error) {
            if (!error) {
              window.location = 'index.jsp';
            }
          });
        }
      });
    })
  };
  // Load the SDK Asynchronously
  (function(d) {
    var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
    if (d.getElementById(id)) {
      return;
    }
    js = d.createElement('script');
    js.id = id;
    js.async = true;
    js.src = "//connect.facebook.net/en_US/all.js";
    ref.parentNode.insertBefore(js, ref);
  }(document));

  if (javaRest.user.isLoggedIn()) {
    window.location = "index.jsp";
  }

  // Listeners
  $('#loginForm').on('submit', function() {
    $('#objectNotFoundException').hide();
    $('#playerNotActiveException').hide();
    $('#unknownException').hide();
    javaRest.user.login($('#username').val(), $('#password').val(), function(error) {
      if (!error) {
        window.location = 'index.jsp';
      } else {
        if (error.responseText === 'objectNotFoundException') {
          $('#objectNotFoundException').show();
        } else if (error.responseText === 'playerNotActiveException') {
          $('#playerNotActiveException').show();
        } else {
          $('#unknownException').show();
        }
      }
    });
    return false;
  });
});

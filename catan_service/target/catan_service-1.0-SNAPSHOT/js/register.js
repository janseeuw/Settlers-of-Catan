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

  // default avatars
  var myCache = new Array();
  myCache = new imageCache('img/default_', 1, 6);
  previewImage(myCache.cache[0]);


  // Listeners
  $('#registerForm').on('submit', function() {
    javaRest.user.create($('#username').val(), $('#password').val(), $('#email').val(), toBase64(), function(error) {
      if (!error) {
        window.location = "verify.jsp";
      } else {
        $('#duplicateUsername').show();
      }
    });
    return false;
  });

  $('#prev').on('click', function() {
    myCache.index--;
    if (myCache.index < 0) {
      myCache.index = myCache.cache.length - 1;
    }
    previewImage(myCache.cache[myCache.index]);
  });

  $('#next').on('click', function() {
    myCache.index++;
    if (myCache.index >= myCache.cache.length) {
      myCache.index = 0;
    }
    previewImage(myCache.cache[myCache.index]);
  });

  $('#avatar').on('change', function(event) {
    var file = event.target.files[0];
    var reader = new FileReader();
    reader.onload = function(event) {
      // Show as preview
      var img = new Image();
      img.src = event.target.result;
      previewImage(img);
    };
    reader.readAsDataURL(file);
  });

  $('#repeatEmail').on('input', function() {
    if (this.value !== document.getElementById('email').value) {
      this.setCustomValidity('The two email addresses must match.');
    } else {
      // input is valid -- reset the error message
      this.setCustomValidity('');
    }
  });

  $('#repeatPassword').on('input', function() {
    if (this.value !== document.getElementById('password').value) {
      this.setCustomValidity('The two passwords must match.');
    } else {
      // input is valid -- reset the error message
      this.setCustomValidity('');
    }
  });


  /*
   * Show a preview of the chosen avatar
   * @param {type} img
   * @returns {undefined}
   */
  function previewImage(img) {
    // Select previous default avatar
    var element = document.getElementById('preview');
    element.src = img.src;
  }

  /*
   * Loads the default avatars in an array
   * @param {type} base
   * @param {type} firstNum
   * @param {type} lastNum
   * @returns {imageCache}
   */
  function imageCache(base, firstNum, lastNum) {
    this.cache = [];
    this.index = 0;
    var img;
    for (var i = firstNum; i <= lastNum; i++) {
      img = new Image();
      img.src = base + i + ".png";
      this.cache.push(img);
    }
  }

  /*
   * Encodes the avatar to base64
   */
  function toBase64() {
    var canvas = document.createElement("canvas");
    var ctx = canvas.getContext("2d");
    var img = document.getElementById("preview");
    ctx.drawImage(img, 10, 10);
    var data = canvas.toDataURL();
    var string = data.replace('data:image/png;base64,', '');
    return string;
  }
});
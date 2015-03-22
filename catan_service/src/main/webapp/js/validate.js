$(function() {
  if (javaRest.user.isLoggedIn()) {
    window.location = "index.jsp";
  }
  
  var token = window.location.search.substring(1);
  javaRest.user.validate(token, function(error){
    if(!error){
      $('.success').show();
    }else{
      $('.error').show();
    }
  });
});
$(function() {
  if (!!javaRest.user.isLoggedIn()) {
    javaRest.user.logout();
  }
  window.location = "index.jsp";
});
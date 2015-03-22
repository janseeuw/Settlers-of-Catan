<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<!--basename_locale.properties-->
<c:if test="${param.language == 'en'}">
  <fmt:setLocale value="en" scope="session" />
</c:if>
<c:if test="${param.language == 'fr'}">
  <fmt:setLocale value="fr" scope="session" />
</c:if>
<c:if test="${param.language == 'nl'}">
  <fmt:setLocale value="nl" scope="session" />
</c:if>
<fmt:setBundle basename="Messages.MessageResource" var="msg"/>
<fmt:setLocale value="${param.language}"/>
<html>
  <head>
    <title><fmt:message key="catanUsers" bundle="${msg}"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    <link href="css/style.css" rel="stylesheet" type="text/css" />
    <link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
  </head>
  <body>
    <div class="navbar navbar-static-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="#"><fmt:message key="soc" bundle="${msg}"/></a>
          <div id="notLoggedIn" style="display: none;">
            <ul class="nav">
              <li><a href="index.jsp"><fmt:message key="lobby" bundle="${msg}"/></a></li>
              <li  class="active"><a href="users.jsp"><fmt:message key="users" bundle="${msg}"/></a></li>
              <li><a href="login.jsp"><fmt:message key="login" bundle="${msg}"/></a></li>
              <li><a href="register.jsp"><fmt:message key="register" bundle="${msg}"/></a></li>
            </ul>
          </div>
          <div id="loggedIn" style="display: none;">
            <ul class="nav">
              <li><a href="index.jsp"><fmt:message key="lobby" bundle="${msg}"/></a></li>
              <li  class="active"><a href="users.jsp"><fmt:message key="users" bundle="${msg}"/></a></li>
              <li><a href="logout.jsp"><fmt:message key="logout" bundle="${msg}"/></a></li> 
            </ul>
          </div>
          <form id="languageForm"  class="navbar-form pull-right" action="users.jsp">
            <input type="button" onclick="setLanguage('en')" value="<fmt:message key="english" bundle="${msg}"/>">
            <input type="button" onclick="setLanguage('fr')" value="<fmt:message key="french" bundle="${msg}"/>">
            <input type="button" onclick="setLanguage('nl')" value="<fmt:message key="dutch" bundle="${msg}"/>">
            <input id="selectedLanguage" type="hidden" name="language"/>
          </form>
        </div>
      </div>
    </div>
    <div class="jumbotron subhead">
      <div class="container">
        <h1><fmt:message key="users" bundle="${msg}"/></h1>
      </div>
    </div>
    <div id="content">
      <div class="container">
        <form class="form-search pull-right">
          <input id="search" type="text" class="input-medium search-query" placeholder="Search">
        </form>
        <div class="row">
          <div class="span6 offset2">
            <table id="usertable" class="table table-condensed table-hover">
              <thead>
                <tr>
                  <th><fmt:message key="user" bundle="${msg}"/></th>
                  <th</th>
                </tr>
              </thead>
              <tbody>
              <script id="row-template" type="text/x-handlebars-template">
                {{#withSort players "name"}}
                <tr class="user-entry" data-id={{id}}>
                <td>{{name}}</td>
                <td>{{#isnt status 'B'}}
                <button class="btn btn-danger" style="display: none;">Disable user</button>
                {{/isnt}}</td>
                </tr>
                {{/withSort}}
              </script>
              </tbody>
            </table>
          </div>
        </div>
        <div class="pagination pagination-centered"></div>
      </div>
    </div>
    <footer id="footer" class="navbar navbar-static-bottom">
      <div class="container">
        <p class="text-center">Team 10</p>
      </div>
    </footer>

    <!-- scripts -->
    <script src="js/jquery.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/javarest.js"></script>
    <script src="js/cookie.js"></script>
    <script src="js/user.js"></script>
    <script src="js/users.js"></script>
    <script src="js/changeLanguage.js"></script>
    <script src="js/handlebars.js"></script>
    <script src="js/swag-min.js"></script>
    <script src="js/bootpag.js"></script>
  </body>
</html>

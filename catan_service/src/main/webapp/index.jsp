<%-- 
    Document   : index
    Created on : 6-apr-2013, 12:18:09
    Author     : Joachim
--%>

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
    <title><fmt:message key="catanLobby" bundle="${msg}"/></title>
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
              <li class="active"><a href="index.jsp"><fmt:message key="lobby" bundle="${msg}"/></a></li>
              <li><a href="users.jsp"><fmt:message key="users" bundle="${msg}"/></a></li>
              <li><a href="login.jsp"><fmt:message key="login" bundle="${msg}"/></a></li>
              <li><a href="register.jsp"><fmt:message key="register" bundle="${msg}"/></a></li>
            </ul>
          </div>
          <div id="loggedIn" style="display: none;">
            <ul class="nav">
              <li><a href="index.jsp"><fmt:message key="lobby" bundle="${msg}"/></a></li>
              <li><a href="users.jsp"><fmt:message key="users" bundle="${msg}"/></a></li>
              <li><a href="logout.jsp"><fmt:message key="logout" bundle="${msg}"/></a></li> 
            </ul>
          </div>
          <form id="languageForm"  class="navbar-form pull-right" action="index.jsp">
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
        <h1><fmt:message key="lobby" bundle="${msg}"/></h1>
      </div>
    </div>
    <div id="content">
      <div class="container">
        <button id="createGame" class="btn pull-right"><fmt:message key="createGame" bundle="${msg}"/></button>
        <p><fmt:message key="gamesPerPage" bundle="${msg}"/></p>
        <select id="itemsPerPage" class="pull-left">
          <option>5</option>
          <option>10</option>
          <option selected="selected">20</option>
          <option>50</option>
          <option>100</option>
        </select>
        <table id="lobby" class="table table-condensed table-hover">
          <thead>
            <tr>
              <th id="players" colspan="4" style="cursor: pointer;"><fmt:message key="players" bundle="${msg}"/></th>
              <th id="status" style="cursor: pointer;"><fmt:message key="status" bundle="${msg}"/></th>
              <th></th>
            </tr>
          </thead>
          <tbody>
          </tbody>
        </table>
        <div class="pagination pagination-centered">
          <ul>
            <li><a href="#" id="prev"><fmt:message key="prev" bundle="${msg}"/></a></li>
            <li><a href="#" id="next"><fmt:message key="next" bundle="${msg}"/></a></li>
          </ul>
        </div>
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
    <script src="js/index.js"></script>
    <script src="js/changeLanguage.js"></script>
    <script src="http://www.java.com/js/deployJava.js"></script>
  </body>
</html>

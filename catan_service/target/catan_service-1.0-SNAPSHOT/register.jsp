<%-- 
    Document   : register
    Created on : 6-apr-2013, 12:34:24
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
    <title><fmt:message key="catanRegister" bundle="${msg}"/></title>
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
            <ul class="nav">
              <li><a href="index.jsp"><fmt:message key="lobby" bundle="${msg}"/></a></li>
              <li><a href="users.jsp"><fmt:message key="users" bundle="${msg}"/></a></li>
              <li><a href="login.jsp"><fmt:message key="login" bundle="${msg}"/></a></li>
              <li class="active"><a href="register.jsp"><fmt:message key="register" bundle="${msg}"/></a></li>
            </ul>
            <form id="languageForm"  class="navbar-form pull-right" action="register.jsp">
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
        <h1><fmt:message key="register" bundle="${msg}"/></h1>
      </div>
    </div>          
    <div id="content">
      <div id="content" class="container">
        <div class="row">
          <form id="registerForm" class="form-horizontal">
            <div class="span4">
              <label><fmt:message key="avatar" bundle="${msg}"/></label>
              <span id="prev" style="cursor: pointer;">&lt</span>
              <img id="preview" src="">
              <span id="next" style="cursor: pointer;">&gt</span>
              <br />
              <input id="avatar" type="file" accept="image/*">
            </div>
            <div class="span4">
              <div id="duplicateUsername" class="alert alert-error" style="display: none;"><fmt:message key="duplicateUsername" bundle="${msg}"/></div>
              <label><fmt:message key="username" bundle="${msg}"/></label>
              <input id="username" type="text" placeholder="<fmt:message key="username" bundle="${msg}"/>" required pattern="[a-zA-Z0-9]{3,}" title="<fmt:message key="nameInput" bundle="${msg}"/>">
              <label><fmt:message key="password" bundle="${msg}"/></label>
              <input id="password" type="password" placeholder="<fmt:message key="password" bundle="${msg}"/>" required pattern="(?=^.{6,}$)(?=.*\W).*$" title="<fmt:message key="passwordInput" bundle="${msg}"/>">
              <label><fmt:message key="repeatPass" bundle="${msg}"/></label>
              <input id="repeatPassword" type="password" placeholder="<fmt:message key="repeatPass" bundle="${msg}"/>" required>
              <label><fmt:message key="email" bundle="${msg}"/></label>
              <input id="email" type="email" placeholder="<fmt:message key="email" bundle="${msg}"/>" required>
              <label><fmt:message key="repeatEmail" bundle="${msg}"/></label>
              <input id="repeatEmail" type="email" placeholder="<fmt:message key="repeatEmail" bundle="${msg}"/>" required>
            </div>
            <div class="span4">
              <div id="fb-root"></div>
              <button class="btn btn-primary" id="fb" scope="email" data-show-faces="false" data-width="300"><fmt:message key="signupFacebook" bundle="${msg}"/></button>
            </div>
        </div>
        <div class="row">
          <div class="span8">
            <div class="form-actions">
              <button type="submit" class="btn btn-primary"><fmt:message key="register" bundle="${msg}"/></button>
              <a href="login.jsp"><fmt:message key="login" bundle="${msg}"/></a>
            </div>
          </div>
        </div>
        </form>
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
    <script src="js/register.js"></script>
    <script src="js/changeLanguage.js"></script>
  </body>
</html>

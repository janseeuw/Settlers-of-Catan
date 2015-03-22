<%-- 
    Document   : resetpassword
    Created on : 6-apr-2013, 12:53:12
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
    <title><fmt:message key="catanResetPass" bundle="${msg}"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    <link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
  </head>
  <body>
    <div class="navbar navbar-static-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="#"><fmt:message key="soc" bundle="${msg}"/></a>
          <ul class="nav">
            <li class="active"><a href="index.jsp"><fmt:message key="lobby" bundle="${msg}"/></a></li>
            <li><a href="login.jsp"><fmt:message key="login" bundle="${msg}"/></a></li>
            <li><a href="register.jsp"><fmt:message key="register" bundle="${msg}"/></a></li>
          </ul>
          <form id="languageForm"  class="navbar-form pull-right" action="resetpassword.jsp">
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
        <h1><fmt:message key="forgotPass" bundle="${msg}"/></h1>
      </div>
    </div> 
    <div id="content">
      <div class="container">
        <form id="resetPasswordForm" class="form-horizontal">
          <div class="control-group">
            <div class="control-label">
              <label><fmt:message key="newPass" bundle="${msg}"/></label>
            </div>
            <div class="controls">
              <input id="newPassword" type="password" placeholder="<fmt:message key="newPass" bundle="${msg}"/>" required>
            </div>
          </div>
          <div class="control-group">
            <div class="control-label">
              <label><fmt:message key="repeatNewPass" bundle="${msg}"/></label>
            </div>
            <div class="controls">
              <input id="repeatNewPassword" type="password" placeholder="<fmt:message key="repeatNewPass" bundle="${msg}"/>" required>
            </div>
          </div>
          <div class="form-actions">
            <input type="submit" class="btn btn-primary" value="<fmt:message key="reset" bundle="${msg}"/>">
          </div>
        </form>
        <div id="success" style="display: none;">
          <p><fmt:message key="resetPassSucces" bundle="${msg}"/></p>
        </div>
        <div id="error"></div>
      </div>
    </div>
    <footer id="footer" class="navbar navbar-statics-bottom">
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
    <script src="js/resetpassword.js"></script>
    <script src="js/changeLanguage.js"></script>
  </body>
</html>

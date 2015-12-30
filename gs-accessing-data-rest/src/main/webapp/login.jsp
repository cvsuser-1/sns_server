<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta charset="utf-8"/>
    <meta property="wb:webmaster" content="73e69228c954c263" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>

<title>Sparklr</title>
    <title>Hello AngularJS</title>
    <meta name="description" content=""/>
    <meta name="viewport" content="width=device-width"/>
    <base href="/"/>
    <!--link rel="stylesheet" type="text/css" href="/webjars/bootstrap/css/bootstrap.min.css"/ -->
    <link rel='stylesheet' type="text/css" href='/webjars/bootstrap/css/bootstrap.min.css'>
    <script type="text/javascript" src="/webjars/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="/webjars/bootstrap/js/bootstrap.min.js"></script>
    <style type="text/css">
      [ng\:cloak], [ng-cloak], .ng-cloak {
        display: none !important;
      }
    </style>
</head>
<body>

	<div class="container">

		<h1>Sparklr</h1>

		<c:if test="${not empty param.authentication_error}">
			<h1>Woops!</h1>

			<p class="error">Your login attempt was not successful.</p>
		</c:if>
		<c:if test="${not empty param.authorization_error}">
			<h1>Woops!</h1>

			<p class="error">You are not permitted to access that resource.</p>
		</c:if>

		<div class="form-horizontal">
			<p>We've got a grand total of 2 users: marissa and paul. Go ahead
				and log in. Marissa's password is "koala" and Paul's password is
				"emu".</p>
			<form action="<c:url value="/login"/>" method="post" role="form">
				<fieldset>
					<legend>
						<h2>Login</h2>
					</legend>
					<div class="form-group">
						<label for="username">Username:</label> <input id="username"
							class="form-control" type='text' name='username'
							value="marissa" />
					</div>
					<div class="form-group">
						<label for="password">Password:</label> <input id="password"
							class="form-control" type='text' name='password' value="koala" />
					</div>
					<button class="btn btn-primary" type="submit">Login</button>
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}" />
				</fieldset>
			</form>

		</div>

		<div class="footer">
			Sample application for <a
				href="http://github.com/spring-projects/spring-security-oauth"
				target="_blank">Spring Security OAuth</a>
		</div>

	</div>
	<h1>Login</h1>
	<div class="container" ng-show="!home.authenticated">
		<div>
			With Facebook: <a href="/login/facebook">click here</a>
		</div>
		<div>
			With Github: <a href="/login/github">click here</a>
		</div>
		<div>
			With Weibo: <a href="/login/sinaweibo">click here</a>
		</div>
	</div>
	<div class="container" ng-show="home.authenticated">
		Logged in as: <span ng-bind="home.user"></span>
        <div>
            Test: <a href="/account/sinaweibo/oauth/confirm_access">新浪微博</a>
        </div>
        <div>
            Test: <a href="/account/github/oauth/confirm_access">GitHub</a>
        </div>
		<div>
			<button ng-click="home.logout()" class="btn btn-primary">Logout</button>
		</div>
	</div>

	<script type="text/javascript" src="/webjars/angularjs/angular2.js"></script>
	<script type="text/javascript">
		angular
				.module("app", [])
				.config(
						function($httpProvider) {
							$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
						}).controller("home", function($http, $location) {
					var self = this;
					$http.get("/user").success(function(data) {
						if (data.name) {
							self.user = data.name;
							self.authenticated = true;
						} else {
							self.user = "N/A";
							self.authenticated = false;
						}
					}).error(function() {
						self.user = "N/A";
						self.authenticated = false;
					});
					self.logout = function() {
						$http.post('logout', {}).success(function() {
							self.authenticated = false;
							$location.path("/");
						}).error(function(data) {
							console.log("Logout failed")
							self.authenticated = false;
						});
					};
				});
	</script>

</body>
</html>

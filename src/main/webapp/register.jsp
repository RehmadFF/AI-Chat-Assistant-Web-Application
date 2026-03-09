<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
<title>Register - ChatOrbit</title>

<style>

body{
font-family:Arial;
background:#0f172a;
color:white;
display:flex;
justify-content:center;
align-items:center;
height:100vh;
}

.container{
background:#1e293b;
padding:30px;
border-radius:10px;
width:350px;
}

input{
width:100%;
padding:10px;
margin:10px 0;
border:none;
border-radius:6px;
}

button{
width:100%;
padding:10px;
background:#6366f1;
color:white;
border:none;
border-radius:6px;
cursor:pointer;
}

.error{
color:#ff6b6b;
}

.success{
color:#4ade80;
}

</style>
</head>

<body>

<div class="container">

<h2>ChatOrbit Register</h2>

<%
String error = request.getParameter("error");
if(error!=null){
%>
<p class="error"><%=error%></p>
<%
}
%>

<form action="register" method="post">

<input type="email" name="email" placeholder="Email" required>

<input type="password" name="password" placeholder="Password" required>

<input type="password" name="confirmPassword" placeholder="Confirm Password" required>

<button type="submit">Sign Up</button>

</form>

<p>Already have an account? <a href="login.jsp">Login</a></p>

</div>

</body>
</html>
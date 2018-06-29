<html>
<head>
    <meta name="layout" content="public"/>
    <title>Home Page</title>
</head>
<body>

<div id="content" role="main">
    <section class="row colset-2-its">
        <h1>Welcome ${name}!</h1>


        <form action="${postUrl ?: '/conversion/convert'}" method="POST" id="loginForm" class="cssform"
              autocomplete="off">
            <p>
                <label for="username">
                    <g:message code='springSecurity.login.username.label'/>
                    :</label>
                <input type="text" class="text_" name="${usernameParameter ?: 'username'}" id="username"/>
            </p>

            <p>
                <label for="password">
                    <g:message code='springSecurity.login.password.label'/>
                    :</label>
                <input type="password" class="text_" name="${passwordParameter ?: 'password'}" id="password"/>
            </p>


            <p>
                <input type="submit" id="submit" value="${message(code: 'springSecurity.login.button')}"/>
            </p>
        </form>

    </section>
</div>

</body>
</html>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
      <form action="${pageContext.request.contextPath}/fileUploadServlet" method="post" enctype="multipart/form-data">
          username:<input type="text" name="username"/><br>
          file:<input type="file" name="file"/><br>
          <input type="submit"/>
      </form>
  </body>
</html>

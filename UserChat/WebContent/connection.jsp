<html>
<head>
<%@ page import="java.sql.* , javax.sql.*, java.io.*, javax.naming.*"%>
</head>
<body>
	<%
		InitialContext initCtx = new InitialContext();
		Context envContext = (Context) initCtx.lookup("java:/comp/env");
		DataSource ds = (DataSource) envContext.lookup("jdbc/UserChat");
		Connection conn = ds.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery("SELECT * FROM user; ");
		while (rset.next()) {
			out.println("MySQL Version : " + rset.getString(1));
		}
	rset.close();
	stmt.close();
	conn.close();
	initCtx.close();
	%>
</body>
</html>
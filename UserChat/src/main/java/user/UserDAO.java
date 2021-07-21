package user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


public class UserDAO {
	DataSource dataSource;

	public UserDAO() {
		try {
			InitialContext initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource  = (DataSource) envContext.lookup("jdbc/UserChat");		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int login(String userID, String userPassword) {
		Connection conn = null;
		PreparedStatement pstmt = null; //sql 인젝션 해킹 공격 방어
		ResultSet rs = null;
		String SQL = "SELECT * FROM USER WHERE userID= ?";
		try {
			conn = (Connection) dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getString("userPassword").equals(userPassword)) {
					return 1; //로그인 성공
				}
				return 2; //비밀번호 틀림
			}else {
				return 0; //해당 사용자 없음
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs !=null) rs.close();
				if(pstmt !=null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1; //데이터 베이스 오류
	}
	
	///////////////////////////////////
	
	public int registerCheck(String userID) {
		Connection conn = null;
		PreparedStatement pstmt = null; //sql 인젝션 해킹 공격 방어
		ResultSet rs = null;
		String SQL = "SELECT * FROM USER WHERE userID= ?";
		try {
			conn = (Connection) dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if(rs.next() || userID.equals("")) {
				return 0; //중복 아이디
			}else {
				return 1; // 미중복 아이디 가입가능한 회원
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(pstmt !=null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1; //데이터 베이스 오류
	}
	
	public int register(String userID,String userPassword, String userName, String userAge, String userGender, String userEmail,String userProfile) {
		Connection conn = null;
		PreparedStatement pstmt = null; //sql 인젝션 해킹 공격 방어
		ResultSet rs = null;
		String SQL = "INSERT INTO USER VALUES (?,?,?,?,?,?,?)";
		try {
			conn = (Connection) dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			pstmt.setString(2, userPassword);
			pstmt.setString(3, userName);
			pstmt.setInt(4, Integer.parseInt(userAge));
			pstmt.setString(5, userGender);
			pstmt.setString(6, userEmail);
			pstmt.setString(7, userProfile);
			return pstmt.executeUpdate();
			//insert는 executeQuery가 아니라 update를 써야함
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(pstmt !=null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1; //데이터 베이스 오류
	}
	
}

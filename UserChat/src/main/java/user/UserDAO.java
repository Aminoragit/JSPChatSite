package user;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import AES256.AES256Util;


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
	
	//ID값에 해당하는 Salt값 불러오기
	private String findByIDtoSalt(String userID) {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		ResultSet rs = null;
		String SQL = "SELECT * FROM USER WHERE userID= ?";
		try {
			conn = (Connection) dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				String IDSalt=rs.getString("salt");
				if(IDSalt!=null) {
				return IDSalt;
			    }
			return IDSalt;
			}else {
				return "0";
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
		return "-1"; //데이터 베이스 오류
	}
	
	public int login(String userID, String userPassword) throws UnsupportedEncodingException {
		Connection conn = null;
		PreparedStatement pstmt = null; //sql 인젝션 해킹 공격 방어
		ResultSet rs = null;
		AES256Util aes256encrypt = new AES256Util();
		String realPassword = null;
		try {
			realPassword = aes256encrypt.encrypt(userPassword+findByIDtoSalt(userID));
		} catch (UnsupportedEncodingException | GeneralSecurityException e1) {
			e1.printStackTrace();
		}
		String SQL = "SELECT * FROM USER WHERE userID= ?";
		try {
			conn = (Connection) dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getString("userPassword").equals(realPassword)) {
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
	//아이디 중복체크
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
			System.out.println("찾는 아이디:"+userID);
			System.out.println("rs결과"+rs);
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
	
	public int register(String userID,String userPassword, String userName, String userAge, String userGender, String userEmail,String userProfile,String salt) {
		Connection conn = null;
		PreparedStatement pstmt = null; //sql 인젝션 해킹 공격 방어
		ResultSet rs = null;
		
		String SQL = "INSERT INTO USER VALUES (?,?,?,?,?,?,?,?)";
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
			pstmt.setString(8, salt);
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
	
	
	public UserDTO getUser(String userID) {
		UserDTO user = new UserDTO();
		Connection conn = null;
		PreparedStatement pstmt = null; //sql 인젝션 해킹 공격 방어
		ResultSet rs = null;
		String SQL = "SELECT * FROM USER WHERE userID= ?";
		try {
			conn = (Connection) dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			System.out.println("찾는 아이디:"+userID);
			System.out.println("rs결과"+rs);
			if(rs.next()) {
				user.setUserID(userID);
				user.setUserPassword(rs.getString("userPassword"));
				user.setUserName(rs.getString("userName"));
				user.setUserAge(rs.getInt("userAge"));
				user.setUserGender(rs.getString("userGender"));
				user.setUserEmail(rs.getString("userEmail"));
				user.setUserProfile(rs.getString("userProfile"));
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
		return user; //데이터 베이스 오류
	}
	
	
	
	public int update(String userID, String userPassword, String userName, String userAge, String userGender, String userEmail) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		AES256Util aes256encrypt = null;
		
		try {
			aes256encrypt = new AES256Util();
		} catch (UnsupportedEncodingException e3) {
			e3.printStackTrace();
		};
		String realPassword=null;
		try {
			realPassword = aes256encrypt.encrypt(userPassword+findByIDtoSalt(userID));
		} catch (UnsupportedEncodingException | GeneralSecurityException e1) {
			e1.printStackTrace();
		}
		String SQL = "UPDATE user SET userPassword = ?, userName = ?, userAge = ?, userGender = ?, userEmail = ? WHERE userID = ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, realPassword);
			pstmt.setString(2, userName);
			pstmt.setInt(3, Integer.parseInt(userAge));
			pstmt.setString(4, userGender);
			pstmt.setString(5, userEmail);
			pstmt.setString(6, userID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return -1; // 데이터베이스 오류
	}

	
	//특정한 사용자가 profile을 수정
	public int profile(String userID, String userProfile) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String SQL = "UPDATE user SET userProfile = ? WHERE userID = ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userProfile);
			pstmt.setString(2, userID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return -1; // 데이터베이스 오류
	}
	
	//기존 profile 호출
	public String getProfile(String userID) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SQL = "SELECT userProfile FROM user WHERE userID = ?";
		try {
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(rs.getString("userProfile").equals(null)) {
					return "http://localhost:8080/UserChat/images/anonymous.png";
				}else {
					return "http://localhost:8080/UserChat/upload/" +rs.getString("userProfile");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return "http://localhost:8080/UserChat/images/anonymous.png";
	}

}
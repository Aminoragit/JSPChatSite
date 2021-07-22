package user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import AES256.AES256Util;

@WebServlet("/UserRegisterServlet")
public class UserRegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// 바이트 값을 16진수로 변경해준다
	private String Byte_to_String(byte[] temp) {
		StringBuilder sb = new StringBuilder();
		for (byte a : temp) {
			sb.append(String.format("%02x", a));
		}
		return sb.toString();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		String userID = request.getParameter("userID");
		AES256Util aes256encrypt = new AES256Util();

		String realPassword = null;

		String userPassword1 = request.getParameter("userPassword1");
		String userPassword2 = request.getParameter("userPassword2");

		String userName = request.getParameter("userName");
		String userAge = request.getParameter("userAge");
		String userGender = request.getParameter("userGender");
		String userEmail = request.getParameter("userEmail");
		String userProfile = request.getParameter("userProfile");

		// 임의의 랜덤바이트 값을 생성후 aes256난수화후 salt에 넣어줌
		SecureRandom rnd = new SecureRandom();
		byte[] temp = new byte[20];
		rnd.nextBytes(temp);
		String saltMake = Byte_to_String(temp);
		String salt = saltMake;
		
		try {
			realPassword = aes256encrypt.encrypt(userPassword1 + salt);
		} catch (UnsupportedEncodingException | GeneralSecurityException e) {
			e.printStackTrace();
		}

		if (userID == null || userID.equals("") || userPassword1 == null || userPassword1.equals("")
				|| userPassword2 == null || userPassword2.equals("") || userName == null || userName.equals("")
				|| userAge == null || userAge.equals("") || userGender == null || userGender.equals("")
				|| userEmail == null || userEmail.equals("") || realPassword == null || realPassword.equals("")) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "모든 내용을 입력하세요");
			response.sendRedirect("join.jsp");
			return;
		}
		if (!userPassword1.equals(userPassword2)) {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "비밀번호가 다릅니다.");
			response.sendRedirect("join.jsp");
			return;
		}

		int result = new UserDAO().register(userID, realPassword, userName, userAge, userGender, userEmail,
				"", salt);
		if (result == 1) {
			request.getSession().setAttribute("userID", userID); // 자동 로그인->접속하기가 회원관리라 자동 변경
			request.getSession().setAttribute("messageType", "가입 성공");
			request.getSession().setAttribute("messageContent", "회원가입 성공");
			response.sendRedirect("index.jsp");
			return;
		} else {
			request.getSession().setAttribute("messageType", "오류 메시지");
			request.getSession().setAttribute("messageContent", "이미 존재하는 유저입니다.");
			response.sendRedirect("join.jsp");
			return;
		}

	}
}

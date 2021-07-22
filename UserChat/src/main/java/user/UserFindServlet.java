package user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UserFindServlet")
public class UserFindServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html'charset=UTF-8");
		String userID = request.getParameter("userID"); 
		System.out.println("Find의 userID : "+userID);
		if(userID == null || userID.equals("")) {
			response.getWriter().write("-1");
			//중복체크 registerCheck 0->중복아이디 1-> 미중복아이디
		}else if(new UserDAO().registerCheck(userID) == 0 ) {
			try {
				response.getWriter().write(find(userID));//중복아이디->해당 아이디가 있을경우 response로 profile를 반환한다.
			} catch (Exception e) {
				e.printStackTrace();
				//해당 아이디가 DB에 있음에도 profile return을 실패했을경우 "DB에러"반환
				response.getWriter().write("-1");
			}
		} 
		//해당 아이디가 없을경우 2 반환
		else {
			response.getWriter().write("2");
		}
	}
	
	public String find(String userID) throws Exception {
		StringBuffer result = new StringBuffer();
		result.append("{\"userProfile\":\"" + new UserDAO().getProfile(userID) + "\"}");
		System.out.println("반환 프로필: "+result.toString());
		return result.toString();
	}
}

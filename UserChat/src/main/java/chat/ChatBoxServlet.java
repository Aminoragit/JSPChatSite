package chat;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import user.UserDAO;

@WebServlet("/ChatBoxServlet")
public class ChatBoxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		String userID = request.getParameter("userID");
		if (userID == null || userID.equals("")) {
			response.getWriter().write("");
		} else {
			try {
				HttpSession session = request.getSession();
				if(!URLDecoder.decode(userID, "UTF-8").equals((String) session.getAttribute("userID"))) {
					response.getWriter().write("");
					return;
				}
				userID = URLDecoder.decode(userID, "UTF-8");
				response.getWriter().write(getBox(userID));
			} catch (Exception e) {
				response.getWriter().write("");
			}
		}
	}

	public String getBox(String userID) {
		StringBuffer result = new StringBuffer();
		result.append("{\"result\":[");
		ChatDAO chatDAO = new ChatDAO();
		ArrayList<ChatDTO> chatList = chatDAO.getBox(userID);
		//System.out.println("userID 체크입니다 : getBox에서 실행한 userID== "+userID);
		if(chatList.size() == 0) return "";
		//기존의것은 가장 오래된순이였고 이것을 반대로만 하면 최신순으로 바뀐다.
		//for (int i = 0; i < chatList.size(); i++) {
		for (int i = chatList.size() - 1; i >= 0; i--) {
//			String unread = "";
//			 int getUnread = chatDAO.getUnreadChat(chatList.get(i).getFromID(), userID); 
//			if(userID.equals(chatList.get(i).getToID().replace(" ","").replace("&nbsp;",""))) {
//			    unread = Integer.toString(getUnread);
//				if(unread.equals("0")) unread="";
//			}
			result.append("[{\"value\": \"" + chatList.get(i).getFromID() + "\"},");
			result.append("{\"value\": \"" + chatList.get(i).getToID() + "\"},");
			result.append("{\"value\": \"" + chatList.get(i).getChatContent() + "\"},");
			result.append("{\"value\": \"" + chatList.get(i).getChatTime() + "\"}]");
		//위의 for문이 변경됬으므로 바꿔줘야한다.
			//기존:	if(i != chatList.size() -1) result.append(",");
			if(i != 0) result.append(",");
		}
		result.append("], \"last\":\"" + chatList.get(chatList.size() - 1).getChatID() + "\"}");
		return result.toString();
	}
}

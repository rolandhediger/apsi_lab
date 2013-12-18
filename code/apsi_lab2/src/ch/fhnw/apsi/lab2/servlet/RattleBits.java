package ch.fhnw.apsi.lab2.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.fhnw.apsi.lab2.Controller;

/**
 * Servlet implementation class RattleBits
 */
@WebServlet("/RattleBits")
public class RattleBits extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final Controller controller;
	private final java.sql.Connection con;

	/**
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @see HttpServlet#HttpServlet()
	 */
	public RattleBits() throws SQLException, ClassNotFoundException, IOException {
		super();
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection("jdbc:mysql://localhost/apsiLab2?user=apsiuser");
		controller = new Controller(con);
		Properties props = System.getProperties();

		BufferedReader r = new BufferedReader(new FileReader("passwd.txt"));
		props.put("mail.smtp.user", r.readLine());
		props.put("mail.password", r.readLine());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String page = request.getParameter("page");
		if ("login".equals(page)) {
			controller.loginPage(request, response);
		} else if ("register".equals(page)) {
			controller.registerPage(request, response);
		} else if ("activate".equals(page)) {
			controller.activatePage(request, response);
		} else if ("ChangePassword".equals(page)) {
			controller.overviewPage(request, response);
		} else {
			controller.indexPage(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("register") != null) {
			controller.registerPage(request, response);
		} else if (request.getParameter("login") != null) {
			controller.loginPage(request, response);
		} else if (request.getParameter("overview") != null) {
			controller.overviewPage(request, response);
		}
	}

	@Override
	public void destroy() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		super.destroy();
	}

}

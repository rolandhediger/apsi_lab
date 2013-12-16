package ch.fhnw.apsi.lab2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ch.fhnw.apsi.lab2.model.Company;

public class Controller {

	private static final String REGISTER = "rattle_bits/register.jsp";
	private static final String SUCCESS = "rattle_bits/success.jsp";
	private static final String LOGIN = "rattle_bits/login.jsp";
	private static final String INDEX = "rattle_bits/index.jsp";
	private static final String OVERVIEW = "rattle_bits/overview.jsp";

	private final Connection con;

	public Controller(Connection connection) {
		con = connection;
	}

	public void indexPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(INDEX).forward(request, response);
	}

	public void overviewPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new LinkedList<>();
		Company c = new Company(con);
		Integer id = (Integer) request.getSession().getAttribute("userId");
		c.loadCompany(id);
		String oldPwd = request.getParameter("oldpassword");
		String newPwd = request.getParameter("newpassword");
		String newPwdRepeat = request.getParameter("newpasswordrepeat");
		messages = c.updatePassword(oldPwd, newPwd, newPwdRepeat);
		request.setAttribute("messages", messages);
		request.getRequestDispatcher(OVERVIEW).forward(request, response);
	}

	public void activatePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		Company c = new Company(con);
		boolean activate = false;
		try {
			activate = c.activate(request.getParameter("acode"));
		} catch (SQLException e) {
			messages.add("Database error,please try again later");
		}
		if (activate) {
			c.sendLoginData();
			request.setAttribute("message", "Activation successful, you will recieve your username and password shortly.");
			request.getRequestDispatcher(SUCCESS).forward(request, response);
		} else {
			if (messages.size() == 0)
				messages.add("Activation Code is invalid");
			request.setAttribute("message", messages.get(0));
			request.getRequestDispatcher(SUCCESS).forward(request, response);
		}

	}

	public void registerPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		Company c = new Company(con);
		c.setUsername(request.getParameter("username"));
		String pwd = request.getParameter("password");
		if (pwd != null && pwd.equals(request.getParameter("passwordrepeat")))
			c.setPassword(pwd);

		c.setCompanyName(request.getParameter("firma"));
		c.setAddress(request.getParameter("address"));
		try {
			c.setZip(Integer.parseInt(request.getParameter("plz")));
		} catch (NumberFormatException e) {
			messages.add("Invalid Postal Code");
		}
		c.setTown(request.getParameter("town"));
		c.setMail(request.getParameter("mail"));
		messages.addAll(c.validate());
		request.setAttribute("firma", c.getCompanyName());
		request.setAttribute("address", c.getAddress());
		request.setAttribute("plz", String.valueOf(c.getZip()));
		request.setAttribute("town", c.getTown());
		request.setAttribute("mail", c.getMail());
		if (messages.size() > 0) {
			request.setAttribute("messages", messages);
			request.getRequestDispatcher(REGISTER).forward(request, response);
		} else {
			c.setActivation(UUID.randomUUID().toString());
			try {
				c.hashPassword();
				c.save();
			} catch (SQLException e) {
				messages.add("Database error,please try again later");
				request.setAttribute("messages", messages);
				request.getRequestDispatcher(REGISTER).forward(request, response);
				e.printStackTrace();
				return;
			}
			c.sendActivationCode();
			request.setAttribute("message", "Registration successful please check your email for the activation link");

			request.getRequestDispatcher(SUCCESS).forward(request, response);
		}
	}

	public void loginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		Company c = new Company(con);
		boolean login = false;
		try {
			login = c.checkLogin(request.getParameter("username"), request.getParameter("password"));
		} catch (SQLException e) {
			messages.add("Database error,please try again later");
		}
		if (login) {
			request.getSession().setAttribute("userId", c.getId());
			request.getSession().setAttribute("username", c.getUsername());
			request.getRequestDispatcher(OVERVIEW).forward(request, response);
		} else {
			if (messages.size() == 0)
				messages.add("username or password invalid");
			request.setAttribute("messages", messages);
			request.getRequestDispatcher(LOGIN).forward(request, response);
		}
	}
}
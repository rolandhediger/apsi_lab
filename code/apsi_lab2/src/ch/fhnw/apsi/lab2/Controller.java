package ch.fhnw.apsi.lab2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
		// TODO: overview / passwort change page
	}

	public void activatePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<String> messages = new ArrayList<>();
		Company c = new Company(con);
		boolean activate = false;
		try {
			activate = c.activate(request.getParameter("acode"));
		} catch (SQLException e) {
			messages.add("Datenbankverbindung fehlgeschlagen, bitte versuchen sie es später noch einmal");
		}
		if (activate) {
			c.sendLoginData();
			request.setAttribute("message", "Aktivierung erfolgreich! Sie bekommen ihr Username und Passwort per Mail zugesant.");
			request.getRequestDispatcher(SUCCESS).forward(request, response);
		} else {
			if (messages.size() == 0)
				messages.add("activierungs code ist ungültig");
			request.setAttribute("message", messages.get(0));
			request.getRequestDispatcher(SUCCESS).forward(request, response);
		}

	}

	public void regsiterPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			messages.add("Ungültige PLZ");
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
				messages.add("Datenbankverbindung fehlgeschlagen, bitte versuchen sie es später noch einmal");
				request.setAttribute("messages", messages);
				request.getRequestDispatcher(REGISTER).forward(request, response);
				e.printStackTrace();
				return;
			}
			c.sendActivationCode();
			request.setAttribute("message", "Registrierung erfolgreich, bitte warten sie auf den Aktivierungslink per Mail");

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
			messages.add("Datenbankverbindung fehlgeschlagen, bitte versuchen sie es später noch einmal");
		}
		if (login) {
			request.getSession().setAttribute("userId", c.getId());
			request.getSession().setAttribute("username", c.getUsername());
			request.getRequestDispatcher(OVERVIEW).forward(request, response);
		} else {
			if (messages.size() == 0)
				messages.add("username oder passwort ist ungültig");
			request.setAttribute("messages", messages);
			request.getRequestDispatcher(LOGIN).forward(request, response);
		}
	}
}
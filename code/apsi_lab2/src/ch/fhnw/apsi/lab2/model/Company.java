package ch.fhnw.apsi.lab2.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.istack.internal.NotNull;

public class Company {

	private static final String usrCleanString = "[ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w]{4,64}";
	private static final String pwdCleanString = "[ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w\\d]{8,64}";
	private static final String adrCleanString = "[ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w\\s\\d]{0,255}";
	private static final String townCleanString = "[ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w]{0,255}";
	private static final String companyCleanString = "[ôÔêÊâÂèéÈÉäöüÄÖÜß\\-\\._\\w]{0,20}";

	private final Connection con;

	private int id;
	private String username;
	private String password;
	private String companyName;
	private String address;
	private int zip;
	private String town;
	private String mail;
	private String activation;
	private boolean valid = false;
	private boolean hashed = false;

	public Company(@NotNull Connection con) {
		this.con = con;
	}

	public Company(@NotNull Connection con, String username, String password) throws Exception {
		this.con = con;

		if (password.matches(pwdCleanString) && username.matches(usrCleanString)) {
			PreparedStatement stm;
			try {
				stm = con.prepareStatement("SELECT `id` FROM company WHERE username = ? ");
				stm.setString(1, username);
				ResultSet rs = stm.executeQuery();
				if (rs.next()) {
					throw new Exception("User already exists");
				}
				this.username = username;
				this.password = hash(password);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else {
			throw new Exception("Username or Password are not valid");
		}
	}

	public final int getId() {
		return id;
	}

	public final String getPassword() {
		return password;
	}

	public final void setPassword(String password) {
		this.password = password;
		valid = false;

	}

	//delete
	public final void hashPassword() {
		//Invariant : Only valid passwords are hashed see controller.
		if (!hashed)
			this.password = hash(this.password);
		hashed = true;
		valid = true;
	}

	public final String getUsername() {
		return this.username;
	}

	public final String getCompanyName() {
		return companyName;
	}

	public final void setCompanyName(String name) {
		this.companyName = name;
		valid = false;
	}

	public final String getAddress() {
		return address;
	}

	public final void setAddress(String address) {
		this.address = address;
		valid = false;
	}

	public final int getZip() {
		return zip;
	}

	public final void setZip(int zip) {
		this.zip = zip;
		valid = false;
	}

	public final String getTown() {
		return town;
	}

	public final void setTown(String town) {
		this.town = town;
		valid = false;
	}

	public final String getMail() {
		return mail;
	}

	public final void setMail(String mail) {
		this.mail = mail;
		valid = false;
	}

	public final String getActivation() {
		return activation;
	}

	public final void setActivation(String activation) {
		this.activation = activation;
		valid = false;
	}

	public boolean login(String user, String password) throws SQLException {
		if (password == null || user == null) {
			return false;
		}

		if (user.matches(usrCleanString) && password.matches(pwdCleanString)) {
			PreparedStatement stm = con
					.prepareStatement("SELECT `id`, `username`, `name`, `address`, `zip`, `town`, `mail` FROM company WHERE username = ? AND password = ? AND activation IS NULL");
			stm.setString(1, user);
			stm.setString(2, hash(password));
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				id = rs.getInt(1);
				username = rs.getString(2);
				companyName = rs.getString(3);
				address = rs.getString(4);
				zip = rs.getInt(5);
				town = rs.getString(6);
				mail = rs.getString(7);
				valid = true;
				return true;
			}
		}
		return false;
	}

	private boolean checkPassword(String oldPassword) {
		if (oldPassword != null && oldPassword.matches(pwdCleanString)) {
			return this.password.equals(hash(oldPassword));
		}

		return false;
	}

	public List<String> updatePassword(String oldPwd, String newPwd, String newPwdRepeat) {
		List<String> errors = new LinkedList<>();
		if (valid) {
			if (newPwd != null && newPwd.equals(newPwdRepeat)) {
				if (checkPassword(oldPwd) && newPwd.matches(pwdCleanString)) {
					try {
						PreparedStatement stm = con.prepareStatement("UPDATE Company SET password = ? WHERE id = ?");
						stm.setString(1, hash(newPwd));
						stm.setInt(2, this.id);
					} catch (SQLException e) {
						errors.add("Database error, please try again later");
					}
				} else {
					errors.add("old password incorrect");
				}
			} else {
				errors.add("New Passwords are invalid or do not match");
			}
		} else {
			errors.add("Application error, please contact your admin");
		}

		return errors;
	}

	public boolean loadCompany(int userId) {
		try {
			PreparedStatement stm = con
					.prepareStatement("SELECT `id`, `username`, `password`,`name`, `address`, `zip`, `town`, `mail` FROM company WHERE id = ? AND activation IS NULL");
			stm.setInt(1, userId);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				id = rs.getInt(1);
				username = rs.getString(2);
				password = rs.getString(3);
				companyName = rs.getString(4);
				address = rs.getString(5);
				zip = rs.getInt(6);
				town = rs.getString(7);
				mail = rs.getString(8);
				valid = true;
			}
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	public boolean activate(String activationCode) throws SQLException {
		PreparedStatement stm = con.prepareStatement("UPDATE company SET activation = NULL WHERE activation = ?");
		stm.setString(1, activationCode);
		return stm.executeUpdate() == 1 ? true : false;
	}

	public List<String> validate() {
		List<String> errors = new LinkedList<>();
		PreparedStatement stm = null;
		try {
			stm = con.prepareStatement("SELECT `id`FROM company WHERE username = ?");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		if (username != null) {
			if (!username.matches(usrCleanString)) {
				errors.add("Invalid Username");
			}
			try {
				stm.setString(1, username);
				ResultSet rs = stm.executeQuery();
				if (rs.next()) {
					errors.add("Username already exists");
				}
			} catch (Exception e) {
				errors.add("Invalid Username");
			}

		} else {
			errors.add("Username is required");
		}

		//		if (password != null) {
		//			if (!password.matches(pwdCleanString)) {
		//				errors.add("Invalid Password");
		//			}
		//		} else {
		//			errors.add(" Password is required and has to be the same as Repeat Password");
		//		}

		if (companyName != null) {
			if (!companyName.matches(companyCleanString)) {
				errors.add("Invalid Company Name");
			}
		} else {
			errors.add("Company Name is required");
		}

		if (address != null) {
			if (!address.matches(adrCleanString)) {
				errors.add("Address contains invalid characters");
			}
		} else {
			errors.add("Address is required");
		}

		if (zip != 0) {
			try {
				boolean result = zip >= 1000 ? validateZipFromInternet(zip) : false;
				if (!result) {
					errors.add("Zip not valid");
				}
			} catch (Exception e) {
				e.printStackTrace();
				errors.add("Unable to check Zip, please try again later.");
			}
		} else {
			errors.add("Zip is required");
		}

		if (town != null) {
			if (town.trim().isEmpty()) {
				errors.add("Town name is required");
			} else if (!town.matches(townCleanString)) {
				errors.add("Invalid Town Name");
			}
		} else {
			errors.add("Town name is required");
		}
		if (mail != null) {
			if (mail.trim().isEmpty()) {
				errors.add("Please enter email");
			} else if (!mail.matches("([^.@]+)(\\.[^.@]+)*@([^.@]+\\.)+([^.@]+)")) {
				errors.add("Invalid email, please try again.");
			}
		} else {
			errors.add("Valid E-Mail Adress is required");
		}

		if (errors.size() == 0)
			valid = true;
		return errors;
	}

	@NotNull
	public final Company save() throws SQLException {
		PreparedStatement stm;
		if (id == 0) {
			stm = con.prepareStatement("INSERT INTO `company`(`username`, `password`, `name`, `address`, `zip`, `town`, `mail`, `activation`) VALUES (?,?,?,?,?,?,?,?)");
		} else {
			stm = con.prepareStatement("UPDATE `company` SET `username`=?,`password`=?,`name`=?,`address`=?,`zip`=?,`town`=?,`mail`=?,`activation`=? WHERE id = ?");
			stm.setInt(9, id);
		}
		stm.setString(1, username);
		stm.setString(2, password);
		stm.setString(3, companyName);
		stm.setString(4, address);
		stm.setInt(5, zip);
		stm.setString(6, town);
		stm.setString(7, mail);
		stm.setString(8, activation);

		if (valid)
			stm.execute();

		return this;
	}

	public final boolean sendActivationCode() {
		boolean success = false;
		String activationCode = this.getActivation();
		String mail = this.getMail();
		Properties props = System.getProperties();
		props.put("mail.smtp.host", "rolandh.tk");
		props.put("mail.smtp.port", "25");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "false");
		props.put("mail.smtp.tls", "false");

		javax.mail.Authenticator auth = new javax.mail.Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("apsi@rolandh.tk", "apsitest");
			}
		};

		Session session = Session.getDefaultInstance(props, auth);

		Message msg = new MimeMessage(session);
		try {
			msg.setFrom(new InternetAddress("apsi@rolandh.tk", "apsi"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail, "TOEXAMPLE"));
			msg.setSubject("Please Activate Your Account");
			msg.setText("Please klick here to activate your Rattle Bits Account:\n" + "http://localhost:8080/apsi_lab2/RattleBits?page=activate&acode=" + activationCode);
			msg.setSentDate(new Date());
			msg.saveChanges();
			Transport.send(msg);
			success = true;
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (MessagingException e) {

			e.printStackTrace();
		}

		return success;
	}

	public final boolean sendLoginData() {
		// TODO: implement login data sending
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (address == null ? 0 : address.hashCode());
		result = prime * result + (con == null ? 0 : con.hashCode());
		result = prime * result + id;
		result = prime * result + (mail == null ? 0 : mail.hashCode());
		result = prime * result + (companyName == null ? 0 : companyName.hashCode());
		result = prime * result + (password == null ? 0 : password.hashCode());
		result = prime * result + (town == null ? 0 : town.hashCode());
		result = prime * result + (username == null ? 0 : username.hashCode());
		result = prime * result + zip;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Company other = (Company) obj;
		if (activation == null) {
			if (other.activation != null)
				return false;
		} else if (!activation.equals(other.activation))
			return false;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (con == null) {
			if (other.con != null)
				return false;
		} else if (!con.equals(other.con))
			return false;
		if (id != other.id)
			return false;
		if (mail == null) {
			if (other.mail != null)
				return false;
		} else if (!mail.equals(other.mail))
			return false;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (town == null) {
			if (other.town != null)
				return false;
		} else if (!town.equals(other.town))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (zip != other.zip)
			return false;
		return true;
	}

	private boolean validateZipFromInternet(int inputZip) throws IOException {

		String url = String.format("http://www.post.ch/db/owa/pv_plz_pack/pr_check_data?p_language=de&p_nap=%d&p_localita=&p_cantone=&p_tipo=luogo", inputZip);

		URL obj = new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(obj.openStream()));
		String inputLine;

		boolean validPostalCode = true;
		while ((inputLine = in.readLine()) != null) {
			if (inputLine.contains("Keine PLZ gefunden")) {
				validPostalCode = false;
				break;
			}

		}
		in.close();

		return validPostalCode;

	}

	private static String hash(String s) {
		byte[] data = null;
		try {
			data = MessageDigest.getInstance("SHA-256").digest(s.getBytes());
		} catch (NoSuchAlgorithmException e) {
			data = s.getBytes();
		}
		return new String(data);
	}
}

package es.uvigo.esei.dai.hybridserver.uuid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UuidDBDAO implements UuidsDAO {

	private String DB_URL;
	private String DB_USER;
	private String DB_PSW;
	private int servicePort;

	public UuidDBDAO(String DB_URL, String DB_USER, String DB_PSW, int servicePort) {
		this.DB_USER = DB_USER;
		this.DB_PSW = DB_PSW;
		this.DB_URL = DB_URL;
		this.servicePort = servicePort;
	}

	public UuidDBDAO() {
	}

	@Override
	public Page create(Page page) {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PSW)) {
			try (PreparedStatement statement = connection
					.prepareStatement("INSERT INTO HTML (uuid, content) VALUES (?, ?)")) {
				statement.setString(1, page.getUuid());
				statement.setString(2, page.getContent());

				if (statement.executeUpdate() != 1)
					throw new SQLException("Error insertando uuid");

			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return page;
	}

	@Override
	public Page delete(String uuid) {
		Page value = get(uuid);
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PSW)) {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM HTML WHERE uuid=?")) {
				statement.setString(1, uuid);

				if (statement.executeUpdate() != 1)
					throw new SQLException("Error eliminado uuid");
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return value;
	}

	@Override
	public Page get(String uuid) {
		String toret = "";
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PSW)) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM HTML WHERE uuid=?")) {
				statement.setString(1, uuid);

				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						toret = result.getString("content");
					}
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new Page(uuid, toret);
	}

	@Override
	public String getString(String uuid) {
		String toret = "";
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PSW)) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM HTML WHERE uuid=?")) {
				statement.setString(1, uuid);

				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						toret = result.getString("content");
					}
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return toret;
	}

	@Override
	public ArrayList<String> lista() {
		final ArrayList<String> toret = new ArrayList<>();
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PSW)) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet result = statement.executeQuery("SELECT * FROM HTML")) {
					while (result.next()) {
						toret.add(result.getString("uuid"));
					}
				}
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return toret;
	}

	public String listaString() {

		String toret = "";
		ArrayList<String> lista = lista();
		for (int i = 0; i < lista.size(); i++) {
			toret += "<a href=/html?uuid=" + lista.get(i) + ">"
					+ lista.get(i)+"</a><br>";
			
		}
		return toret;
	}

}

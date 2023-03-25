package es.uvigo.esei.dai.hybridserver.xml;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class XmlDBDAO implements XmlDAO {

	private String DB_URL;
	private String DB_USER;
	private String DB_PSW;

	public XmlDBDAO(String DB_URL, String DB_USER, String DB_PSW) {
		this.DB_USER = DB_USER;
		this.DB_PSW = DB_PSW;
		this.DB_URL = DB_URL;
	}

	@Override
	public Xml create(String uuid, String content) {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PSW)) {
			try (PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO XML (uuid, content) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
				statement.setString(1, uuid);
				statement.setString(2, content);

				if (statement.executeUpdate() != 1)
					throw new SQLException("Error insertando uuid");

			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Xml(uuid, content);
	}

	@Override
	public Xml delete(String uuid) {
		Xml value = get(uuid);
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PSW)) {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM XML WHERE uuid=?")) {
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
	public Xml get(String uuid) {
		String toret = "";
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PSW)) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM XML WHERE uuid=?")) {
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

		return new Xml(uuid, toret);
	}

	@Override
	public String getString(String uuid) {
		String toret = "";
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PSW)) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM XML WHERE uuid=?")) {
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
				try (ResultSet result = statement.executeQuery("SELECT * FROM XML")) {
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
			toret += "<a href=/xml?uuid=" + lista.get(i) + ">"
					+ lista.get(i)+"</a><br>";
			
		}
		return toret;
	}

}

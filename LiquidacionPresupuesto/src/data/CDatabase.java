package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import utils.CProperties;

public class CDatabase {

	private String host;
	private Integer port;
	private String user;
	private String password;
	private String schema;

	private Connection connection;
	private Statement stSelect;

	private PreparedStatement pstInsert;
	private Integer batch;
	private Integer rowBatch;

	private String sqlInsert;

	public String getHost() {
		if (host == null || host.isEmpty()) {
			host = CProperties.getHost();
		}
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		if (port == null || port == 0) {
			port = CProperties.getPort();
		}
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUser() {
		if (user == null || user.isEmpty()) {
			user = CProperties.getUser();
		}
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		if (password == null || password.isEmpty()) {
			password = CProperties.getPassword();
		}
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSchema() {
		if (schema == null || schema.isEmpty()) {
			schema = CProperties.getSchema();
		}
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public Connection getConnection() throws SQLException {
		if (connection == null || connection.isClosed()) {
			try{
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getSchema()
					+ "?" + "user=" + getUser() + "&password=" + getPassword());
			}
			catch(Exception e){
				
			}
		}

		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Statement getStSelect() throws SQLException {
		if (stSelect == null) {
			stSelect = getConnection().createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			stSelect.setFetchSize(Integer.MIN_VALUE);
		}
		return stSelect;
	}

	public void setStSelect(Statement stSelect) {
		this.stSelect = stSelect;
	}

	public PreparedStatement getPstInsert() throws SQLException {
		if (pstInsert == null) {
			pstInsert = getConnection().prepareStatement(getSqlInsert());
		}
		return pstInsert;
	}

	public void setPstInsert(PreparedStatement pstInsert) {
		this.pstInsert = pstInsert;
	}

	public String getSqlInsert() {
		return sqlInsert;
	}

	public void setSqlInsert(String sqlInsert) {
		this.sqlInsert = sqlInsert;
		this.rowBatch = 0;
	}

	public Integer getBatch() {
		if (batch == null || batch <= 0)
			batch = 1000;
		return batch;
	}

	public void setBatch(Integer batch) {
		this.batch = batch;
	}

	public boolean isOpen() throws SQLException {
		return !getConnection().isClosed();
	}

	public void close() throws SQLException {
		try {
			if (rowBatch != null && rowBatch > 0) {
				getPstInsert().executeBatch();
			}
		} finally {
			connection.close();
		}
	}

	public ResultSet runQuery(String query) throws SQLException {
		ResultSet ret = null;

		if (isOpen()) {
			ret = getStSelect().executeQuery(query);
		}

		return ret;
	}

	public void addBatch(Object... values) throws SQLException {

		if (isOpen()) {

			int index = 1;
			for (Object value : values) {
				if (value instanceof Number) {
					getPstInsert().setDouble(index, (Double) value);
				} else if (value instanceof String) {
					getPstInsert().setString(index, (String) value);
				} else if (value instanceof Date) {
					getPstInsert().setDate(index, (new java.sql.Date(((Date) value).getTime())));
				}

				index++;
			}

			getPstInsert().addBatch();
			rowBatch++;

			if (rowBatch % getBatch() == 0) {
				getPstInsert().executeBatch();
				rowBatch = 0;
			}

		}
	}

}

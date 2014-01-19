package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import utils.Constants;
import utils.Credentials;

public class DatabaseConnector
{
	// Connection to the database
	// is connecting to. You can include user and password after this
	// by adding (say) ?user=paulr&password=paulr. Not recommended!

	Credentials cred = null;

	public DatabaseConnector(Credentials credential){
		Map<String, String> tmpCred = credential.getCredentials();
		cred = new Credentials(tmpCred.get(Constants.dbUrl), tmpCred.get(Constants.schemaName), tmpCred.get(Constants.username), tmpCred.get(Constants.pwd));
	}

	public Connection connect(){
		Map<String, String> creds = cred.getCredentials();

		// Properties for user and password. Here the user and password are both 'paulr'
		Properties p = new Properties();
		p.put("user",creds.get(Constants.username));
		p.put("password",creds.get(Constants.pwd));
		String connection = creds.get(Constants.dbUrl) + creds.get(Constants.schemaName);
		// Now try to connect
		Connection c;
		try {
			c = DriverManager.getConnection(connection,p);
			return c;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}




}
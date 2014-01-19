package utils;

import java.util.HashMap;
import java.util.Map;

public class Credentials {
	private Map<String,String> credentials = null;
	
	public Credentials(String url, String schema, String usr, String pwd){
		credentials = new HashMap<String, String>();
		credentials.put(Constants.dbUrl, url);
		credentials.put(Constants.schemaName, schema);
		credentials.put(Constants.username,	usr);
		credentials.put(Constants.pwd, pwd);
		
	}
	
	public Map<String, String> getCredentials(){
		return credentials;
	}
	
}

package utils;

public class Constants {
	
	//table column names
	public final static String nodeId = "nid"; // node id
	public final static String[] companyProfilesCols = {"field_company_mission_value", "field_competition_value", 
	                                                  "field_traction_value", "field_epitch_value", "field_gotomarket_value",
	                                                  "field_project_problem_short_value", "field_project_solution_short_value",
	                                                  "field_project_busmodel_short_value"}; // fiels which contain company information
	public final static String versionId = "vid";
	public final static String vocal = "name";
	public final static String path = "path";
	public final static String nodeType = "type";
	public final static String userId = "uid";
	public final static String word = "word";
	public final static String conceptProb = "concept_prob";
	
	
	//filters
	public final static String[] langFilter = {"der", "das", "und", "de", "la", "f�r"};
	public final static String[] signFilterRegx = {"[.]", "[,]", "[?]", "[$]", "[\"]", "[\\/]", "[\\\\]", "[&]", "[°]", "[(]", "[)]", "[*]", "[-]", "[_]", "[=]", "[�]"};
	public final static String[] signFilter = {".", ",", "?", "$", "\"", "\\/", "\\", "&", "°", "(", ")", "*", "-", "_", "=", "�"};
	//database login
	public final static String dbUrl = "database_url";
	public final static String schemaName = "schema_name";
	public final static String username = "user_name";
	public final static String pwd = "password";
	public final static String defaultUrl = "jdbc:mysql://127.0.0.1/";
	public final static String defaultUsr = "root";
	public final static String defaultSchema = "staging_investiere_ch";
	public final static String defaultPwd = "root";
	public final static String deliminator = ":";
	
	//file system 
	public final static String companyStatementPath = "/home/fshi/company_statement/";
	public final static String dictPath = "/home/fshi/words";
	public final static String matlabData = "/home/fshi/matlab.txt";
	
}

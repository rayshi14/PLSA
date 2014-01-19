import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import plsa.PLSA;
import utils.Constants;
import utils.Credentials;
import data.DataProcessor;
import data.DatabaseConnector;
import data.DocumentModel;


public class Main {

	private final static int numConcept = 10; // number of concepts


	public static void main(String[] args) throws ClassNotFoundException,SQLException
	{


		Credentials credential = new Credentials(Constants.defaultUrl, Constants.defaultSchema, Constants.defaultUsr, Constants.defaultPwd); // credentials for accessing the database

		DatabaseConnector dbConn = new DatabaseConnector(credential);

		// Now try to connect
		Connection c = dbConn.connect();

		//query a table joining various company information
		
		Statement stmt = c.createStatement();
		String sqlQuery = "select txnnode.nid as TxnID, c.field_company_mission_value, n.*, t.*, b.* from node n inner join content_type_company c on n.nid=c.nid and n.vid=c.vid left join content_field_comp_ref crf on n.nid=crf.field_comp_ref_nid left join node txnnode on crf.nid=txnnode.nid and crf.vid=txnnode.vid left join content_type_txn t on txnnode.nid=t.nid and txnnode.vid = t.vid left join node bpnode on bpnode.nid = t.field_bp_ref_nid left join content_type_businessplan b on bpnode.nid=b.nid and bpnode.vid=b.vid";
		ResultSet comRs = stmt.executeQuery(sqlQuery);
		
		// initialize a data processor to process current data
		DocumentModel docu = new DocumentModel();
		docu.setNumConcept(numConcept);
		docu.setConnection(c);
		
		//get company profiles in text
		docu.processComProfiles(comRs);
		
		// get vocabulary with profiles
		docu.getTextVocabulary();
//		System.out.println(vocabularies.toString());
//		System.out.println(vocabularies.size());
		
		// generate frequency matrix of occurrence of words in documents
		int[][] frequencies = docu.generateFreqMatrix();

		// initiliaze a plsa
		PLSA plsa = new PLSA(frequencies, numConcept, 50);
//		TemPLSA templsa = new TemPLSA(frequencies, numConcept, 5);
		//train data
		plsa.train();
//		templsa.train();
		
		
		//output documents concepts prob
		double[][] p_z_d = plsa.getTopicsProbBasedOnDocs();
		double[][] p_w_z = plsa.getWordsProbBasedOnTopics();
		double[][] p_d_z = DataProcessor.matrixTraverse(p_z_d);
		
		docu.storeWordsProb(p_w_z);
		
		docu.getWordsProb();
		
//		Map<Integer, Integer> stmtRow = docu.getRowNid();
//		for(int i=0; i<p_z_d[0].length; i++){
//			// topics probabilities related to a document
//			DataProcessor.vectorPrintOut(p_d_z[i]);
//			System.out.println(String.valueOf(i) + " . " + docu.getProfiles().get(stmtRow.get(i)));
//		}
//		
//		
//		//List<HashMap<String, Double>> wordProbs = new ArrayList<HashMap<String, Double>>();
//		
//		for(int i=0; i<p_w_z[0].length; i++){
//			System.out.println("topic" + String.valueOf(i+1));
//			ArrayList<Map.Entry<String, Double>> wordProb = docu.getWordProbListForTopic(DataProcessor.matrixTraverse(p_w_z)[i]);
//			for(int j=0; j<10; j++){
//				System.out.print(wordProb.get(j).getKey());
//				System.out.print(":");
//				System.out.println(wordProb.get(j).getValue());
//			}
//		}
//		
//		// query user access log
//		sqlQuery = "select a.path, u.uid, u.name, ur.rid, r.name from users u inner join accesslog a on u.uid=a.uid left join users_roles ur on u.uid=ur.uid left join role r on ur.rid=r.rid where r.name like \'%investor%\'";		
//		ResultSet userRs = stmt.executeQuery(sqlQuery);
//		
//		// get user preference record: map structure {{user id, company id}, number of views}
//		Map<Map<Integer, Integer>, Integer> userPrefs = docu.processUserPrefs(userRs);
//		System.out.println(userPrefs);
//		docu.generateUserTopics(userPrefs, p_d_z);
//		
//		// get the topic probability for each user with {user id: topic probability}
//		Map<Integer, double[]> userTopics = docu.getUserTopics();
//		
//		int numRecom = 3; // number of companies recommended 
//		
//		Iterator<Entry<Integer, double[]>> it = userTopics.entrySet().iterator();
//		while (it.hasNext()) {
//			System.out.print("recommended companies:");
//			// get {user id: topic probability} pair
//			Entry<Integer, double[]> pair = it.next();
//			
//			// make recommendations and return the sorted company list by similarity between user profile and company profile
//			ArrayList<Map.Entry<Integer, Double>> compSim = docu.makeRecommendations(p_d_z, pair.getValue());
//			for(int i=0; i<numRecom; i++){
//				
//				System.out.print(compSim.get(i).getKey()); // company id
//				System.out.print(":");
//				System.out.print(compSim.get(i).getValue()); // weight, similarity
//				System.out.print(":");
//				System.out.println(docu.getProfiles().get(compSim.get(i).getKey())); // company statements
//			}
//		}
		
		
//		String newDocument = "medical patient healthcare service people";
//		plsa.beginIPLSA(newDocument);
		c.close();

	}
}

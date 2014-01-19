package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

import utils.Constants;


public class DocumentModel {
	private Map<Integer, String> companyProfiles;
	private int noConcept; // number of concept
	private ArrayList<String> vocabularies; //vocabularies list
	private Map<Integer, Integer>rowNid = new HashMap<Integer, Integer>(); //{row id: node id}
	private Map<Integer, Integer>nidRow = new HashMap<Integer, Integer>(); //{node id:row id}
	private Map<Integer, double[]>userTopics = new HashMap<Integer, double[]>(); //probability of each topic related to a user
	private Connection conn;

	public DocumentModel(){
		companyProfiles = new HashMap<Integer, String>();
		vocabularies = new ArrayList<String>();
	}

	public void setNumConcept(int noConcept){
		this.noConcept = noConcept;
	}

	public void setConnection(Connection c){
		this.conn = c;
	}

	/*
	 * get company profiles from database
	 */
	public void processComProfiles(ResultSet rs){
		//process the data
		Map<Integer, Integer>versionId = new HashMap<Integer, Integer>();
		Map<Integer, String>companyProfiles = new HashMap<Integer, String>();
		int nid;
		int vid;

		try {
			while(rs.next()){
				String companyProfile = null;
				boolean extFlag = false;
				nid = rs.getInt(Constants.nodeId);
				vid = rs.getInt(Constants.versionId);

				// get the content in relevant fields to form a single company profile string
				for(String fieldValue : Constants.companyProfilesCols){
					String profile;
					if((profile = rs.getString(fieldValue))!=null){
						if(companyProfile == null){
							companyProfile = "";
						}
						companyProfile += " ";
						companyProfile += profile;
					}
				}

				if(companyProfile != null){


					//filter out special characters and languages other than English
					companyProfile = filterStrings(Jsoup.parse(companyProfile).text());
					List<String> profile = Arrays.asList(companyProfile.split("[\\s]"));
					for(String lang: Constants.langFilter){
						if(profile.indexOf(lang) >= 0){
							extFlag = true;
						}
					}
					if(!extFlag){

						String regx = "[\\s]*";
						if(Pattern.compile(regx).matcher(companyProfile).matches()){
							//empty
						}
						else{

							//if has key nid, compare version id to get the newest version 
							if(versionId.containsKey(nid)){

								int oldVid = versionId.get(nid);
								if(vid > oldVid){
									versionId.put(nid, vid);
									companyProfiles.put(nid, companyProfile.toLowerCase());
								}
							}
							else{
								versionId.put(nid, vid);
								companyProfiles.put(nid, companyProfile.toLowerCase());
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// store the profiles in .txt files
		Map<Integer, String>tmpCompanyProfiles = new HashMap<Integer, String>(companyProfiles);
		Iterator<Entry<Integer, String>> it = tmpCompanyProfiles.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Integer, String> pairs = it.next();
			if(pairs.getValue() != null){
				String filename = Constants.companyStatementPath + String.valueOf(pairs.getKey()) + ".txt";
				writeToFile(filename, pairs.getValue());;
			}
			it.remove(); // avoids a ConcurrentModificationException
		}

		this.companyProfiles = companyProfiles;
	}


	/*
	 *  filter special characters
	 */
	private String filterStrings(String s){
		for(String sign : Constants.signFilter){
			List<String> list = Arrays.asList(Constants.signFilter);
			int index = list.indexOf(sign);
			String regx = Constants.signFilterRegx[index];
			s = s.replaceAll(regx, " ");
		}
		return s;
	}


	/*
	 * store the content in file with filename
	 */
	private boolean writeToFile(String filename, String content){
		FileOutputStream fop = null;
		try {
			File newFile = new File(filename);
			fop = new FileOutputStream(newFile);
			// if file doesnt exists, then create it
			if (!newFile.exists()) {
				newFile.createNewFile();
			}
			// get the content in bytes
			byte[] contentInBytes = content.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}


	/*
	 * get vocabulary list from dictionary with company profiles
	 */
	public void getTextVocabulary(){
		//process the data
		Dictionary dict = new Dictionary();
		ArrayList<String> wordList = dict.getWordList();
		ArrayList<String> vocals = new ArrayList<String>();

		Map<Integer, String>tmpComProfiles = new HashMap<Integer, String>(companyProfiles);

		Iterator<Entry<Integer, String>> it = tmpComProfiles.entrySet().iterator();
		while (it.hasNext()) {
			boolean isEmpty = true;
			Entry<Integer, String> pairs = it.next();

			String[] wordSet = pairs.getValue().split("[\\s]+");

			for (String s : wordSet){
				if(wordList.contains(s.toLowerCase())){
					String regx = "[\\w]+";
					if(s.length() >= 4 && Pattern.compile(regx).matcher(s.toLowerCase()).matches()){
						isEmpty = false;
						if(!vocals.contains(s.toLowerCase())){
							vocals.add(s.toLowerCase());
						}

					}
				}
			}
			if(isEmpty){
				companyProfiles.remove(pairs.getKey());
			}
			it.remove(); // avoids a ConcurrentModificationException
		}

		this.vocabularies = vocals;
	}


	/*
	 * get the frequency matrix from the profiles(documents) and vocabularies(words)
	 */
	public int[][] generateFreqMatrix(){
		Map<Integer, String>tmpCompanyProfiles = new HashMap<Integer, String>(companyProfiles);
		int row = companyProfiles.size();
		int column = vocabularies.size();

		int[][] frequencies = new int[row][column];
		int rowNum = 0;
		int colNum;
		Iterator<Entry<Integer, String>> it = tmpCompanyProfiles.entrySet().iterator();
		while (it.hasNext()) {
			colNum = 0;
			Entry<Integer, String> pairs = it.next();
			String mStmt = pairs.getValue().toLowerCase();
			String regx = "[\\s]+";

			List<String> stmt = Arrays.asList(mStmt.split(regx));

			for (String s : vocabularies){
				frequencies[rowNum][colNum] = Collections.frequency(stmt, s);
				colNum ++;
			}
			rowNid.put(rowNum, pairs.getKey());
			nidRow.put(pairs.getKey(), rowNum);
			it.remove(); // avoids a ConcurrentModificationException

			rowNum++;
		}

		int[][] freqs = clearCommonWords(frequencies);

		return freqs;

	}

	/*
	 *  filter out too common words in the document, word matrix
	 */
	private int[][] clearCommonWords(int [][] freq){

		int row = freq.length;
		int col = freq[0].length;
		for(int i = 0; i<col; i++){
			int numNonZero = 0;
			for(int j=0; j<row; j++){
				if(freq[j][i]!=0){
					numNonZero++;
				}
			}
			if(numNonZero > (row / noConcept)){
				for(int j=0; j<row; j++){
					freq[j][i] = 0;
				}
			}
		}

		return freq;
	}

	/*
	 * return the word list related to a topic z in descending order of probability
	 */
	public ArrayList<Map.Entry<String, Double>> getWordProbListForTopic(double[] probs){
		Map<String, Double> wordProbList = new HashMap<String, Double>();
		for(int i=0; i<probs.length; i++){
			wordProbList.put(vocabularies.get(i), probs[i]);
		}
		ArrayList<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(wordProbList.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {      
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {      
				if((o2.getValue() - o1.getValue())>0){
					return 1;
				}
				else if((o2.getValue() - o1.getValue())<0){
					return -1;
				}
				else{
					return 0;
				}
			}
		});
		return list;
	}

	/*
	 * generate topics probabiliy for each user
	 */
	public void generateUserTopics(Map<Map<Integer, Integer>, Integer> userPrefs, double[][] p_d_z){
		double[] compTopic = new double[p_d_z[0].length];

		Iterator<Entry<Map<Integer, Integer>, Integer>> it = userPrefs.entrySet().iterator();
		while (it.hasNext()) {
			double[] userTopic = new double[p_d_z[0].length];
			Entry<Map<Integer, Integer>, Integer> pairs = it.next();
			int freq = pairs.getValue();
			Map<Integer, Integer> userComp = pairs.getKey();
			Iterator<Entry<Integer, Integer>> it1 = userComp.entrySet().iterator();
			int userId = 0;
			int compId = 0;
			while (it1.hasNext()) {
				Entry<Integer, Integer> pair1 = it1.next();
				userId = pair1.getKey();
				compId = pair1.getValue();
			}
			if(nidRow.containsKey(compId)){
				compTopic = p_d_z[nidRow.get(compId)];
				for(int i=0; i<userTopic.length; i++){
					userTopic[i] = freq * compTopic[i];
				}
				if(userTopics.containsKey(userId)){

					for(int i=0; i<userTopic.length; i++){
						userTopic[i] += userTopics.get(userId)[i];
					}

				}
				userTopics.put(userId, userTopic);
			}
		}
	}


	/*
	 * make recommendations
	 */
	public ArrayList<Map.Entry<Integer, Double>> makeRecommendations(double[][] p_d_z, double[] userProfile){
		Map<Integer, Double> compsim = new HashMap<Integer, Double>(); //{comp id: similarity}
		Map<Integer, Integer> tmpNidRow = new HashMap<Integer, Integer>(nidRow);
		Iterator<Entry<Integer, Integer>> it = tmpNidRow.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Integer> pair = it.next();
			int compId = pair.getKey();
			int rowId = pair.getValue();
			double[] compProfile = p_d_z[rowId];
			double weight = DataProcessor.innerProdcut(compProfile, userProfile);
			compsim.put(compId, weight);
		}
		ArrayList<Map.Entry<Integer, Double>> compSimList = new ArrayList<Map.Entry<Integer, Double>>(compsim.entrySet());
		Collections.sort(compSimList, new Comparator<Map.Entry<Integer, Double>>() {      
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {      
				if((o2.getValue() - o1.getValue())>0){
					return 1;
				}
				else if((o2.getValue() - o1.getValue())<0){
					return -1;
				}
				else{
					return 0;
				}
			}
		});

		return compSimList;
	}



	/*
	 * sort 
	 */
	public Map<Integer, ArrayList<String>> sortDocuments(){
		Map<Integer, ArrayList<String>> documentsCollectofaConcept = new HashMap<Integer, ArrayList<String>>();

		return documentsCollectofaConcept;
	}

	/*
	 * get the userpreferences from database using information in acceslog
	 */
	public Map<Map<Integer, Integer>, Integer> processUserPrefs(ResultSet userRs){
		Map<Map<Integer, Integer>, Integer> userPref = new HashMap<Map<Integer, Integer>, Integer>();
		try {
			Statement stmt = conn.createStatement();

			while(userRs.next()){
				String url = userRs.getString(Constants.path);
				int uid = userRs.getInt(Constants.userId);
				if(url.contains("c-profile") || url.contains("node")){
					if(url.split("/").length > 1){

						if(DataProcessor.isNumeric(url.split("/")[1])){
							String sql = "select type from node where nid = " + url.split("/")[1];
							ResultSet rs = stmt.executeQuery(sql);
							if(rs.next()){
								if(rs.getString(Constants.nodeType).contains("company")){
									Map<Integer, Integer> userRecord = new HashMap<Integer, Integer>();
									userRecord.put(uid, Integer.parseInt(url.split("/")[1]));
									if(userPref.containsKey(userRecord)){
										int times = userPref.get(userRecord);
										times ++;
										userPref.put(userRecord, Integer.valueOf(times));
									}
									else{
										userPref.put(userRecord, 1);
									}
								}
							}
						}
					}
				}			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userPref;
	}


	//store p(w|z) matrix in the database
	public void storeWordsProb(double[][] p_w_z){

		for(int i=0; i<p_w_z.length; i++){
			String conceptProb = null;
			for(int j=0; j<p_w_z[0].length; j++){
				if(conceptProb == null){
					conceptProb = String.valueOf(p_w_z[i][j]);
				}
				else{
					conceptProb += Constants.deliminator;
					conceptProb += String.valueOf(p_w_z[i][j]);
				}
			}

			try {

				Statement stmt = conn.createStatement();
				String sqlQuery = "select * from word_concept where word = '" + vocabularies.get(i) +"'";
				ResultSet wordRs = stmt.executeQuery(sqlQuery);
				if(wordRs.next()){
					sqlQuery = "update word_concept set concept_prob = '" + conceptProb +"' where word = '" + vocabularies.get(i) +"'";
					stmt.execute(sqlQuery);
				}
				else{
					sqlQuery = "insert into word_concept (word, concept_prob) values ('" + vocabularies.get(i) + "' , '" + conceptProb + "')";
					stmt.execute(sqlQuery);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("finished");



	}

	// get matrix p(w|z)
	public double[][] getWordsProb(){
		double[][] p_w_z = null;
		try {
			Statement stmt = conn.createStatement();
			String sqlQuery = "select * from word_concept";
			ResultSet wordRs = stmt.executeQuery(sqlQuery);
			wordRs.last();
		    int wordSize = wordRs.getRow();
		    wordRs.beforeFirst();
		    
		    p_w_z = new double[wordSize][noConcept];
		    int i=0;
			while(wordRs.next()){
				String word = wordRs.getString(Constants.word);
				String conceptProb = wordRs.getString(Constants.conceptProb);
				String[] conceptProbs = conceptProb.split(Constants.deliminator);
				int z=0;
				for(String prob: conceptProbs){
					p_w_z[i][z] = Double.parseDouble(prob);
					z++;
				}
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DataProcessor.vectorPrintOut(p_w_z[0]);
		return p_w_z;
	}	

	/*
	 * return company profiles 
	 */
	public Map<Integer, String> getProfiles(){
		return companyProfiles;
	}

	/*
	 * return vocabularies
	 */
	public ArrayList<String> getVocabularies(){
		return vocabularies;
	}

	/*
	 * return row id --> node id
	 */
	public Map<Integer, Integer> getRowNid(){
		return rowNid;
	}


	/*
	 * return userTopics
	 */
	public Map<Integer, double[]> getUserTopics(){
		return userTopics;
	}
}

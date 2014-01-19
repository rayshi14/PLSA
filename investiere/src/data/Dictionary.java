package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import utils.Constants;

public class Dictionary {

	private String dictPath; 
	private BufferedReader br;
	private FileInputStream fis;
	private ArrayList<String> wordList;


	public Dictionary(){
		dictPath = Constants.dictPath;
		wordList = new ArrayList<String>();
		File dict = new File(dictPath);
		try {
			fis = new FileInputStream(dict);
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			String word;
			while ((word = br.readLine()) != null) {
				// Deal with the line
				wordList.add(word.toLowerCase());
			}

			// Done with the file
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> getWordList(){
		return wordList;
	}

}

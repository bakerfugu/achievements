package com.hosthorde.baker.achievements;

import java.io.*;
import java.net.URL;
import java.util.*;

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */

class ConfigParser {

	private String path;
	private ArrayList<String> lines;
	private boolean success;

	// we first open the file and load its lines into the data i-var
	// then we have a getter, which splits each line in data, checks for match
	// getter works like this: getData("database", "user"), which returns String

	public ConfigParser(String path) {

		this.path = path;
		this.lines = new ArrayList<String>();

		String line = null;

		System.out.println("Creating config parser");
		try {
			System.out.println("Attempting to access file");
			// FileReader reads text files in the default encoding.
			Class myClass = this.getClass();
			System.out.println("1");
			ClassLoader loader = myClass.getClassLoader();
			System.out.println("2");
			URL myURL = loader.getResource(path);
			System.out.println("3");
			System.out.println(myURL);
			String currentPath = myURL.getPath();
			System.out.println("4");
			System.out.println(currentPath);
			currentPath = currentPath.replaceAll("%20", " ");
			System.out.println("5");
			FileReader fileReader = 
					new FileReader(currentPath);
			System.out.println("File access successful.");

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = 
					new BufferedReader(fileReader);

			while((line = bufferedReader.readLine()) != null) {
				this.lines.add(line);
			}   

			// Always close files.
			bufferedReader.close();         

			this.success = true;
		}
		catch(FileNotFoundException ex) {
			System.out.println(
					"Unable to open file '" + 
							path + "'");    
			this.success = false;
		}
		catch(IOException ex) {
			System.out.println(
					"Error reading file '" 
							+ path + "'");                  
			this.success = false;
		}


	}

	public boolean verify() {
		return this.success;
	}

	public String getData(String category, String infoID) {

		// getData("database", "user") -> "6216"

		for (int i = 0; i < lines.size(); i++) {

			String currentLine = lines.get(i);
			String[] parts = currentLine.split(", ");

			if (parts[0].equals(category) && parts[1].equals(infoID)) {
				return parts[2];
			}

		}

		return null;    

	}

}

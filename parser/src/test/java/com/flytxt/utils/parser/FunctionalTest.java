package com.flytxt.utils.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FunctionalTest {

	private static String[] process(String line){
		int l1 = line.indexOf("|*");
		int l2 = line.indexOf(']');
		String str45 = line.substring(l2+1,l1);
		String[] t = line.split("\\*");
		String t2 = t[2];
		String[] t3 = t2.split(",");
		String rank = t3[1];
		String[] ret = new String[2];
		ret[0] = str45;
		ret[1] = rank;
		System.out.println(str45+":-:"+ rank);
		return ret;
	}
	public static void main(String[] args) {
		try {
			File file = new File("/Users/arunsoman/Downloads/worldcupplayerinfo_20140701.csv");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				process(line);
			}
			fileReader.close();
			System.out.println("Contents of file:");
			System.out.println(stringBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
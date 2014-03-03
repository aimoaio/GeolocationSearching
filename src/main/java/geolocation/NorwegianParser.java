package geolocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class NorwegianParser implements Parser {
	
	ArrayList<String> pagecontents;
	ArrayList<String> searchterms;
	
	public NorwegianParser(ArrayList<String> pages, ArrayList<String> terms){
		
		pagecontents = pages;
		searchterms = terms;
		
	}

	@Override
	public String Exec() {
		
		
		ArrayList<String> colours = new ArrayList<String>();
		String address = searchterms.get(searchterms.size()-1);
		searchterms.remove(searchterms.size()-1);

		//check for stopwords, for duplicates
		ArrayList<String> tokens = splitStringComma(address);
		
		StringBuilder build1 = new StringBuilder();
		for(String s:tokens){
			build1.append(s);
		}
		
		ArrayList<String> tokens2 = splitStringSpace(build1.toString());
		
		
		for(int s1=0;s1<tokens2.size(); s1++){
			String s = tokens2.get(s1);
			searchterms.add(s);
			if(s.equalsIgnoreCase("on")){
				searchterms.remove(s);
			}
			else if(s.equals(null)){
				searchterms.remove(s);
			}
		}
		//ArrayList with duplicates String
        List<String> duplicateList = searchterms;
        
        //Converting ArrayList to HashSet to remove duplicates
        LinkedHashSet<String> listToSet = new LinkedHashSet<String>(duplicateList);
      
        //Creating Arraylist without duplicate values
        List<String> listWithoutDuplicates = new ArrayList<String>(listToSet);

		
		System.out.println("Tokens: " + tokens2);
		System.out.println("GEO: " + listWithoutDuplicates);
		
		searchterms = (ArrayList<String>) listWithoutDuplicates;
		
		
		
		//add colour variation for highlighting terms
		colours.add("blue");
		colours.add("yellow");
		colours.add("green");
		colours.add("purple");
		colours.add("pink");
		colours.add("red");
		colours.add("blue");
		colours.add("yellow");
		colours.add("green");
		colours.add("purple");
		colours.add("pink");
		colours.add("red");
		colours.add("blue");
		colours.add("yellow");
		colours.add("green");
		colours.add("purple");
		colours.add("pink");
		colours.add("red");
		colours.add("blue");
		colours.add("yellow");
		colours.add("green");
		colours.add("purple");
		colours.add("pink");
		colours.add("red");
		colours.add("blue");
		colours.add("yellow");
		colours.add("green");
		colours.add("purple");
		colours.add("pink");
	
		
		//context
		StringBuilder sb = new StringBuilder();
		for (int k=0; k<pagecontents.size(); k++){
			String s = pagecontents.get(k);
			for(int i =0; i<searchterms.size(); i++){
				String term = searchterms.get(i);
				
				if(s.contains(term)){
					sb.append("<br/><span style='background-color: yellow;'>PAGE NUMBER: " + k + "</span><br/>");
					sb.append(s);
				}
			
			}
		}
		
		//split context pages to rows
		ArrayList<String> rows = splitStrings(sb.toString());
		System.out.println(rows.toString());
		
		//add highlight to rows
		if(searchterms.size()!=0){
			
			
			/*
			 * This will take every term inside the geoterm list (the list of terms to search for)
			 * For each term, it will assign the colour in the same position from the colour list
			 * (this means each term will have a different colour to distinguish them from the other terms)
			 * The system will then replace the term with a newly created string based on the geoterm,
			 * which will consist of: the span id tag (for jump links), the term and the unique highlight colour.
			 */
			for(int j=0;j<searchterms.size();j++){
			String replace = searchterms.get(j);
			String colour = colours.get(j);
			ArrayList<String> highlightedcontents = new ArrayList<String>();
			highlightedcontents = addHighlight(rows, replace, colour);
			rows = highlightedcontents;
			}
			
		}
		StringBuilder build = new StringBuilder();
		for(String s: rows){
			build.append("<br>");
			build.append(s);
			
		}
		
		return build.toString();
	}
	
	private ArrayList<String> addHighlight(ArrayList<String> rows, String geoterm, String colour){
		
		ArrayList<String> highlighted = new ArrayList<String>();
		int anchorcounter = 1;
		
		for(int i=0; i<rows.size(); i++){
			
			String rowcontent = rows.get(i);
			
			if(rowcontent.contains(geoterm)){
				String replacementcontent = "<span id=\""+geoterm+anchorcounter+"\" style='background-color:"+ colour + ";'>" + "<a href=\"#"+geoterm+(anchorcounter-1)+ "\"> < </a>"+ geoterm + "<a href=\"#"+geoterm+(anchorcounter+1)+ "\"> > </a>" + "</span>"; 
				rowcontent = rowcontent.replace(geoterm, replacementcontent);
				highlighted.add(rowcontent);
				anchorcounter++;
			} else {
				highlighted.add(rowcontent);
			}
		}
		
		return highlighted;
	}
	
	private static ArrayList<String> splitStrings(String str){
		ArrayList<String> split = new ArrayList<String>();
		String[] lines = str.split("\r\n|\r|\n");
		Collections.addAll(split, lines); 
		
		return split;
	}
	
	private static ArrayList<String> splitStringSpace(String str){
		ArrayList<String> split = new ArrayList<String>();
		String[] splited = str.split("\\s+");
		Collections.addAll(split, splited); 
		return split;
	}
	private static ArrayList<String> splitStringComma(String str){
		ArrayList<String> split = new ArrayList<String>();
		String[] splited = str.split(",");
		Collections.addAll(split, splited); 
		return split;
	}

}

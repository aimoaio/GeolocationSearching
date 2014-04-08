package parsers;

/**
 * This class is identical to the NorwegianRoadDataParser which first served as the sample
 * for a generic parser. In time and future upgrades, this class will most likely be unchanged,
 * as the NorwegianRoadDataParser is more complex and may require additional features for
 * formatting the contents more appropriately. 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class GenericParser implements Parser {
	
	ArrayList<String> pagecontents;
	ArrayList<String> searchterms;
	
	public GenericParser(ArrayList<String> pages, ArrayList<String> terms){
		
		pagecontents = pages;
		searchterms = terms;
		
	}
	
	/**
	 * The entire first part of this method is the same as the other parsers (search term cleanup and colour
	 * assignment). The comments are also the exact same, assuming the reader has not read the comments
	 * for the other parsers.
	 */

	@Override
	public String format() {
		
		
		ArrayList<String> colours = new ArrayList<String>();
		
		
		String address = searchterms.get(searchterms.size()-1); //this is the full reverse geocoded address
		searchterms.remove(searchterms.size()-1); //remove the address from the geoterm list (don't want it searching same content twice!)

		/*
		 * Now we need to check for any duplicate search terms.
		 * For example, Glasgow and Glasgow City. The only term we need to search here is "Glasgow"
		 * In the event the fully reverse geocoded address is used, we must break up the address. This may lead
		 * to terms such as opp, on etc. We do not want these to be highlighted so they must be removed from the
		 * search term list. Additional stopwords can be added here, such as "bus" and "station", neither of
		 * which are of much use as they are too generic.
		 */
		
		ArrayList<String> tokens = splitStringComma(address); //break the address component based on commas.
		
		
		/* 
		 * We must reconstruct the new address without the commas first. This is because the split address
		 * returned by the above method will have broken the address like so: 
		 * "Glasgow, Glasgow City G2, UK becomes
		 * Glasgow | Glasgow City G2 and UK.
		 * Splitting by white space means the commas are still attached. Therefore we must first remove the commas,
		 * then resplit the string based on white space so get the following instead:
		 * Glasgow, Glasgow, City, G2, UK.
		 * 
		 */
		
		StringBuilder build1 = new StringBuilder();
		for(String s:tokens){
			build1.append(s);
		}
		
		ArrayList<String> tokens2 = splitStringSpace(build1.toString()); //break the new address based on white spaces.
		
		/*
		 * Now we remove any stopwords or additional useless words we do not want searched.
		 * Also any NULL terms (returned when geocoder fails to separate components) should be
		 * removed before checking for duplicates.
		 */
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
		
		/*
		 * Now we remove the duplicates by converting our list a LinkedHashSet and then back.
		 */
		
        List<String> duplicateList = searchterms; //ArrayList with duplicates String
        LinkedHashSet<String> listToSet = new LinkedHashSet<String>(duplicateList); //Converting ArrayList to HashSet to remove duplicates
        List<String> listWithoutDuplicates = new ArrayList<String>(listToSet); //Creating Arraylist without duplicate values

		
		searchterms = (ArrayList<String>) listWithoutDuplicates; //assign the new list to our searchterms list
		
		
		colours = new ArrayList<String>();
		
		/*
		 * Add colour variation for highlighting terms.
		 * A minimum of 6 tends to ensure that each term highlighted is a unique colour.
		 * However due to the larger number of terms returned when breaking the address component,
		 * the number assigned to each term may differ per PDF. (First term is not always highlighted blue.)
		 */
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
		
	
		
		/*
		 * Context filtering occurs at this stage. Each page is checked to see if it contains
		 * ANY of the search terms. If it does, the page number and the page contents are appended
		 * into the string builder.
		 */
		
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
		
		
		ArrayList<String> rows = splitStrings(sb.toString()); //the pages are then broken up into rows
		
		
		/*
		 * Now we go straight to checking each row to see if any highlighting or jump links are required.
		 * The Norwegian text does not contain, or need any special formatting at this current moment in time.
		 * The main reason is that the contents are too widely varied in style that finding a common pattern to
		 * analyze and reconstruct is not viable in the time limitations of the project.
		 * 
		 */
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
		
		rows.add(0, "<div id=\"tbl1\">");
		
		for(String s: rows){
			build.append("<br>"); //append a <br> tag so contents are displayed on their own rows
			build.append(s);
			
		}
		
		return build.toString();
	}
	
	/**
	 * Returns a list of the rows with highlighting and jump links added.
	 * This method is the same for all parsers (currently).
	 */
	@Override
	public ArrayList<String> addHighlight(ArrayList<String> rows, String geoterm, String colour){
		
		ArrayList<String> highlighted = new ArrayList<String>();
		
		int anchorcounter = 1; //this counter is used for numbering the jump links
		
		for(int i=0; i<rows.size(); i++){
			
			String rowcontent = rows.get(i);
			
			if(rowcontent.contains(geoterm)){ //if the term exists in the row
				
				/*
				 * The new content includes:-
				 * Span id = made up of the geoterm and its placement of occurrence (first of its term to occur, second etc).
				 * Background colour = the highlight colour. Randomly selected.
				 * A "<" that links to the previous anchor (geoterm, -1 on placement)
				 * The term itself.
				 * A ">" that links to the next anchor (geoterm, +1 on placement)
				 * 
				 */
				String replacementcontent = "<span id=\""+geoterm+anchorcounter+"\" style='background-color:"+ 
				colour + ";'>" + "<a href=\"#"+geoterm+(anchorcounter-1)+ "\"> < </a>"+ geoterm + 
				"<a href=\"#"+geoterm+(anchorcounter+1)+ "\"> > </a>" + "</span>"; 
				
				/*
				 * Then we replace the old row with the newly created string and
				 * add this to the final list to be returned to the parser.
				 * If this IF was executed, then a term was found. This means anchor
				 * counter should also be incremented so we can keep track of
				 * each term's occurrence rate.
				 */
				rowcontent = rowcontent.replaceFirst(geoterm, replacementcontent);
				highlighted.add(rowcontent);
				anchorcounter++;
				
			} else { //no need to highlight anything, return the row as is
				highlighted.add(rowcontent);
			}
		}
		
		return highlighted;
	}
	
	/**
	 * Breaks up the string at every carriage return/new line detected.
	 * @param str - the string to be split.
	 * @return list of strings now broken into rows of content.
	 */
	private static ArrayList<String> splitStrings(String str){
		ArrayList<String> split = new ArrayList<String>();
		String[] lines = str.split("\r\n|\r|\n");
		Collections.addAll(split, lines); 
		
		return split;
	}
	
	/**
	 * Splits the string based on white space.
	 * @param str - the string to be split
	 * @return a list of words broken from the string
	 */
	private static ArrayList<String> splitStringSpace(String str){
		ArrayList<String> split = new ArrayList<String>();
		String[] splited = str.split("\\s+");
		Collections.addAll(split, splited); 
		return split;
	}
	
	/**
	 * Splits the string based on commas.
	 * @param str - the string to be split
	 * @return a list of words without commas
	 */
	private static ArrayList<String> splitStringComma(String str){
		ArrayList<String> split = new ArrayList<String>();
		String[] splited = str.split(",");
		Collections.addAll(split, splited); 
		return split;
	}

}

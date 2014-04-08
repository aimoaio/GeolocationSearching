package parsers;

/** 
 * This class is for parsing the FirstGroup bus timetables. This means only
 * FirstBus PDF timetables will be formatted correctly as such. Other PDFs
 * will fail to display correctly due to the detection systems in place designed
 * for tables and row information.
 * 
 * The first stage involves checking and tidying up of the geoterms. The geoterms
 * should not contain stopwords such as on, opp and there should be no duplicates 
 * in the list.
 * 
 * Once this is done, the system loops through the row contents passed by the extractor
 * class and matches these against certain rules to determine which type of formatting
 * will be required. Formatting is unique to the type of row content and failure to
 * format correctly will result in a broken table being displayed. Ensuring appropriate
 * tags are implemented is crucial to displaying a timetable correctly.
 * 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class BusParser implements Parser {
	
	
	static ArrayList<String> contents;
	static ArrayList<String> geoterms;
	
	public BusParser(ArrayList<String> contentspassed, ArrayList<String> geotermspassed){
		contents = contentspassed;
		geoterms = geotermspassed;
	}
	
	/**
	 * The entire first part of this method is the same as the other parsers (search term cleanup and colour
	 * assignment). The comments are also the exact same, assuming the reader has not read the comments
	 * for the other parsers.
	 */
	
	@Override
	public String format() {
		
		ArrayList<String> finalCellList = new ArrayList<String>();
		ArrayList<String> colours = new ArrayList<String>();
		
		
		String address = geoterms.get(geoterms.size()-1); //this is the full reverse geocoded address
		geoterms.remove(geoterms.size()-1); //remove the address from the geoterm list (don't want it searching same content twice!)

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
			geoterms.add(s);
			if(s.equalsIgnoreCase("on")){
				geoterms.remove(s);
			}
			else if(s.equals(null)){
				geoterms.remove(s);
			}
		}
		
		/*
		 * Now we remove the duplicates by converting our list a LinkedHashSet and then back.
		 */
		
        List<String> duplicateList = geoterms; //ArrayList with duplicates String
        LinkedHashSet<String> listToSet = new LinkedHashSet<String>(duplicateList); //Converting ArrayList to HashSet to remove duplicates
        List<String> listWithoutDuplicates = new ArrayList<String>(listToSet); //Creating Arraylist without duplicate values

		
		geoterms = (ArrayList<String>) listWithoutDuplicates; //assign the new list to our geoterms list
		
		
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
		
		
		
		int position = 0; //set initial positions of cell contents to 0
		int dashposition = 0;
		
		
		/*
		 * Now we loop over each row to see how we should format it appropriately.
		 * This is done inside a massive if/else statement. A case statement does not
		 * work very well as we are checking multiple scenarios as opposed to a single
		 * parameter.
		 */
		
		for(String row:contents){
			
			
			/* If the row contains the world valid, then it is a date and despite containing
			 * over 4 digits (time data), it should not be placed in a different table cell
			 * as this would create a massive space between the first two rows resulting in
			 * warped formatting. As such, it is checked first before any other number check
			 * is implemented.
			 */
			
			if(row.contains("Valid")){
				
				String content = validDateFormat(row);
				finalCellList.add(content);
			}
			
			/*
			 * If the row contains more than 2 digits (meaning it is not just the bus service
			 * number alone, and does not contain any dashes (meaning no available time), then
			 * format using this method. This will check for the first position in which time data
			 * is detected (referred to as "position").
			 * Any cells found before the position mark, is therefore a place name and should be
			 * appended into 1 table cell with spaces between each cell. The <td> initiating tag
			 * should be inserted at the start of the name, and after the <tr> tags (therefore the 
			 * position for insertion is at 4 because <tr> is 4 in length).
			 * Any cells found after the position mark, is time data and should be in a cell of its own,
			 * these are just appended with <td> tags.
			 */
			
			else if(numberOfDigits(row)>2 && containsDash(row)==false){
				
				ArrayList<String> cells = splitStringSpace(row);
				position = firstPositionOfNumbers(cells);
				
				StringBuilder b1 = new StringBuilder();
				
				for (int j=0; j<position;j++){
					b1.append(cells.get(j));
					b1.append(" ");
				}
				b1.append("</td>");
				b1.insert(4, "<td>");
				finalCellList.add(b1.toString());
				
				for(int i=position;i<cells.size(); i++){
					StringBuilder b2 = new StringBuilder();
					b2.append("<td>");
					b2.append(cells.get(i));
					b2.append(" ");
					b2.append("</td>");
					finalCellList.add(b2.toString());
				}
			}  
			
			/*
			 * If the row contains more than 2 digits and also contains dashes
			 * then we need to find out if the first instance is a time cell or a
			 * dash cell. If it's a time cell that contains time data, then simply
			 * apply the <td> tags to the first cell (at position 4, after <tr>) and also
			 * to every cell from that point onwards.
			 * this requires an extra <td> tag to be added because the row starts with a
			 * time data cell and not with a place name. this tends to happen when a place
			 * name is too long and thus written over two lines. The PDF parser will detect
			 * this on separate lines and will then set the time cells on a different row itself,
			 * using the previous formatting plans will skew up the time cells to the left
			 * instead of aligning to the right.
			 * 
			 * if the first cell instance is not a time data cell, we must look and check for the
			 * first position of number and dash occurrence. the positions are then compared and 
			 * the lower number is set as the loop number for the rest of the cells. Anything before
			 * this position, will require the <td> tag inserted at position 4 because of the <tr> tags
			 * and then everything within that area and the position is appended.
			 * Everything afterwards can be looped and appended appropriately. 
			 */
			
			else if(numberOfDigits(row)>2 && containsDash(row)==true){
				
				
				ArrayList<String> cells = splitStringSpace(row);
				
				if(isTimeValue(cells.get(0))==true){
					StringBuilder sb = new StringBuilder();
					sb.append(cells.get(0));
					sb.insert(4, "<td>");
					sb.append("</td>");
					sb.insert(4, "<td></td>");
					finalCellList.add(sb.toString());
					
					
					for(int i=1;i<cells.size(); i++){
						StringBuilder b = new StringBuilder();
						b.append("<td>");
						b.append(cells.get(i));
						b.append(" ");
						b.append("</td>");
						finalCellList.add(b.toString());
					}
					
				} else {
				
					position = firstPositionOfNumbers(cells);
					dashposition = firstPositionOfDashes(cells);

					int finalposition = 0;
				
						if(position>dashposition){
							finalposition = dashposition;
						} else {
							finalposition = position;
						}
				
					StringBuilder b1 = new StringBuilder();
				
					
					for (int j=0; j<finalposition;j++){
						b1.append(cells.get(j));
						b1.append(" ");
					}
				
					b1.append("</td>");
					b1.insert(4, "<td>");
					finalCellList.add(b1.toString());
				
					for(int i=finalposition;i<cells.size(); i++){
						StringBuilder b = new StringBuilder();
						b.append("<td>");
						b.append(cells.get(i));
						b.append(" ");
						b.append("</td>");
						finalCellList.add(b.toString());
					}
				}
			
			} 
			
			/*
			 * If the row fails the above checks but contains dashes, then it simply
			 * means it is missing time information (unavailable or no bus). In which 
			 * case, we simply append contents similarly to numbers previously. 
			 * First occurrence of dash = position. Anything before is appended
			 * into its own <td> tag (likely to be a placename), and everything after
			 * is appended into separate tags.
			 */
				else if(containsDash(row)){
					
					ArrayList<String> cells = splitStringSpace(row);
					dashposition = firstPositionOfDashes(cells);
				
					StringBuilder b1 = new StringBuilder();
				
					for (int j=0; j<dashposition;j++){
						b1.append(cells.get(j));
						b1.append(" ");
					}
				
					b1.append("</td>");
					b1.insert(4, "<td>");
					finalCellList.add(b1.toString());
				
				
					for(int i=dashposition;i<cells.size(); i++){
						StringBuilder b = new StringBuilder();
						b.append("<td>");
						b.append(cells.get(i));
						b.append(" ");
						b.append("</td>");
						finalCellList.add(b.toString());
					}
				}
			
				/*
				 * If the row does not contain anything else but contains
				 * the tags <table> then simply add the original row contents
				 * to the list. No <td> tags need to be added as it is the end
				 * of the contents.
				 */
			
				else if(row.equals("<table>") || row.equals("</table>")){
					finalCellList.add(row);
				}
			
			
				/*
				 * Else the row should be unedited. With the exception of adding in
				 * <td> tags so they are placed in a row on their own in the table.
				 * This is for additional information such as the service numbers or notes or
				 * any other text that is not timetable information and thus, should not
				 * be aligned into columns and rows.
				 */
				else {
					
					StringBuilder sb = new StringBuilder();
					sb.append(row);
					sb.insert(4, "<td>");
					sb.insert(row.length()-2, "</td>");
					finalCellList.add(sb.toString());
				
				}
		
		}
		
		/*
		 * The newly formatted cells are then appended into a single string for highlighting and
		 * addition of jump links.
		 */
		
		StringBuilder b = new StringBuilder();
		for(String s:finalCellList){
			b.append(s);
		}
		
		String s = b.toString(); 
		
		/*
		 * First we must clean up the contents a little by modifying the table and adding an additional </tr> closing
		 * tag to each row. This is to ensure each row is closed properly allowing the table to be displayed correctly.
		 */
		ArrayList<String> finalcontents = splitStringsByRow(s);
		ArrayList<String> cleancontents = new ArrayList<String>();
		
		cleancontents.add("<table id=\"tbl1\">");
		
		for(String row: finalcontents){
			StringBuilder build = new StringBuilder();
			build.append(row);
			build.append("</tr>");
			cleancontents.add(build.toString());
		}
		
		cleancontents.add("</table>");
		
		
		
		if(geoterms.size()!=0){ //assuming the geoterm list is not empty (and it never should be)...
			
			/*
			 * This will take every term inside the geoterm list (the list of terms to search for)
			 * For each term, it will assign the colour in the same position from the colour list
			 * (this means each term will have a different colour to distinguish them from the other terms)
			 * The system will then replace the term with a newly created string based on the geoterm,
			 * which will consist of: the span id tag (for jump links), the term and the unique highlight colour.
			 */
			
			for(int j=0;j<geoterms.size();j++){
				String replace = geoterms.get(j);
				String colour = colours.get(j);
				ArrayList<String> highlightedcontents = new ArrayList<String>();
				highlightedcontents = addHighlight(cleancontents, replace, colour);
				cleancontents = highlightedcontents;
			}
			
		}
		
		/*
		 * Now the updated clean contents with highlighting and jump links are appended to a single string
		 * and returned to the TextExtractor class which then passes it back to main.jsp for display.
		 */
		StringBuilder sb = new StringBuilder();
		for(String st: cleancontents){
			sb.append(st);
		}
		return sb.toString();
	}
	
	
	/**
	 * Simply appends <td> tags to the valid date row.
	 * @param row - the row to be appended to.
	 * @return a string of the row with appended tags.
	 */
	private String validDateFormat(String row) {
		StringBuilder sb = new StringBuilder();
		sb.append(row);
		sb.insert(4, "<td>");
		sb.insert(row.length()-2, "</td>");
		return sb.toString();
	}

	
	/**
	 * Returns a list of the rows with highlighting and jump links added.
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
	
	/**
	 * Splits the string based on table row information
	 * @param str - the string to be split
	 * @return a list of strings for each row
	 */
	private static ArrayList<String> splitStringsByRow(String str){
		ArrayList<String> split = new ArrayList<String>();
		String[] lines = str.split("</tr>|</table>|<table>");
		Collections.addAll(split, lines); 
		return split;
	}
	
	/**
	 * Check for digits in a string
	 * @param s - the string to check
	 * @return true if numbers are found, else false
	 */
	public final static boolean containsDigit(String s) {
	    boolean containsDigit = false;

	    if (s != null && !s.isEmpty()) {
	        for (char c : s.toCharArray()) {
	            if (containsDigit = Character.isDigit(c)) {
	                break;
	            }
	        }
	    }

	    return containsDigit;
	}
	
	/**
	 * Checks for dashes in a string
	 * @param s - the string to check
	 * @return true if dashes are found, else false
	 */
	public final static boolean containsDash(String s) {
	    boolean containsDash = false;

	    if (s != null && !s.isEmpty()) {
	        if(s.contains("----")){
	        	containsDash = true;
	        }
	    }

	    return containsDash;
	}
	
	/**
	 * Counts the number of digits in a string
	 * @param s - the string to count
	 * @return the number of digits found in the string
	 */
	public final static int numberOfDigits(String s) {
	    int counter=0;

	    if (s != null && !s.isEmpty()) {
	        for (char c : s.toCharArray()) {
	            if (Character.isDigit(c)) {
	            	counter++;
	            }
	        }
	    }

	    return counter;
	}
	
	/**
	 * Looks for the first instance in which numbers occur 
	 * @param s - the list of strings to search through
	 * @return the position of the first position in which numbers have appeared
	 */
	private static int firstPositionOfNumbers(ArrayList<String> s){
			int position = 0;
			for(int i=0;i<s.size();i++){
				if(containsDigit(s.get(i))){
					position = i;
					break;
				}
			} return position;

	}
	
	/**
	 * Looks for the first instance in which dashes occur
	 * @param s - the list of strings to search
	 * @return the first position in which dashes have appeared
	 */
	private static int firstPositionOfDashes(ArrayList<String> s){
			int position = 0;
			for(int i=0;i<s.size();i++){
				if(s.get(i).contains("-")){
					position = i;
					break;
				}
			} return position;

	}
		
	/**
	 * Checks to see if the string is a time value (time data is defined as 4 digits in a string)
	 * @param s - the string to check
	 * @return true if 4 digits are found, else false
	 */
	private static boolean isTimeValue(String s){
			int digits = numberOfDigits(s);
			boolean isTime = false;
			if(digits==4){
				isTime = true;
			}
			return isTime;
	}

}

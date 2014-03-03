package geolocation;

/** 
 * This class is for parsing the FirstGroup bus timetables. This means only
 * FirstBus PDF timetables will be formatted correctly as such. Other PDFs
 * will fail to display correctly due to the detection systems in place designed
 * for tables and row information.
 * Therefore it is recommended that a new check for the type of PDF document
 * be implemented in the demo class to ensure that only timetables from the firstgroup
 * website are passed into this class when deciding on the parser for formatting.
 */

import java.util.ArrayList;
import java.util.Arrays;
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
	

	
	public String Exec() {
		
		ArrayList<String> finalCellList = new ArrayList<String>();
		ArrayList<String> colours = new ArrayList<String>();
		String test = geoterms.get(2);
		String address = geoterms.get(geoterms.size()-1);
		geoterms.remove(geoterms.size()-1);

		//check for stopwords, for duplicates
		ArrayList<String> tokens = splitStringComma(address);
		
		StringBuilder build1 = new StringBuilder();
		for(String s:tokens){
			build1.append(s);
		}
		
		ArrayList<String> tokens2 = splitStringSpace(build1.toString());
		
		
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
		//ArrayList with duplicates String
        List<String> duplicateList = geoterms;
        
        //Converting ArrayList to HashSet to remove duplicates
        LinkedHashSet<String> listToSet = new LinkedHashSet<String>(duplicateList);
      
        //Creating Arraylist without duplicate values
        List<String> listWithoutDuplicates = new ArrayList<String>(listToSet);

		
		System.out.println("Tokens: " + tokens2);
		System.out.println("GEO: " + listWithoutDuplicates);
		
		geoterms = (ArrayList<String>) listWithoutDuplicates;
		
		
		colours = new ArrayList<String>();
		
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
		
		
		//set initial positions of cell contents to 0
		int position = 0;
		int dashposition = 0;
		
		
		//looping over every row of contents extracted previously
		for(String row:contents){
			System.out.println("Row: " + row);
			
			
			/* If the row contains the world valid, then it is a date and despite containing
			 * over 4 digits (time data), it should not be placed in a different table cell
			 * as this would create a massive space between the first two rows resulting in
			 * warped formatting. As such, it is checked first before any other number check
			 * is implemented.
			 */
			
			if(row.contains("Valid")){
				StringBuilder sb = new StringBuilder();
				sb.append(row);
				sb.insert(4, "<td>");
				sb.insert(row.length()-2, "</td>");
				System.out.println("Row length: " + row.length());
				finalCellList.add(sb.toString());
				System.out.println("Valid row: " + sb.toString());
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
				System.out.println("Digits: " + numberOfDigits(row));
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
				System.out.println("E Cells: " + b1.toString());
				
				for(int i=position;i<cells.size(); i++){
					StringBuilder b = new StringBuilder();
					b.append("<td>");
					b.append(cells.get(i));
					b.append(" ");
					b.append("</td>");
					finalCellList.add(b.toString());
					System.out.println("Cells: " + b.toString());
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
			 * and then everything within that area and the positon is appended.
			 * Everything afterwards can be looped and appended appropriately. 
			 */
			
			else if(numberOfDigits(row)>2 && containsDash(row)==true){
				System.out.println("Digits: " + numberOfDigits(row));
				ArrayList<String> cells = splitStringSpace(row);
				
				if(isTimeValue(cells.get(0))==true){
					StringBuilder sb = new StringBuilder();
					sb.append(cells.get(0));
					sb.insert(4, "<td>");
					sb.append("</td>");
					sb.insert(4, "<td></td>");
					System.out.println("First cell: "  + sb.toString());
					finalCellList.add(sb.toString());
					
					
					for(int i=1;i<cells.size(); i++){
						StringBuilder b = new StringBuilder();
						b.append("<td>");
						b.append(cells.get(i));
						b.append(" ");
						b.append("</td>");
						finalCellList.add(b.toString());
						System.out.println("Special Cells: " + b.toString());
					}
					
				} 
				
				else {
				
				position = firstPositionOfNumbers(cells);
				dashposition = firstPositionOfDashes(cells);
				int finalposition = 0;
				
					if(position>dashposition){
						finalposition = dashposition;
					} 
					
					else {
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
				System.out.println("F Cells: " + b1.toString());
				
				for(int i=finalposition;i<cells.size(); i++){
					StringBuilder b = new StringBuilder();
					b.append("<td>");
					b.append(cells.get(i));
					b.append(" ");
					b.append("</td>");
					finalCellList.add(b.toString());
					System.out.println("F2 Cells: " + b.toString());
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
				System.out.println("Dash found");
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
				System.out.println("D Cells: " + b1.toString());
				
				for(int i=dashposition;i<cells.size(); i++){
					StringBuilder b = new StringBuilder();
					b.append("<td>");
					b.append(cells.get(i));
					b.append(" ");
					b.append("</td>");
					finalCellList.add(b.toString());
					System.out.println("D2 Cells: " + b.toString());
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
				System.out.println("Row length: " + row.length());
				finalCellList.add(sb.toString());
				System.out.println("Unedited: " + sb.toString());
				}
		
		}
		
		//the newly formatted cells are then appended into a single string
		StringBuilder b = new StringBuilder();
		for(String s:finalCellList){
			b.append(s);
		}
		
		String s = b.toString();
//		
//		//and highlighting is added to the string
//		if(geoterms.size()!=0){
//			
//			
//			/*
//			 * This will take every term inside the geoterm list (the list of terms to search for)
//			 * For each term, it will assign the colour in the same position from the colour list
//			 * (this means each term will have a different colour to distinguish them from the other terms)
//			 * The system will then replace the term with a newly created string based on the geoterm,
//			 * which will consist of: the span id tag (for jump links), the term and the unique highlight colour.
//			 */
//			for(int j=0;j<geoterms.size();j++){
//			String replace = geoterms.get(j);
//			String colour = colours.get(j);
//			String newterm = "<span id=\""+replace+"\" style='background-color:"+ colour + ";'>" + replace + "</span>";
//				s = s.replace(replace, newterm);
//			
//			}
//		}
		
		
		//the final string to be returned for display is the string that was replaced with highlighting above
		ArrayList<String> finalcontents = splitStringsByRow(s);
		ArrayList<String> cleancontents = new ArrayList<String>();
		
		cleancontents.add("<table>");
		for(String row: finalcontents){
			StringBuilder build = new StringBuilder();
			build.append(row);
			build.append("</tr>");
			cleancontents.add(build.toString());
		}
		
		cleancontents.add("</table>");
		
		
		System.out.println("list of terms before highlight: " + geoterms);
		//and highlighting is added to the string
		if(geoterms.size()!=0){
			
		
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
		
		
		System.out.println("Final contents: " + cleancontents.toString());
		StringBuilder sb = new StringBuilder();
		for(String st: cleancontents){
			sb.append(st);
		}
		System.out.println("add: " + address);
		System.out.println("test: " + test);
		return sb.toString();
	}
	
	private ArrayList<String> addHighlight(ArrayList<String> rows, String geoterm, String colour){
		
		ArrayList<String> highlighted = new ArrayList<String>();
		int anchorcounter = 1;
		
		for(int i=0; i<rows.size(); i++){
			
			String rowcontent = rows.get(i);
			
			if(rowcontent.contains(geoterm)){
				
				System.out.println("Geoterm: " + geoterm + anchorcounter);
				System.out.println("Row Before Alter: " + rowcontent);
				String replacementcontent = "<span id=\""+geoterm+anchorcounter+"\" style='background-color:"+ colour + ";'>" + "<a href=\"#"+geoterm+(anchorcounter-1)+ "\"> < </a>"+ geoterm + "<a href=\"#"+geoterm+(anchorcounter+1)+ "\"> > </a>" + "</span>"; 
				rowcontent = rowcontent.replaceFirst(geoterm, replacementcontent);
				highlighted.add(rowcontent);
				System.out.println("Row Content: " + rowcontent);
				anchorcounter++;
			} else {
				highlighted.add(rowcontent);
			}
		}
		
		return highlighted;
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
	
	private static ArrayList<String> splitStringsByRow(String str){
		ArrayList<String> split = new ArrayList<String>();
		String[] lines = str.split("</tr>|</table>|<table>");
		Collections.addAll(split, lines); 
		
		return split;
	}
	
	private static ArrayList<String> splitStrings(String str){
		ArrayList<String> split = new ArrayList<String>();
		String[] lines = str.split("\r\n|\r|\n");
		Collections.addAll(split, lines); 
		
		return split;
	}
	
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
	
	public final static boolean containsDash(String s) {
	    boolean containsDash = false;

	    if (s != null && !s.isEmpty()) {
	        if(s.contains("----")){
	        	containsDash = true;
	        }
	    }

	    return containsDash;
	}
	
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
		
		private static int firstPositionOfNumbers(ArrayList<String> s){
			int position = 0;
			for(int i=0;i<s.size();i++){
				if(containsDigit(s.get(i))){
					position = i;
					break;
				}
			} return position;

		}
		
		private static int firstPositionOfDashes(ArrayList<String> s){
			int position = 0;
			for(int i=0;i<s.size();i++){
				if(s.get(i).contains("-")){
					position = i;
					break;
				}
			} return position;

		}
		
		private static boolean isTimeValue(String s){
			int digits = numberOfDigits(s);
			boolean isTime = false;
			if(digits==4){
				isTime = true;
			}
			return isTime;
		}

}

package geolocation;

import java.util.ArrayList;
import java.util.Collections;

public class BusParser {
	

	
	public static String Exec(ArrayList<String> contents, ArrayList<String> geoterms) {
		
		ArrayList<String> finalList = new ArrayList<String>();
		ArrayList<String> finalCellList = new ArrayList<String>();
		ArrayList<String> colours = new ArrayList<String>();
		
		colours = new ArrayList<String>();
		
		//add colours
		colours.add("blue");
		colours.add("yellow");
		colours.add("green");
		colours.add("purple");
		colours.add("pink");
		colours.add("red");
		
//		for(String row:contents){
//			ArrayList<String> cells = splitStringSpace(row);
//			for(String cell: cells){
//				StringBuilder builder = new StringBuilder();
//				builder.append("<td>");
//				builder.append(cell);
//				builder.append("</td>");
//				finalCellList.add(builder.toString());
//			}
//		}
//		
		int position = 0;
		int dashposition = 0;
		for(String row:contents){
			System.out.println("Row: " + row);
			
			if(row.contains("Valid")){
				StringBuilder sb = new StringBuilder();
				sb.append(row);
				sb.insert(4, "<td>");
				sb.insert(row.length()-2, "</td>");
				System.out.println("Row length: " + row.length());
				finalCellList.add(sb.toString());
				System.out.println("Valid row: " + sb.toString());
			}
			
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
			}  else if(numberOfDigits(row)>2 && containsDash(row)==true){
				System.out.println("Digits: " + numberOfDigits(row));
				ArrayList<String> cells = splitStringSpace(row);
				
				if(isTimeValue(cells.get(0))==true){
					
					//loop around everything and apply extra td
					//first one is special
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
			
			} else if(containsDash(row)){
			
				
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
			else if(row.equals("<table>") || row.equals("</table>")){
			
				
			finalCellList.add(row);
			
			}
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
		StringBuilder b = new StringBuilder();
		for(String s:finalCellList){
			b.append(s);
		}
		
		String s = b.toString();
		
		//add the highlighting
		if(geoterms.size()!=0){
			
			for(int j=0;j<geoterms.size();j++){
			String replace = geoterms.get(j);
			//System.out.println("Terms: " + replace);
			String colour = colours.get(j);
			String newterm = "<span id=\""+replace+"\" style='background-color:"+ colour + ";'>" + replace + "</span>";
			//System.out.println("new line is: " + newterm);
			s = s.replace(replace, newterm);
			
			}
		}
		
		String finalcontents = s;
		
		return finalcontents;
	}
	

	private static ArrayList<String> splitStringSpace(String str){
		ArrayList<String> split = new ArrayList<String>();
		String[] splited = str.split("\\s+");
		Collections.addAll(split, splited); 
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

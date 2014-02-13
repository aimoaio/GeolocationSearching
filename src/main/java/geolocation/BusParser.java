package geolocation;

import java.util.ArrayList;
import java.util.Collections;

public class BusParser {
	
	static ArrayList<String> finalList = new ArrayList<String>();
	static ArrayList<String> finalCellList = new ArrayList<String>();
	static ArrayList<String> colours = new ArrayList<String>();
	
	public static String Exec(ArrayList<String> contents, ArrayList<String> geoterms) {
		
		colours = new ArrayList<String>();
		
		//add colours
		colours.add("blue");
		colours.add("yellow");
		colours.add("green");
		colours.add("purple");
		colours.add("pink");
		colours.add("red");
		
		for(String row:contents){
			ArrayList<String> cells = splitStringSpace(row);
			for(String cell: cells){
				StringBuilder builder = new StringBuilder();
				builder.append("<td>");
				builder.append(cell);
				builder.append("</td>");
				finalCellList.add(builder.toString());
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
			System.out.println("new line is: " + newterm);
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

}

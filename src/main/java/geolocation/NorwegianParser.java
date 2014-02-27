package geolocation;

import java.util.ArrayList;

public class NorwegianParser implements Parser {
	
	ArrayList<String> pagecontents;
	ArrayList<String> searchterms;
	
	public NorwegianParser(ArrayList<String> pages, ArrayList<String> terms){
		
		pagecontents = pages;
		searchterms = terms;
		
	}

	@Override
	public String Exec() {
		
		StringBuilder sb = new StringBuilder();
		for (int k=0; k<pagecontents.size(); k++){
			String s = pagecontents.get(k);
			for(int i =0; i<searchterms.size(); i++){
				String term = searchterms.get(i);
				
				if(s.contains(term)){
					sb.append("PAGE NUMBER: " + k + "<br/>");
					sb.append(s);
				}
			
			}
		}
		
		return sb.toString();
	}

}

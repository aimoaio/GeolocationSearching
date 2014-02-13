package geolocation;















import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripperByArea;

public class PdfBoxGAEDemo {

	private static final Logger log = Logger.getLogger(PdfBoxGAEDemo.class.getName());
	public static final String NEW_LINE = System.getProperty("line.separator");
	
	
	static ArrayList<String> text;
	static String finaltext;
	static String s;
	static int linecounter;
	static ArrayList<String> tokens;
	static ArrayList<String> cells;
	static ArrayList<String> HTML;
	static ArrayList<String> HTMLFINAL;

	
	public static ArrayList<String> Exec(String pdfUrl, int x, int y, int w, int h, ArrayList<String> terms) {

		log.info("PdfUrl=" + pdfUrl);
		System.out.println("didnt get into try");
		text = new ArrayList<String>();
		tokens = new ArrayList<String>();
		cells = new ArrayList<String>();
		HTML = new ArrayList<String>();
		HTMLFINAL = new ArrayList<String>();
		
		
		ArrayList<String> searchterms = terms;
		linecounter =0;
	
		try {
			URL urlObj = new URL(pdfUrl);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.addRequestProperty("Cache-Control", "no-cache,max-age=0");
			connection.addRequestProperty("Pragma", "no-cache");
			connection.setInstanceFollowRedirects(false);
			int httpRespCode = connection.getResponseCode();
		

			if (httpRespCode == 200) {
				RandomAccessBuffer tempMemBuffer = new RandomAccessBuffer();
				PDDocument doc = PDDocument.load(connection.getInputStream(), tempMemBuffer);
				

				PDFTextStripperByArea sa = new PDFTextStripperByArea();
	
				
				sa.addRegion("Area1", new Rectangle2D.Double(x, y, w, h));
			
				//PDPage p = (PDPage) doc.getDocumentCatalog().getAllPages().get(0); //this line is the problem
				PDDocumentCatalog cat = doc.getDocumentCatalog();
		
				//System.out.println(cat.toString());
				List <PDPage> pages = cat.getAllPages();
				//
				
				//loop for pages
				for (int i=0;i<2; i++){
					PDPage p = pages.get(i);
					sa.extractRegions(p);
					
					//s is a string of the PAGE
					s = sa.getTextForRegion("Area1"); //get the text for the page
					
						linecounter = countLines(s);
						
						//adding tr to all new lines
						tokens = splitStrings(s); //split by line
						for(String tk:tokens){
						
							//for every row TK, append relevant tags
							StringBuilder sb = new StringBuilder();
							sb.append("<tr>");
							sb.append(tk);
							sb.append("</tr> ");
							//System.out.println(sb.toString());
							
							String row = sb.toString(); //row of text with tags
							HTML.add(row); //add row of text with tags
						}
						
						//compile previous tags to string first
//						StringBuilder b = new StringBuilder();
//						for(String s:HTML){
//							b.append(s);
//						}
				}
				
				//text per page
				text.add("<table>");
				for(String t:HTML){
				text.add(t);
				}
				text.add("</table>");
				doc.close();
				

			} else{
				System.out.println("didnt get into if");
				throw new Exception("Http return code <> 200. Received: " + httpRespCode);
			}
			

		} catch (Exception e) {
			log.severe("EXCEPTION: " + e.toString());
			//return "*** EXCEPTION *** " + e.toString();
		}
		return text;
	}
	
	//for no numbers
	private static String formatMethod2(String row) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//for numbers
	private static String formatMethod1(String row) {
		ArrayList<String> finalstringcontents = new ArrayList<String>();
		ArrayList<String> celldata = new ArrayList<String>();
		
		//split row into cells
		celldata = splitStringSpace(row); 
		
		//find first occurence of numbers
		int position = firstPositionOfNumbers(celldata); 	
		
		//for everything before that point we append into 1 string
		
		StringBuilder compile = new StringBuilder();
		compile.append("<td>");
		for(int f=0; f<position;f++){
			
			compile.append(celldata.get(f));
		}
		compile.append("</td>");
		
		//add to final list
		finalstringcontents.add(compile.toString());
		
		//for everything that point onwards we append separate td tags for each cell
		for(int e=position;e<celldata.size();e++){
			StringBuilder build1 = new StringBuilder();
			build1.append("<td>");
			build1.append(celldata.get(e));
			build1.append("</td>");
			
			//add our newbuild string to list.
			finalstringcontents.add(build1.toString());
		}
		
		String finalstring = finalstringcontents.toString();
		System.out.println("F string: " + finalstring);
		return finalstring;
	}

	private static int countLines(String str){
		   String[] lines = str.split("\r\n|\r|\n");
		   return  lines.length;
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
		
		private static int firstPositionOfNumbers(ArrayList<String> s){
			int position = 0;
			for(int i=0;i<s.size();i++){
				if(containsDigit(s.get(i))){
					position = i;
					break;
				}
			} return position;

		}

}

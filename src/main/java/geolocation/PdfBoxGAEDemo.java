package geolocation;

/**
 * This class strips text from the PDF document and appends <tr> tags to each new line
 * of text extracted. Line detection is done checking for new lines \n or carriage returns \r
 * each line is then separated from the original string of text and tags are added.
 * To ensure lines are in some sort of vague format, <table> tags are added for current 
 * formatting. In later versions, it's possible that these will be removed and be for the BusParser 
 * only with this class being the "if all else fails to match" class for simple text extraction 
 * and display with zero formatting.
 */


import java.awt.geom.Rectangle2D;
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
	static ArrayList<String> text;
	static String s;
	static ArrayList<String> tokens;
	static ArrayList<String> HTML;
	static String finalcontents;

	
	public static String Exec(String pdfUrl, int x, int y, int w, int h, ArrayList<String> terms) {

		log.info("PdfUrl=" + pdfUrl);
		text = new ArrayList<String>();
		tokens = new ArrayList<String>();
		HTML = new ArrayList<String>();
		
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
				PDDocumentCatalog cat = doc.getDocumentCatalog();
				List <PDPage> pages = cat.getAllPages();
				
				if(pdfUrl.contains("firstgroup")){
					System.out.println("First bus timetable detected");
					//loop for pages
					for (int i=0;i<pages.size(); i++){
						PDPage p = pages.get(i);
						sa.extractRegions(p);
						
						//s is a string of the PAGE
						s = sa.getTextForRegion("Area1"); //get the text for the page
						
							
							tokens = splitStrings(s); //split by line
							for(String tk:tokens){
							
								//for every row TK, append relevant tags
								StringBuilder sb = new StringBuilder();
								sb.append("<tr>");
								sb.append(tk);
								sb.append("</tr> ");
								
								String row = sb.toString(); //row of text with tags
								HTML.add(row); //add row of text with tags
							}
							
					}
					
					//text per page
					text.add("<table>"); //placing table tags means all the data will be formatted as such.
					for(String t:HTML){
					text.add(t);
					}
					text.add("</table>");
					
					//then we need to go into bus parser
					BusParser bp = new BusParser(text, terms);
					finalcontents = bp.Exec();
					
				} else {
					
					//this is the generic parser - only rips text out, zero formatting.
					for (int i=0;i<4; i++){
						PDPage p = pages.get(i);
						sa.extractRegions(p);
						
						//s is a string of the PAGE
						s = sa.getTextForRegion("Area1"); //get the text for the page
						text.add(s);
						
						StringBuilder appender = new StringBuilder();
						for(String s:text){
							appender.append(s);
						}
						
						finalcontents = appender.toString();
					}
				}
				
				
				doc.close();
				

			} else{
				throw new Exception("Http return code <> 200. Received: " + httpRespCode);
			}
			

		} catch (Exception e) {
			log.severe("EXCEPTION: " + e.toString());
		}
		return finalcontents;
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
		
	

}

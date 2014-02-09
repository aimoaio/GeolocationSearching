package org.jboss.as.quickstarts.helloworld;







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

	public static String Exec(String pdfUrl, int x, int y, int w, int h, ArrayList<String> terms) {

		log.info("PdfUrl=" + pdfUrl);
		System.out.println("didnt get into try");
		text = new ArrayList<String>();
		tokens = new ArrayList<String>();
		cells = new ArrayList<String>();
		HTML = new ArrayList<String>();
		ArrayList<String> searchterms = terms;
		linecounter =0;
	
		try {
			URL urlObj = new URL(pdfUrl);
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.addRequestProperty("Cache-Control", "no-cache,max-age=0");
			connection.addRequestProperty("Pragma", "no-cache");
			connection.setInstanceFollowRedirects(false);
			int httpRespCode = connection.getResponseCode();
			System.out.println("just before the if");

			if (httpRespCode == 200) {
				RandomAccessBuffer tempMemBuffer = new RandomAccessBuffer();
				PDDocument doc = PDDocument.load(connection.getInputStream(), tempMemBuffer);
				System.out.println("line1");

				PDFTextStripperByArea sa = new PDFTextStripperByArea();
	
				System.out.println("line 1.1");
				sa.addRegion("Area1", new Rectangle2D.Double(x, y, w, h));
				System.out.println("line 1.2");
				//PDPage p = (PDPage) doc.getDocumentCatalog().getAllPages().get(0); //this line is the problem
				PDDocumentCatalog cat = doc.getDocumentCatalog();
				System.out.println("line 1.3");
				//System.out.println(cat.toString());
				List <PDPage> pages = cat.getAllPages();
				
				System.out.println("line 1.4");
				
				//loop for pages
				for (int i=0;i<2; i++){
					PDPage p = pages.get(i);
					sa.extractRegions(p);
					
					//s is a string of the PAGE
					s = sa.getTextForRegion("Area1"); //get the text for the page
					
					if(searchterms.size()!=0){
						
						for(int j=0;j<searchterms.size();j++){
						String replace = searchterms.get(j);
						//System.out.println("Terms: " + replace);
						String newterm = "<span style='background-color:yellow;'>" + replace + "</span>";
						s = s.replace(replace, newterm);
						
						}
						
						linecounter = countLines(s);
						System.out.println("Line counter at: " + linecounter);
						
						//adding tr to all new lines
						tokens = splitStrings(s); //split by line
						for(String tk:tokens){
						
							//for every row TK, append relevant tags
							StringBuilder sb = new StringBuilder();
							sb.append("<tr>");
							sb.append("<td>");
							sb.append(tk);
							sb.append("</td>");
							sb.append("</tr>");
							System.out.println(sb.toString());
							
							String row = sb.toString(); //row of text with tags
							HTML.add(row); //add row of text with tags
						}
						
						
						
						}
					
					
					
				}
				
				//text per page
				text.add("<table>");
				StringBuilder stringer = new StringBuilder();
				for(String t:HTML){
					stringer.append(t);
				}
				text.add(stringer.toString());
				text.add("</table>");
				text.add("<br/>");
				System.out.println("T: " + text.toString());
				StringBuilder stringbuild = new StringBuilder();
				for(String t:text){
					stringbuild.append(t);
				}
				finaltext = stringbuild.toString();
				
				doc.close();
				return finaltext;
				

			} else{
				System.out.println("didnt get into if");
				throw new Exception("Http return code <> 200. Received: " + httpRespCode);
			}
			

		} catch (Exception e) {
			log.severe("EXCEPTION: " + e.toString());
			//return "*** EXCEPTION *** " + e.toString();
		}
		return finaltext;
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
}

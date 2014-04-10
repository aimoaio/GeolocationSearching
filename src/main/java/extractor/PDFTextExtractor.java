package extractor;

/**
 * 
 * This class strips text from the PDF document and passes the contents into either of the
 * following three parsers: BusParser, NorwegianRoadDataParser or the GenericParser.
 * 
 * Text extraction is done by a third party package - PDFBox.
 * Pages are extracted from the PDF Document using the getAllPages method.
 * getTextForRegion() then returns the text specified in a region (Rectangle) of a page.
 * This text is then modified before being passed to the parser classes.
 * 
 * As of current, the GenericParser and the NorwegianRoadDataParser page extraction method work the same.
 * Both include page numbering, context display and search term highlighting.
 * 
 * The BusParser text extraction also includes adding additional <table> and <tr> tags before the formatting stage.
 * This is required for the Parser later on to check the type of row content before it applies the relevant formatting rules.
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

import parsers.BusParser;
import parsers.GenericParser;
import parsers.NorwegianRoadDataParser;



public class PDFTextExtractor {

	private static final Logger log = Logger.getLogger(PDFTextExtractor.class.getName());
	static ArrayList<String> text;
	static String page;
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
				
				/*
				 * If the PDF is from the FirstGroup website then pass it to the BusParsr class.
				 * For this, each page must be appended with the <tr> and <table> tags. These are used for
				 * row type detection and formatting in the BusParser class in which formatting is
				 * decided on the category type of the row.
				 */
				
				if(pdfUrl.contains("firstgroup")){
					
					for (int i=0;i<pages.size(); i++){ //loop for all pages
						PDPage p = pages.get(i);
						sa.extractRegions(p);
						
						page = sa.getTextForRegion("Area1"); //get the text for the page
						tokens = splitStrings(page); //split each line into rows
							
						
						for(String tk:tokens){
							
								/*
								 * For every row in each page, append the <tr> tags.
								 * This is done using StringBuilder by compiling the tag and the row
								 * into one string before adding to a new arraylist.
								 */
								StringBuilder sb = new StringBuilder();
								sb.append("<tr>");
								sb.append(tk);
								sb.append("</tr> ");
								
								String row = sb.toString(); //row of text with tags
								HTML.add(row); //add row of text with tags to list
						}
							
					}
					
					/*
					 * Table tags are added at this stage so the BusParser formatter is able to detect these
					 * and keep them as originals later on. Table tags are ONLY added to the start and end of
					 * each page's contents. Meaning the PDFs from FirstGroup are all formatted in a table structure.
					 */
					text.add("<table>"); 
					for(String t:HTML){
						text.add(t);
					}
					text.add("</table>");
					
					
					BusParser bp = new BusParser(text, terms); //now we pass our contents to the parser for formatting.
					finalcontents = bp.format();
					
					
				/*
				 * If the PDF contains the word Norwegian, then we can assume it is the road data PDFs provided by the government.
				 * This parser differs from the BusParser in the sense that no table structure is used. Context display is also included
				 * due to the sheer size of these PDFs generally to reduce processing time in which only pages where the term occurs
				 * are returned instead of everything in the PDF. Term highlighting however, is the same as the BusParser.
				 * 
				 * In the event, no search terms were found in the PDF at all, the
				 * parser will return ALL pages and display a warning that nothing has been found at the beginning of the text.
				 * 
				 * However the code here only checks for the size of the list being returned by the parser before displaying the warning.
				 * (Not sure why it's 4...when it's blank...)
				 */
					
				} else if(pdfUrl.contains("norwegian")){
					
					
					for (int i=0;i<pages.size(); i++){
						PDPage p = pages.get(i);
						sa.extractRegions(p);
						
						page = sa.getTextForRegion("Area1"); 
						text.add(page);
					}
				
					NorwegianRoadDataParser np = new NorwegianRoadDataParser(text, terms);
					finalcontents = np.format();
					
				
					
					
					//23 includes the <br><div id="tbl1"><br> tags that are added.
					if(finalcontents.length()==23){
						
						/*
						 * We construct the text arraylist (which held all the pages) into one string as
						 * this string will be returned to main.jsp for display including all format tags.
						 */
						StringBuilder builder = new StringBuilder();
						for (String textstuff :text){
							builder.append(textstuff);
						}
						
						/*
						 * This is the warning in yellow to be added to the start of the pages being returned.
						 * Insert is used so the text is added at the beginning of the contents and not at the end as
						 * we want it to be displayed as a warning.
						 * The final contents are then converted to a string.
						 */
						builder.insert(0, "<span style='background-color: yellow;'> WARNING: NO MATCHING TERMS FOUND. "+finalcontents + "</span><br/>");
						finalcontents = builder.toString();
					}
				
					
				/*
				 * If it's not from FirstGroup or isn't a Norwegian road data PDF, then use the generic parser.
				 * This parser works exactly the same as the Norwegian parser - context only with page numbering 
				 * and highlighting. But can be modified in future to suit. 
				 */
				} else {
					
					for (int i=0;i<pages.size(); i++){
						PDPage p = pages.get(i);
						sa.extractRegions(p);
						
						page = sa.getTextForRegion("Area1"); 
						text.add(page);
					}
				
					GenericParser gp = new GenericParser(text, terms);
					finalcontents = gp.format();
					
				
					
					//23 includes the <br><div id="tbl1"><br> tags that are added.
					if(finalcontents.length()==23){
						
						StringBuilder builder = new StringBuilder();
						for (String textstuff :text){
							builder.append(textstuff);
						}
						
						builder.insert(0, "<span style='background-color: yellow;'> WARNING: NO MATCHING TERMS FOUND. </span><br/>");
						finalcontents = builder.toString();
					}
					
				}
				doc.close();
				}
			
			/*
			 * Assuming that the only user input required is the PDF URL. The only time the system should break is when
			 * the PDF URL is invalid, blank or the PDF cannot be obtained from the URL. In this event, the system will still
			 * go ahead and work as default but instead of returning the PDF contents, it will return the following line as
			 * an error message to the user.
			 */
			else {
				finalcontents = "<div id=\"tbl2\">Blank or Invalid URL</div>";
				throw new Exception("Http return code <> 200. Received: " + httpRespCode);
				
			}
			

		} catch (Exception e) {
			finalcontents = "<div id=\"tbl2\">Blank or Invalid URL</div>";
			log.severe("EXCEPTION: " + e.toString());
		}
		return finalcontents;
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
	

}

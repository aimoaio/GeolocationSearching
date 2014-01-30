package org.jboss.as.quickstarts.helloworld;





import java.awt.geom.Rectangle2D;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

	public static ArrayList<String> Exec(String pdfUrl, int x, int y, int w, int h, ArrayList<String> terms) {

		log.info("PdfUrl=" + pdfUrl);
		System.out.println("didnt get into try");
		text = new ArrayList<String>();
		ArrayList<String> searchterms = terms;
	
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
				for (int i=0;i<4; i++){
					PDPage p = pages.get(i);
					sa.extractRegions(p);
					s = sa.getTextForRegion("Area1"); //get the text for the page
					
					if(searchterms.size()!=0){
						
						for(int j=0;j<searchterms.size();j++){
						String replace = searchterms.get(j);
						System.out.println("Terms: " + replace);
						String newterm = "<span style='background-color:yellow;'>" + replace + "</span>";
						s = s.replace(replace, newterm);
						
						}
						text.add(s);
						}
				}
				
				//System.out.println("line 1.5");
				//System.out.println("line2");
				//text = sa.getTextForRegion("Area1");
				//System.out.println(text);
				doc.close();
				return text;
				

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
}

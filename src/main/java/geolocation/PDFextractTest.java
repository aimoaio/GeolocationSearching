package geolocation;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;


public class PDFextractTest {

	public static void main(String[] args) {
		PDDocument document;
		try {
			PDFTextStripper stripper;
			document = PDDocument.load(new File("src\\main\\webapp\\firstbustimetable.pdf"));
			stripper = new PDFTextStripper();
			stripper.setSortByPosition( true );
			System.out.println(stripper.getText(document));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}

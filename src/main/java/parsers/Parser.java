package parsers;

/**
 * An interface for all classes that implement the Parser interface.
 * This interface includes methods required by each Parser class.
 * format - the method of formatting unique to each class.
 * addHighlight - the method of highlighting, may be identical or with modifications for each class.
 */
import java.util.ArrayList;

public interface Parser {
	
	public String format();
	
	public ArrayList<String> addHighlight(ArrayList<String> rows, String geoterm, String colour);

}

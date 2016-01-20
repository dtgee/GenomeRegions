import java.util.ArrayList;

/*
 * Since we might have a very large number of regions/ranges, we won't read every region into memory right away.
 * Instead, we'll read one line at a time and process it that way.
 * This class takes in an inputPath to the regions text file and create our ArrayList of regions
 */
public class RegionSeparator {
	
	public RegionSeparator() {
		
	}
	
	public ArrayList<ModifiedRegion> separate(Parser parser) {
		ArrayList<ModifiedRegion> modifiedRegions = new ArrayList<ModifiedRegion>();
		
		int origPos = 1;
		while(parser.hasNextLine()) {
			Region r = parser.parseRegion();
			modifiedRegions.add(new ModifiedRegion(r.begin, origPos, false));
			modifiedRegions.add(new ModifiedRegion(r.end, origPos, true));
			origPos++;
		}
		parser.close();
		return modifiedRegions;
	}
}

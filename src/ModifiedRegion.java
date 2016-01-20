import java.util.Comparator;

/*
 *  ModifiedRegion is our begin and end points for our Integer ranges. We save the value of the begin or end,
 *  and mark it as so. We also save the original position in the text file that we read the
 *  value from, so that we can later print the drawing row back in the same order as the integer region.
 *  Lastly, we store the row of the Integer range, which we will assign to later. The idea of this class
 *  is that we can sort a list of start and end point, and so then we can figure out the overlapping integer ranges
 *  This is the core of solving the overlapping regions and will cost O(n log n) time and O(n) space
 */
public class ModifiedRegion implements Comparable<ModifiedRegion> {
	private int val;
	private int origPos;
	private int row;
	private boolean isEndpoint;

	public ModifiedRegion(int val, int origIdx, boolean isEndpoint) {
		this.val = val;
		this.origPos = origIdx;
		row = -1;
		this.isEndpoint = isEndpoint;
	}
	
	@Override
	public int compareTo(ModifiedRegion o) {
		return Comparators.VAL.compare(this, o);
	}

	/*
	 *  We'll need some comparators to be able to sort our ModifiedRegions class. We sort by the value of the begin
	 *  or end point, and prioritize start points. Once we have the points sorted, we can start assigning drawing
	 *  rows when we iterate through this list. For example, if we run into two start points in a row, e.g.
	 *  255 255, we know we're overlapping two integer ranges.
	 *  
	 *  We'll also need a comparator to resort the list based on position in the original text file, so that
	 *  we can reconstruct the correct line to print the drawing row on.
	 */
	public static class Comparators {

		public static Comparator<ModifiedRegion> VAL = new Comparator<ModifiedRegion>() { 
			@Override
			public int compare(ModifiedRegion mr1, ModifiedRegion mr2) {
				int result = compareIntFields(mr1.getVal(), mr2.getVal());
				
				result = prioritizeStartpoints(result, mr1.isEndpoint(), mr2.isEndpoint());
				return result;
			}
		};

		public static Comparator<ModifiedRegion> POS = new Comparator<ModifiedRegion>() { 
			@Override
			public int compare(ModifiedRegion mr1, ModifiedRegion mr2) {
				
				int result = compareIntFields(mr1.getOrigPos(), mr2.getOrigPos());
				result = prioritizeStartpoints(result, mr1.isEndpoint(), mr2.isEndpoint());
				return result;
			}
		};
		
		public static int prioritizeStartpoints(int result, boolean mr1Endpoint, boolean mr2Endpoint) {
			/*
			*	We have to put the starting point ahead of the endpoint otherwise
			*	our algorithm won't work correctly for overlapping single point i.e. 31744131	31744131
			*/
			if (result == 0) {
				return compareBooleanFields(mr1Endpoint, mr2Endpoint);
			}
			return result;
		}
		
		public static int compareBooleanFields(boolean mr1Field, boolean mr2Field) {
			Boolean b1 = new Boolean(mr1Field);
			Boolean b2 = new Boolean(mr2Field);
			return b1.compareTo(b2);
		}
		
		public static int compareIntFields(int mr1Field, int mr2Field) {
			return Integer.compare(mr1Field, mr2Field);
		}

	}
	
	public int getRow() {
		return row;
	}

	public int getOrigPos() {
		return origPos;
	}


	public int getVal() {
		return val;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public boolean isEndpoint() {
		return isEndpoint;
	}

}

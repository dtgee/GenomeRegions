import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

public class PrintRows {

	public static void main(String[] args){
		/*
		 * Initialize Parser stuff
		 */
		RegionSeparator rs = new RegionSeparator();
		Parser parser = new Parser();
		parser.setInputPath(args[0]);
		parser.initialize();
		File input = new File(parser.getInputPath());

		/*
		 * Solve parts A and B
		 */
		List<ModifiedRegion> modifiedRegions = rs.separate(parser);
		Collections.sort(modifiedRegions, ModifiedRegion.Comparators.VAL);
		printSegments(input, modifiedRegions);
		calculateRows(modifiedRegions);

		/*
		 * Reconstruct results so that we print in the matching order of the text file
		 */
		Collections.sort(modifiedRegions, ModifiedRegion.Comparators.POS);
		HashMap<Integer, ArrayList<Integer>> valMap = createMap(modifiedRegions);
		updateRows(valMap, modifiedRegions);
		printDrawingRows(input, modifiedRegions);

	}

	public static void printDrawingRows(File input, List<ModifiedRegion> modifiedRegions) {
		PrintWriter writer = createWriter(input, "PartA.txt");
		for (int i = 0; i < modifiedRegions.size(); i+=2){
			ModifiedRegion mRegion = modifiedRegions.get(i);
			writer.println(mRegion.getRow());
		}
		writer.close();
	}

	public static void printSegments(File input, List<ModifiedRegion> modifiedRegions) {
		PrintWriter writer = createWriter(input, "PartB.txt");
		int rows = 0;
		ModifiedRegion prev = null;

		for (ModifiedRegion mRegion : modifiedRegions) {
			if (prev == null) {
				prev = mRegion;
				rows++;
				continue;
			}
			int begin =  prev.getVal();
			int end = (mRegion.getVal() - 1);
			if (!mRegion.isEndpoint()) {
				if ((prev.getVal() != mRegion.getVal())) {
					writer.println(begin + "\t" + end + "\t" + rows);
				}
				rows++;
			} else {
				writer.println(begin + "\t" + end + "\t" + rows);
				rows--;
			}
			prev = mRegion;
		}
		writer.close();
	}

	public static PrintWriter createWriter(File input, String name) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(input.getParentFile() + File.separator + name, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return writer;
	}

	/*
	 * To calculate the rows, we need to keep track of which rows are free and which ones are not.
	 * We need to prioritize the rows that are free based on lowest drawing row, so we'll do that
	 * using a min heap. To keep track of what is in use, we'll use a stack because we want
	 * our drawing rows to be set up so that the largest integer range takes on the lowest
	 * drawing row., and the shortest integer ranges will be at the upper drawing rows. Note that
	 * a very important step is that we do not push whatever is in use until after we've hit an
	 * endpoint. This is because if we pushed whatever is in use right away, an endpoint could
	 * come right after and free the drawing row we just put in use immediately. To do that,
	 * we'll buffer all the drawing rows that we started into an ArrayList, and then let our
	 * program know that all those drawing rows are in use when we hit an endpoint.
	 */
	public static void calculateRows(List<ModifiedRegion> mRegionList) {
		PriorityQueue<Integer> unused = new PriorityQueue<Integer>();
		Stack<Integer> inUse = new Stack<Integer>();
		ArrayList<Integer> buffer = new ArrayList<Integer>();
		unused.add(1);

		for (ModifiedRegion mRegion : mRegionList) {
			update(mRegion, unused, inUse, buffer);
		}
	}

	public static void update(ModifiedRegion mRegion, PriorityQueue<Integer> unused, Stack<Integer> inUse, ArrayList<Integer> buffer) {
		int row = -1;
		if (!mRegion.isEndpoint()) {
			row = assignRow(unused);
			buffer.add(row);
			mRegion.setRow(row);
		} else {
			inUse.addAll(buffer);
			buffer.clear();
			row = updateFree(unused, inUse);
			mRegion.setRow(row);
		}
	}

	public static int assignRow(PriorityQueue<Integer> unused) {
		int row = unused.remove();
		if (unused.isEmpty()) {
			unused.add(row + 1);
		}
		return row;
	}

	public static int updateFree(PriorityQueue<Integer> unused, Stack<Integer> inUse) {
		int nextAvailable = inUse.pop();
		unused.add(nextAvailable);
		return nextAvailable;
	}

	/*
	 * Since we can have the same starting point for many integer ranges, our algorithm won't know
	 * which drawing row corresponds with each entry in the original text file if the integer ranges
	 * start with the same value. Therefore, we need to recalculate the drawing rows for each starting
	 * point that is the same as some other starting point. To do so, we can find the endpoints of 
	 * each starting point since we're sorted by original position. Once we have the endpoints, we
	 * know that the largest integer range is supposed to go in the lowest possible drawing row.
	 * Since the starting points are all the same, but the endpoints usually differ, we can sort by the
	 * endpoints and assign the correct drawing row based on that. For the largest endpoint, assign the smallest
	 * drawing row (using a min heap). Then, after we have the pairs of endpoints and corresponding drawing rows,
	 * store these key value pairs into a hashmap. Since our list of modified regions will be currently
	 * sorted by original positions of the text file, we can calculate where the endpoints and start points will
	 * be of each integer range. We can then update the drawing rows by calculating indices of the start 
	 * and endpoints, grabbing the endpoint, and then using the endpoint as a key to lookup the correct corresponding
	 * drawing row in the hashmap.
	 * 
	 * For each starting point value, we need to construct an ArrayList of the original positions which the
	 * value corresponds with. Then use the original positions to grab the endpoints and rows, and sort them.
	 * The complexity of this is O(n logn) mainly characterized by the sorting. However, our list is usually small.
	 */
	public static HashMap<Integer, ArrayList<Integer>> createMap(List<ModifiedRegion> mRegionList) {
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
		for (int i = 0; i < mRegionList.size(); i+=2) {
			ModifiedRegion mRegion = mRegionList.get(i);
			int mRegionVal = mRegion.getVal();
			if (!map.containsKey(mRegionVal)) {
				ArrayList<Integer> origPositions = new ArrayList<Integer>();
				origPositions.add(mRegion.getOrigPos());
				map.put(mRegionVal, origPositions);
			} else {
				map.get(mRegionVal).add(mRegion.getOrigPos());
			}
		}
		return map;
	}

	public static void updateRows(HashMap<Integer, ArrayList<Integer>> valMap, List<ModifiedRegion> mRegionList) {
		for (Map.Entry<Integer, ArrayList<Integer>> entry : valMap.entrySet()) {
			ArrayList<Integer> origPositions = entry.getValue();
			ArrayList<Integer> endpoints = new ArrayList<Integer>();
			ArrayList<Integer> rows = new ArrayList<Integer>();
			HashMap<Integer, PriorityQueue<Integer>> hm = new HashMap<Integer, PriorityQueue<Integer>>();
			
			calcEndptsAndRows(origPositions, mRegionList, endpoints, rows);
			Collections.sort(endpoints, Collections.reverseOrder());
			Collections.sort(rows);
			
			for (int i = 0; i < endpoints.size(); i++) {
				int key = endpoints.get(i);
				if (!hm.containsKey(key)) {
					PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>(); 
					minHeap.add(rows.get(i));
					hm.put(key, minHeap);
				} else {
					PriorityQueue<Integer> minHeap = hm.get(key);
					minHeap.add(rows.get(i));
					hm.put(key, minHeap);
				}
			}
			
			for (int i = 0; i < origPositions.size(); i++) {
				int index = origPositions.get(i);
				ModifiedRegion[] mRegionsPair = getStartAndEndRegions(index, mRegionList);
				int endpoint = mRegionsPair[1].getVal();
				PriorityQueue<Integer> minHeap = hm.get(endpoint);
				int row = minHeap.poll();
				hm.put(endpoint, minHeap);
				mRegionsPair[0].setRow(row);
			}
		}
	}
	
	public static void calcEndptsAndRows(ArrayList<Integer> origPositions, List<ModifiedRegion> mRegionList, ArrayList<Integer> endpoints, ArrayList<Integer> rows) {
		for (int i : origPositions) {
			ModifiedRegion[] mRegionsPair = getStartAndEndRegions(i, mRegionList);
			int endpoint = mRegionsPair[1].getVal();
			int row = mRegionsPair[0].getRow();
			endpoints.add(endpoint);
			rows.add(row);
		}
	}
	
	public static ModifiedRegion[] getStartAndEndRegions(int index, List<ModifiedRegion> mRegionList) {
		int startPointIndex = ((index-1)*2);
		int endpointIndex = startPointIndex + 1;
		ModifiedRegion mRegionEnd = mRegionList.get(endpointIndex);
		ModifiedRegion mRegionStart = mRegionList.get(startPointIndex);
		ModifiedRegion[] mRegions = new ModifiedRegion[2];
		mRegions[0] = mRegionStart;
		mRegions[1] = mRegionEnd;
		return mRegions;
	}

 	public static void debugPrint(List<ModifiedRegion> modifiedRegions) {
		for (int i = 0; i < modifiedRegions.size(); i++){
			ModifiedRegion mRegion = modifiedRegions.get(i);
			System.out.println(mRegion.getVal() + " " + mRegion.getOrigPos() + " " + mRegion.getRow());
		}
	}
}

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Parser {
	private FileInputStream inputStream;
	private Scanner sc;
	private String inputPath;
	
	public Parser() {
		inputStream = null;
		sc = null;
		this.inputPath = "";
	}
	
	public void initialize() {
		try {
			inputStream = new FileInputStream(inputPath);
			sc = new Scanner(inputStream, "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 *  We expect two integers in each line and we assume the first integer is always less than the second.
	 */
	public Region parseRegion() {
		String line = sc.nextLine();
		Scanner lineSc = new Scanner(line);
		
		while (lineSc.hasNextInt()) {
			int begin = lineSc.nextInt();
			if (!lineSc.hasNextInt()) {
				throw new NumberFormatException("Missing an integer in a pair of integers!");
			} else {
				int end = lineSc.nextInt();
				
				/*
				 *  Make sure we have a valid range for our region.
				 */
				if (begin <= end) {
					Region r = new Region(begin, end);
					if (lineSc.hasNextInt()) {
						throw new NumberFormatException("One pair of Integers per line!");
					}
					return r;
				} else {
					throw new IndexOutOfBoundsException("Range is out of bounds!");
				}
			}
		}
		lineSc.close();
		return null;
	}
	
	public String nextLine() {
		return sc.nextLine();
	}
	
	public boolean hasNextLine() {
		return sc.hasNextLine();
	}
	
	public void close() {
		sc.close();
	}
	
	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}
	
	public String getInputPath() {
		return inputPath;
	}
	
}

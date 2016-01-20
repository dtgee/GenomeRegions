
public class Extras {
	
	/*
	 * 
	 * 
	 * 
	 * BELONGS IN RegionSeparator
	 * 
	 * 
	 * 
	 * 
	 * 
	public void writeToTextFile(String outputPath, int count, int val, boolean end) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(outputPath + File.separator + count +  ".txt", "UTF-8");
		if (end) {
			writer.println(val + "e");
		} else {
			writer.println(val);
		}
		writer.close();
	}
	
	
//	 Create the output directory if it doesn't exist and return the absolute path name.
//	 NOTE: Unused. Random thought of external file sorting/merging

	public String setOutputDir(String inputPath) {
		if(!inputPath.endsWith(File.separator)) {
			inputPath = inputPath + File.separator;
		}
		File input = new File(inputPath);
		File newDir = new File(input.getParentFile() + File.separator + "output" + File.separator);
		if (!newDir.exists()) {
			newDir.mkdir();
		}
		String outputDir = newDir.getAbsolutePath() + File.separator;
		
		return outputDir;
	}
	
	public void printArgs(String[] args) {
        for (String s: args) {
            System.out.println(s);
        }
	}
	*/
}

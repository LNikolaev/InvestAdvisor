import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Nikolaev
 *
 */
public class InvestAdvisor {

	private static int totalInvestmentValue;
	private static ArrayList<Project> projects = new ArrayList<Project>();
	private static ArrayList<Project> selectedProjects = new ArrayList<Project>();
	
	public static void main(String[] args) {
		String pathname = null;
		try{
			pathname = args[0];
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Please provide file path argument!");
			return;
		}
		
		// Point one and two
		readFileAndLoadData(pathname);

		System.out.println("--------Data loaded---------");
		System.out.println("totalInvestmentValue: " + totalInvestmentValue);
		//print values from read data
		for (Project project : projects) {
			System.out.println("Project: investmentValue = "
					+ project.getInvestmentValue() + " netvalue = "
					+ project.getNetValue());
		}
		
		processData();
		System.out.println("--------Data processed---------");
		System.out.println("--------Selected Projects---------");
		//print result from knapsack algorithm
		for (Project project : selectedProjects) {
			System.out.println("Project: investmentValue = "
					+ project.getInvestmentValue() + " netvalue = "
					+ project.getNetValue());
		}
		System.out.println("--------Writting to file---------");
		//write result in file
		writeSeletedProjectsToFile(pathname);
		System.out.println("--------Job Done---------");
	}

	/**
	 * @author Nikolaev
	 * Write result to file
	 */
	public static void writeSeletedProjectsToFile(String pathname){
		File file = new File(pathname);
		File newFile = null;
		if(file.getParent() == null){  //check if result file "selectedProjects.txt" exist. If does not exist, program will create it.
			newFile = new File("selectedProjects.txt");
		}
		else{
			newFile = new File(file.getParent() + File.separator + "selectedProjects.txt");
		}
		
		FileWriter fileWriter = null;
		BufferedWriter writer = null;
		try {
			fileWriter = new FileWriter(newFile);
			writer = new BufferedWriter(fileWriter);
			for(Project project : selectedProjects){
				writer.write(project.getInvestmentValue() + " " + project.getNetValue());
				writer.newLine(); //insert new line
			}
			// flushes the stream
			writer.flush();
			
		} catch (FileNotFoundException e) {
			System.out.println("Error reading file: " + pathname + " "
					+ e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error reading file: " + pathname + " "
					+ e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * @author Nikolaev
	 * Present knapsack algorithm
	 */
	public static void processData() {

		int N = projects.size(); // number of items
		int W = totalInvestmentValue; // maximum weight of knapsack

		int[] investment = new int[projects.size()+1];
		int[] netValue = new int[projects.size()+1];
		for (int i = 0; i < projects.size(); i++) {
			Project currentProject = projects.get(i);
			investment[i+1] = currentProject.getInvestmentValue();
			netValue[i+1] = currentProject.getNetValue();
		}

		// opt[n][w] = max profit of packing items 1..n with weight limit w
		// sol[n][w] = does opt solution to pack items 1..n with weight limit w
		// include item n?
		int[][] opt = new int[N + 1][W + 1];
		boolean[][] sol = new boolean[N + 1][W + 1];

		for (int n = 1; n <= N; n++) {
			for (int w = 1; w <= W; w++) {

				// don't take item n
				int option1 = opt[n - 1][w];

				// take item n
				int option2 = Integer.MIN_VALUE;
				if (investment[n] <= w)
					option2 = netValue[n] + opt[n - 1][w - investment[n]];

				// select better of two options
				opt[n][w] = Math.max(option1, option2);
				sol[n][w] = (option2 > option1);
			}
		}

		// determine which items to take
		boolean[] take = new boolean[N + 1];
		for (int n = N, w = W; n > 0; n--) {
			if (sol[n][w]) {
				take[n] = true;
				w = w - investment[n];
			} else {
				take[n] = false;
			}
		}

		for (int n = 1; n <= N; n++) {
			boolean selected = take[n];
			if(selected){
				selectedProjects.add(new Project(investment[n], netValue[n]));
			}
		}
	}

	public static void readFileAndLoadData(String filepath) {
		// Will come from arg parameter
		File file = new File(filepath);
		FileReader fileReader = null;
		BufferedReader reader = null;
		try {
			fileReader = new FileReader(file);
			reader = new BufferedReader(fileReader);
			String line = null;

			boolean firstLine = true;
			while ((line = reader.readLine()) != null) { //read lines for data.txt
				if (firstLine) { //Use this if condition only for first line, where is total investment value.
					firstLine = false;
					totalInvestmentValue = Integer.parseInt(line.trim()); //read and set total investment value
				} else {
					String[] split = line.split(" "); //split values by white space
					if(split.length != 2){ // check if elements is not equals to two.
						System.out.println("Missed line due format error --> " + line);
					}
					else{
						try{
							int investmentValue = Integer.parseInt(split[0]); //set investment value from split. 
							int netValue = Integer.parseInt(split[1]); //set net value from split.
							Project project = new Project(investmentValue, netValue); //create new Project object with values from split.
							projects.add(project);
						}
						catch(NumberFormatException e){
							System.out.println("Error parsing number at line --> " + line);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Error reading file: " + filepath + " "
					+ e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error reading file: " + filepath + " "
					+ e.getMessage());
			e.printStackTrace();
		} finally { //close IO
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

class Project {

	private int investmentValue;
	private int netValue;

	Project(int investmentValue, int netValue) {
		this.investmentValue = investmentValue;
		this.netValue = netValue;
	}

	public int getInvestmentValue() {
		return investmentValue;
	}

	public void setInvestmentValue(int investmentValue) {
		this.investmentValue = investmentValue;
	}

	public int getNetValue() {
		return netValue;
	}

	public void setNetValue(int netValue) {
		this.netValue = netValue;
	}
	
}
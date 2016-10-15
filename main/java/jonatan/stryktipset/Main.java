package jonatan.stryktipset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main
{
	public static String SEPARATOR = "\t";

	public static void main(String[] args)
			throws IOException
	{
		System.out.println("Starting application");

		String inputDataLocation = args[0];
		System.out.println("Loading input data from " + inputDataLocation);
		List<Match> matches = new ArrayList<Match>();
		try(BufferedReader buffReader = new BufferedReader(new FileReader(inputDataLocation));) {
			String line = buffReader.readLine();
			int matchat = 0;
			while(line != null) {
				matchat++;
				//System.out.println("LINE:\n" + line);
				String[] matchArgs = line.split(SEPARATOR);
				Match match = new Match(matchat, Float.parseFloat(matchArgs[0]), Float.parseFloat(matchArgs[1]), Float.parseFloat(matchArgs[2]));
				System.out.println(match);
				matches.add(match);
				line = buffReader.readLine();
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Input file not found");
		}

		List<ColumnAlternative> columnAlternatives = new ColumnsCreator(matches).createColumns();

		System.out.println("Asserting...");
		try {
			assertTotalChance(columnAlternatives);
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}

		WeeklyBet weeklyBet = new WeeklyBet(columnAlternatives, 12, 12, 12);
		List<ColumnAlternative> chosenColumns = weeklyBet.getChosenColumns();
		for(ColumnAlternative columnAlternative : chosenColumns) {
			System.out.println(columnAlternative.toString() + " p13: " + columnAlternative.getProbability13() + " p12: "
					+ columnAlternative.getProbability12());
		}

		long startTime = System.nanoTime();
		try {
			sumProbabilities(chosenColumns);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Calculating total probabilities took: " + (System.nanoTime() - startTime) / 1000000000 + " seconds");
	}

	private static void assertTotalChance(List<ColumnAlternative> columnAlternatives)
			throws Exception
	{
		double totalChance = 0;
		for(ColumnAlternative columnAlternative : columnAlternatives) {
			totalChance += columnAlternative.getProbability13();
		}
		if(totalChance > 1.01 || totalChance < 0.99) {
			throw new Exception("Total chance is not 1, it is " + totalChance);
		}
	}

	private static void sumProbabilities(List<ColumnAlternative> chosenColumns)
			throws InterruptedException
	{
		Set<ColumnAlternative> columnsCoveredFor11 = new HashSet<ColumnAlternative>();
		Set<ColumnAlternative> columnsCoveredFor12 = new HashSet<ColumnAlternative>();
		Set<ColumnAlternative> columnsCoveredFor13 = new HashSet<ColumnAlternative>();

		for(ColumnAlternative columnAlternativePrinted : chosenColumns) {
			columnsCoveredFor13.add(columnAlternativePrinted);
			columnsCoveredFor12.addAll(columnAlternativePrinted.getCoveredFor12());
			//TODO: Add 11
		}

		double probability11 = 0;
		double probability12 = 0;
		double probability13 = 0;
		for(ColumnAlternative columnAlternative : columnsCoveredFor11) {
			probability11 += columnAlternative.getProbability11();
		}
		for(ColumnAlternative columnAlternative : columnsCoveredFor12) {
			probability12 += columnAlternative.getProbability12();
//			System.out.println(columnAlternative.toString());
		}
		for(ColumnAlternative columnAlternative : columnsCoveredFor13) {
			probability13 += columnAlternative.getProbability13();
		}
		System.out.println(columnsCoveredFor11.size() + " rows are covered for 11 correct");
		System.out.println("Total probability of 11 correct: " + probability11 * 100 + "%");
		System.out.println(columnsCoveredFor12.size() + " rows are covered for 12 correct");
		System.out.println("Total probability of 12 correct: " + probability12 * 100 + "%");
		System.out.println(columnsCoveredFor13.size() + " rows are covered for 13 correct");
		System.out.println("Total probability of 13 correct: " + probability13 * 100 + "%");
	}
}

package jonatan.stryktipset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main
{
	public static int NUMBER_OF_COLUMNS_PER_PAGE = 8;
	public static String SEPARATOR = "\t";

	public static void main(String[] args) throws IOException
	{
		System.out.println("Starting application");

		String inputDataLocation = args[0];
		System.out.println("Loading input data from " + inputDataLocation);
		List<Match> matches = new ArrayList<Match>();
		try(BufferedReader buffReader = new BufferedReader(new FileReader(inputDataLocation));) {
			String line = buffReader.readLine();
			while(line != null) {
				System.out.println("LINE:\n" + line);
				String[] matchArgs = line.split(SEPARATOR);
				Match match = new Match(Float.parseFloat(matchArgs[0]), Float.parseFloat(matchArgs[1]), Float.parseFloat(matchArgs[2]));
				matches.add(match);
				line = buffReader.readLine();
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Input file not found");
		}

		ColumnsCreator columnsCreator = new ColumnsCreator(matches);
		columnsCreator.createColumns();
		List<ColumnAlternative> columnAlternatives = columnsCreator.getColumnAlternatives();

		try {
			assertTotalChance(columnAlternatives);
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Sorting columns...");
		columnAlternatives.sort(new ColumnAlternative.ColumnSorter());
		Collections.reverse(columnAlternatives);

		List<ColumnAlternative> columnAlternativesAlreadyPrinted = new ArrayList<ColumnAlternative>();
		print13(columnAlternatives, columnAlternativesAlreadyPrinted);
		print12(columnAlternatives, columnAlternativesAlreadyPrinted);
		print11(columnAlternatives, columnAlternativesAlreadyPrinted);

		long startTime = System.nanoTime();
		try {
			new ProbabilityMultiThreader(8).printAndCalculate(columnAlternatives, columnAlternativesAlreadyPrinted);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Calculating total probabilities took: " + (System.nanoTime() - startTime) / 1000000000 + " seconds");
	}

	private static void assertTotalChance(List<ColumnAlternative> columnAlternatives) throws Exception
	{
		float totalChance = 0;
		for(ColumnAlternative columnAlternative : columnAlternatives) {
			totalChance += columnAlternative.getProbability();
		}
		if(totalChance > 1.01 || totalChance < 0.99) {
			throw new Exception("Total chance is not 1, it is " + totalChance);
		}
	}

	private static void print13(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnAlternativesAlreadyPrinted)
	{
		printX(columnAlternatives, columnAlternativesAlreadyPrinted, 13 - 13);
	}

	private static void print12(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnAlternativesAlreadyPrinted)
	{
		printX(columnAlternatives, columnAlternativesAlreadyPrinted, 13 - 12);
	}

	private static void print11(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnAlternativesAlreadyPrinted)
	{
		printX(columnAlternatives, columnAlternativesAlreadyPrinted, 13 - 11);
	}

	private static void printX(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnsAlreadyPrinted, int difference)
	{
		System.out.println("\nPrinting " + (13 - difference) + ":");

		int origSize = columnsAlreadyPrinted.size();
		for(int i = 0; i < columnAlternatives.size(); i++) {
			if(columnsAlreadyPrinted.size() >= origSize + NUMBER_OF_COLUMNS_PER_PAGE) {
				break;
			}
			ColumnAlternative columnAlternative = columnAlternatives.get(i);
			if(isCoveredFor(columnAlternative, columnsAlreadyPrinted, difference)) {
				continue;
			}
			ColumnAlternative columnAlternativeToAdd = getColumnToCoverFor(columnAlternative, columnAlternatives, columnsAlreadyPrinted, difference);

			System.out.println(columnAlternativeToAdd.toString());
			columnsAlreadyPrinted.add(columnAlternativeToAdd);
		}
	}

	private static boolean isCoveredFor(ColumnAlternative columnAlternativeToCheck, List<ColumnAlternative> columnsAlreadyPrinted,
			int wantedDifference)
	{
		for(ColumnAlternative columnAlreadyPrinted : columnsAlreadyPrinted) {
			int difference = compareColumns(columnAlreadyPrinted, columnAlternativeToCheck, wantedDifference + 1);
			if(difference > wantedDifference) {
				continue;
			}
			return true;
		}
		return false;
	}

	private static ColumnAlternative getColumnToCoverFor(ColumnAlternative columnAlternativeToCover, List<ColumnAlternative> columnAlternatives,
			List<ColumnAlternative> columnsAlreadyPrinted, int wantedDifference)
	{
		int indexToStartAt = columnAlternatives.indexOf(columnAlternativeToCover);

		for(int i = indexToStartAt; i < columnAlternatives.size(); i++) {
			ColumnAlternative columnAlternativeInList = columnAlternatives.get(i);

			int differenceToColumn = compareColumns(columnAlternativeInList, columnAlternativeToCover, wantedDifference + 1);
			if(differenceToColumn > wantedDifference) {
				continue;
			}

			boolean isOkayToAdd = true;
			for(ColumnAlternative columnAlternativeAlreadyPrinted : columnsAlreadyPrinted) {
				int differenceToAlreadyPrinted = compareColumns(columnAlternativeInList, columnAlternativeAlreadyPrinted, wantedDifference * 2 + 1);
				if(differenceToAlreadyPrinted > Math.max(wantedDifference, 0)) {
					continue;
				} else {
					isOkayToAdd = false;
					break;
				}
			}

			if(isOkayToAdd) {
				return columnAlternativeInList;
			}

		}

		System.out.println("No matching column found, return column itself");
		return columnAlternativeToCover;
	}

	public static int compareColumns(ColumnAlternative columnAlternative1, ColumnAlternative columnAlternative2, int limit)
	{
		char[] first = columnAlternative1.toString().toLowerCase().toCharArray();
		char[] second = columnAlternative2.toString().toLowerCase().toCharArray();

		int counter = 0;
		for(int i1 = 0; i1 < first.length; i1++) {
			if(first[i1] != second[i1]) {
				counter++;
				if(counter >= limit) {
					break;
				}
			}
		}
		return counter;
	}

}

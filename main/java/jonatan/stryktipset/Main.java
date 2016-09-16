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
				String[] matchArgs = line.split(";");
				Match match = new Match(matchArgs[0], Float.parseFloat(matchArgs[1]), Float.parseFloat(matchArgs[2]), Float.parseFloat(matchArgs[3]));
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
		List<ColumnAlternative> a = checkIndexesCoveredFor(columnAlternatives, columnAlternativesAlreadyPrinted);
		
//		columnAlternativesAlreadyPrinted = new ArrayList<ColumnAlternative>();
//		print12(columnAlternatives, columnAlternativesAlreadyPrinted);
//		List<ColumnAlternative> b = checkIndexesCoveredFor(columnAlternatives, columnAlternativesAlreadyPrinted);
//		
//		for(ColumnAlternative columnAlternative : b) {
//			if(a.indexOf(columnAlternative) == -1){
//				System.out.println(columnAlternative.toString() + " is not covered for in 11");
//			}
//		}
		
//		try {
//			new ProbabilityMultiThreader(8).printAndCalculate(columnAlternatives, columnAlternativesAlreadyPrinted);
//		} catch(InterruptedException e) {
//			e.printStackTrace();
//		}
		System.out.println("Calculating total probabilities took: " + (System.nanoTime() - startTime) / 1000000000 + " seconds");
	}

	private static List<ColumnAlternative> checkIndexesCoveredFor(List<ColumnAlternative> columnAlternatives,
			List<ColumnAlternative> columnAlternativesAlreadyPrinted)
	{
		
		//TODO: Make use of multithreading
		System.out.println("Checking covered for by 12...");

		long count = 0;
		List<ColumnAlternative> columnsCoveredFor11 = new ArrayList<ColumnAlternative>();
		List<ColumnAlternative> columnsCoveredFor12 = new ArrayList<ColumnAlternative>();
		List<ColumnAlternative> columnsCoveredFor13 = new ArrayList<ColumnAlternative>();
		for(ColumnAlternative columnAlternative : columnAlternatives) {
			count++;
			if(count % 10000 == 0) {
				System.out.println(count);
			}
			for(ColumnAlternative columnAlternativePrinted : columnAlternativesAlreadyPrinted) {
				int difference = compareColumns(columnAlternativePrinted, columnAlternative, 3);
				if(difference <= 2) {
					if(columnsCoveredFor11.indexOf(columnAlternative) == -1) {
						columnsCoveredFor11.add(columnAlternative);
					}
					if(difference <= 1) {
						if(columnsCoveredFor12.indexOf(columnAlternative) == -1) {
							columnsCoveredFor12.add(columnAlternative);
						}
						if(difference <= 0 && columnsCoveredFor13.size() < columnAlternativesAlreadyPrinted.size()) {
							if(columnsCoveredFor13.indexOf(columnAlternative) == -1) {
								columnsCoveredFor13.add(columnAlternative);
							}
						}
					}
				}
			}
		}
		
		float probability11 = 0;
		float probability12 = 0;
		float probability13 = 0;
		for(ColumnAlternative columnAlternative : columnsCoveredFor11) {
			probability11 += columnAlternative.getProbability();
		}
		for(ColumnAlternative columnAlternative : columnsCoveredFor12) {
			probability12 += columnAlternative.getProbability();
		}
		for(ColumnAlternative columnAlternative : columnsCoveredFor13) {
			probability13 += columnAlternative.getProbability();
		}
		System.out.println(columnsCoveredFor11.size() + " rows are covered for 11 correct");
		System.out.println("Total probability of 11 correct: " + probability11 * 100 + "%");
		System.out.println(columnsCoveredFor12.size() + " rows are covered for 12 correct");
		System.out.println("Total probability of 12 correct: " + probability12 * 100 + "%");
		System.out.println(columnsCoveredFor13.size() + " rows are covered for 13 correct");
		System.out.println("Total probability of 13 correct: " + probability13 * 100 + "%");
		
		return columnsCoveredFor11;
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

//	private static void print13(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnAlternativesAlreadyPrinted)
//	{
//		for(int i = 0; i < columnAlternatives.size(); i++) {
//			if(i > 13) {
//				break;
//			}
//			ColumnAlternative columnAlternative = columnAlternatives.get(i);
//			System.out.println(columnAlternative.getProbability() * 100 + "%  " + columnAlternative.toString());
//			columnAlternativesAlreadyPrinted.add(columnAlternative);
//		}
//	}
	
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
			if(columnsAlreadyPrinted.size() >= origSize + 12) {
				break;
			}
			ColumnAlternative columnAlternative = columnAlternatives.get(i);
			if(isCoveredFor(columnAlternative, columnsAlreadyPrinted, difference)) {
				continue;
			}
			ColumnAlternative columnAlternativeToAdd = getColumnToCoverFor(columnAlternative, columnAlternatives, columnsAlreadyPrinted, difference);
			
//			System.out.println("IndexChecked: " + i);
//			System.out.println("IndexAdded: " + columnAlternatives.indexOf(columnAlternativeToAdd));
			System.out.println(columnAlternativeToAdd.toString());
			columnsAlreadyPrinted.add(columnAlternativeToAdd);
		}
	}

	private static boolean isCoveredFor(ColumnAlternative columnAlternativeToCheck, List<ColumnAlternative> columnsAlreadyPrinted, int wantedDifference)
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

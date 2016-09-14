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
		
		checkIndexesCoveredFor(columnAlternatives, columnAlternativesAlreadyPrinted);
	}

	private static void checkIndexesCoveredFor(List<ColumnAlternative> columnAlternatives,
			List<ColumnAlternative> columnAlternativesAlreadyPrinted)
	{
		//TODO: Make use of multithreading
		System.out.println("Checking covered for by 12...");
		int coveredFor11 = 0;
		float probability11 = 0;
		int coveredFor12 = 0;
		float probability12 = 0;
		int coveredFor13 = 0;
		float probability13 = 0;
		for(ColumnAlternative columnAlternative : columnAlternatives) {
			if(columnAlternatives.indexOf(columnAlternative) % 100000 == 0){
				System.out.println(columnAlternatives.indexOf(columnAlternative));
			}
			for(ColumnAlternative columnAlternativePrinted : columnAlternativesAlreadyPrinted) {
				int difference = compareColumns(columnAlternativePrinted, columnAlternative);
				if(difference <= 2) {
					coveredFor11++;
					probability11 += columnAlternative.getProbability();
					if(difference <= 1) {
						coveredFor12++;
						probability12 += columnAlternative.getProbability();
						System.out.println("12: " + probability12);
						if(difference <= 0 && coveredFor13 < 39) {
							coveredFor13++;
							probability13 += columnAlternative.getProbability();
							System.out.println("13: " + probability13);
							break;
						}
					}
				}
			}
		}
		System.out.println(coveredFor11 + " rows are covered for 11 correct");
		System.out.println("Total probability of 11 correct: " + probability11 * 100 + "%");
		System.out.println(coveredFor12 + " rows are covered for 12 correct");
		System.out.println("Total probability of 12 correct: " + probability12 * 100 + "%");
		System.out.println(coveredFor13 + " rows are covered for 13 correct");
		System.out.println("Total probability of 13 correct: " + probability13 * 100 + "%");
	}

	private static void assertTotalChance(List<ColumnAlternative> columnAlternatives) throws Exception
	{
		float totalChance = 0;
		for(ColumnAlternative columnAlternative : columnAlternatives) {
			totalChance += columnAlternative.getProbability();
		}
		if(totalChance > 1.001 || totalChance < 0.999) {
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
//		int startingPoint = columnsAlreadyPrinted.size();
		for(int i = 0; i < columnAlternatives.size(); i++) {
			if(columnsAlreadyPrinted.size() >= origSize + 13) {
				break;
			}
			ColumnAlternative columnAlternative = columnAlternatives.get(i);
			if(isCoveredFor(columnAlternative, columnsAlreadyPrinted, difference)) {
				continue;
			}
			ColumnAlternative columnAlternativeToAdd = getColumnToCoverFor(columnAlternative, columnAlternatives, columnsAlreadyPrinted, difference);
			
			System.out.println("IndexChecked: " + i);
			System.out.println("IndexAdded: " + columnAlternatives.indexOf(columnAlternativeToAdd));
			System.out.println(columnAlternativeToAdd.toString());
			columnsAlreadyPrinted.add(columnAlternativeToAdd);
		}
	}

	private static boolean isCoveredFor(ColumnAlternative columnAlternativeToCheck, List<ColumnAlternative> columnsAlreadyPrinted, int wantedDifference)
	{
		for(ColumnAlternative columnAlreadyPrinted : columnsAlreadyPrinted) {
			int difference = compareColumns(columnAlreadyPrinted, columnAlternativeToCheck);
			if(difference >= wantedDifference + 1) {
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

			int differenceToColumn = compareColumns(columnAlternativeInList, columnAlternativeToCover);
			if(differenceToColumn > wantedDifference) {
				continue;
			}
			
			boolean isOkayToAdd = true;
			for(ColumnAlternative columnAlternativeAlreadyPrinted : columnsAlreadyPrinted) {
				int differenceToAlreadyPrinted = compareColumns(columnAlternativeInList, columnAlternativeAlreadyPrinted);
				if(differenceToAlreadyPrinted >= wantedDifference * 2 + 1) {
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
	
	//TODO: Add int to say when to stop, for performance reasons
	private static int compareColumns(ColumnAlternative columnAlternative1, ColumnAlternative columnAlternative2)
	{
		char[] first = columnAlternative1.toString().toLowerCase().toCharArray();
		char[] second = columnAlternative2.toString().toLowerCase().toCharArray();

		int counter = 0;
		for(int i1 = 0; i1 < first.length; i1++) {
			if(first[i1] != second[i1]) {
				counter++;
			}
		}
		return counter;
	}

}

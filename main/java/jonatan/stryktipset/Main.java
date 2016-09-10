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

		System.out.println("Sorting columns...");
		columnAlternatives.sort(new ColumnAlternative.ColumnSorter());
		Collections.reverse(columnAlternatives);

		print13(columnAlternatives);
		print12(columnAlternatives);
		print11(columnAlternatives);
	}

	private static void print11(List<ColumnAlternative> columnAlternatives)
	{
		// TODO Auto-generated method stub

	}

	private static void print12(List<ColumnAlternative> columnAlternatives)
	{
		System.out.println("\nPrinting 12:");

		List<ColumnAlternative> columnAlternativesAlreadyPrinted = new ArrayList<ColumnAlternative>();

		for(int i = 0; i < columnAlternatives.size(); i++) {
			if(columnAlternativesAlreadyPrinted.size() >= 13) {
				break;
			}
			ColumnAlternative columnAlternative = columnAlternatives.get(i);
			boolean isOkayToAdd = true;
			for(ColumnAlternative columnAlternativePrinted : columnAlternativesAlreadyPrinted) {
				boolean isAlreadyCoveredFor = compareColumns(columnAlternative, columnAlternativePrinted);
				if(isAlreadyCoveredFor) {
					isOkayToAdd = false;
				}
			}
			if(isOkayToAdd) {
				System.out.println("Index: " + i);
				System.out.println(columnAlternative.getProbability() * 100 + "%  " + columnAlternative.toString());
				columnAlternativesAlreadyPrinted.add(columnAlternative);
			}
		}
	}

	private static boolean compareColumns(ColumnAlternative columnAlternative, ColumnAlternative columnAlternativePrinted)
	{
		char[] first = columnAlternative.toString().toLowerCase().toCharArray();
		char[] second = columnAlternativePrinted.toString().toLowerCase().toCharArray();

		int counter = 0;
		for(int i1 = 0; i1 < first.length; i1++) {
			if(first[i1] != second[i1]) {
				counter++;
			}
			if(counter >= 2) {
				return false;
			}
		}
		return true;
	}

	private static void print13(List<ColumnAlternative> columnAlternatives)
	{
		for(int i = 0; i < columnAlternatives.size(); i++) {
			if(i > 13) {
				break;
			}
			ColumnAlternative columnAlternative = columnAlternatives.get(i);
			System.out.println(columnAlternative.getProbability() * 100 + "%  " + columnAlternative.toString());
		}
	}

}

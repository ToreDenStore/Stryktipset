package jonatan.stryktipset;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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
			int matchat = 0;
			while(line != null) {
				matchat++;
				String[] matchArgs = line.split(SEPARATOR);
				Match match = new Match(matchat,Float.parseFloat(matchArgs[0]), Float.parseFloat(matchArgs[1]), Float.parseFloat(matchArgs[2]));
				System.out.println(match);
				matches.add(match);
				line = buffReader.readLine();
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Input file not found");
		}

		ColumnsCreator columnsCreator = new ColumnsCreator(matches);
		columnsCreator.createColumns(5e-6); //Don't build rows with odds less than 5e-6
		List<ColumnAlternative> columnAlternatives = columnsCreator.getColumnAlternatives();
		columnAlternatives.sort(Collections.reverseOrder(new ColumnAlternative.ColumnSorter(13)));


		//assertTotalChance(columnAlternatives);


		List<ColumnAlternative> selectedColumns;


		long startTime = System.nanoTime();
		try {
			selectedColumns = topCategories(columnAlternatives);
			//selectedColumns = bruteforceBest(columnsCreator, columnAlternatives);
			columnAlternatives = columnsCreator.buildAlternativesFor(selectedColumns);
			new ProbabilityMultiThreader(4).printAndCalculate(columnAlternatives, selectedColumns);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(String.format("Calculating total probabilities took: %.3f seconds",(System.nanoTime() - startTime) / 1000000000.0));
	}

	private static List<ColumnAlternative> topCategories(List<ColumnAlternative> columnAlternatives) {
		System.out.println("Sorting columns for 13 right...");
		List<ColumnAlternative> selectedColumns = new ArrayList<ColumnAlternative>();
		columnAlternatives.sort(Collections.reverseOrder(new ColumnAlternative.ColumnSorter(13)));
		printAndSelect(columnAlternatives, selectedColumns,13);

		System.out.println("Sorting columns for 12 right...");
		columnAlternatives.sort(Collections.reverseOrder(new ColumnAlternative.ColumnSorter(12)));
		printAndSelect(columnAlternatives, selectedColumns,12);

		System.out.println("Sorting columns for 11 right...");
		columnAlternatives.sort(Collections.reverseOrder(new ColumnAlternative.ColumnSorter(11)));
		printAndSelect(columnAlternatives, selectedColumns,11);
		return selectedColumns;
	}

	private static List<ColumnAlternative> bruteforceBest(ColumnsCreator columnsCreator, List<ColumnAlternative> columnAlternatives) throws InterruptedException {
		List<ColumnAlternative> selectedColumns = new ArrayList<>();
		Map<ColumnAlternative,List<ColumnAlternative>> forselected = new HashMap<>();

		columnAlternatives = columnAlternatives.subList(0,50);
		for (ColumnAlternative columnAlternative : columnAlternatives) {
            selectedColumns.add(columnAlternative);
            forselected.put(columnAlternative, columnsCreator.buildAlternativesFor(selectedColumns));
            selectedColumns.clear();
        }

		for (int i = 0; i < NUMBER_OF_COLUMNS_PER_PAGE; i++) {
            double best = 0;
            ColumnAlternative na = null,nb = null,nc = null;

            for (int j = 0; j < columnAlternatives.size(); j++) {
                ColumnAlternative a = columnAlternatives.get(j);
                if (selectedColumns.contains(a))
                    continue;
                selectedColumns.add(a);
                for (int k = j+1; k < columnAlternatives.size(); k++) {
                    ColumnAlternative b = columnAlternatives.get(k);
                    if (selectedColumns.contains(b))
                        continue;
                    selectedColumns.add(b);
                    for (int l = k+1; l < columnAlternatives.size(); l++) {
                        ColumnAlternative c = columnAlternatives.get(l);
                        if (selectedColumns.contains(c))
                            continue;
                        selectedColumns.add(c);
                        ArrayList<ColumnAlternative> localAlts = (ArrayList)columnsCreator.buildAlternativesFor(selectedColumns);
                        selectedColumns.remove(selectedColumns.size()-1);
                        double vv = new ProbabilityMultiThreader(4).printAndCalculate(localAlts, selectedColumns);
                        if (vv > best)
                        {
                            best = vv;
                            na = a;
                            nb = b;
                            nc = c;

                        }
                    }
                    selectedColumns.remove(selectedColumns.size()-1);
                }
                selectedColumns.remove(selectedColumns.size()-1);
            }
            selectedColumns.add(na);
            selectedColumns.add(nb);
            selectedColumns.add(nc);

            System.out.println("Vv: " + best);
        }
        return selectedColumns;
	}

	private static void assertTotalChance(List<ColumnAlternative> columnAlternatives)
	{
		double totalChance = 0;
		for(ColumnAlternative columnAlternative : columnAlternatives) {
			totalChance += columnAlternative.getProbability();
		}
		if(totalChance > 1.0 + 1e-13 || totalChance < 1-1e-13) {
			throw new RuntimeException("Total chance is not 1, it is " + totalChance);
		}
	}

	private static void printAndSelect(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnsAlreadyPrinted, int numright)
	{
		int difference = 13-numright;
		System.out.println("\nPrinting " + numright + ":");

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
			int difference = columnAlreadyPrinted.compareTo(columnAlternativeToCheck);
			if(difference > wantedDifference) {
				continue;
			}
			return true;
		}
		return false;
	}

	private static ColumnAlternative getColumnToCoverFor(ColumnAlternative columnAlternativeToCover, List<ColumnAlternative> columnAlternatives,
			List<ColumnAlternative> selectedColumns, int wantedDifference)
	{
		int indexToStartAt = columnAlternatives.indexOf(columnAlternativeToCover);

		for(int i = indexToStartAt; i < columnAlternatives.size(); i++) {
			ColumnAlternative columnAlternativeInList = columnAlternatives.get(i);

			int differenceToColumn = columnAlternativeInList.compareTo(columnAlternativeToCover);
			if(differenceToColumn > wantedDifference) {
				continue;
			}

			boolean isOkayToAdd = true;
			for(ColumnAlternative columnAlternativeAlreadyPrinted : selectedColumns) {
				int differenceToAlreadyPrinted = columnAlternativeInList.compareTo(columnAlternativeAlreadyPrinted);
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

}

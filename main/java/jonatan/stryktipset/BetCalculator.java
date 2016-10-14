package jonatan.stryktipset;

import java.util.List;

@Deprecated
public class BetCalculator
{

	public static void add13(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnAlternativesAlreadyPrinted, int numberOfRows)
	{
		BetCalculator.printX(columnAlternatives, columnAlternativesAlreadyPrinted, 13 - 13, numberOfRows);
	}

	public static void print12(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnAlternativesAlreadyPrinted, int numberOfRows)
	{
		BetCalculator.printX(columnAlternatives, columnAlternativesAlreadyPrinted, 13 - 12, numberOfRows);
	}

	public static void print11(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnAlternativesAlreadyPrinted, int numberOfRows)
	{
		BetCalculator.printX(columnAlternatives, columnAlternativesAlreadyPrinted, 13 - 11, numberOfRows);
	}

	private static List<ColumnAlternative> printX(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnsAlreadyPrinted, int difference, int numberOfRows)
	{
		System.out.println("\nPrinting " + (13 - difference) + ":");
	
		int origSize = columnsAlreadyPrinted.size();
		for(int i = 0; i < columnAlternatives.size(); i++) {
			if(columnsAlreadyPrinted.size() >= origSize + numberOfRows) {
				break;
			}
			ColumnAlternative columnAlternative = columnAlternatives.get(i);
			if(BetCalculator.isCoveredFor(columnAlternative, columnsAlreadyPrinted, difference)) {
				continue;
			}
			ColumnAlternative columnAlternativeToAdd = BetCalculator.getColumnToCoverFor(columnAlternative, columnAlternatives, columnsAlreadyPrinted, difference);
	
			columnsAlreadyPrinted.add(columnAlternativeToAdd);
		}
		return columnsAlreadyPrinted;
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
			List<ColumnAlternative> columnsAlreadyPrinted, int wantedDifference)
	{
		int indexToStartAt = columnAlternatives.indexOf(columnAlternativeToCover);
	
		for(int i = indexToStartAt; i < columnAlternatives.size(); i++) {
			ColumnAlternative columnAlternativeInList = columnAlternatives.get(i);
	
			int differenceToColumn = columnAlternativeInList.compareTo(columnAlternativeToCover);
			if(differenceToColumn > wantedDifference) {
				continue;
			}
	
			boolean isOkayToAdd = true;
			for(ColumnAlternative columnAlternativeAlreadyPrinted : columnsAlreadyPrinted) {
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

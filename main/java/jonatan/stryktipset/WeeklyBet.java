package jonatan.stryktipset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeeklyBet
{
	private final int _columns13;
	private final int _columns12;
	private final int _columns11;
	private List<ColumnAlternative> _chosenColumns = new ArrayList<ColumnAlternative>();
	private final List<ColumnAlternative> _allColumns;
	private final List<ColumnAlternative> _allColumnsOriginal = new ArrayList<ColumnAlternative>();

	/**
	 * @param allColumns - All existing match result alternatives
	 * @param columns13 - Amount of rows to bet for 13 right.
	 * @param columns12 - Amount of rows to bet for 12 right.
	 * @param columns11 - Amount of rows to bet for 11 right.
	 */
	public WeeklyBet(List<ColumnAlternative> allColumns, int columns13, int columns12, int columns11)
	{
		System.out.println("Creating new weekly bet...");
		_columns13 = columns13;
		_columns12 = columns12;
		_columns11 = columns11;
		_allColumns = allColumns;

		try {
			populateWithColumns13();
			populateWithColumns12();
//			populateWithColumns11();
		} catch(InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public List<ColumnAlternative> getChosenColumns()
	{
		return _chosenColumns;
	}

	private void populateWithColumns13()
	{
		System.out.println("Adding " + _columns13 + " number of rows for 13 correct");
		prepareFor13();
		_allColumnsOriginal.addAll(_allColumns);
		for(int i = 0; i < _columns13; i++) {
			ColumnAlternative chosenColumn = _allColumns.get(i);
			_chosenColumns.add(chosenColumn);
			System.out.println("Added for 13: " + chosenColumn.toString() + ", index: " + _allColumnsOriginal.indexOf(chosenColumn) + " p13: " + chosenColumn.getProbability13());
		}
	}
	
	private void populateWithColumns12() throws InterruptedException
	{
		System.out.println("Adding " + _columns12 + " number of rows for 12 correct");
		for(int i = 0; i < _columns12; i++) {
			prepareFor12();
			ColumnAlternative chosenColumn = _allColumns.get(0);
			_chosenColumns.add(chosenColumn);
			System.out.println("Added for 12: " + chosenColumn.toString() + ", index: " + _allColumnsOriginal.indexOf(chosenColumn) + " p12: " + chosenColumn.getProbability12());
		}
	}
	
	private void populateWithColumns11()
	{
		System.out.println("Adding " + _columns11 + " number of rows for 11 correct");
		for(int i = 0; i < _columns11; i++) {
			prepareFor11();
			_chosenColumns.add(_allColumns.get(i));
		}
	}

	private void prepareFor13()
	{
		System.out.println("Sorting for 13");
		_allColumns.sort(new ColumnAlternative.ColumnSorter13());
		Collections.reverse(_allColumns);
//		for(ColumnAlternative columnAlternative : _allColumns) {
//			System.out.println(columnAlternative.getProbability13());
//		}
	}
	
	private void prepareFor12() throws InterruptedException
	{
		System.out.println("Pre-calculating chosen columns covered for 12");
		for(ColumnAlternative columnAlternative : _chosenColumns) {
			columnAlternative.preCalculateCoveredFor12();
		}
		System.out.println("Calculating for 12");
		new CalculateMultiThreader(8).calculate12(_allColumns, _chosenColumns);
		System.out.println("Sorting for 12");
		_allColumns.sort(new ColumnAlternative.ColumnSorter12());
		Collections.reverse(_allColumns);
//		for(ColumnAlternative columnAlternative : _allColumns) {
//			System.out.println(columnAlternative.getProbability12());
//		}
	}
	
	private void prepareFor11()
	{
		for(ColumnAlternative columnAlternative : _allColumns) {
			//TODO: Multithread
			columnAlternative.calculateProbability11(_chosenColumns);
		}
		_allColumns.sort(new ColumnAlternative.ColumnSorter11());
		Collections.reverse(_allColumns);
	}
}

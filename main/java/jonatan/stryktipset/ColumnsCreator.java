package jonatan.stryktipset;

import java.util.ArrayList;
import java.util.List;

public class ColumnsCreator
{
	private final List<Match> _matches;
	private List<ColumnAlternative> _columnAlternatives;

	public ColumnsCreator(List<Match> matches)
	{
		_matches = matches;
		_columnAlternatives = new ArrayList<ColumnAlternative>();
	}

	public List<ColumnAlternative> createColumns()
	{
		System.out.println("Creating columns...");
		createRow(null, 0, 0);
		createRow(null, 0, 1);
		createRow(null, 0, 2);
		System.out.println("Created " + _columnAlternatives.size() + " columns");
		return _columnAlternatives;
	}

	private void createRow(ColumnAlternative columnAlternative, int i, int j)
	{
		Match match = _matches.get(i);
		if(columnAlternative == null) {
			columnAlternative = new ColumnAlternative();
		}

		RowAlternative resultAlternative = null;
		if(j == 0) {
			resultAlternative = new RowAlternative(match, Result._1);
		} else if(j == 1) {
			resultAlternative = new RowAlternative(match, Result._X);
		} else if(j == 2) {
			resultAlternative = new RowAlternative(match, Result._2);
		}
		columnAlternative.addRowAlternative(resultAlternative, i);

		i++;
		if(i < _matches.size()) {
			createRow(columnAlternative, i, 0);
			createRow(columnAlternative, i, 1);
			createRow(columnAlternative, i, 2);
		} else {
			ColumnAlternative column = columnAlternative.cloneColumn();
			_columnAlternatives.add(column);
			if(_columnAlternatives.size() % 100000 == 0) {
				System.out.println("Created " + _columnAlternatives.size() + " columns");
			}
		}
	}
}

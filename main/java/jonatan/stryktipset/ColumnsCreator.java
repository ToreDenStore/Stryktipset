package jonatan.stryktipset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ColumnsCreator
{
	private final List<Match> _matches;
	private List<ColumnAlternative> _columnAlternatives;
	private ColumnAlternative columnAlternative;
	private RowAlternative[][] rowAlternatives;

	public ColumnsCreator(List<Match> matches)
	{
		_matches = matches;
		_columnAlternatives = new ArrayList<ColumnAlternative>((int)(Math.pow(3,ColumnAlternative.MAXSIZE)+1));
		rowAlternatives = new RowAlternative[ColumnAlternative.MAXSIZE][3];
		for (int i = 0; i < ColumnAlternative.MAXSIZE; i++) {
			rowAlternatives[i][0] = new RowAlternative(_matches.get(i), Result._1);
			rowAlternatives[i][1] = new RowAlternative(_matches.get(i), Result._X);
			rowAlternatives[i][2] = new RowAlternative(_matches.get(i), Result._2);

		}
	}

	private ColumnAlternative getBest() {
		ColumnAlternative ret = new ColumnAlternative();
		for (int i = 0; i < ColumnAlternative.MAXSIZE; i++) {
			RowAlternative max = rowAlternatives[i][0];
			for (int j = 1; j < 3; j++) {
				if (rowAlternatives[i][j].getProbability() > max.getProbability())
					max = rowAlternatives[i][j];
			}
			ret.setRowAlternative(max,i);
		}
		return ret;
	}


	public void createColumns(double cutoff)
	{
		System.out.println("Creating columns...");
		columnAlternative = new ColumnAlternative();
		createRow(0, 0,cutoff,1);
		createRow( 0, 1,cutoff,1);
		createRow(0, 2,cutoff,1);
		System.out.println("Created " + _columnAlternatives.size() + " columns");
	}

	private void createRow(int i, int j, double cutoff, double currprob)
	{
		RowAlternative resultAlternative = rowAlternatives[i][j];

		currprob *= resultAlternative.getProbability();
		if (cutoff > currprob)
			return;

		columnAlternative.setRowAlternative(resultAlternative, i);
		i++;
		if(i < ColumnAlternative.MAXSIZE) {
			createRow(i, 0, cutoff, currprob);
			createRow(i, 1, cutoff, currprob);
			createRow(i, 2, cutoff, currprob);
		} else {
			_columnAlternatives.add(columnAlternative.clone());
			if(_columnAlternatives.size() % 100000 == 0) {
				System.out.println("Created " + _columnAlternatives.size() + " columns");
			}
		}
	}


	public List<ColumnAlternative> getColumnAlternatives()
	{
		return _columnAlternatives;
	}


	public List<ColumnAlternative> buildAlternativesFor(List<ColumnAlternative> selectedColumns) {
		HashSet<ColumnAlternative> columns = new HashSet<>();
		for (ColumnAlternative selectedColumn : selectedColumns) {
			columns.addAll( buildAlternativesForRecursive(selectedColumn,13-11));
		}
		return new ArrayList<>(columns);

	}

	private HashSet<ColumnAlternative> buildAlternativesForRecursive(ColumnAlternative selectedColumn, int depth) {
		HashSet<ColumnAlternative> ret = new HashSet<>();
		if (depth == 0)
		{
			ret.add(selectedColumn);
			return ret;
		}

		for (int i = 0; i < ColumnAlternative.MAXSIZE; i++) {
			for (int l = 0; l < 3; l++) {
				ColumnAlternative n = selectedColumn.clone();
				n.setRowAlternative(rowAlternatives[i][l],i);
				ret.addAll(buildAlternativesForRecursive(n,depth-1));

			}
		}
		return ret;
	}
}

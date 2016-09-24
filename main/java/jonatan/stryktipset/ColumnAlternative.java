package jonatan.stryktipset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ColumnAlternative
{
	private static final int MAXSIZE = 13;
	private List<RowAlternative> _alternatives = new ArrayList<RowAlternative>();

	public void addRowAlternative(RowAlternative rowAlternative, int i)
	{
		if(_alternatives.size() <= i) {
			_alternatives.add(rowAlternative);
		} else {
			_alternatives.set(i, rowAlternative);
		}
		if(_alternatives.size() > MAXSIZE) {
			throw new RuntimeException("Cannot add more than 13 rows");
		}
	}

	public List<RowAlternative> getRowAlternatives()
	{
		return _alternatives;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for(RowAlternative resultAlternative : _alternatives) {
			Result result = resultAlternative.getResult();
			switch(result) {
			case _1:
				sb.append("1");
				break;
			case _X:
				sb.append("X");
				break;
			case _2:
				sb.append("2");
				break;
			default:
				throw new RuntimeException("Default shouldn't happen");
			}
		}
		return sb.toString();
	}

	public float getProbability()
	{
		if(_alternatives.size() != MAXSIZE) {
			throw new RuntimeException("Size is not 13, it is " + _alternatives.size());
		}
		float probability = 1;
		for(RowAlternative resultAlternative : _alternatives) {
			probability = probability * resultAlternative.getProbability();
		}
		return probability;
	}

	public static class ColumnSorter implements Comparator<ColumnAlternative>
	{
		@Override
		public int compare(ColumnAlternative o1, ColumnAlternative o2)
		{
			if(o1.getProbability() > o2.getProbability()) {
				return 1;
			} else if(o1.getProbability() < o2.getProbability()) {
				return -1;
			} else {
				return 0;
			}
		}

	}

	public int compareTo(ColumnAlternative columnAlternative)
	{
		int counter = 0;
		for(int j = 0; j < _alternatives.size(); j++) {
			RowAlternative rowAlternative = _alternatives.get(j);
			RowAlternative rowAlternative2 = columnAlternative._alternatives.get(j);
			if(!rowAlternative.equals(rowAlternative2)) {
				counter++;
			}
		}
		return counter;
	}

}

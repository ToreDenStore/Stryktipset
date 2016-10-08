package jonatan.stryktipset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ColumnAlternative
{
	private static final int MAXSIZE = 13;
	private List<RowAlternative> _alternatives = new ArrayList<RowAlternative>();
	private float _prob13 = -1;
	private  float _prob12 = -1;
	private  float _prob11 = -1;
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
		// Do not allow anybody to edit our rowalternatives!
		return new ArrayList<>(_alternatives);
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
	public float getProbability(int numright) {
		float tot = getProbability();
		if (numright == 13)
			return tot;

		 if (numright == 12)
		 {
			 if (_prob12 != -1)
			 	return _prob12;
			 // 1*b*c+a*1*c+a*b*1 = bc+ac+ab = abc/a+abc/b+abc/c
			 float t2 = 0;
			 for(RowAlternative resultAlternative : _alternatives) {
				 t2 += tot/ resultAlternative.getProbability();
			 }
			 _prob12 = t2;
			 return t2;
		 }

		if (numright == 11)
		{
			if (_prob11 != -1)
				return _prob11;
			//1*1*c+a*1*1+1*b*1 = c+a+b = abc/ab+abc/bc+abc/ca
			float t2 = 0;
			for (int i = 0; i < 12; i++) {
				for (int j = i+1; j < 13; j++) {
					t2 += tot/ (_alternatives.get(i).getProbability()*_alternatives.get(j).getProbability());
				}
			}
			_prob11 = t2;
			return t2;
		}
		throw new IllegalArgumentException(String.format("Cannot handle %d right. Try 13, 12 or 11 right",numright));

	}
	public float getProbability()
	{
		if (_prob13 != -1)
			return _prob13;
		if(_alternatives.size() != MAXSIZE) {
			throw new RuntimeException("Size is not 13, it is " + _alternatives.size());
		}
		float probability = 1;
		for(RowAlternative resultAlternative : _alternatives) {
			probability = probability * resultAlternative.getProbability();
		}
		_prob13 = probability;
		return probability;
	}

	public static class ColumnSorter implements Comparator<ColumnAlternative>
	{
		int numcorr;
		public ColumnSorter(int numcorr) {
			this.numcorr = numcorr;
		}
		@Override
		public int compare(ColumnAlternative o1, ColumnAlternative o2)
		{
			float o1prob = o1.getProbability(numcorr);
			float o2prob = o2.getProbability(numcorr);
			if( o1prob > o2prob) {
				return 1;
			} else if(o1prob < o2prob) {
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

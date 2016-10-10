package jonatan.stryktipset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ColumnAlternative implements Cloneable
{
	public static final int MAXSIZE = 13;
	private final RowAlternative[] _alternatives;
	private double _prob13 = -1;
	private double _prob12 = -1;
	private double _prob11 = -1;

    private ColumnAlternative(RowAlternative[] clone) {
        if (clone.length != MAXSIZE)
            throw new RuntimeException("Cannot clone less or more than 13 rows");
        _alternatives = clone;
    }
    public ColumnAlternative() {
        _alternatives = new RowAlternative[MAXSIZE];
    }

    public void setRowAlternative(RowAlternative rowAlternative, int i)
	{
        _alternatives[i] = rowAlternative;
		if(i > MAXSIZE) {
			throw new RuntimeException("Cannot add more than 13 rows");
		}
	}

	public RowAlternative[] getRowAlternatives()
	{
		// Do not allow anybody to edit our rowalternatives!
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
	public double getProbability(int numright) {
		double tot = getProbability();
		if (numright == 13)
			return tot;

		 if (numright == 12)
		 {
			 if (_prob12 != -1)
			 	return _prob12;
			 // 1*b*c+a*1*c+a*b*1 = bc+ac+ab = abc/a+abc/b+abc/c
			 double t2 = 0;
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
			double t2 = 0;
			for (int i = 0; i < 12; i++) {
				for (int j = i+1; j < 13; j++) {
					t2 += tot/ (_alternatives[i].getProbability()*_alternatives[j].getProbability());
				}
			}
			_prob11 = t2;
			return t2;
		}
		throw new IllegalArgumentException(String.format("Cannot handle %d right. Try 13, 12 or 11 right",numright));

	}
	public double getProbability()
	{
		if (_prob13 != -1)
			return _prob13;

		double probability = 1;
        for (int i = 0; i < _alternatives.length; i++) {
            probability = probability * _alternatives[i].getProbability();

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
			return compare(o1,o2,numcorr);
		}

        private int compare(ColumnAlternative o1, ColumnAlternative o2, int numcorr) {
            double o1prob = o1.getProbability(numcorr);
            double o2prob = o2.getProbability(numcorr);
            if( o1prob > o2prob) {
                return 1;
            } else if(o1prob < o2prob) {
                return -1;
            } else {
                if (numcorr == 13)
                    return 0;
                // So they are equal in the n right sense, but then we'd rather
                // take the one that is better in the n+1 right sense.
                return compare(o1,o2,numcorr+1);
            }
        }

    }

    public int compareTo(ColumnAlternative columnAlternative, int max) {
        int counter = 0;
        for (int i = 0; i < _alternatives.length; i++) {
            RowAlternative alternative = _alternatives[i];
            RowAlternative alternative2 = columnAlternative._alternatives[i];
            if(!alternative.equals(alternative2)) {
                counter++;
                if (counter == max)
                    break;
            }
        }
        return counter;
    }

	public int compareTo(ColumnAlternative columnAlternative) {
        return compareTo(columnAlternative, ColumnAlternative.MAXSIZE);
    }

	protected ColumnAlternative clone() {
		return new ColumnAlternative(this._alternatives.clone());
	}

    @Override
    public int hashCode() {
        return Arrays.hashCode(this._alternatives);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColumnAlternative))
            return false;

        return compareTo((ColumnAlternative)o,1) == 0;
    }
}

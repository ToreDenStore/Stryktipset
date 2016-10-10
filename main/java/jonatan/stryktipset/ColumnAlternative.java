package jonatan.stryktipset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ColumnAlternative implements Cloneable
{
	private static final int MAXSIZE = 13;
	private List<RowAlternative> _alternatives = new ArrayList<RowAlternative>();
	private Set<ColumnAlternative> _preCalculatedCoveringFor12 = new HashSet<ColumnAlternative>();
//	private List<ColumnAlternative> _coveringFor11 = new ArrayList<ColumnAlternative>();

	private double _probability13;
	private double _probability12;
	private double _probability11;

	public void addRowAlternative(RowAlternative rowAlternative, int i)
	{
		if(_alternatives.size() <= i) {
			_alternatives.add(rowAlternative);
		} else {
			_alternatives.set(i, rowAlternative);
		}
		if(_alternatives.size() == MAXSIZE) {
			calculateProbability13();
		}
		if(_alternatives.size() > MAXSIZE) {
			throw new RuntimeException("Cannot add more than 13 rows");
		}
	}

	public void getCoveredFor11()
	{
		throw new NotImplementedException();
	}

	public double getProbability13()
	{
		if(_probability13 == 0) {
			throw new RuntimeException("p13 is 0");
		}
		return _probability13;
	}

	public double getProbability12()
	{
		return _probability12;
	}

	public double getProbability11()
	{
		return _probability11;
	}

	private void calculateProbability13()
	{
		if(_alternatives.size() != MAXSIZE) {
			throw new RuntimeException("Size is not 13, it is " + _alternatives.size());
		}
		double probability = 1;
		for(RowAlternative resultAlternative : _alternatives) {
			probability = probability * resultAlternative.getProbability();
		}
		_probability13 = probability;
	}

	public void calculateProbability12(List<ColumnAlternative> alreadyTakenAlternatives)
	{
//		long time = System.nanoTime() / 1000;
		if(_alternatives.size() != MAXSIZE) {
			throw new RuntimeException("Size is not 13, it is " + _alternatives.size());
		}

		double probability = 0;
		Set<ColumnAlternative> alternatives12 = getAlternatives12(alreadyTakenAlternatives);
		for(ColumnAlternative columnAlternative : alternatives12) {
			probability += columnAlternative.getProbability13();
		}

//		System.out.println(System.nanoTime() / 1000 - time);
		_probability12 = probability;
	}

	public void calculateProbability11(List<ColumnAlternative> alreadyTakenAlternatives)
	{
		throw new NotImplementedException();
	}

	/**
	 * Returns the alternatives for 12 correct that this ColumnAlternative
	 * covers, which is all 27 minus all of the columns covered for by existing
	 * column alternatives of the already chosen column alternatives.
	 * 
	 * @param existingAlternatives
	 * @return
	 */
	private Set<ColumnAlternative> getAlternatives12(List<ColumnAlternative> existingAlternatives)
	{
//		long time = System.nanoTime() / 1000;
		Set<ColumnAlternative> alreadyCoveredFor12 = new HashSet<ColumnAlternative>();
		for(ColumnAlternative columnAlternative : existingAlternatives) {
			alreadyCoveredFor12.addAll(columnAlternative.getPreCalculatedCoveringFor12());
		}
//		System.out.println("Adding all covered for: " + (System.nanoTime() / 1000 - time)); //170us
//		time = System.nanoTime() / 1000;
		Set<ColumnAlternative> alternatives12 = new HashSet<ColumnAlternative>();
		for(ColumnAlternative columnAlternative : getCoveredFor12()) {
			if(!alreadyCoveredFor12.contains(columnAlternative)) {
				alternatives12.add(columnAlternative);
			}
//			else {
//				System.out.println(columnAlternative.toString() + " is already covered for");
//			}
		}
//		System.out.println("Calculating not covered for: " + (System.nanoTime() / 1000 - time)); //65us
		return alternatives12;
	}

	public Set<ColumnAlternative> getCoveredFor12()
	{
		Set<ColumnAlternative> coveringFor12 = new HashSet<ColumnAlternative>();
		if(_alternatives.size() != 13) {
			throw new RuntimeException("ColumnAlternative is not fully constructed yet");
		}
		coveringFor12.add(this);
		for(int i = 0; i < _alternatives.size(); i++) {
			RowAlternative rowAlternative = _alternatives.get(i);
			Result result = rowAlternative.getResult();
			if(result != Result._1) {
				ColumnAlternative copy = clone();
				copy.addRowAlternative(new RowAlternative(rowAlternative.getMatch(), Result._1), i);
				coveringFor12.add(copy);
			}
			if(result != Result._X) {
				ColumnAlternative copy = clone();
				copy.addRowAlternative(new RowAlternative(rowAlternative.getMatch(), Result._X), i);
				coveringFor12.add(copy);
			}
			if(result != Result._2) {
				ColumnAlternative copy = clone();
				copy.addRowAlternative(new RowAlternative(rowAlternative.getMatch(), Result._2), i);
				coveringFor12.add(copy);
			}
		}
		if(coveringFor12.size() != 27) {
			throw new RuntimeException("CoveringFor12 is not 27"); //TODO: Replace with unit test
		}
		return coveringFor12;
	}

	public Set<ColumnAlternative> getPreCalculatedCoveringFor12()
	{
		return _preCalculatedCoveringFor12;
	}

	public void preCalculateCoveredFor12()
	{
		_preCalculatedCoveringFor12 = getCoveredFor12();
	}

	public static class ColumnSorter13 implements Comparator<ColumnAlternative>
	{
		@Override
		public int compare(ColumnAlternative o1, ColumnAlternative o2)
		{
			if(o1.getProbability13() > o2.getProbability13()) {
				return 1;
			} else if(o1.getProbability13() < o2.getProbability13()) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	public static class ColumnSorter12 implements Comparator<ColumnAlternative>
	{
		@Override
		public int compare(ColumnAlternative o1, ColumnAlternative o2)
		{
			double probability1 = o1._probability12;
			double probability2 = o2._probability12;
			if(probability1 > probability2) {
				return 1;
			} else if(probability1 < probability2) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	public static class ColumnSorter11 implements Comparator<ColumnAlternative>
	{
		@Override
		public int compare(ColumnAlternative o1, ColumnAlternative o2)
		{
			double probability1 = o1._probability11;
			double probability2 = o2._probability11;
			if(probability1 > probability2) {
				return 1;
			} else if(probability1 < probability2) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	@Override
	public ColumnAlternative clone()
	{
		ColumnAlternative newColumn = new ColumnAlternative();
		for(RowAlternative row : _alternatives) {
			newColumn.addRowAlternative(row, _alternatives.indexOf(row));
		}
		return newColumn;
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

	@Override
	public boolean equals(Object other)
	{
		if(other == null)
			return false;
		if(other == this)
			return true;
		if(!(other instanceof ColumnAlternative))
			return false;
		ColumnAlternative otherColumnAlternative = (ColumnAlternative) other;
		if(otherColumnAlternative._alternatives.size() != _alternatives.size()) {
			return false;
		}
		for(int j = 0; j < _alternatives.size(); j++) {
			RowAlternative rowAlternative = _alternatives.get(j);
			RowAlternative rowAlternative2 = otherColumnAlternative._alternatives.get(j);
			if(!rowAlternative.equals(rowAlternative2)) {
				return false;
			}
		}
		return true;
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

	@Override
	public int hashCode()
	{
//		return this.getRowAlternatives().hashCode();
		return this.toString().hashCode();
	}

	//TODO: Write unit tests for hashcode and equals

//	@Override
//	public boolean equals(Object other)
//	{
//		ColumnAlternative otherColumnAlternative = (ColumnAlternative) other;
////		return this.getRowAlternatives().equals(otherColumnAlternative.getRowAlternatives());
//		return this.toString().equals(otherColumnAlternative.toString());
//	}
}

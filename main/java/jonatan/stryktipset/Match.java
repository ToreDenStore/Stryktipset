package jonatan.stryktipset;

public class Match
{
	private final int id;
	private final double _odds1;
	private final double _oddsX;
	private final double _odds2;
	private double _probability1;
	private double _probabilityX;
	private double _probability2;

	public Match(int match_id, double input1, double inputX, double input2)
	{
		id = match_id;
		_odds1 = input1;
		_oddsX = inputX;
		_odds2 = input2;

		calculateProbabilites(_odds1, _oddsX, _odds2);
	}

	public String toString()
	{
		return String.format("Match %3d: %5.2f %5.2f %5.2f", id, _probability1, _probabilityX, _probability2);
	}

	private void calculateProbabilites(double odds1, double oddsX, double odds2)
	{
		double total = 1 / odds1 + 1 / oddsX + 1 / odds2;
		double factor = 1 / total;
		_probability1 = factor * 1 / odds1;
		_probabilityX = factor * 1 / oddsX;
		_probability2 = factor * 1 / odds2;
		double totalProbability = _probability1 + _probabilityX + _probability2;
		if(totalProbability > 1 + 1e-6 || totalProbability < 1 - 1e-6) {
			throw new RuntimeException(String.format("Total probability of match %d is not close to 1 ( %f )", id, totalProbability));
		}
	}

	public double getProbability1()
	{
		return _probability1;
	}

	public double getProbabilityX()
	{
		return _probabilityX;
	}

	public double getProbability2()
	{
		return _probability2;
	}

	public double getOdds1()
	{
		return _odds1;
	}

	public double getOddsX()
	{
		return _oddsX;
	}

	public double getOdds2()
	{
		return _odds2;
	}

	public double getProbability(Result result)
	{
		switch(result) {
		case _1:
			return getProbability1();
		case _X:
			return getProbabilityX();
		case _2:
			return getProbability2();
		}
		return 0;
	}

}

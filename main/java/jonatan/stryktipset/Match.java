package jonatan.stryktipset;

import java.math.BigDecimal;

public class Match
{
	private final int id;
	private final float _odds1;
	private final float _oddsX;
	private final float _odds2;
	private double _probability1;
	private double _probabilityX;
	private double _probability2;

	@Override
	public int hashCode() {
		return id ^ Float.hashCode(_odds1) ^ Float.hashCode(_oddsX) ^ Float.hashCode(_odds2);
	}

	public Match(int match_id, float input1, float inputX, float input2)
	{
		id = match_id;
		_odds1 = input1;
		_oddsX = inputX;
		_odds2 = input2;

		calculateProbabilites(_odds1, _oddsX, _odds2);
	}

	public String toString() {
		return String.format("Match %3d: %5.2f %5.2f %5.2f",id , _probability1, _probabilityX,_probability2);
	}

	private void calculateProbabilites(double odds1, double oddsX, double odds2)
	{
		// More accurate version of (1/ ( 1 / odds1 + 1 / oddsX + 1 / odds2 ) ) * 1/odds1
		// Accuracy achieved by working with larger numbers
		double tot = odds1*oddsX+odds1*odds2+oddsX*odds2;

		//double total = 1 / odds1 + 1 / oddsX + 1 / odds2;
		//double factor = 1 / total;
		_probability1 = oddsX*odds2/tot;
		_probabilityX = odds1*odds2/tot;
		_probability2 = odds1*oddsX/tot;
		/*
		_probability1 = factor * 1/odds1;
		_probabilityX = factor * 1/oddsX;
		_probability2 = factor * 1/odds2;
		*/
		double totalProbability = _probability1+_probabilityX+_probability2;
		if (totalProbability > 1+1e-20 || totalProbability < 1-1e-20) {
			throw new RuntimeException(String.format("Total probability of match %d is not close to 1 ( %.20f )",id,totalProbability));
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

	public float getOdds1()
	{
		return _odds1;
	}

	public float getOddsX()
	{
		return _oddsX;
	}

	public float getOdds2()
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

	public int getId() {
		return id;
	}
}

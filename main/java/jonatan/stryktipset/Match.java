package jonatan.stryktipset;

public class Match
{
	private final int id;
	private final float _odds1;
	private final float _oddsX;
	private final float _odds2;
	private float _probability1;
	private float _probabilityX;
	private float _probability2;

	public Match(int match_id,float input1, float inputX, float input2)
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

	private void calculateProbabilites(float odds1, float oddsX, float odds2)
	{
		float total = 1 / odds1 + 1 / oddsX + 1 / odds2;
		float factor = 1 / total;
		_probability1 = factor * 1 / odds1;
		_probabilityX = factor * 1 / oddsX;
		_probability2 = factor * 1 / odds2;
		float totalProbability = _probability1+_probabilityX+_probability2;
		if (totalProbability > 1+1e-6 || totalProbability < 1-1e-6) {
			throw new RuntimeException(String.format("Total probability of match %d is not close to 1 ( %f )",id,totalProbability));
		}
	}

	public float getProbability1()
	{
		return _probability1;
	}

	public float getProbabilityX()
	{
		return _probabilityX;
	}

	public float getProbability2()
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

	public float getProbability(Result result)
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

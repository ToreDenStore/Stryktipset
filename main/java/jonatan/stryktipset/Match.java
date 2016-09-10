package jonatan.stryktipset;

public class Match
{
	private final String _name;
	private final float _odds1;
	private final float _oddsX;
	private final float _odds2;
	private float _probability1;
	private float _probabilityX;
	private float _probability2;

	public Match(String name, float input1, float inputX, float input2)
	{
		_name = name;
		_odds1 = input1;
		_oddsX = inputX;
		_odds2 = input2;

		calculateProbabilites(_odds1, _oddsX, _odds2);
	}

	private void calculateProbabilites(float odds1, float oddsX, float odds2)
	{
		float total = 1 / odds1 + 1 / oddsX + 1 / odds2;
		float factor = 1 / total;
		_probability1 = factor * 1 / odds1;
		_probabilityX = factor * 1 / oddsX;
		_probability2 = factor * 1 / odds2;
	}

	public String getName()
	{
		return _name;
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

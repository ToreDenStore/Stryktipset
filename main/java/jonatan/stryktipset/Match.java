package jonatan.stryktipset;

public class Match
{
	private final String _name;
	private final float _1;
	private final float _X;
	private final float _2;
	private float probability1;
	private float probabilityX;
	private float probability2;

	public Match(String name, float input1, float inputX, float input2)
	{
		_name = name;
		_1 = input1;
		_X = inputX;
		_2 = input2;

		calculateProbabilites(_1, _X, _2);
	}

	private void calculateProbabilites(float odds1, float oddsX, float odds2)
	{
		float total = odds1 + oddsX + odds2;
		float factor = 1 / total;
		probability1 = 1 - (odds1 * factor);
		probabilityX = 1 - (oddsX * factor);
		probability2 = 1 - (odds2 * factor);
	}

	public String getName()
	{
		return _name;
	}

	public float getProbability1()
	{
		return probability1;
	}

	public float getProbabilityX()
	{
		return probabilityX;
	}

	public float getProbability2()
	{
		return probability2;
	}

}

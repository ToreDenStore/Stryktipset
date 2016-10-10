package jonatan.stryktipset;

public class RowAlternative
{
	private final Match _match;
	private final Result _result;

	public RowAlternative(Match match, Result result)
	{
		_match = match;
		_result = result;
	}

	public double getProbability()
	{
		return _match.getProbability(_result);
	}

	public Match getMatch()
	{
		return _match;
	}

	public Result getResult()
	{
		return _result;
	}

	@Override
	public int hashCode() {
		return _match.hashCode()^_result.hashCode();
	}

	@Override
	public boolean equals(Object other)
	{
		if(other == null)
			return false;
		if(other == this)
			return true;
		if(!(other instanceof RowAlternative))
			return false;
		RowAlternative rowAlternative2 = (RowAlternative) other;
		if(_match.getOdds1() == rowAlternative2.getMatch().getOdds1()
				&& _match.getOddsX() == rowAlternative2.getMatch().getOddsX()
				&& _match.getOdds2() == rowAlternative2.getMatch().getOdds2()
				&& _result.equals(rowAlternative2._result)) {
			return true;
		}
		return false;
	}

}

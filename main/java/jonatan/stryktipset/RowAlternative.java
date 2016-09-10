package jonatan.stryktipset;

public class RowAlternative
{
	private final Match _match;
	private final Result _result;
	
	public RowAlternative(Match match, Result result){
		_match = match;
		_result = result;
	}
	
	public float getProbability()
	{
		return _match.getProbability(_result);
	}

	public Result getResult()
	{
		return _result;
	}
	
	
}

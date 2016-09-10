package jonatan.stryktipset;

public class ResultAlternative
{
	private final Match _match;
	private final Result _result;
	
	public ResultAlternative(Match match, Result result){
		_match = match;
		_result = result;
	}
	
	public float getProbability()
	{
		return _match.getProbability(_result);
	}
}

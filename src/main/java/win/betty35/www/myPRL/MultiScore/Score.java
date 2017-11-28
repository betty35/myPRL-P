package win.betty35.www.myPRL.MultiScore;

public class Score 
{
	public int topicID;
	public double good;
	public double bad;
	
	public Score()
	{
		good=0;
		bad=0;
	};
	
	public Score(int topic)
	{
		this.topicID=topic;
		good=0;
		bad=0;
	}
	
	public void addToGood(double g)
	{
		good=good+g;
	}
	
	public void addToBad(double b)
	{
		bad=bad+b;
	}
	
}

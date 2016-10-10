package jonatan.stryktipset;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProbabilityMultiThreader
{
	private int _numberOfThreads;

	AtomicLong _columnsFinished = new AtomicLong();

	public ProbabilityMultiThreader(int numberOfThreads)
	{
		_numberOfThreads = numberOfThreads;
	}

	private void addToFinished()
	{
		long columnsFinished = _columnsFinished.incrementAndGet();
		if(columnsFinished % 10000 == 0) {
			System.out.println(columnsFinished);
		}
	}

	public double printAndCalculate(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnAlternativesAlreadyPrinted)
			throws InterruptedException
	{
		System.out.println("Checking covered for by 12...");
		Queue<ColumnAlternative> columnsCoveredFor11 = new ConcurrentLinkedQueue<ColumnAlternative>();
		Queue<ColumnAlternative> columnsCoveredFor12 = new ConcurrentLinkedQueue<ColumnAlternative>();
		Queue<ColumnAlternative> columnsCoveredFor13 = new ConcurrentLinkedQueue<ColumnAlternative>();

		List<Thread> threads = new ArrayList<Thread>();
		for(int i = 0; i < _numberOfThreads; i++) {

			final int threadNumber = i;

			Thread thread = new Thread()
			{
				public void run()
				{
					int part = columnAlternatives.size() / _numberOfThreads;
					int start = threadNumber * part;
					int end = (threadNumber + 1) * part;
					if (_numberOfThreads == threadNumber+1)
						end = columnAlternatives.size();

					List<ColumnAlternative> shorterList = columnAlternatives.subList(start, end);
					for(ColumnAlternative columnAlternative : shorterList) {
						boolean added11 = false;
						boolean added12 = false;
						boolean added13 = false;

						addToFinished();
						for(ColumnAlternative columnAlternativePrinted : columnAlternativesAlreadyPrinted) {
							if (added11 && added12 && added13)
								break;

							int difference = columnAlternativePrinted.compareTo(columnAlternative,3);

							if(difference <= 0) {
								columnsCoveredFor13.add(columnAlternative);
								added13 = true;
								continue; //Won't get money for 11 or 12 right for this column
							}

							if(!added12 && difference <= 1) {
								columnsCoveredFor12.add(columnAlternative);
								added12 = true;
								continue; //Won't get money for 11 right on this one
							}

							if(!added11 && difference <= 2) {
								columnsCoveredFor11.add(columnAlternative);
								added11 = true;
							}


						}
					}
				}
			};
			thread.start();

			threads.add(thread);
		}

		for(Thread thread : threads) {
			thread.join();
		}
		if (_columnsFinished.get() != columnAlternatives.size())
			throw new RuntimeException("Did not process all columns!");

		double probability11 = 0;
		double probability12 = 0;
		double probability13 = 0;
		for(ColumnAlternative columnAlternative : columnsCoveredFor11) {
			probability11 += columnAlternative.getProbability();
		}
		for(ColumnAlternative columnAlternative : columnsCoveredFor12) {
			probability12 += columnAlternative.getProbability();
		}
		for(ColumnAlternative columnAlternative : columnsCoveredFor13) {
			probability13 += columnAlternative.getProbability();
		}
		System.out.println(columnsCoveredFor11.size() + " rows are covered for 11 correct");
		System.out.println("Total probability of 11 correct: " + probability11 * 100 + "%");
		System.out.println(columnsCoveredFor12.size() + " rows are covered for 12 correct");
		System.out.println("Total probability of 12 correct: " + probability12 * 100 + "%");
		System.out.println(columnsCoveredFor13.size() + " rows are covered for 13 correct");
		System.out.println("Total probability of 13 correct: " + probability13 * 100 + "%");
		double vv = probability11*100+probability12*1000+probability13*100000;
		System.out.println(String.format("Väntevärde: %.2f",vv));
		return vv;
	}
}

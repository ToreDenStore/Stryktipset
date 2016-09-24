package jonatan.stryktipset;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProbabilityMultiThreader
{
	private int _numberOfThreads;

	AtomicInteger _coveredFor11 = new AtomicInteger();
	AtomicFloat _probability11 = new AtomicFloat();
	AtomicInteger _coveredFor12 = new AtomicInteger();
	AtomicFloat _probability12 = new AtomicFloat();
	AtomicInteger _coveredFor13 = new AtomicInteger();
	AtomicFloat _probability13 = new AtomicFloat();

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

	public void printAndCalculate(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnAlternativesAlreadyPrinted)
			throws InterruptedException
	{
		System.out.println("Checking covered for by 12...");

		List<ColumnAlternative> columnsCoveredFor11 = new ArrayList<ColumnAlternative>();
		List<ColumnAlternative> columnsCoveredFor12 = new ArrayList<ColumnAlternative>();
		List<ColumnAlternative> columnsCoveredFor13 = new ArrayList<ColumnAlternative>();

		List<Thread> threads = new ArrayList<Thread>();
		for(int i = 0; i < _numberOfThreads; i++) {

			final int threadNumber = i;

			Thread thread = new Thread()
			{
				public void run()
				{
					int part = columnAlternatives.size() / _numberOfThreads;
					List<ColumnAlternative> shorterList = columnAlternatives.subList(threadNumber * part, (threadNumber + 1) * part);

					for(ColumnAlternative columnAlternative : shorterList) {
						addToFinished();
						for(ColumnAlternative columnAlternativePrinted : columnAlternativesAlreadyPrinted) {
							int difference = columnAlternativePrinted.compareTo(columnAlternative);
							if(difference <= 2) {
								if(!columnsCoveredFor11.contains(columnAlternative)) {
									synchronized (columnsCoveredFor11) {
										columnsCoveredFor11.add(columnAlternative);
									}
								}
								if(difference <= 1) {
									if(!columnsCoveredFor12.contains(columnAlternative)) {
										synchronized (columnsCoveredFor12) {
											columnsCoveredFor12.add(columnAlternative);
										}
									}
									if(difference <= 0 && _coveredFor13.get() < 39) {
										if(!columnsCoveredFor13.contains(columnAlternative)) {
											synchronized (columnsCoveredFor13) {
												columnsCoveredFor13.add(columnAlternative);
											}
										}
									}
								}
							}
						}
					}
				}
			};
			threads.add(thread);

		}

		for(Thread thread2 : threads) {
			thread2.start();
		}

		for(Thread thread2 : threads) {
			thread2.join();
		}

		float probability11 = 0;
		float probability12 = 0;
		float probability13 = 0;
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
	}
}

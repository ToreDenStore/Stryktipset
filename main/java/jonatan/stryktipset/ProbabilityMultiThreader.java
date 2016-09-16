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
		_columnsFinished.incrementAndGet();
		if(_columnsFinished.get() % 10000 == 0) {
			System.out.println(_columnsFinished);
		}
	}

	public void printAndCalculate(List<ColumnAlternative> columnAlternatives, List<ColumnAlternative> columnAlternativesAlreadyPrinted)
			throws InterruptedException
	{
		System.out.println("Checking covered for by 12...");

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
							int difference = Main.compareColumns(columnAlternativePrinted, columnAlternative, 3);
							if(difference <= 2) {
								_coveredFor11.incrementAndGet();
//								_probability11 += columnAlternative.getProbability();
								_probability11.set(_probability11.get() + columnAlternative.getProbability());
								if(difference <= 1) {
									_coveredFor12.incrementAndGet();
//									_probability12 += columnAlternative.getProbability();
									_probability12.set(_probability12.get() + columnAlternative.getProbability());
//									System.out.println("12: " + _coveredFor12);
									if(difference <= 0 && _coveredFor13.get() < 39) {
										_coveredFor13.incrementAndGet();
//										_probability13 += columnAlternative.getProbability();
										_probability13.set(_probability13.get() + columnAlternative.getProbability());
										break;
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
		
		System.out.println(_coveredFor11 + " rows are covered for 11 correct");
		System.out.println("Total probability of 11 correct: " + _probability11.get() * 100 + "%");
		System.out.println(_coveredFor12 + " rows are covered for 12 correct");
		System.out.println("Total probability of 12 correct: " + _probability12.get() * 100 + "%");
		System.out.println(_coveredFor13 + " rows are covered for 13 correct");
		System.out.println("Total probability of 13 correct: " + _probability13.get() * 100 + "%");
	}
}

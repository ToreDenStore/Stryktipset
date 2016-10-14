package jonatan.stryktipset;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class CalculateMultiThreader
{
	private final int _numberOfThreads;

	AtomicLong _columnsFinished = new AtomicLong();

	public CalculateMultiThreader(int numberOfThreads)
	{
		_numberOfThreads = numberOfThreads;
	}

	private void addToFinished()
	{
		long columnsFinished = _columnsFinished.incrementAndGet();
		if(columnsFinished % 100000 == 0) {
			System.out.println(columnsFinished);
		}
	}

	public void calculate12(List<ColumnAlternative> allColumns, List<ColumnAlternative> chosenColumns)
			throws InterruptedException
	{
		List<Thread> threads = new ArrayList<Thread>();
		for(int i = 0; i < _numberOfThreads; i++) {

			final int threadNumber = i;

			threads.add(new Thread()
			{
				public void run()
				{
					int part = allColumns.size() / _numberOfThreads;
					List<ColumnAlternative> shorterList = allColumns.subList(threadNumber * part, (threadNumber + 1) * part);

					for(ColumnAlternative columnAlternative : shorterList) {
						addToFinished();
						columnAlternative.calculateProbability12(chosenColumns);
					}
				}
			});
		}

		for(Thread thread2 : threads) {
			thread2.start();
		}

		for(Thread thread2 : threads) {
			thread2.join();
		}
	}
	
	public void calculate11(List<ColumnAlternative> allColumns, List<ColumnAlternative> chosenColumns)
			throws InterruptedException
	{
		List<Thread> threads = new ArrayList<Thread>();
		for(int i = 0; i < _numberOfThreads; i++) {

			final int threadNumber = i;

			threads.add(new Thread()
			{
				public void run()
				{
					int part = allColumns.size() / _numberOfThreads;
					List<ColumnAlternative> shorterList = allColumns.subList(threadNumber * part, (threadNumber + 1) * part);

					for(ColumnAlternative columnAlternative : shorterList) {
						addToFinished();
						columnAlternative.calculateProbability11(chosenColumns);
					}
				}
			});
		}

		for(Thread thread2 : threads) {
			thread2.start();
		}

		for(Thread thread2 : threads) {
			thread2.join();
		}
	}
}

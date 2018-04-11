package sorting;

import java.util.Random;

public class SortingTests {

	public static void main(String[] args) {
		System.out.println("Warming up...");
		int[] someArray = new int[10000];
		Random r = new Random();
		for (int i = 0; i < someArray.length; i++) {
			someArray[i] = r.nextInt(someArray.length);
		}
		// printArray(someArray);
		Long[] times = new Long[] {0l,0l,0l};
		for (int i = 0; i < 100; i++) {
			times[0] += sortUsing(new BubbleSort(someArray));
			times[1] += sortUsing(new InsertionSort(someArray));
			times[2] += sortUsing(new SelectionSort(someArray));
		}
		for(int i = 0; i < times.length; i++) {
			times[i] = times[i] / 100;
		}
		System.out.println("Beginning...");
		times = new Long[] {0l,0l,0l};
		for (int i = 0; i < 1000; i++) {
			times[0] += sortUsing(new BubbleSort(someArray));
			times[1] += sortUsing(new InsertionSort(someArray));
			times[2] += sortUsing(new SelectionSort(someArray));
		}
		printArray(times);
		for(int i = 0; i < times.length; i++) {
			times[i] = times[i] / 1000;
		}
		printArray(times);
		System.out.println("Done.");
	}

	private static long sortUsing(SorterTemplate toSort) {
		long curr = System.currentTimeMillis();
		toSort.sort();
		return System.currentTimeMillis() - curr;
	}

	private static void printArray(Object[] a) {
		String toPrint = "[";
		for (int i = 0; i < a.length; i++) {
			toPrint += a[i];
			if (i < a.length - 1) {
				toPrint += ", ";
			}
		}
		toPrint += "]";
		System.out.println(toPrint);
	}

}

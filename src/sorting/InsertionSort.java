package sorting;

public class InsertionSort extends SorterTemplate {

	public InsertionSort(int[] a) {
		super(a);
	}

	@Override
	void sort() {
		for (int i = 1; i < a.length; i++) {
			int j = i;
			int t = a[j];
			while (j > 0 && t < a[j - 1]) {
				a[j] = a[j - 1];
				j--;
			}
			a[j] = t;
		}
	}

}

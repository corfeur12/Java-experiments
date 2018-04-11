package sorting;

public class SelectionSort extends SorterTemplate {

	public SelectionSort(int[] a) {
		super(a);
	}

	@Override
	void sort() {
		for (int i = 0; i < a.length - 1; i++) {
			int k = i;
			for (int j = i + 1; j < a.length; j++) {
				if (a[j] < a[k]) {
					k = j;
				}
			}
			int temp = a[i];
			a[i] = a[k];
			a[k] = temp;
		}
	}

}

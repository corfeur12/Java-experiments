package sorting;

public class BubbleSort extends SorterTemplate {

	public BubbleSort(int[] a) {
		super(a);
	}

	@Override
	public void sort() {
		for (int i = 1; i < a.length; i++) {
			for (int j = a.length - 1; j >= i; j--) {
				if (a[j] < a[j - 1]) {
					int temp = a[j];
					a[j] = a[j - 1];
					a[j - 1] = temp;
				}
			}
		}
	}

}

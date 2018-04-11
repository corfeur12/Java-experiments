package sorting;

public abstract class SorterTemplate {

	final int[] a;

	public SorterTemplate(int[] a) {
		this.a = a;
	}

	public int[] getArray() {
		return a;
	}

	abstract void sort();

}

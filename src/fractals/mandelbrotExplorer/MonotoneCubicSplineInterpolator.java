package fractals.mandelbrotExplorer;

import java.util.List;

public class MonotoneCubicSplineInterpolator {

	private List<Double> xs;
	private List<Double> ys;
	private double[] ms;

	public MonotoneCubicSplineInterpolator(List<Double> _x, List<Double> _y) {
		this(_x, _y, new double[0]);
	}

	private MonotoneCubicSplineInterpolator(List<Double> _x, List<Double> _y, double[] _m) {
		if (_x == null || _y == null || _x.size() != _y.size() || _x.size() < 2) {
			throw new IllegalArgumentException("Both lists must be the same length and have length of at least 2.");
		}
		this.xs = _x;
		this.ys = _y;
		this.ms = _m;
		processInterpolator();
	}

	private void processInterpolator() {
		final int xSize = xs.size();
		double[] midMidSemiMid = new double[xSize - 1];
		ms = new double[xSize];
		// compute slopes of secant lines between points
		for (int i = 0; i < xSize - 1; i++) {
			double h = xs.get(i + 1) - xs.get(i);
			if (h <= 0f) {
				throw new IllegalArgumentException("Values for the X list must be in ascending order.");
			}
			midMidSemiMid[i] = (ys.get(i + 1) - ys.get(i)) / h;
		}
		// initialise tangents as the average of secants
		ms[0] = midMidSemiMid[0];
		for (int i = 1; i < xSize - 1; i++) {
			ms[i] = (midMidSemiMid[i - 1] + midMidSemiMid[i]) * 0.5f;
		}
		ms[xSize - 1] = midMidSemiMid[xSize - 2];
		// update tangents to preserve monotonicity
		for (int i = 0; i < xSize - 1; i++) {
			if (midMidSemiMid[i] == 0f) { // successive y values are equal
				ms[i] = 0f;
				ms[i + 1] = 0f;
			} else {
				double a = ms[i] / midMidSemiMid[i];
				double b = ms[i + 1] / midMidSemiMid[i];
				double h = Math.hypot(a, b);
				if (h > 9f) {
					double t = 3f / h;
					ms[i] = t * a * midMidSemiMid[i];
					ms[i + 1] = t * b * midMidSemiMid[i];
				}
			}
		}
	}

	public double interpolate(double _x) {
		// handle the boundary cases
		final int xSize = xs.size();
		if (Double.isNaN(_x)) {
			return _x;
		}
		if (_x <= xs.get(0)) {
			return ys.get(0);
		}
		if (_x >= xs.get(xSize - 1)) {
			return ys.get(xSize - 1);
		}
		// find the index of the last point with smaller x
		// we know this will be within the spline from the boundary tests
		int i = 0;
		while (_x >= xs.get(i + 1)) {
			i += 1;
			if (_x == xs.get(i)) {
				return ys.get(i);
			}
		}
		// perform cubic Hermite spline interpolation
		double h = xs.get(i + 1) - xs.get(i);
		double t = (_x - xs.get(i)) / h;
		return (ys.get(i) * (1 + 2 * t) + h * ms[i] * t) * (1 - t) * (1 - t)
				+ (ys.get(i + 1) * (3 - 2 * t) + h * ms[i + 1] * (t - 1)) * t * t;
	}

}

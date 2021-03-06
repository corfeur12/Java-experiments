package fractals.mandelbrotExplorer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

public class Mandelbrot extends JPanel {

	private static final Logger logger = Logger.getLogger(Mandelbrot.class);

	private static final long serialVersionUID = 610332705523598428L;
	private static final int BLACK = Color.BLACK.getRGB();

	private int RENDER_METHOD = 3;

	private int imagePixelsSquare;
	private double xAxisScale;
	private double yAxisScale;
	private double xAxisOffset;
	private double yAxisOffset;
	private int sampleDepth;
	private BufferedImage imageBuffer;
	private int[] colourPalette;
	private int[] histogram;
	private double[] complex;

	public Mandelbrot() {
		// user input for certain attributes
		Scanner input = new Scanner(System.in);
		logger.trace("User input parameters");
		System.out.print("Canvas scale: ");
		imagePixelsSquare = Integer.parseInt(input.next().trim());
		logger.trace("Canvas scale: " + imagePixelsSquare);
		System.out.print("X axis scale: ");
		xAxisScale = Double.parseDouble(input.next().trim());
		logger.trace("X axis scale: " + xAxisScale);
		System.out.print("Y axis scale: ");
		yAxisScale = Double.parseDouble(input.next().trim());
		logger.trace("Y axis scale: " + yAxisScale);
		System.out.print("X axis offset: ");
		xAxisOffset = Double.parseDouble(input.next().trim());
		logger.trace("X axis offset: " + xAxisOffset);
		System.out.print("Y axis offset: ");
		yAxisOffset = Double.parseDouble(input.next().trim());
		logger.trace("Y axis offset: " + yAxisOffset);
		System.out.print("Maximum depth pass limit: ");
		sampleDepth = Integer.parseInt(input.next().trim());
		logger.trace("Maximum depth pass limit: " + sampleDepth);
		input.close();
		logger.info("Settings: " + imagePixelsSquare + "px square, (" + xAxisScale + ", " + yAxisScale + ") scale, ("
				+ xAxisOffset + ", " + yAxisOffset + ") offset, " + sampleDepth + " escape.");
		setPreferredSize(new Dimension(imagePixelsSquare, imagePixelsSquare));
		// sets up the colours
		colourPalette = new int[1000];
		histogram = new int[sampleDepth + 1];
		for (int i = 0; i < sampleDepth + 1; i++) {
			histogram[i] = 0;
		}
		// inefficient but useful way to store values
		complex = new double[imagePixelsSquare * imagePixelsSquare];
		preGenerateColours();
		calculateIterantTerminations();
		switch (RENDER_METHOD) {
		case 0:
			linearColouring();
			break;
		case 1:
			histogramColouring();
			break;
		case 2:
			linearColouringSmoothed();
			break;
		case 3:
			histogramColouringSmoothed();
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	private void preGenerateColours() {
		List<Double> xs = Arrays.asList(0.0, 16.0, 42.0, 64.25, 85.75, 100.0);
		List<Double> ys = Arrays.asList(0.0, 0.125, 0.92578125, 1.0, 0.0, 0.0);
		MonotoneCubicSplineInterpolator red = new MonotoneCubicSplineInterpolator(xs, ys);
		ys = Arrays.asList(0.03125, 0.421875, 1.0, 0.6640625, 0.0078125, 0.03125);
		MonotoneCubicSplineInterpolator green = new MonotoneCubicSplineInterpolator(xs, ys);
		ys = Arrays.asList(0.390625, 0.7890625, 1.0, 0.0, 0.0, 0.390625);
		MonotoneCubicSplineInterpolator blue = new MonotoneCubicSplineInterpolator(xs, ys);
		for (int i = 0; i < colourPalette.length; i++) {
			double thisRed = rangeCheck(red.interpolate(i * (100.0 / colourPalette.length)));
			double thisGreen = rangeCheck(green.interpolate(i * (100.0 / colourPalette.length)));
			double thisBlue = rangeCheck(blue.interpolate(i * (100.0 / colourPalette.length)));
			colourPalette[i] = (new Color((float) thisRed, (float) thisGreen, (float) thisBlue)).getRGB();
		}
	}

	private double rangeCheck(double a) {
		if (a < 0.0) {
			return 0;
		} else if (a > 1.0) {
			return 1.0;
		}
		return a;
	}

	private void calculateIterantTerminations() {
		imageBuffer = new BufferedImage(imagePixelsSquare, imagePixelsSquare, BufferedImage.TYPE_INT_RGB);
		for (int pixelY = 0; pixelY < imagePixelsSquare; pixelY++) {
			for (int pixelX = 0; pixelX < imagePixelsSquare; pixelX++) {
				double x0 = (pixelX - imagePixelsSquare / 2.0) / imagePixelsSquare * 4.0 / xAxisScale + xAxisOffset;
				double y0 = (pixelY - imagePixelsSquare / 2.0) / imagePixelsSquare * 4.0 / yAxisScale + yAxisOffset;
				double x = 0.0;
				double y = 0.0;
				int i;
				for (i = 0; i < sampleDepth; i++) {
					if (x * x + y * y >= 256) {
						break;
					}
					double xTemp = x * x - y * y + x0;
					double yTemp = 2 * x * y + y0;
					if (xTemp == x && yTemp == y) {
						i = sampleDepth - 1;
					}
					x = xTemp;
					y = yTemp;
				}
				histogram[i]++;
				double i2;
				if (i == sampleDepth || RENDER_METHOD < 2) {
					i2 = i;
				} else {
					i2 = i + 2 - Math.log(Math.log(x * x + y * y)) / Math.log(2);
				}
				complex[pixelX + pixelY * imagePixelsSquare] = i2;
			}
		}
	}

	private void linearColouring() {
		for (int pixelY = 0; pixelY < imagePixelsSquare; pixelY++) {
			for (int pixelX = 0; pixelX < imagePixelsSquare; pixelX++) {
				int i = (int) complex[pixelX + pixelY * imagePixelsSquare];
				if (i == sampleDepth) {
					imageBuffer.setRGB(pixelX, pixelY, BLACK);
				} else {
					imageBuffer.setRGB(pixelX, pixelY, colourPalette[i * 40 % colourPalette.length]);
				}
			}
		}
	}

	private void linearColouringSmoothed() {
		for (int pixelY = 0; pixelY < imagePixelsSquare; pixelY++) {
			for (int pixelX = 0; pixelX < imagePixelsSquare; pixelX++) {
				if (complex[pixelX + pixelY * imagePixelsSquare] == (double) sampleDepth) {
					imageBuffer.setRGB(pixelX, pixelY, BLACK);
				} else {
					double i2 = complex[pixelX + pixelY * imagePixelsSquare];
					imageBuffer.setRGB(pixelX, pixelY,
							smoothColourGen(i2, colourPalette[((int) Math.floor(i2 * 40)) % colourPalette.length],
									colourPalette[((int) Math.floor(i2 * 40 + 1)) % colourPalette.length]));
				}
			}
		}
	}

	private void histogramColouring() {
		int total = 0;
		for (int i = 0; i < histogram.length; i++) {
			total += histogram[i];
		}
		float[] hues = new float[histogram.length];
		float h = 0f;
		for (int i = 0; i < sampleDepth; i++) {
			h += histogram[i] / (float) total;
			hues[i] = h;
		}
		for (int pixelY = 0; pixelY < imagePixelsSquare; pixelY++) {
			for (int pixelX = 0; pixelX < imagePixelsSquare; pixelX++) {
				if ((int) complex[pixelX + pixelY * imagePixelsSquare] == sampleDepth) {
					imageBuffer.setRGB(pixelX, pixelY, BLACK);
				} else {
					float hue = hues[(int) complex[pixelX + pixelY * imagePixelsSquare]];
					imageBuffer.setRGB(pixelX, pixelY,
							colourPalette[((int) (hue * colourPalette.length * 10 + colourPalette.length / 3))
									% colourPalette.length]);
				}
			}
		}
	}

	private void histogramColouringSmoothed() {
		int total = 0;
		for (int i = 0; i < sampleDepth; i++) {
			total += histogram[i];
		}
		float[] hues = new float[sampleDepth];
		float h = 0f;
		for (int i = 0; i < sampleDepth; i++) {
			h += histogram[i] / (float) total;
			hues[i] = h;
		}
		for (int pixelY = 0; pixelY < imagePixelsSquare; pixelY++) {
			for (int pixelX = 0; pixelX < imagePixelsSquare; pixelX++) {
				if (complex[pixelX + pixelY * imagePixelsSquare] == (double) sampleDepth) {
					imageBuffer.setRGB(pixelX, pixelY, BLACK);
				} else {
					double i2 = complex[pixelX + pixelY * imagePixelsSquare];
					float hue1 = hues[((int) Math.floor(i2)) % hues.length];
					float hue2 = hues[((int) Math.floor(i2) + 1) % hues.length];
					imageBuffer.setRGB(pixelX, pixelY, smoothLoopedColourGen(hue1, hue2, 2, 0.8, i2));
				}
			}
		}
	}

	private int histogramColourIndexFunction(float hue, double repetitions, double offsetPercentage) {
		return (int) Math.floor(hue * colourPalette.length * repetitions + colourPalette.length * offsetPercentage);
	}

	private int smoothColourGen(double i2, int colourStart, int colourEnd) {
		int colourStartRed = new Color(colourStart).getRed();
		int colourStartGreen = new Color(colourStart).getGreen();
		int colourStartBlue = new Color(colourStart).getBlue();
		int colourEndRed = new Color(colourEnd).getRed();
		int colourEndGreen = new Color(colourEnd).getGreen();
		int colourEndBlue = new Color(colourEnd).getBlue();
		float thisRed = linearlyInterpolate(colourStartRed, colourEndRed, (float) i2 % 1) / 255f;
		float thisGreen = linearlyInterpolate(colourStartGreen, colourEndGreen, (float) i2 % 1) / 255f;
		float thisBlue = linearlyInterpolate(colourStartBlue, colourEndBlue, (float) i2 % 1) / 255f;
		if (thisRed > 1f) {
			thisRed = 1f;
		}
		if (thisGreen > 1f) {
			thisGreen = 1f;
		}
		if (thisBlue > 1f) {
			thisBlue = 1f;
		}
		return (new Color(thisRed, thisGreen, thisBlue)).getRGB();
	}

	private int smoothLoopedColourGen(float hue1, float hue2, double repetitions, double offsetPercentage, double i2) {
		int startIndex = histogramColourIndexFunction(hue1, repetitions, offsetPercentage);
		int endIndex = histogramColourIndexFunction(hue2, repetitions, offsetPercentage);
		int out = (int) linearlyInterpolate(startIndex, endIndex, (float) i2 % 1);
		return colourPalette[(out % colourPalette.length + colourPalette.length) % colourPalette.length];
	}

	private float linearlyInterpolate(float x1, float x2, float percentageAlong) {
		if (percentageAlong >= 0.0 && percentageAlong <= 1.0) {
			return x1 + (x2 - x1) * percentageAlong;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(imageBuffer, 0, 0, null);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame f = new JFrame("Mandelbrot");
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JFrame.setDefaultLookAndFeelDecorated(true);
				Mandelbrot canvas = new Mandelbrot();
				f.add(canvas);
				f.getContentPane().add(new JScrollPane(canvas, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
				f.pack();
				f.setVisible(true);
			}
		});
	}

}

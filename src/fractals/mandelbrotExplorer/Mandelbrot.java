package fractals.mandelbrotExplorer;

import java.awt.BorderLayout;
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

public class Mandelbrot extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 610332705523598428L;
	private int imagePixelsSquare;
	private double xAxisScale;
	private double yAxisScale;
	private double xAxisOffset;
	private double yAxisOffset;
	private int sampleDepth;
	private BufferedImage imageBuffer;
	private int[] colourPalette;
	private int[] histogram;
	private int[] iterants;

	private static final int BLACK = Color.BLACK.getRGB();

	public Mandelbrot() {
		// user input for certain attributes
		Scanner input = new Scanner(System.in);
		System.out.print("Canvas scale: ");
		imagePixelsSquare = Integer.parseInt(input.next().trim());
		System.out.print("X axis scale: ");
		xAxisScale = Double.parseDouble(input.next().trim());
		System.out.print("Y axis scale: ");
		yAxisScale = Double.parseDouble(input.next().trim());
		System.out.print("X axis offset: ");
		xAxisOffset = Double.parseDouble(input.next().trim());
		System.out.print("Y axis offset: ");
		yAxisOffset = Double.parseDouble(input.next().trim());
		System.out.print("Maximum depth pass limit: ");
		sampleDepth = Integer.parseInt(input.next().trim());
		input.close();
		setPreferredSize(new Dimension(imagePixelsSquare, imagePixelsSquare));
		// sets up the colours
		colourPalette = new int[1000];
		histogram = new int[sampleDepth + 1];
		for (int i = 0; i < sampleDepth + 1; i++) {
			histogram[i] = 0;
		}
		// inefficient but useful way to store values
		iterants = new int[imagePixelsSquare * imagePixelsSquare];
		preGenerateColours();
		calculateIterantTerminations();
		// linearColouring();
		histogramColouring();
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
					if (x * x + y * y >= 4) {
						break;
					}
					double tempX = x * x - y * y + x0;
					y = 2 * x * y + y0;
					x = tempX;
				}
				histogram[i]++;
				iterants[pixelX + pixelY * imagePixelsSquare] = i;
			}
		}
	}

	@SuppressWarnings("unused")
	private void linearColouring() {
		for (int pixelY = 0; pixelY < imagePixelsSquare; pixelY++) {
			for (int pixelX = 0; pixelX < imagePixelsSquare; pixelX++) {
				int i = iterants[pixelX + pixelY * imagePixelsSquare];
				if (i == sampleDepth) {
					imageBuffer.setRGB(pixelX, pixelY, BLACK);
				} else {
					imageBuffer.setRGB(pixelX, pixelY, colourPalette[i * 40 % colourPalette.length]);
				}
			}
		}
	}

	private void histogramColouring() {
		int total = 0;
		for (int i = 0; i < histogram.length; i++) {
			total += histogram[i];
		}
		for (int pixelY = 0; pixelY < imagePixelsSquare; pixelY++) {
			for (int pixelX = 0; pixelX < imagePixelsSquare; pixelX++) {
				if (iterants[pixelX + pixelY * imagePixelsSquare] == sampleDepth) {
					imageBuffer.setRGB(pixelX, pixelY, BLACK);
				} else {
					float hue = 0f;
					for (int i = 0; i < iterants[pixelX + pixelY * imagePixelsSquare]; i++) {
						hue += histogram[i] / (float) (total);
					}
					imageBuffer.setRGB(pixelX, pixelY,
							colourPalette[((int) (hue * colourPalette.length * 10 + colourPalette.length / 3))
									% colourPalette.length]);
				}
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(imageBuffer, 0, 0, null);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame("Mandelbrot");
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JFrame.setDefaultLookAndFeelDecorated(true);
				Mandelbrot canvas = new Mandelbrot();
				f.add(canvas, BorderLayout.CENTER);
				f.getContentPane().add(new JScrollPane(canvas));
				f.pack();
				f.setVisible(true);
			}
		});
	}

}

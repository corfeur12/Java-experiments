package fractals.mandelbrotExplorer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Mandelbrot extends JPanel {

	private static final long serialVersionUID = 610332705523598428L;
	private static final int BLACK = Color.BLACK.getRGB();

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

	public Mandelbrot(RenderSettings settings) {
		imagePixelsSquare = settings.getImagePixelsSquare();
		xAxisScale = settings.getXAxisScale();
		yAxisScale = settings.getYAxisScale();
		xAxisOffset = settings.getXAxisOffset();
		yAxisOffset = settings.getYAxisOffset();
		sampleDepth = settings.getSampleDepth();
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
		calculateIterantTerminations(settings.getSmoothed());
		switch (settings.getRenderMethod()) {
		case RenderSettings.LINEAR:
			linearColouring(settings.getSmoothed());
			break;
		case RenderSettings.HISTOGRAM:
			histogramColouring(settings.getSmoothed());
			break;
		default:
			throw new IllegalArgumentException("Invalid render method.");
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
			double thisRed = clipInRange(red.interpolate(i * (100.0 / colourPalette.length)), 0, 1);
			double thisGreen = clipInRange(green.interpolate(i * (100.0 / colourPalette.length)), 0, 1);
			double thisBlue = clipInRange(blue.interpolate(i * (100.0 / colourPalette.length)), 0, 1);
			colourPalette[i] = (new Color((float) thisRed, (float) thisGreen, (float) thisBlue)).getRGB();
		}
	}

	private static double clipInRange(double _a, double _min, double _max) {
		if (_a < _min) {
			return _min;
		} else if (_a > _max) {
			return _max;
		}
		return _a;
	}

	private void calculateIterantTerminations(boolean _smoothed) {
		imageBuffer = new BufferedImage(imagePixelsSquare, imagePixelsSquare, BufferedImage.TYPE_INT_RGB);
		for (int pixelY = 0; pixelY < imagePixelsSquare; pixelY++) {
			for (int pixelX = 0; pixelX < imagePixelsSquare; pixelX++) {
				double x0 = absoluteToRelativePosition(pixelX, imagePixelsSquare, xAxisScale, xAxisOffset);
				double y0 = absoluteToRelativePosition(pixelY, imagePixelsSquare, yAxisScale, yAxisOffset);
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
				if (i == sampleDepth || !_smoothed) {
					i2 = i;
				} else {
					i2 = i + 2 - Math.log(Math.log(x * x + y * y)) / Math.log(2);
				}
				complex[pixelX + pixelY * imagePixelsSquare] = i2;
			}
		}
	}
	
	private static double absoluteToRelativePosition(int _pixel, int _imageScale, double _axisScale, double _axisOffset) {
		return (_pixel - _imageScale / 2.0) / _imageScale * 4.0 / _axisScale + _axisOffset;
	}

	private void linearColouring(boolean _smoothed) {
		for (int pixelY = 0; pixelY < imagePixelsSquare; pixelY++) {
			for (int pixelX = 0; pixelX < imagePixelsSquare; pixelX++) {
				if (_smoothed) {
					if (complex[pixelX + pixelY * imagePixelsSquare] == (double) sampleDepth) {
						imageBuffer.setRGB(pixelX, pixelY, BLACK);
					} else {
						double i2 = complex[pixelX + pixelY * imagePixelsSquare];
						imageBuffer.setRGB(pixelX, pixelY,
								smoothColourGen(i2, colourPalette[((int) Math.floor(i2 * 40)) % colourPalette.length],
										colourPalette[((int) Math.floor(i2 * 40 + 1)) % colourPalette.length]));
					}
				} else {
					int i = (int) complex[pixelX + pixelY * imagePixelsSquare];
					if (i == sampleDepth) {
						imageBuffer.setRGB(pixelX, pixelY, BLACK);
					} else {
						imageBuffer.setRGB(pixelX, pixelY, colourPalette[i * 40 % colourPalette.length]);
					}
				}
			}
		}
	}

	private void histogramColouring(boolean _smoothed) {
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
				if (_smoothed) {
					if (complex[pixelX + pixelY * imagePixelsSquare] == (double) sampleDepth) {
						imageBuffer.setRGB(pixelX, pixelY, BLACK);
					} else {
						double i2 = complex[pixelX + pixelY * imagePixelsSquare];
						float hue1 = hues[((int) Math.floor(i2)) % hues.length];
						float hue2 = hues[((int) Math.floor(i2) + 1) % hues.length];
						imageBuffer.setRGB(pixelX, pixelY, smoothLoopedColourGen(hue1, hue2, 1, 0.8, i2));
					}
				} else {
					if ((int) complex[pixelX + pixelY * imagePixelsSquare] == sampleDepth) {
						imageBuffer.setRGB(pixelX, pixelY, BLACK);
					} else {
						float hue = hues[(int) complex[pixelX + pixelY * imagePixelsSquare]];
						imageBuffer.setRGB(pixelX, pixelY,
								colourPalette[((int) (hue * colourPalette.length + colourPalette.length * 0.8))
										% colourPalette.length]);
					}
				}
			}
		}
	}

	private static int getHistogramColourIndex(int[] _colourPalette, float _hue, double _repetitions, double _offsetPercentage) {
		return (int) Math.floor(_hue * _colourPalette.length * _repetitions + _colourPalette.length * _offsetPercentage);
	}

	private static int smoothColourGen(double _i2, int _colourStart, int _colourEnd) {
		float red = linearlyInterpolate(new Color(_colourStart).getRed(), new Color(_colourEnd).getRed(),
				(float) _i2 % 1) / 255f;
		float green = linearlyInterpolate(new Color(_colourStart).getGreen(), new Color(_colourEnd).getGreen(),
				(float) _i2 % 1) / 255f;
		float blue = linearlyInterpolate(new Color(_colourStart).getBlue(), new Color(_colourEnd).getBlue(),
				(float) _i2 % 1) / 255f;
		red = clipInRange(red, 0f, 1f);
		green = clipInRange(green, 0f, 1f);
		blue = clipInRange(blue, 0f, 1f);
		return (new Color(red, green, blue)).getRGB();
	}

	private static float clipInRange(float _a, float _min, float _max) {
		if (_a < _min) {
			return _min;
		} else if (_a > _max) {
			return _max;
		}
		return _a;
	}

	private int smoothLoopedColourGen(float _hue1, float _hue2, double _repetitions, double _offsetPercentage,
			double _i2) {
		int startIndex = getHistogramColourIndex(colourPalette, _hue1, _repetitions, _offsetPercentage);
		int endIndex = getHistogramColourIndex(colourPalette, _hue2, _repetitions, _offsetPercentage);
		int out = (int) linearlyInterpolate(startIndex, endIndex, (float) _i2 % 1);
		return colourPalette[(out % colourPalette.length + colourPalette.length) % colourPalette.length];
	}

	private static float linearlyInterpolate(float _start, float _along, float _percentageAlong) {
		if (_percentageAlong >= 0.0 && _percentageAlong <= 1.0) {
			return _start + (_along - _start) * _percentageAlong;
		} else {
			throw new IllegalArgumentException("Percentage not in accepted range (0 to 1).");
		}
	}

	@Override
	public void paint(Graphics _g) {
		super.paint(_g);
		_g.drawImage(imageBuffer, 0, 0, null);
	}

	public static void mandelbrotSet(RenderSettings settings) {
		JFrame frame = new JFrame("Mandelbrot");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		Mandelbrot canvas = new Mandelbrot(settings);
		ImageBarGUI toolbar = new ImageBarGUI(canvas.imageBuffer);
		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				double x = absoluteToRelativePosition(e.getX(), canvas.imagePixelsSquare, canvas.xAxisScale, canvas.xAxisOffset);
				double y = absoluteToRelativePosition(e.getY(), canvas.imagePixelsSquare, canvas.yAxisScale, canvas.yAxisOffset);
				toolbar.setMouseSavedPosition(x, y);
			}
		});
		canvas.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				double x = absoluteToRelativePosition(e.getX(), canvas.imagePixelsSquare, canvas.xAxisScale, canvas.xAxisOffset);
				double y = absoluteToRelativePosition(e.getY(), canvas.imagePixelsSquare, canvas.yAxisScale, canvas.yAxisOffset);
				toolbar.setMouseCurrentPosition(x, y);
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		JScrollPane scroller = new JScrollPane(canvas);
		scroller.setBorder(BorderFactory.createEmptyBorder());
		frame.add(toolbar.getToolbar(), BorderLayout.NORTH);
		frame.add(scroller, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new LaunchGUI();
			}
		});
	}

}

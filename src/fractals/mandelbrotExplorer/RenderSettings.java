package fractals.mandelbrotExplorer;

public class RenderSettings {

	public static final int LINEAR = 0;
	public static final int HISTOGRAM = 1;
	
	public static final String RENDER_METHOD = "renderMethod";
	public static final String IS_SMOOTHED = "isSmoothed";
	public static final String IMAGE_PIXELS_SQUARE = "imagePixelsSquare";
	public static final String X_AXIS_SCALE = "xAxisScale";
	public static final String Y_AXIS_SCALE = "yAxisScale";
	public static final String X_AXIS_OFFSET = "xAxisOffset";
	public static final String Y_AXIS_OFFSET = "yAxisOffset";
	public static final String SAMPLE_DEPTH = "sampleDepth";
	
	public static final int DEFAULT_RENDER_METHOD = RenderSettings.LINEAR;
	public static final boolean DEFAULT_IS_SMOOTHED = false;
	public static final int DEFAULT_IMAGE_PIXELS_SQUARE = 500;
	public static final double DEFAULT_X_AXIS_SCALE = 1.0;
	public static final double DEFAULT_Y_AXIS_SCALE = 1.0;
	public static final double DEFAULT_X_AXIS_OFFSET = 0;
	public static final double DEFAULT_Y_AXIS_OFFSET = 0;
	public static final int DEFAULT_SAMPLE_DEPTH = 25;

}

package fractals.mandelbrotExplorer;

//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
//import javax.xml.bind.annotation.XmlType;

@XmlRootElement
// @XmlAccessorType(XmlAccessType.FIELD)
// @XmlType(propOrder = { "users" })
public class RenderSettings {

	public static final int LINEAR = 0;
	public static final int HISTOGRAM = 1;

	private int renderMethod;
	private boolean smoothed;
	private int imagePixelsSquare;
	private double xAxisScale;
	private double yAxisScale;
	private double xAxisOffset;
	private double yAxisOffset;
	private int sampleDepth;

	public RenderSettings() {
		initialiseDefaults();
	}

	public RenderSettings(int _renderMethod, boolean _smoothed, int _imagePixelsSquare, double _xAxisScale,
			double _yAxisScale, double _xAxisOffset, double _yAxisOffset, int _sampleDepth) {
		this.renderMethod = _renderMethod;
		this.smoothed = _smoothed;
		this.imagePixelsSquare = _imagePixelsSquare;
		this.xAxisScale = _xAxisScale;
		this.yAxisScale = _yAxisScale;
		this.xAxisOffset = _xAxisOffset;
		this.yAxisOffset = _yAxisOffset;
		this.sampleDepth = _sampleDepth;
	}

	public void initialiseDefaults() {
		this.renderMethod = LINEAR;
		this.smoothed = false;
		this.imagePixelsSquare = 500;
		this.xAxisScale = 1;
		this.yAxisScale = 1;
		this.xAxisOffset = 0;
		this.yAxisOffset = 0;
		this.sampleDepth = 25;
	}

	public int getRenderMethod() {
		return renderMethod;
	}

	public void setRenderMethod(int _renderMethod) {
		this.renderMethod = _renderMethod;
	}

	public boolean getSmoothed() {
		return smoothed;
	}

	public void setSmoothed(boolean smoothed) {
		this.smoothed = smoothed;
	}

	public int getImagePixelsSquare() {
		return imagePixelsSquare;
	}

	public void setImagePixelsSquare(int _imagePixelsSquare) {
		this.imagePixelsSquare = _imagePixelsSquare;
	}

	public double getXAxisScale() {
		return xAxisScale;
	}

	public void setXAxisScale(double _xAxisScale) {
		this.xAxisScale = _xAxisScale;
	}

	public double getYAxisScale() {
		return yAxisScale;
	}

	public void setYAxisScale(double _yAxisScale) {
		this.yAxisScale = _yAxisScale;
	}

	public double getXAxisOffset() {
		return xAxisOffset;
	}

	public void setXAxisOffset(double _xAxisOffset) {
		this.xAxisOffset = _xAxisOffset;
	}

	public double getYAxisOffset() {
		return yAxisOffset;
	}

	public void setYAxisOffset(double _yAxisOffset) {
		this.yAxisOffset = _yAxisOffset;
	}

	public int getSampleDepth() {
		return sampleDepth;
	}

	public void setSampleDepth(int _sampleDepth) {
		this.sampleDepth = _sampleDepth;
	}

}

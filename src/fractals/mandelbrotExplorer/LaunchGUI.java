package fractals.mandelbrotExplorer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LaunchGUI {

	public static final Properties DEFAULT_RENDER_PROPERTIES;
	static {
		DEFAULT_RENDER_PROPERTIES = new Properties();
		DEFAULT_RENDER_PROPERTIES.setProperty(RenderSettings.RENDER_METHOD, String.valueOf(RenderSettings.LINEAR));
		DEFAULT_RENDER_PROPERTIES.setProperty(RenderSettings.IS_SMOOTHED, Boolean.toString(false));
		DEFAULT_RENDER_PROPERTIES.setProperty(RenderSettings.IMAGE_PIXELS_SQUARE, "500");
		DEFAULT_RENDER_PROPERTIES.setProperty(RenderSettings.X_AXIS_SCALE, "1");
		DEFAULT_RENDER_PROPERTIES.setProperty(RenderSettings.Y_AXIS_SCALE, "1");
		DEFAULT_RENDER_PROPERTIES.setProperty(RenderSettings.X_AXIS_OFFSET, "0");
		DEFAULT_RENDER_PROPERTIES.setProperty(RenderSettings.Y_AXIS_OFFSET, "0");
		DEFAULT_RENDER_PROPERTIES.setProperty(RenderSettings.SAMPLE_DEPTH, "25");
	}

	private JFrame frame;
	private ButtonGroup renderTypeButtons;
	private JCheckBox smoothedCheckBox;
	private JSpinner xAxisScaleSpinner;
	private JSpinner yAxisScaleSpinner;
	private JSpinner xAxisOffsetSpinner;
	private JSpinner yAxisOffsetSpinner;
	private JSpinner imagePixelsSquareSpinner;
	private JSpinner sampleDepthSpinner;

	public LaunchGUI() {
		frame = new JFrame("Render Settings");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel renderPanel = new JPanel();
		renderPanel.setBorder(BorderFactory.createEtchedBorder());
		renderPanel.setLayout(new GridLayout(1, 0));
		JRadioButton linearRadio = new JRadioButton("Linear");
		JRadioButton histogramRadio = new JRadioButton("Histogram");
		smoothedCheckBox = new JCheckBox("Smooth colouring");
		linearRadio.setSelected(true);
		linearRadio.setActionCommand(String.valueOf(RenderSettings.LINEAR));
		histogramRadio.setActionCommand(String.valueOf(RenderSettings.HISTOGRAM));
		renderTypeButtons = new ButtonGroup();
		renderTypeButtons.add(linearRadio);
		renderTypeButtons.add(histogramRadio);
		renderPanel.add(linearRadio);
		renderPanel.add(histogramRadio);
		renderPanel.add(smoothedCheckBox);
		JPanel transformationPanel = new JPanel();
		transformationPanel.setLayout(new GridLayout(2, 2, 10, 10));
		JPanel xScalePanel = labeledSpinnerDoubleInput("X axis scale: ", 1, 0, Double.POSITIVE_INFINITY, 1);
		xAxisScaleSpinner = (JSpinner) xScalePanel.getComponent(1);
		JPanel yScalePanel = labeledSpinnerDoubleInput("Y axis scale: ", 1, 0, Double.POSITIVE_INFINITY, 1);
		yAxisScaleSpinner = (JSpinner) yScalePanel.getComponent(1);
		JPanel xOffsetPanel = labeledSpinnerDoubleInput("X axis offset: ", 0, Double.NEGATIVE_INFINITY,
				Double.POSITIVE_INFINITY, 0.1);
		xAxisOffsetSpinner = (JSpinner) xOffsetPanel.getComponent(1);
		JPanel yOffsetPanel = labeledSpinnerDoubleInput("Y axis offset: ", 0, Double.NEGATIVE_INFINITY,
				Double.POSITIVE_INFINITY, 0.1);
		yAxisOffsetSpinner = (JSpinner) yOffsetPanel.getComponent(1);
		transformationPanel.add(xScalePanel);
		transformationPanel.add(yScalePanel);
		transformationPanel.add(xOffsetPanel);
		transformationPanel.add(yOffsetPanel);
		JPanel imagePanel = new JPanel();
		imagePanel.setLayout(new GridLayout(2, 1, 10, 10));
		JPanel pixelSizeInput = labeledSpinnerIntegerInput("Image pixels (square): ", 500, 1, Integer.MAX_VALUE, 10);
		imagePixelsSquareSpinner = (JSpinner) pixelSizeInput.getComponent(1);
		JPanel sampleDepthInput = labeledSpinnerIntegerInput("Maximum sample depth: ", 25, 1, Integer.MAX_VALUE, 5);
		sampleDepthSpinner = (JSpinner) sampleDepthInput.getComponent(1);
		imagePanel.add(pixelSizeInput);
		imagePanel.add(sampleDepthInput);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 3, 10, 10));
		JButton renderButton = new JButton("Render");
		renderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				RenderSettings settings = getInputs();
				Properties renderProperties = getInputs();
//				Mandelbrot.mandelbrotSet(settings);
				Mandelbrot.mandelbrotSet(renderProperties);
			}
		});
		JButton saveButton = new JButton("Export settings");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
//				saveRenderJAXB(getInputs());
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Save settings");
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setFileFilter(new FileNameExtensionFilter("XML file", "xml"));
				fileChooser.setSelectedFile(new File("mandelbrot_render_settings.xml"));
				int userSelection = fileChooser.showSaveDialog(frame);
				if (userSelection == JFileChooser.APPROVE_OPTION) {
					File saveFile = fileChooser.getSelectedFile();
					saveProperties(getInputs(), saveFile.getAbsolutePath());
				}
			}
		});
		JButton loadButton = new JButton("Import settings");
		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
//				setInputs(loadRenderJAXB());
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Load setttings");
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setFileFilter(new FileNameExtensionFilter("XML file", "xml"));
				int userSelection = fileChooser.showSaveDialog(frame);
				if (userSelection == JFileChooser.APPROVE_OPTION) {
					File saveFile = fileChooser.getSelectedFile();
					setInputs(loadProperties(saveFile.getAbsolutePath()));
				}
			}
		});
		buttonPanel.add(renderButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(loadButton);
		// frame.setLayout(new GridLayout(0, 1, 20, 20));
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridwidth = 0;
		constraints.insets = new Insets(5, 5, 5, 5);
		frame.setLayout(layout);
		frame.add(renderPanel, constraints);
		frame.add(transformationPanel, constraints);
		frame.add(imagePanel, constraints);
		frame.add(buttonPanel, constraints);
		frame.pack();
		frame.setVisible(true);
	}

//	private RenderSettings getInputs() {
//		RenderSettings settings = new RenderSettings();
//		settings.setRenderMethod(Integer.parseInt(renderTypeButtons.getSelection().getActionCommand()));
//		settings.setSmoothed(smoothedCheckBox.isSelected());
//		settings.setXAxisScale((double) xAxisScaleSpinner.getValue());
//		settings.setYAxisScale((double) yAxisScaleSpinner.getValue());
//		settings.setXAxisOffset((double) xAxisOffsetSpinner.getValue());
//		settings.setYAxisOffset((double) yAxisOffsetSpinner.getValue());
//		settings.setImagePixelsSquare((int) imagePixelsSquareSpinner.getValue());
//		settings.setSampleDepth((int) sampleDepthSpinner.getValue());
//		return settings;
//	}
	
	private Properties getInputs() {
		Properties properties = new Properties();
		properties.setProperty(RenderSettings.RENDER_METHOD, renderTypeButtons.getSelection().getActionCommand());
		properties.setProperty(RenderSettings.IS_SMOOTHED, Boolean.toString(smoothedCheckBox.isSelected()));
		properties.setProperty(RenderSettings.X_AXIS_SCALE, String.valueOf(xAxisScaleSpinner.getValue()));
		properties.setProperty(RenderSettings.Y_AXIS_SCALE, String.valueOf(yAxisScaleSpinner.getValue()));
		properties.setProperty(RenderSettings.X_AXIS_OFFSET, String.valueOf(xAxisOffsetSpinner.getValue()));
		properties.setProperty(RenderSettings.Y_AXIS_OFFSET, String.valueOf(yAxisOffsetSpinner.getValue()));
		properties.setProperty(RenderSettings.IMAGE_PIXELS_SQUARE, String.valueOf(imagePixelsSquareSpinner.getValue()));
		properties.setProperty(RenderSettings.SAMPLE_DEPTH, String.valueOf(sampleDepthSpinner.getValue()));
		return properties;
	}

//	private void setInputs(RenderSettings toSet) {
//		Enumeration<AbstractButton> buttons = renderTypeButtons.getElements();
//		AbstractButton thisButton = buttons.nextElement();
//		for (int i = 0; i < toSet.getRenderMethod(); i++) {
//			thisButton = buttons.nextElement();
//		}
//		renderTypeButtons.setSelected(thisButton.getModel(), true);
//		smoothedCheckBox.setSelected(toSet.getSmoothed());
//		xAxisScaleSpinner.setValue(toSet.getXAxisScale());
//		yAxisScaleSpinner.setValue(toSet.getYAxisScale());
//		xAxisOffsetSpinner.setValue(toSet.getXAxisOffset());
//		yAxisOffsetSpinner.setValue(toSet.getYAxisOffset());
//		imagePixelsSquareSpinner.setValue(toSet.getImagePixelsSquare());
//		sampleDepthSpinner.setValue(toSet.getSampleDepth());
//	}
	
	private void setInputs(Properties properties) {
		Enumeration<AbstractButton> buttons = renderTypeButtons.getElements();
		AbstractButton thisButton = buttons.nextElement();
		for (int i = 0; i < Integer.parseInt(properties.getProperty(RenderSettings.RENDER_METHOD)); i++) {
			thisButton = buttons.nextElement();
		}
		renderTypeButtons.setSelected(thisButton.getModel(), true);
		smoothedCheckBox.setSelected(Boolean.parseBoolean(properties.getProperty(RenderSettings.IS_SMOOTHED)));
		xAxisScaleSpinner.setValue(Double.parseDouble(properties.getProperty(RenderSettings.X_AXIS_SCALE)));
		yAxisScaleSpinner.setValue(Double.parseDouble(properties.getProperty(RenderSettings.Y_AXIS_SCALE)));
		xAxisOffsetSpinner.setValue(Double.parseDouble(properties.getProperty(RenderSettings.X_AXIS_OFFSET)));
		yAxisOffsetSpinner.setValue(Double.parseDouble(properties.getProperty(RenderSettings.Y_AXIS_OFFSET)));
		imagePixelsSquareSpinner.setValue(Integer.parseInt(properties.getProperty(RenderSettings.IMAGE_PIXELS_SQUARE)));
		sampleDepthSpinner.setValue(Integer.parseInt(properties.getProperty(RenderSettings.SAMPLE_DEPTH)));
	}
	
	private static void saveProperties(Properties properties, String fileName) {
		try {
			File f = new File(fileName);
			FileOutputStream outputStream = new FileOutputStream(f);
			properties.storeToXML(outputStream, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	private static void saveRenderJAXB(RenderSettings _render) {
//		try {
//			JAXBContext jaxb = JAXBContext.newInstance(RenderSettings.class);
//			Marshaller marsh = jaxb.createMarshaller();
//			marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//			OutputStream saveFile = new FileOutputStream("renderSave" + ".xml");
//			marsh.marshal(_render, saveFile);
//		} catch (JAXBException e) {
//			System.err.println("Data not saved to XML file. " + e);
//		} catch (FileNotFoundException e) {
//			System.err.println("BufferedReader failed. " + e);
//		}
//	}
	
	private static Properties loadProperties(String fileName) {
		try {
			Properties properties = new Properties();
			File f = new File(fileName);
			FileInputStream inputStream = new FileInputStream(f);
			properties.loadFromXML(inputStream);
			return properties;
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

//	private static RenderSettings loadRenderJAXB() {
//		try {
//			File f = new File("renderSave" + ".xml");
//			JAXBContext jaxb = JAXBContext.newInstance(RenderSettings.class);
//			Unmarshaller unmarsh = jaxb.createUnmarshaller();
//			return (RenderSettings) unmarsh.unmarshal(f);
//		} catch (JAXBException e) {
//			System.err.println("Data not loaded from XML file. " + e);
//		}
//		return null;
//	}

	private static JPanel labeledSpinnerDoubleInput(String _name, double _begin, double _min, double _max,
			double _interval) {
		JPanel panel = labeledSpinnerInput(_name);
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(_begin, _min, _max, _interval));
		panel.add(spinner);
		return panel;
	}

	private static JPanel labeledSpinnerIntegerInput(String _name, int _begin, int _min, int _max, int _interval) {
		JPanel panel = labeledSpinnerInput(_name);
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(_begin, _min, _max, _interval));
		panel.add(spinner);
		return panel;
	}

	private static JPanel labeledSpinnerInput(String _name) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel(_name);
		panel.setLayout(new GridLayout(1, 2));
		panel.add(label);
		return panel;
	}

}

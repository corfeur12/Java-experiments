package fractals.mandelbrotExplorer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class LaunchGUI {

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
		linearRadio.setActionCommand(Integer.toString(RenderSettings.LINEAR));
		histogramRadio.setActionCommand(Integer.toString(RenderSettings.HISTOGRAM));
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
				RenderSettings settings = getInputs();
				Mandelbrot.mandelbrotSet(settings);
			}
		});
		JButton saveButton = new JButton("Export settings");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveRenderJAXB(getInputs());
			}
		});
		JButton loadButton = new JButton("Import settings");
		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setInputs(loadRenderJAXB());
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

	private RenderSettings getInputs() {
		RenderSettings settings = new RenderSettings();
		settings.setRenderMethod(Integer.parseInt(renderTypeButtons.getSelection().getActionCommand()));
		settings.setSmoothed(smoothedCheckBox.isSelected());
		settings.setXAxisScale((double) xAxisScaleSpinner.getValue());
		settings.setYAxisScale((double) yAxisScaleSpinner.getValue());
		settings.setXAxisOffset((double) xAxisOffsetSpinner.getValue());
		settings.setYAxisOffset((double) yAxisOffsetSpinner.getValue());
		settings.setImagePixelsSquare((int) imagePixelsSquareSpinner.getValue());
		settings.setSampleDepth((int) sampleDepthSpinner.getValue());
		return settings;
	}

	private void setInputs(RenderSettings toSet) {
		Enumeration<AbstractButton> buttons = renderTypeButtons.getElements();
		AbstractButton thisButton = buttons.nextElement();
		for (int i = 0; i < toSet.getRenderMethod(); i++) {
			thisButton = buttons.nextElement();
		}
		renderTypeButtons.setSelected(thisButton.getModel(), true);
		smoothedCheckBox.setSelected(toSet.getSmoothed());
		xAxisScaleSpinner.setValue(toSet.getXAxisScale());
		yAxisScaleSpinner.setValue(toSet.getYAxisScale());
		xAxisOffsetSpinner.setValue(toSet.getXAxisOffset());
		yAxisOffsetSpinner.setValue(toSet.getYAxisOffset());
		imagePixelsSquareSpinner.setValue(toSet.getImagePixelsSquare());
		sampleDepthSpinner.setValue(toSet.getSampleDepth());
	}

	private static void saveRenderJAXB(RenderSettings _render) {
		try {
			JAXBContext jaxb = JAXBContext.newInstance(RenderSettings.class);
			Marshaller marsh = jaxb.createMarshaller();
			marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			OutputStream saveFile = new FileOutputStream("renderSave" + ".xml");
			marsh.marshal(_render, saveFile);
		} catch (JAXBException e) {
			System.err.println("Data not saved to XML file. " + e);
		} catch (FileNotFoundException e) {
			System.err.println("BufferedReader failed. " + e);
		}
	}

	private static RenderSettings loadRenderJAXB() {
		try {
			File f = new File("renderSave" + ".xml");
			JAXBContext jaxb = JAXBContext.newInstance(RenderSettings.class);
			Unmarshaller unmarsh = jaxb.createUnmarshaller();
			return (RenderSettings) unmarsh.unmarshal(f);
		} catch (JAXBException e) {
			System.err.println("Data not loaded from XML file. " + e);
		}
		return null;
	}

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

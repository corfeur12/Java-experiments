package fractals.mandelbrotExplorer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageBarGUI {

	private JToolBar toolbar;
	private JLabel mousePositionCurrent;
	private JLabel mousePositionSaved;

	public ImageBarGUI(BufferedImage _imageBuffer) {
		toolbar = new JToolBar("Image settings");
		JButton saveButton = new JButton("Save");
		saveButton.setToolTipText("Saves the full rendered image");
		mousePositionCurrent = new JLabel("Current position: ");
		mousePositionSaved = new JLabel("Saved position: ");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent _event) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Save image");
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setFileFilter(new FileNameExtensionFilter("PNG image", "png"));
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG image", "jpg", "jpeg"));
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("GIF image", "gif"));
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP image", "bmp"));
				int userSelection = fileChooser.showSaveDialog(toolbar);
				if (userSelection == JFileChooser.APPROVE_OPTION) {
					File saveFile = fileChooser.getSelectedFile();
					String selectedDescription = fileChooser.getFileFilter().getDescription().toLowerCase();
					String selectedFileTypeExtension = selectedDescription.substring(0,
							selectedDescription.indexOf(" "));
					if (!saveFile.toString().endsWith("." + selectedFileTypeExtension)) {
						saveFile = new File(saveFile.toString() + "." + selectedFileTypeExtension);
					}
					try {
						ImageIO.write(_imageBuffer, selectedFileTypeExtension, saveFile);
					} catch (IOException _exception) {
						System.err.println("Save failed due to I/O error.");
						_exception.printStackTrace();
					}
				}
			}
		});
		toolbar.add(saveButton);
		JPanel positionPanel = new JPanel();
		positionPanel.setLayout(new GridLayout(0, 1));
		positionPanel.setBorder(null);
		positionPanel.add(mousePositionCurrent);
		positionPanel.add(mousePositionSaved);
		toolbar.add(positionPanel);
		toolbar.setFloatable(false);
	}

	public JToolBar getToolbar() {
		return toolbar;
	}

	public void setMouseCurrentPosition(double _x, double _y) {
		mousePositionCurrent.setText("Current position: (" + _x + ", " + _y + ")");
	}

	public void setMouseSavedPosition(double _x, double _y) {
		mousePositionSaved.setText("Saved position: (" + _x + ", " + _y + ")");
	}

}

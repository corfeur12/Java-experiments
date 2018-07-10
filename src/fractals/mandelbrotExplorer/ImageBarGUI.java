package fractals.mandelbrotExplorer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageBarGUI {

	private JToolBar toolbar;
	private JLabel mousePositionCurrent;
	private JLabel mousePositionSaved;

	public ImageBarGUI(BufferedImage imageBuffer) {
		toolbar = new JToolBar("Image settings");
		JButton saveButton = new JButton("Save image");
		mousePositionCurrent = new JLabel("Current position: ");
		mousePositionSaved = new JLabel("Saved position: ");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Save image");
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
				fileChooser.setAcceptAllFileFilterUsed(true);
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
					if (selectedFileTypeExtension.equals("all")) {
						selectedFileTypeExtension = saveFile.toString()
								.substring(saveFile.toString().lastIndexOf(".") + 1, saveFile.toString().length());
					} else if (!saveFile.toString().endsWith("." + selectedFileTypeExtension)) {
						saveFile = new File(saveFile.toString() + "." + selectedFileTypeExtension);
					}
					try {
						ImageIO.write(imageBuffer, selectedFileTypeExtension, saveFile);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		toolbar.add(saveButton);
		toolbar.add(new JSeparator(SwingConstants.VERTICAL));
		toolbar.add(mousePositionCurrent);
		toolbar.add(mousePositionSaved);
//		toolbar.add(new JSeparator(SwingConstants.VERTICAL));
		toolbar.setFloatable(false);
	}

	public JToolBar getToolbar() {
		return toolbar;
	}

	public void setMouseCurrentPosition(double x, double y) {
		mousePositionCurrent.setText("Current position: (" + x + ", " + y + ")");
	}

	public void setMouseSavedPosition(double x, double y) {
		mousePositionSaved.setText("Saved position: (" + x + ", " + y + ")");
	}

}
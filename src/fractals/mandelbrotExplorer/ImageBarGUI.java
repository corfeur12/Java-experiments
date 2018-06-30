package fractals.mandelbrotExplorer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ImageBarGUI {
	
	private JPanel panel;

	public ImageBarGUI() {
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints normalConstraints = new GridBagConstraints();
		normalConstraints.insets = new Insets(5, 5, 5, 5);
		normalConstraints.anchor = GridBagConstraints.NORTHWEST;
		normalConstraints.weightx = 0;
		JLabel directoryLabel = new JLabel("...");
		JButton directoryButton = new JButton("Directory");
		JLabel nameLabel = new JLabel("File name: ");
		JTextField nameText = new JTextField("Mandelbrot");
		JList<String> typeDropdown = new JList<>(new String[] {".png", ".jpg"});
		JScrollPane typeDropdownScroll = new JScrollPane(typeDropdown);
		typeDropdown.setVisibleRowCount(1);
		JButton saveButton = new JButton("Save");
		JPanel emptySpace = new JPanel();
		emptySpace.setSize(Integer.MAX_VALUE, 1);
		panel.add(directoryButton, normalConstraints);
		panel.add(directoryLabel, normalConstraints);
		panel.add(nameLabel, normalConstraints);
		panel.add(nameText, normalConstraints);
		panel.add(typeDropdownScroll, normalConstraints);
		panel.add(saveButton, normalConstraints);
	}
	
	public JPanel getPanel() {
		return panel;
	}

}

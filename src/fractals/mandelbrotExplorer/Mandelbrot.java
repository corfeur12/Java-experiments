package fractals.mandelbrotExplorer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
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
	private int sampleDepth;
	private BufferedImage imageBuffer;

	public Mandelbrot() {
		Scanner input = new Scanner(System.in);
		System.out.print("Canvas scale: ");
		imagePixelsSquare = Integer.parseInt(input.next().trim());
		System.out.print("Maximum depth pass limit: ");
		sampleDepth = Integer.parseInt(input.next().trim());
		setPreferredSize(new Dimension(imagePixelsSquare, imagePixelsSquare));
		input.close();
		generateImage();
	}
	
	private void generateImage() {
		imageBuffer = new BufferedImage(imagePixelsSquare, imagePixelsSquare, BufferedImage.TYPE_INT_RGB);
		for(int pixelY = 0; pixelY < imagePixelsSquare; pixelY++) {
			for(int pixelX = 0; pixelX < imagePixelsSquare; pixelX ++) {
				Double x0 = (pixelX - imagePixelsSquare / 2.0) / imagePixelsSquare * 4.0;
				Double y0 = (pixelY - imagePixelsSquare / 2.0) / imagePixelsSquare * 4.0;
				Double x = 0.0;
				Double y = 0.0;
				int i;
				for(i = 0; i < sampleDepth; i++) {
					if(x*x + y*y >= 4) {
						break;
					}
					Double tempX = x * x - y * y + x0;
					y = 2 * x * y + y0;
					x = tempX;
				}
				if(i == sampleDepth) {
					imageBuffer.setRGB(pixelX, pixelY, (Color.BLACK).getRGB());
				}
				else {
					imageBuffer.setRGB(pixelX, pixelY, (new Color(Color.HSBtoRGB((float) (i/30.0), 1, 0.5f))).getRGB());
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

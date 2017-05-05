import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.*;
import java.io.File;
import java.awt.*;

public class Render {
		
	public static boolean compressing = true;
	
	public static JProgressBar taskBar, progressBar;
	public static JLabel task, progress, icon;
	public static JFrame cFrame, appframe;
	public static JButton ok, cancel;
	
	public static HashMap <Integer, Integer> map;
	public static Thread animation, compress;
	public static BufferedImage image;
	private static HuffmanTree huff;
	public static int[] pixel2;
	public static int[] pixel;
	public static int index;
	public static int size;
	public static int ind;
	
	public void renderImage(File file, int huffMode) {
		
		compress = new Thread() {
			public void run() {
				try{
					ind = 0;
					size = 0;
					index = 0;
					cFrame.toFront();
					cFrame.repaint();
					task.setText("Loading Image...");
					image = ImageIO.read(file);
					
					map = new HashMap();
					task.setText("Getting height and width of image...");
					pixel = new int[size = image.getWidth() * image.getHeight()];
					pixel2 = new int[image.getWidth() * image.getHeight()];
					updateProgress(9);
					
					taskBar.setMaximum(size);
					for(int i = 0; i < image.getHeight(); i++) {
						for(int j = 0; j < image.getWidth(); j++) {
							int RGBValue = image.getRGB(j, i) & 0xffffff;
							pixel2[ind] = RGBValue;
							
							if(map.containsKey(RGBValue))
								map.put(RGBValue, map.get(RGBValue) + 1);
							else map.put(RGBValue, 1); 
							
							task.setText("Reading Image... Pixel[" + ((i+1)*ind) + "]   :  RGB(" + pixel2[ind] + ")");
							ind++;
							
							if(ind % 1000 == 0) {
								taskBar.setValue(ind);
								Thread.sleep(1);						
							}
							
						}
					}
					updateProgress(15);
					
					huff = new HuffmanTree(size);
					huff.compress(appframe, huffMode);
					
				}catch(IOException e) {
					JOptionPane.showMessageDialog(null, "There has been a problem reading the image.", "Image Exception", JOptionPane.WARNING_MESSAGE);
				}catch(NullPointerException n) {
					JOptionPane.showMessageDialog(null, "File must be image of .PNG format.", "File Exception", JOptionPane.INFORMATION_MESSAGE);
				}catch(InterruptedException ie) {}
			}
		};
		
		animation.start();
		compress.start();
		
	}
	
	public void compress(JFrame appframe) {
		
		if(ImageUtil.hasFile()) {
			
			this.appframe = appframe;
			Object[] options = {"New .HUFF File", "Existing .HUFF File"};
			int choice = JOptionPane.showOptionDialog(null, "Where would you like to train your Huffman Tree?",
													  "Select .HUFF Mode", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
													  null, options, options[0]);
			
			if(choice == 0 || choice == 1) {
				appframe.setEnabled(false);
				progressFrame();
				renderImage(ImageUtil.selectedFile, choice);
			}
			
		}else{
			JOptionPane.showMessageDialog(null, "No image yet to be compressed. Import Image first.", "Select File", JOptionPane.WARNING_MESSAGE);
		}
		
	}
	
	public static BufferedImage getImage() {
		return huff.getCompressedImage();
	}
	
	private void progressFrame() {
		
		ImageIcon img1 = new ImageIcon("images/turn_1.png");
		ImageIcon img2 = new ImageIcon("images/turn_2.png");
		JPanel panel = new JPanel();		
		
		icon = new JLabel(img1);
		ok = new JButton("  OK  ");
		taskBar = new JProgressBar();
		cancel = new JButton("Cancel");
		progressBar = new JProgressBar();
		cFrame = new JFrame("Compressing...");
		task = new JLabel("Starting Compression...");
		progress = new JLabel("Overall Progress Status :                          0 %");

		task.setForeground(Color.WHITE);
		progress.setForeground(Color.WHITE);
		progressBar.setPreferredSize(new Dimension(370,20));
		taskBar.setPreferredSize(new Dimension(370,20));
		taskBar.setMaximum(Render.index-2);
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		taskBar.setMinimum(0);
		
		ok.setFont(Frame.CALIBRI);
		cancel.setFont(Frame.CALIBRI);
		progress.setFont(Frame.CALIBRI);
		task.setFont(Frame.CALIBRI);
		
		ok.setVisible(false);
		cFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.setPreferredSize(new Dimension(390,130));
		panel.setBackground(Frame.THEME);
		
		panel.add(task);
		panel.add(taskBar);
		panel.add(progress);
		panel.add(progressBar);
		cFrame.add(icon);
		cFrame.add(panel);
		cFrame.add(ok);
		cFrame.add(cancel);
		
		cFrame.setSize(420,280);
		cFrame.setUndecorated(true);
		cFrame.setVisible(true);
		cFrame.setLocationRelativeTo(null);
		cFrame.getRootPane().setBorder(Frame.myBorder());
		cFrame.getContentPane().setBackground(Frame.THEME);
		cFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		animation = new Thread() {
			public void run() {
				try{
					while(compressing) {
						icon.setIcon(img2);
						Thread.sleep(500);
						icon.setIcon(img1);
						Thread.sleep(500);
					}
					icon.setIcon(new ImageIcon("images/finished_icon.png"));
				}catch(InterruptedException ie) {}
			}
		};
		
		cancel.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent a) {
					int choice = JOptionPane.showConfirmDialog(null, "   Cancel Compression?", "Abort Notice", JOptionPane.YES_NO_OPTION);
					if(choice == JOptionPane.YES_OPTION) {
						cFrame.dispose();
						appframe.toFront();
						appframe.repaint();
						compressing = false;
						appframe.setEnabled(true);
						JOptionPane.showMessageDialog(null, "     Compression Aborted.", "Terminated", JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		);

		ok.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent a) {
					cFrame.dispose();
					appframe.toFront();
					appframe.repaint();
					compressing = false;
					appframe.setEnabled(true);
				}
			}
		);			
				
	}
	
	public static void updateProgress(int progBarCount) {
		
		try{
			taskBar.setValue(taskBar.getMaximum());
			Thread.sleep(100);
			progressBar.setValue(progBarCount);
			progress.setText("Overall Progress Status :                          " + progBarCount + " %");
			taskBar.setValue(0);
		}catch(InterruptedException ie) {}
		
	}
	
}
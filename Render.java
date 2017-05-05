import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.swing.JFrame;
import java.util.HashMap;
import java.io.File;

public class Render {
	
	public static HashMap <Integer, Integer> map;
	public static BufferedImage image;
	private static HuffmanTree huff;
	public static int[] pixel2;
	public static int[] pixel;
	public static int index;
	public static int size;
	public static int ind;
	
	public static void renderImage(File file, JFrame cFrame) {
			
		try{
			ind = 0;
			size = 0;
			index = 0;
			cFrame.toFront();
			cFrame.repaint();
			huff.task.setText("Loading Image...");
			image = ImageIO.read(file);
			huff.updateProgress(100);
			
			map = new HashMap();
			huff.task.setText("Getting height and width of image...");
			pixel = new int[size = image.getWidth() * image.getHeight()];
			pixel2 = new int[image.getWidth() * image.getHeight()];
			huff.updateProgress(9);
			
			huff.taskBar.setMaximum(size);
			for(int i = 0; i < image.getHeight(); i++) {
				for(int j = 0; j < image.getWidth(); j++) {
					int RGBValue = image.getRGB(j, i) & 0xffffff;
					pixel2[ind] = RGBValue;
					
					if(map.containsKey(RGBValue))
						map.put(RGBValue, map.get(RGBValue) + 1);
					else map.put(RGBValue, 1); 
					
					huff.task.setText("Reading Image... Pixel[" + ((i+1)*ind) + "]   :  RGB(" + pixel2[ind] + ")");
					ind++;
					
					if(ind % 1000 == 0) {
						huff.taskBar.setValue(ind);
						huff.thread.sleep(1);						
					}
					
				}
			}
			huff.updateProgress(15);
			
		}catch(IOException e) {
			JOptionPane.showMessageDialog(null, "There has been a problem reading the image.", "Image Exception", JOptionPane.WARNING_MESSAGE);
		}catch(NullPointerException n) {
			JOptionPane.showMessageDialog(null, "File must be image of .PNG format.", "File Exception", JOptionPane.INFORMATION_MESSAGE);
		}catch(InterruptedException ie) {}
		
	}
	
	public static void compress(JFrame frame) {
		
		if(ImageUtil.hasFile()) {
			huff = new HuffmanTree(10000000);
			huff.compress(frame);			
		}else{
			JOptionPane.showMessageDialog(null, "No image yet to be compressed. Import Image first.", "Select File", JOptionPane.WARNING_MESSAGE);
		}
		
	}
	
	public static BufferedImage getImage() {
		return huff.getCompressedImage();
	}
	
}

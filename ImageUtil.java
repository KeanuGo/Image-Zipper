import javax.swing.filechooser.FileFilter;
import java.awt.image.BufferedImage;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Image;
import java.io.File;

public class ImageUtil extends FileFilter {

	public static boolean compressed;
	public static File selectedFile, newFile;
	public static String sizeType = "0 B";
	private static JFileChooser fc;
	
	public boolean accept(File f) {
		return isPNG(f);
	}
	
	public String getDescription() {
		return "Images(*.PNG)";
	}
	
	public static boolean hasFile() {
		return selectedFile != null;
	}
	
	public static boolean isPNG(File f) {
		return f.getName().toLowerCase().endsWith(".png");
	}
	
	public static void importImage() {
		
		fc = new JFileChooser();
		fc.addChoosableFileFilter(new ImageUtil());
		
		if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			selectedFile = fc.getSelectedFile();
			if(isPNG(selectedFile)) {
				try{
					BufferedImage image = ImageIO.read(selectedFile);
					Image fit = image.getScaledInstance(Frame.imageWindows[0].getWidth(), Frame.imageWindows[0].getHeight(), Image.SCALE_SMOOTH);
					
					Frame.imageWindows[0].setIcon(new ImageIcon(fit));
					Frame.info[0][0].setText(selectedFile.getName());
					Frame.info[0][1].setText(image.getWidth() + " x " + image.getHeight() + " pixels");
					Frame.info[0][2].setText(getFileSize(selectedFile) + sizeType);
				}catch(IOException i) {}
			}else{
				JOptionPane.showMessageDialog(null, "File must be image of .PNG format.", "File Exception", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
	}
	
	public static void saveImage() {
		
		if(compressed) {	
			fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  
			fc.showSaveDialog(null);
			
			try{
				String path = fc.getSelectedFile().getAbsolutePath();
				newFile = new File(path);
				
				if(!isPNG(newFile)) {
					JOptionPane.showMessageDialog(null, "   File Name must be of .PNG extension", "Extension Error", JOptionPane.ERROR_MESSAGE);
					saveImage();
				}
				
				ImageIO.write(Render.getImage(), "png", newFile);
				newFile.createNewFile();
				
				Frame.info[1][0].setText(newFile.getName());
				Frame.info[1][1].setText(Render.getImage().getWidth() + " x " + Render.getImage().getHeight() + " pixels");
				Frame.info[1][2].setText(getFileSize(newFile) + sizeType);
			}catch(Exception e) {}
		}else{
			JOptionPane.showMessageDialog(null, "Image not yet compressed.", "Uncompressed", JOptionPane.WARNING_MESSAGE);
		}
		
	}
	
	public static String getFileSize(File file) {
		
		double size = file.length();
		
		if(size > Math.pow(1024,1) && size < Math.pow(1024,2)) {
			size /= 1024;
			sizeType = " KB";
		}else if(size > Math.pow(1024,2) && size < Math.pow(1024,3)) {
			size /= Math.pow(1024,2);
			sizeType = " MB";
		}else if(size >= 0 && size < Math.pow(1024,1)) {
			sizeType = " B";
		}
		
		return roundOff(size);
		
	}	
	
	private static String roundOff(double evaluatedValue) {
		
		int integerAns = (int) evaluatedValue;
		double doubleAns = evaluatedValue - integerAns;
		
		if(doubleAns == 0.0)
			return String.valueOf(integerAns);
		else{
			String postfix = String.format("%1$.2f", doubleAns);
			return String.valueOf(Double.parseDouble(postfix) + integerAns);
		}
		
	}
	
}

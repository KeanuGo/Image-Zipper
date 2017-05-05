import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.util.Iterator;
import java.util.Scanner;
import java.awt.Image;
import java.io.*;

public class HuffmanTree {

	private InputStreamReader reader;
	private BufferedWriter bw1, bw;
	private FileWriter fw1, fw;
	private BufferedImage bimg;
	private Render render;
	private FileReader fr;
	private File file, f;
	private Scanner scan;

	private String x = "";
	private StringBuilder finCodedBit;
	private String[] codedBit;
	private String[] fi;
	private Node[] nodeStat;
	private Node[] node;
	
	private static int[] test;
	private int[] newCount;
	private int[] chArr;
	private int[] wed;
	
	private int numofChars = 0;
	private int nItems = 0;
	private int count = 1;
	private int ctr = 0;
	private int maxSize;
	private int cout;
	private int ind;
	
	public HuffmanTree(int size) {
		
		bw = null;
		fw = null;
		bw1 = null;
		fw1 = null;
		reader = null;
		
		nItems = 0;
		maxSize = size;
		
		wed = new int[maxSize];
		test = new int[maxSize];
		chArr = new int[maxSize];
		fi = new String[maxSize];
		node = new Node[maxSize];
		newCount =  new int[maxSize];
		nodeStat = new Node[maxSize];
		codedBit = new String[maxSize];
		
	}
	
	public void compress(JFrame appframe, int huffMode) {
			
		render = new Render();
		
		try{
			 
			if(huffMode == 0)						  
				CREATE_HUFF_FILE("Color_Freq.huff", true);
			else if(huffMode == 1)
				CREATE_HUFF_FILE("Color_Freq.huff", false);
			else
				return;
			
			render.updateProgress(48);
			render.taskBar.setMaximum(ctr);
			render.task.setText("GENERATING HUFFMAN TREE...");
			
			int barCount = 0;
			while(ctr > 1) {
				Node l = dequeue(true);
				Node r = dequeue(true);
				enqueue('\0', l.getFrequency() + r.getFrequency(), l, r);
				
				if(ctr % 10 == 0) {
					render.taskBar.setValue(++barCount);
					Thread.sleep(1);							
				}
				ctr++;
			}
			
			render.updateProgress(46);
			preOrderTraversal(node[0], "");
			
			render.task.setText("Appending Corresponding Pixel Code Bits from .HUFF file...");
			render.taskBar.setMaximum(Render.size * numofChars);
			
			finCodedBit = new StringBuilder();
			for(int d = 0; d < Render.size; d++) {
				for(int c = 0; c < numofChars; c++) {
					if(chArr[c] == Render.pixel2[d])
						finCodedBit.append(codedBit[c]);
				}
			}
			
			render.updateProgress(60);
			CREATE_ALGO_FILE("image.algo");
			render.task.setText("Creating image from .ALGO file...Traversing Huffman Tree...");
			
			int h = 0;
			Node de = node[0];
			render.taskBar.setMaximum(fi.length * 7);
			
			barCount = 0;
			for(int j = 0; j < cout; j++) {
				for(int i = 0; i < 7; i++) {
					if(j == cout - 1 && i == finCodedBit.length() + 1) 
						break;
					
					char a = fi[j].charAt(i);
					if(de.isLeaf()) {
						test[h] = de.getItem();
						de = node[0];
						h++;
					}
				
					if(!de.isLeaf()) {
						if(a == '0')
							de = de.getLeftChild();
						else if(a == '1')
							de = de.getRightChild();	
					}
					++barCount;
				}	
				if((barCount * j) % (fi.length * 7) == 0) {
					render.taskBar.setValue(j * barCount);
				}
				++barCount;						
			}
			
			render.updateProgress(80);
			render.task.setText("Getting Image Width and Height...");
			Thread.sleep(200);
			render.taskBar.setValue(render.taskBar.getMaximum()/4);
			bimg = new BufferedImage(Render.image.getWidth(), Render.image.getHeight(), BufferedImage.TYPE_INT_RGB);
			render.updateProgress(98);
			
			render.task.setText("Buffering Image...Creating compressed image...");
			Thread.sleep(200);
			render.taskBar.setValue(render.taskBar.getMaximum()/3);
			getCompressedImage();
			
			render.task.setText("COMPRESSION FINISHED.");
			render.updateProgress(100);
			render.taskBar.setValue(render.taskBar.getMaximum());
			render.compressing = false;
			render.cancel.setVisible(false);
			render.ok.setVisible(true);				
			
		}catch(InterruptedException ie) {}		
		
	}
	
	public BufferedImage getCompressedImage() {
		
		int index = 0;
		for(int i = 0; i < Render.image.getHeight() && render.compressing; i++) {
			for(int j = 0; j < Render.image.getWidth() && render.compressing; j++) {
				bimg.setRGB(j, i, test[index]);
				index++;
			}
			if(i % 4 == 0) {
				Image img = bimg.getScaledInstance(Frame.imageWindows[1].getWidth(), Frame.imageWindows[1].getHeight(), Image.SCALE_SMOOTH);
				Frame.imageWindows[1].setIcon(new ImageIcon(img));
				Frame.imageWindows[1].repaint();
			}else{
				Image img = bimg.getScaledInstance(Frame.imageWindows[1].getWidth(), Frame.imageWindows[1].getHeight(), Image.SCALE_SMOOTH);
				Frame.imageWindows[1].setIcon(new ImageIcon(img));
				Frame.imageWindows[1].repaint();
			}
		}
		return bimg;
		
	}
	
	private void CREATE_HUFF_FILE(String fileName, boolean newHuff) {
		
		int w = 0;
		
		if(!newHuff) {
			try{
				file = new File(fileName);
				fr = new FileReader(file);
				scan = new Scanner(fr);
				
				int x = 0, y = 0;
				while(scan.hasNext()) {
					if(w % 2 == 0) {
						wed[x] = Integer.parseInt(scan.next());
						x++;
					}
					if(w % 2 == 1) {
						newCount[y] = Integer.parseInt(scan.next());
						y++;
					}
					w++;
				}
				
				scan.close();
				fr.close();
			}catch(Exception e){}
		}
		
		try{
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			boolean isThere = false;
			
			render.task.setText("Counting Pixel Color Frequencies...");
			Iterator <Integer> keySetIterator = Render.map.keySet().iterator();
			
			while(keySetIterator.hasNext()) { 
				Integer key = keySetIterator.next();
				chArr[ctr] = key;
				bw.write(key + " " + Render.map.get(key));
				bw.newLine();
				enqueue(key, Render.map.get(key), null, null);
				
				ctr++;
				count = 0;
				numofChars++;
				
				if(ctr % 1000 == 0) {
					render.taskBar.setValue(ctr);
					Thread.sleep(1);
				}
				render.task.setText("Writing Color Distribution Frequency to .HUFF File...");
			}
			
			bw.flush();
			bw.close();
			render.task.setText("Created .HUFF File (Closing BufferedWriter)");
			render.updateProgress(21);
			fw.close();
			render.task.setText("Created .HUFF File (Closing FileWriter)");
			render.updateProgress(23);
			
		}catch(Exception e){}
		
	}
	
	private void CREATE_ALGO_FILE(String fileName) {
		
		try{
			render.task.setText("Creating .ALGO File...");
			Thread.sleep(100);
			
			f = new File(fileName);
			f.createNewFile();
			fw1 = new FileWriter(fileName);
			bw1 = new BufferedWriter(fw1);
			
			int barCount = 0;
			String string1 = "";
			render.taskBar.setMaximum((7 - (finCodedBit.length() % 7)) - 1);
			render.task.setText("Creating .ALGO File (Chunking Code Bits to 7-BIT VALUES)");
			
			for(int i = 0; i < 7 - (finCodedBit.length() % 7); i++) {
				if(finCodedBit.length() % 7 != 0)
					string1 += "0";
				render.taskBar.setValue(++barCount);
				Thread.sleep(1);
			}
			x = finCodedBit + string1;
			
			render.taskBar.setMaximum((x.length()/7) - 1);
			render.task.setText("Writing 7-Bit Code Character Values to .ALGO File...");
			
			for(int j = 0; j < x.length() / 7; j++) {
				bw1.write((char) Integer.parseInt(x.substring((j*7), (j+1)*7), 2));
				if(j % (x.length() / 7) == 0) {
					render.taskBar.setValue(j);
					Thread.sleep(1);
				}
			}
			
			bw1.flush();
			bw1.close();
			render.task.setText("Created .ALGO File (Closing BufferedWriter)");
			render.updateProgress(62);
			fw1.close();
			render.task.setText("Created .ALGO File (Closing FileWriter)");
			render.updateProgress(64);
			
			int sc;
			barCount = 0;
			String p = "";
			
			reader = new InputStreamReader(new FileInputStream(f));
			render.task.setText("Reading chars from .ALGO File...");
			render.taskBar.setMaximum((int) f.length() * (7 - p.length()));
			
			while((sc = reader.read()) != -1) {
				p = Integer.toBinaryString(sc);
				String string2 = "";
				
				for(int i = 0; i < 7 - p.length(); i++) {
					string2 += "0";
				}
				fi[cout] = "";
				fi[cout++] = string2 + p;
				
				if(barCount % 1000 == 0) {
					render.taskBar.setValue(++barCount * (7-p.length()));
					Thread.sleep(1);
				}
			}
			
			reader.close();
			render.updateProgress(75);
			
		}catch(Exception e) {}
		
	}	
	
	public void preOrderTraversal(Node n, String coded) {
		
		if(n != null) {
			if(n.isLeaf()) {
				for(int b = 0; b < numofChars; b++) {
					if(chArr[b] == n.getItem()) {
						codedBit[b] = coded;
						break;
					}
				}
			}else if(!n.isLeaf()) {
				try{
					preOrderTraversal(n.getLeftChild(), coded + "0");
					preOrderTraversal(n.getRightChild(), coded + "1");
				}catch(NullPointerException f) {}
			}
		}
		
	}
	
	public void enqueue(int item, int frequency, Node left, Node right) {
		
		int j;
		
		if(nItems == 0) {
			node[nItems++] = new Node(item, frequency, left, right);
		}else{
			for(j = nItems-1; j >= 0; j--) {
				if(frequency > node[j].getFrequency())
					node[j+1] = node[j];
				else if((frequency == node[j].getFrequency()) && (item > node[j].getItem()))
					node[j+1] = node[j];
				else break;
			}
			node[j+1] = new Node(item, frequency, left, right);
			nItems++;
		}
		
	}
	
	public Node dequeue(boolean isString) {
		
		ctr--;
		if(isString) 
			return node[--nItems];
		else return nodeStat[--nItems];
		
	}
	
	public int getLevel() {
		
		int lvl = codedBit[0].length();
		
		for(int c = 1; c < numofChars; c++) {
			if(codedBit[c].length() > lvl) 
				lvl = codedBit[c].length();
		}
		return lvl;
		
	}
	
}

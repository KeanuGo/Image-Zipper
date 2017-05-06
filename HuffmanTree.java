import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.util.Scanner;
import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.io.*;

public class HuffmanTree {
	
	public static JProgressBar taskBar, progressBar;
	public static JLabel task, progress, icon;
	private JButton ok, cancel;
	private JFrame cf;
	
	public static Thread thread, animation;
	private InputStreamReader reader;
	private BufferedWriter bw1, bw;
	private FileWriter fw1, fw;
	private BufferedImage bimg;
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
	private int ctr = 0;
	private int maxSize;
	private int cout;
	
	private boolean compressing = true;
	
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
		ImageUtil.compressed = false;
		
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
		cf = new JFrame("Compressing...");
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
		cf.setLayout(new FlowLayout(FlowLayout.CENTER));
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.setPreferredSize(new Dimension(390,130));
		panel.setBackground(Frame.THEME);
		
		panel.add(task);
		panel.add(taskBar);
		panel.add(progress);
		panel.add(progressBar);
		cf.add(icon);
		cf.add(panel);
		cf.add(ok);
		cf.add(cancel);
		
		cf.setSize(420,280);
		cf.setUndecorated(true);
		cf.setVisible(true);
		cf.setLocationRelativeTo(null);
		cf.getRootPane().setBorder(Frame.myBorder());
		cf.getContentPane().setBackground(Frame.THEME);
		cf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
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
	
	public void compress(JFrame appframe) {
			
		Object[] options = {"New .HUFF File", "Existing .HUFF File"};
		int choice = JOptionPane.showOptionDialog(null, "Where would you like to train your Huffman Tree?",
												  "Select .HUFF Mode", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
												  null, options, options[0]);

		if(choice == 0 || choice == 1) {
			appframe.setEnabled(false);
			progressFrame();
			animation.start();
		
			thread = new Thread() {
				public void run() {
					try{
						Render.renderImage(ImageUtil.selectedFile, cf);
						 
						if(choice == 0)						  
							CREATE_HUFF_FILE("Color_Freq.huff", true);
						else if(choice == 1)
							CREATE_HUFF_FILE("Color_Freq.huff", false);
						else if(choice == -1)
							return;
						
						updateProgress(48);
						taskBar.setMaximum(ctr);
						task.setText("GENERATING HUFFMAN TREE...");
						
						int barCount = 0;
						while(ctr > 1) {
							Node l = dequeue(true);
							Node r = dequeue(true);
							enqueue('\0', l.getFrequency() + r.getFrequency(), l, r);
							
							if(ctr % 10 == 0) {
								taskBar.setValue(++barCount);
								Thread.sleep(1);							
							}
							ctr++;
						}
						
						updateProgress(46);
						preOrderTraversal(node[0], "");
						
						task.setText("Appending Corresponding Pixel Code Bits from .HUFF file...");
						taskBar.setMaximum(Render.size * numofChars);
						
						taskBar.setMaximum(Render.size);
						finCodedBit = new StringBuilder();
						for(int d = 0; d < Render.size; d++) {
							for(int c = 0; c < numofChars; c++) {
								if(chArr[c] == Render.pixel2[d])
									finCodedBit.append(codedBit[c]);
								if(d == Render.size / 2)
									taskBar.setValue(Render.size / 2);
							}
						}
						
						updateProgress(60);
						CREATE_ALGO_FILE("image.algo");
						task.setText("Creating image from .ALGO file...Traversing Huffman Tree...");
						
						int h = 0;
						Node de = node[0];
						taskBar.setMaximum(fi.length * 7);
						
						barCount = 0;
						for(int j = 0; j < cout; j++) {
							for(int i = 0; i < 7; i++) {
								if(j == cout - 1 && i == finCodedBit.length() % 7 + 1) 
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
								taskBar.setValue(j * barCount);
							}
							++barCount;						
						}
						
						updateProgress(80);
						task.setText("Getting Image Width and Height...");
						bimg = new BufferedImage(Render.image.getWidth(), Render.image.getHeight(), BufferedImage.TYPE_INT_RGB);
						updateProgress(98);
						
						task.setText("Buffering Image...Creating compressed image...");
						getCompressedImage();
						
						task.setText("COMPRESSION FINISHED.");
						updateProgress(100);
						taskBar.setValue(taskBar.getMaximum());
						
						compressing = false;
						cancel.setVisible(false);
						ok.setVisible(true);					
						
						ok.addActionListener(
							new ActionListener() {
								public void actionPerformed(ActionEvent a) {
									cf.dispose();
									appframe.toFront();
									appframe.repaint();
									compressing = false;
									appframe.setEnabled(true);
									ImageUtil.compressed = true;
								}
							}
						);	
						
					}catch(InterruptedException ie) {}
				}   // run
			};  // thread
			
			thread.start();
			
			cancel.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent a) {
						int choice = JOptionPane.showConfirmDialog(null, "   Cancel Compression?", "Abort Notice", JOptionPane.YES_NO_OPTION);
						if(choice == JOptionPane.YES_OPTION) {
							cf.dispose();
							appframe.toFront();
							appframe.repaint();
							compressing = false;
							appframe.setEnabled(true);
							JOptionPane.showMessageDialog(null, "     Compression Aborted.", "Terminated", JOptionPane.WARNING_MESSAGE);
						}
					}
				}
			);			
		}
		
	}
	
	public BufferedImage getCompressedImage() {
		
		int index = 0, interval = 5;
		taskBar.setMaximum(Render.image.getHeight());
		
		for(int i = 0; i < Render.image.getHeight() && compressing; i++) {
			for(int j = 0; j < Render.image.getWidth() && compressing; j++) {
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
			if(i == Render.image.getHeight() / interval)
				taskBar.setValue(taskBar.getMaximum() / interval--);
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

			task.setText("Writing Color Distribution Frequency to .HUFF File...");
			Iterator <Integer> keySetIterator = Render.map.keySet().iterator();
			
			while(keySetIterator.hasNext()) { 
				Integer key = keySetIterator.next();
				chArr[ctr] = key;
				bw.write(key + " " + Render.map.get(key));
				bw.newLine();
				enqueue(key, Render.map.get(key), null, null);
				
				ctr++;
				numofChars++;
				
				if(ctr % 1000 == 0)
					taskBar.setValue(ctr);
			}
			
			bw.flush();
			bw.close();
			task.setText("Created .HUFF File (Closing BufferedWriter)");
			updateProgress(21);
			fw.close();
			task.setText("Created .HUFF File (Closing FileWriter)");
			updateProgress(23);
			
		}catch(Exception e){}
		
	}
	
	private void CREATE_ALGO_FILE(String fileName) {
		
		try{
			task.setText("Creating .ALGO File...");
			Thread.sleep(100);
			
			f = new File(fileName);
			f.createNewFile();
			fw1 = new FileWriter(fileName);
			bw1 = new BufferedWriter(fw1);
			
			int barCount = 0;
			String string1 = "";
			taskBar.setMaximum((7 - (finCodedBit.length() % 7)) - 1);
			task.setText("Creating .ALGO File (Chunking Code Bits to 7-BIT VALUES)");
			
			for(int i = 0; i < 7 - (finCodedBit.length() % 7); i++) {
				if(finCodedBit.length() % 7 != 0)
					string1 += "0";
				taskBar.setValue(++barCount);
				Thread.sleep(1);
			}
			x = finCodedBit + string1;
			
			taskBar.setMaximum((x.length()/7) - 1);
			task.setText("Writing 7-Bit Code Character Values to .ALGO File...");
			
			for(int j = 0; j < x.length() / 7; j++) {
				bw1.write((char) Integer.parseInt(x.substring((j*7), (j+1)*7), 2));
				if(j % (x.length() / 7) == 0) {
					taskBar.setValue(j);
					Thread.sleep(1);
				}
			}
			
			bw1.flush();
			bw1.close();
			task.setText("Created .ALGO File (Closing BufferedWriter)");
			updateProgress(62);
			fw1.close();
			task.setText("Created .ALGO File (Closing FileWriter)");
			updateProgress(64);
			
			int sc;
			barCount = 0;
			String p = "";
			
			reader = new InputStreamReader(new FileInputStream(f));
			task.setText("Reading chars from .ALGO File...");
			taskBar.setMaximum((int) f.length() * (7 - p.length()));
			
			while((sc = reader.read()) != -1) {
				p = Integer.toBinaryString(sc);
				String string2 = "";
				
				for(int i = 0; i < 7 - p.length(); i++) {
					string2 += "0";
				}
				fi[cout] = "";
				fi[cout++] = string2 + p;
				
				if(barCount % 1000 == 0) {
					taskBar.setValue(++barCount * (7-p.length()));
					Thread.sleep(1);
				}
			}
			
			reader.close();
			updateProgress(75);
			
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

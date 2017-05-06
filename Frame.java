import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")

public class Frame extends JFrame implements MenuListener {

	public static final Font CONSOLAS = new Font("Consolas", Font.BOLD, 15);
	public static final Font CALIBRI = new Font("Calibri", Font.BOLD, 15);
	public static final Color TRANSPARENT_GRAY = new Color(20,20,20,150);
	public static final Color THEME = new Color(40,40,40);
	
	public static JTextField[][] info = new JTextField[2][3];
	public static JLabel[] imageWindows = new JLabel[2];
	private JPanel[] infoPanel = new JPanel[2];
	private JMenu[] menus = new JMenu[3];
	private JMenuBar menuBar;

	private final Insets insets = this.getInsets();
	
	public Frame() {
		
		super("Image Zipper");
		setLayout(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage("images/app_icon.png"));
		setContentPane(new JLabel(new ImageIcon("images/gray.jpg")));
		setComponents();
		setVisible(true);
		setResizable(false);
		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}	
	
	public void cleanComponents() {

		ImageUtil.selectedFile = null;
		for(int x = 0; x < 2; x++) {
			remove(imageWindows[x]);
			remove(infoPanel[x]);
		}
		
		menuBar.setVisible(false);
		repaint();
		setComponents();
		
	}
	
	private void setComponents() {
		
		UIManager.put("OptionPane.messageFont", CALIBRI);
		JLabel[][] infoType = new JLabel[2][3];
		JPanel[][] infoGrid = new JPanel[2][3];
		int position = 105;
		
		for(int x = 0; x < 2; x++) {
			imageWindows[x] = new JLabel();
			imageWindows[x].setBackground(TRANSPARENT_GRAY);
			imageWindows[x].setBorder(myBorder());
			imageWindows[x].setOpaque(true);

			infoPanel[x] = new JPanel();
			infoPanel[x].setBackground(TRANSPARENT_GRAY);
			infoPanel[x].setLayout(new FlowLayout(FlowLayout.CENTER));
			infoPanel[x].setBounds(position + insets.left, 530 + insets.top, 500, 130);
			
			String[] type = {"  FILE NAME : ", "  DIMENSION : ", "    SIZE    : "};
			
			for(int y = 0; y < 3; y++) {
				info[x][y] = new JTextField(35);
				info[x][y].setHorizontalAlignment(SwingConstants.CENTER);
				info[x][y].setBackground(Color.BLACK);
				info[x][y].setForeground(Color.WHITE);
				info[x][y].setEditable(false);
				info[x][y].setFont(CONSOLAS);
				
				infoType[x][y] = new JLabel(type[y]);
				infoType[x][y].setForeground(Color.WHITE);
				infoType[x][y].setFont(CONSOLAS);
								
				infoGrid[x][y] = new JPanel();
				infoGrid[x][y].setBackground(new Color(0,0,0,0));
				infoGrid[x][y].setLayout(new FlowLayout(FlowLayout.LEFT));
				infoGrid[x][y].add(infoType[x][y]);
				infoGrid[x][y].add(info[x][y]);
				
				infoPanel[x].add(infoGrid[x][y]);
			}
			add(imageWindows[x]);
			add(infoPanel[x]);
			position += 645;
		}
		
		imageWindows[0].setBounds(80 + insets.left, 55 + insets.top, 550, 460);
		imageWindows[1].setBounds(720 + insets.left, 55 + insets.top, 550, 460);
		
		String[] menuLabels = {"images/open.jpg", "images/save.png", "images/compress.jpg"};
		String[] menuTexts = {"Import  Alt + F", "Save  Alt + S", "Compress  Alt + C"};
		char[] shortcuts = {'F', 'S', 'C'};
		
		menuBar = new JMenuBar();
		for(int a = 0; a < 3; a++) {			
			menus[a] = new JMenu();
			menus[a].setIcon(new ImageIcon(menuLabels[a]));
			menus[a].setToolTipText(menuTexts[a]);
			menus[a].setMnemonic(shortcuts[a]);
			menus[a].addMenuListener(this);
			menus[a].setFont(CALIBRI);
			menuBar.add(menus[a]);
		}
		setJMenuBar(menuBar);
		
	}
	
	public static Border myBorder() {
		
		Border line1, line2, compound;
		line1 = BorderFactory.createLineBorder(new Color(80,80,80), 4);
		line2 = BorderFactory.createLineBorder(Color.BLACK, 7);
		compound = BorderFactory.createCompoundBorder(line1, line2);
		return compound;
		
	}	
	
	public void menuSelected(MenuEvent m) {
		
		if(m.getSource() == menus[0])  {
			
			try{
				cleanComponents();
				ImageUtil.importImage();
				if(ImageUtil.isPNG(ImageUtil.selectedFile))
					setTitle(ImageUtil.selectedFile.getCanonicalPath() + " - Image Zipper");
			}catch(Exception ex) {}
			
		}else if(m.getSource() == menus[1]) {
			
			try{
				if(ImageUtil.hasFile())
					ImageUtil.saveImage();
				else
					JOptionPane.showMessageDialog(null, "There is no image to be saved.", "Import Image First", JOptionPane.WARNING_MESSAGE);
			}catch(NullPointerException np) {
				JOptionPane.showMessageDialog(null, "     Save image cancelled.", "Cancelled", JOptionPane.WARNING_MESSAGE);
			}
			
		}else if(m.getSource() == menus[2]) {
			Render.compress(this);
		}
		
	}
	
	public void menuDeselected(MenuEvent m) {}
	public void menuCanceled(MenuEvent m) {}
	
	public static void main(String[] args) {
		
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new Frame();
		}catch(Exception e){}		
		
	}

}

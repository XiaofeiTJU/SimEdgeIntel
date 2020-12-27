package cn.edu.tju.simulation.swing.map;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import cn.edu.tju.simulation.context.Context;
import cn.edu.tju.simulation.swing.operator.Operator;
import cn.edu.tju.simulation.swing.operator.Signal;
import cn.edu.tju.simulation.IF.devices.MobilityModel;
import cn.edu.tju.simulation.IF.edgenode.BaseStation;
import cn.edu.tju.simulation.IF.edgenode.WirelessNetwork;

/**
 * Canvas
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 * 
 */
@SuppressWarnings("serial")
public class MapCanvas extends JPanel implements MouseWheelListener {
	/**
	 * Context
	 */
	private Context context;
	/**
	 * Base station icon
	 */
	private File BSFile;
	/**
	 * Base station schematic
	 */
	private Image image;
	/**
	 * Right-click menu
	 */
	private JPopupMenu popupMenu;
	/**
	 * Right-click menu options - delete
	 */
	private JMenuItem delete;
	/**
	 * Right-click menu options - configuration
	 */
    private JMenuItem configure;
    /**
     * Right-click menu options - Clear Canvas
     */
    private JMenuItem clear;
    /**
     * Selected base station
     */
    private WirelessNetwork selectedNetwork;


    public MapCanvas() {
		this.setPreferredSize(new Dimension(1000, 1000));
		this.setBackground(Color.white);
		initial();
		addListener();
		//��ͼ����
		new Thread(new PaintThread()).start();
		
	}

	/**
	 * Draw on the canvas
	 */
	public void paint(Graphics g) {
		// Call the parent class function to complete initialization
		super.paint(g);
		if(context !=null && context !=null){
			//Draw base station
			for (int i = 0; i < context.getWirelessNetworkGroup().getBSAmount(); i++) {
				WirelessNetwork network = context.getWirelessNetworkGroup().BS.getNetwork(i);
				int x = (int) network.getLocation().getX();
				int y = (int) network.getLocation().getY();
				int radius = network.getRadius();
				// Draw a circle
				g.drawOval(x - (radius), (y - radius), radius*2,radius*2);
				// Draw a Base station schematic
				g.drawImage(image, x - (image.getWidth(this) / 2),y - (image.getHeight(this) / 2), this);

			}
			
			//Draw the devices
			for(int j = 0; j< context.getUsers().getSimpleUsersAmount(); j++){
				MobilityModel user = context.getUsers().getSimpleUsers().get(j);
				int x = (int)(user.getLocation().getX());
				int y = (int)(user.getLocation().getY());
				g.fillOval(x, y, 2, 2);
			}
		}

	}

	/**
	 * Add keyboard and mouse events
	 */
	public void addListener() {
		MouseAction listener = new MouseAction();
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		this.addMouseWheelListener(this);
		this.delete.addMouseListener(listener);
		this.clear.addMouseListener(listener);
		this.configure.addMouseListener(listener);
	}

	/**
	 * Get the mouse button event
	 * @author ���Ŀ�
	 * 
	 */
	private class MouseAction extends MouseAdapter {
		@SuppressWarnings("deprecation")
		public void mousePressed(MouseEvent e) { // Press the left mouse button
			if(e.getButton() == MouseEvent.BUTTON1){
				if (Signal.Button_BS_Click) {
					String radiusText = context.getOperationPanel().getRadiusText().getText().trim();
					String cacheSizeText = context.getOperationPanel().getCacheSizeText().getText().trim();
					if(!radiusText.equals("") && !cacheSizeText.equals("") &&radiusText!=null&&cacheSizeText !=null){
						int radius = Integer.parseInt(radiusText);
						int cacheSize = (int)(Float.parseFloat(cacheSizeText)*1024*1024);
						
						System.out.println(cacheSize);
						
						context.getWirelessNetworkGroup().BS.addWirelessNetwork(new BaseStation(new Point2D.Double(e.getX(), e.getY()),true,cacheSize,radius));
						context.appendLog(null,"Add base station��Coordinates("+e.getX()+","+e.getY()+")  Cache size "+ context.getOperationPanel().getCacheSizeText().getText(),null);
					}else{
						JOptionPane.showMessageDialog(null, "Radius or Cache size can not be empty", "Prompt", JOptionPane.ERROR_MESSAGE);
					}
				}else if(e.getSource().equals(clear)){
					context.clearAll();
				}else if(e.getSource().equals(delete) && selectedNetwork !=null){
		
					context.getWirelessNetworkGroup().BS.removeNetwork(selectedNetwork);
					selectedNetwork = null;

				}else if(e.getSource().equals(configure) && selectedNetwork !=null){
					NetworkAdjust wirelessNetworkAdjust = new NetworkAdjust(selectedNetwork);
					wirelessNetworkAdjust.setNetworkAdjust(wirelessNetworkAdjust);
				}
			}
			if(e.getButton() == MouseEvent.BUTTON3){
				JFrame jframe = (JFrame) getRootPane().getParent();
				if(Signal.Button_BS_Click){
					jframe.setCursor(Cursor.DEFAULT_CURSOR);
					context.getOperationPanel().getAddBS().setText(Operator.addBSName);
					Signal.Button_BS_Click = false;
				}else{
					Iterator<WirelessNetwork> it = context.getWirelessNetworkGroup().BS.getIterator();
					double temp = Double.POSITIVE_INFINITY;
					selectedNetwork = null;
					while(it.hasNext()){
						WirelessNetwork network = it.next();
						double x = network.getLocation().getX();
						double y = network.getLocation().getY();

						double distance  = Math.sqrt(Math.pow((e.getX() - x), 2) + Math.pow((e.getY() - y), 2));
						if(distance < temp && distance <= network.getRadius()){
							temp = distance;
							selectedNetwork = network;
						}
					}
					if(selectedNetwork != null){
						popupMenu.removeAll();
						popupMenu.add(delete);
						popupMenu.add(configure);
				        popupMenu.show(e.getComponent(),e.getX(),e.getY());
//				        System.out.println("�����ʱ"+wirelessNetwork.getNumber());
					}else{
						popupMenu.removeAll();
						popupMenu.add(clear);
				        popupMenu.show(e.getComponent(),e.getX(),e.getY());
					}	
				}
			}
		}

		public void mouseEntered(MouseEvent e) { // Mouse to enter
			if(Signal.Button_BS_Click){
				JFrame jframe = (JFrame) getRootPane().getParent();
				jframe.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
						new ImageIcon("src/main/resources/images/BS.png").getImage(),
						new Point(10, 20), "stick"));
				
			}
		}

		@SuppressWarnings("deprecation")
		public void mouseExited(MouseEvent e) { // Mouse to exit
			JFrame jframe = (JFrame) getRootPane().getParent();
			jframe.setCursor(Cursor.DEFAULT_CURSOR);
		}

		public void mouseMoved(MouseEvent e) { // Mouse move
			setToolTipText("(" + e.getX() + "," + e.getY() + ")");
		}
	}

	/**
	 * Mouse wheel scroll event
	 * @author ���Ŀ�
	 * 
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {

	}

	public void initial() {
		this.context = Context.getInstance();
		
		this.BSFile = new File("src/main/resources/images/BS.png");
		popupMenu = new JPopupMenu();  //Pop-up menu
		delete = new JMenuItem("Delete");
        configure = new JMenuItem("Configure");
        clear = new JMenuItem("Clear");
        this.selectedNetwork = null;
		
		// Draw base station
		try {
//			image = ImageIO.read(BSFile).getScaledInstance(25, 55, 1);
			image = ImageIO.read(BSFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Drawing thread
	 */
	private class PaintThread implements Runnable {
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public File getBSFile() {
		return BSFile;
	}

	public void setBSFile(File BSFile) {
		this.BSFile = BSFile;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
}

package cn.edu.tju.simulation.swing.map;

import javax.swing.JScrollPane;

import cn.edu.tju.simulation.context.Context;

/**
 * Drawing container (contains canvas)
 * 
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 * 
 */
@SuppressWarnings("serial")
public class Map extends JScrollPane {
	/**
	 * Canvas
	 */
	private MapCanvas mapCanvas;
	/**
	 * Context
	 */
	private Context context;

	public Map() {
		// Set the horizontal and vertical scroll bar are always appear
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Initial();
	}

	public void Initial() {
		mapCanvas = new MapCanvas();
		this.setViewportView(mapCanvas);
	}

	public MapCanvas getMapCanvas() {
		return mapCanvas;
	}

	public void setMapCanvas(MapCanvas mapCanvas) {
		this.mapCanvas = mapCanvas;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
		mapCanvas.setContext(context);
	}
	
}

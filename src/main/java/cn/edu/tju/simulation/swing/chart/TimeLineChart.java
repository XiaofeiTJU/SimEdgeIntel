package cn.edu.tju.simulation.swing.chart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cn.edu.tju.simulation.context.Context;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cn.edu.tju.simulation.data.ResultData;

/**
 * 
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 *
 */
public class TimeLineChart extends BaseLineChart{
	private static final double XAIS_SPACE = 5;
	private static final double YAIS_SPACE = 0.05; 
	private static final double YAIS_MIN_VALUE = 0;
	private static final double YAIS_MAX_VALUE = 0.5; 

	public TimeLineChart() {
		super();
	}
	
	public void draw() {
		XYSeriesCollection collection = new XYSeriesCollection();
		HashMap<String,LinkedList<ResultData>> dataMap = Context.getInstance().getResultDataList();
		Iterator<String> it = Context.getInstance().getResultDataList().keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			XYSeries series = new XYSeries(key);
			List<ResultData> resultDataList = dataMap.get(key);
			series.add(0,0);
			for (ResultData resultData2 : resultDataList) {
				series.add(resultData2.getTimeSlice()+1, resultData2.getHitRate());
			}
			collection.addSeries(series);
		}
		
		
		@SuppressWarnings("unchecked")
		List <XYSeries> list = collection.getSeries();
		XYSeriesCollection collect = new XYSeriesCollection();
		
		for(int i = 0 ;i<list.size();i++){
			if(list.get(i).getKey().equals("KNAPSACK")){
				System.out.print("������ƽ����������:" );
				int size = list.get(i).getItemCount();
				float sum = 0;
				for(int j = 0 ; j <size;j++){
					sum +=list.get(i).getY(j).floatValue();
				}
				System.out.println(sum/size);	
				collect.addSeries(list.get(i));
				break;
			}
		}
		for(int i = 0 ;i<list.size();i++){
			if(list.get(i).getKey().equals("GREEDY")){
				System.out.print("̰�ĵ�ƽ����������:" );
					int size = list.get(i).getItemCount();
					float sum = 0;
					for(int j = 0 ; j <size;j++){
						sum +=list.get(i).getY(j).floatValue();
					}
					System.out.println(sum/size);	
				collect.addSeries(list.get(i));
				break;
		}
		}
		for(int i = 0 ;i<list.size();i++){
			if(list.get(i).getKey().equals("LRU")){
				System.out.print("LRU��ƽ����������:" );
					int size = list.get(i).getItemCount();
					float sum = 0;
					for(int j = 0 ; j <size;j++){
						sum +=list.get(i).getY(j).floatValue();
					}
					System.out.print(sum/size);	
				collect.addSeries(list.get(i));
				break;
			}
		}for(int i = 0 ;i<list.size();i++){
			if(list.get(i).getKey().equals("LFU")){
				System.out.print("LFU��ƽ����������:" );
					int size = list.get(i).getItemCount();
					float sum = 0;
					for(int j = 0 ; j <size;j++){
						sum +=list.get(i).getY(j).floatValue();
					}
					System.out.println(sum/size);	
				collect.addSeries(list.get(i));
				break;
			}
		}	
		
		generateLineChart("Time Slice","Hit Rate",new NumberTickUnit(XAIS_SPACE), new NumberTickUnit(YAIS_SPACE),YAIS_MIN_VALUE, YAIS_MAX_VALUE, collect);
	}
	
}

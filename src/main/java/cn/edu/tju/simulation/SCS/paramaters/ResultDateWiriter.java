package cn.edu.tju.simulation.SCS.paramaters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import cn.edu.tju.simulation.context.Context;
import cn.edu.tju.simulation.data.ResultData;

/**
 * 
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 *
 */
public class ResultDateWiriter {
	
	public void write(){
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File("src/main/resources/data/ResultData"));
			bw = new BufferedWriter(fw);
			HashMap<String,LinkedList<ResultData>> dataMap = Context.getInstance().getResultDataList();
			Iterator<String> it = dataMap.keySet().iterator();
			while(it.hasNext()){
				String algorithm = it.next();
				bw.write("Algorithm:" + algorithm+"\r\n");
				
				Iterator<ResultData> mIt = dataMap.get(algorithm).iterator();
				int i = 0;
				while(mIt.hasNext()){
					i++;
					ResultData resultData = mIt.next();
					bw.write(resultData.getTimeSlice()+"," + resultData.getHitRate()+","+ resultData.getLatency()+","+ resultData.getSaveTraffic()+" ");
					if(i % 8 == 0){
						bw.write("\r\n");
					}
				}
				bw.write("\r\n");
			}			
			bw.flush();
			fw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{		
			try {
				if(fw != null){
					fw.close();
				}
				if(bw != null){
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

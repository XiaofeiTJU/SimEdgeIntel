package cn.edu.tju.simulation.SCS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import cn.edu.tju.simulation.context.Context;
import cn.edu.tju.simulation.data.ResultData;
import org.apache.log4j.Logger;

import cn.edu.tju.simulation.algorithm.OneTimeAlgorithm;
import cn.edu.tju.simulation.algorithm.RealTimeAlgorithm;
import cn.edu.tju.simulation.IF.files.SingleLocalHobby;
import cn.edu.tju.simulation.SCS.state.State;
import cn.edu.tju.simulation.tool.ToolKit;
import cn.edu.tju.simulation.IF.devices.MobilityModel;
import cn.edu.tju.simulation.IF.edgenode.SameTypeWirelessNetwork;
import cn.edu.tju.simulation.IF.edgenode.WirelessNetwork;

/**
 * The class that handles devices requests, with the state flow as the main line
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 */
public class RequestHandler {
	/**
	 * Iterators of state list
	 */
	public Iterator<State> stateIterator;
	/**
	 * Iterator for wireless network collection
	 */
	public Iterator<WirelessNetwork> wirelessNetworkIterator;
	public static HashMap<String,LinkedList<mydata>> ndMap;
	/**
	 * Log
	 */
	private static Logger logger = Logger.getLogger(RequestHandler.class);
	
	/**
	 * Context
	 */
	private Context context;
	
	private SameTypeWirelessNetwork BSs ;
	
	public RequestHandler(){
		this.context = Context.getInstance();
		this.BSs = context.getWirelessNetworkGroup().BS;
		this.ndMap = new HashMap<String, LinkedList<mydata>>();
	}
	
	public class mydata{
		public float hitrate;
		public int hitamount;
		
		public mydata(float hitrate,int hitamount){
			this.hitamount = hitamount;
			this.hitrate = hitrate;
		}
	}
	
	public void processRequest(Object algorithm,String algorithmName, int maxTimes){
		for (int i = 0; i < maxTimes; i++) {
			ToolKit.printCache(context);
			context.appendLog("debug","Concentrate on the request...",logger);
			if(OneTimeAlgorithm.class.isAssignableFrom(algorithm.getClass())){
				context.appendLog("debug","-------------------------------------------------------------------------------------" + algorithmName + " Algorithm-----------------------------------------------------------------------------------------------------------------------------------", null);
				((OneTimeAlgorithm)algorithm).setCache();
			}
			//画图用的数据类
			ResultData newResultData = initResultData(algorithmName, i);
			addLocalHobby(context.getStateQueue().getStateListIterator(i));
			//一共有多少个状态
			Iterator<State> it  = context.getStateQueue().getStateListIterator(i);
			while(it.hasNext()){
				State state = it.next();
				SingleLocalHobby requestedContent = state.getRequestSingleContent();
				MobilityModel user = state.getUser();
				WirelessNetwork network = user.getWirelessNetwork();
				
				//add latency , this latency is needed by all state
				newResultData.addLatency(state.getLatency());

				
				if(user.query(requestedContent)){
					context.appendLog(null,i+" User "+state.getUser().getID()+" in the network "+network.getNumber()+" request files is "+state.getRequestSingleContent().getName()+"----Hit!",null);
					network.addHitAmount();			
	        		newResultData.addTraffic(requestedContent.getSize());
				}else if(network.getRelationalWirelessNetwork().getNetworkList().size() != 0){
			    	//view relational network
			    	Set<WirelessNetwork> relationalNetwork = network.getRelationalWirelessNetwork().getNetworkList();
			    	for (WirelessNetwork wirelessnetwork : relationalNetwork) {
			    		//associated base stations are also not
			        	if(!wirelessnetwork.query(requestedContent)){
			        		context.appendLog(null,i+ " View the associated base station "+wirelessnetwork.getNumber()+" No Hit！",null);
			        		network.addRequestAmount();
			        		//add traffic and latency 
			        		newResultData.addLatency(10);
							
			        	}else{
			        	//associated base stations have
			        		context.appendLog(null,i+ " View the associated base station "+wirelessnetwork.getNumber()+" Hit！",null);
							network.addHitAmount();
			        		newResultData.addTraffic(requestedContent.getSize());
							break;
			        	}
			    	}
				}else{
					context.appendLog(null,i+" User "+state.getUser().getID()+" in the network "+network.getNumber()+" request files is "+state.getRequestSingleContent().getName()+"----No Hit!",null);
	        		network.addRequestAmount();
	        		
	        		//add traffic and latency 
	        		newResultData.addLatency(10);
				}
				if(RealTimeAlgorithm.class.isAssignableFrom(algorithm.getClass())){
					((RealTimeAlgorithm)algorithm).setCache(network, state.getRequestSingleContent());
				}
			}
			newResultData.setLatency(newResultData.getLatency()/ context.getStateQueue().getStateList(i).size());
			printResult(algorithmName, newResultData);
			
		}
	}	
	
	public ResultData initResultData(String algorithm, int times){
		ResultData resultData = new ResultData(times);
		if(times ==0){
			LinkedList<ResultData> resultDataList = new LinkedList<ResultData>();
			context.getResultDataList().put(algorithm, resultDataList);
		}
		for(int j =0;j<BSs.getAmount();j++){
			WirelessNetwork network = BSs.getNetwork(j);
			network.resetAmountOfRequestAndHits();
			network.getContent().sortByHobby();
		}
		
		return resultData;
	}

	public void printResult(String algorithm, ResultData resultData){
		int amount = 0;
		int hitCount =0;
		//Calculate the hit rate
		for(int i =0;i<BSs.getAmount();i++){
			WirelessNetwork network = BSs.getNetwork(i);
			hitCount = hitCount+network.getHitAmount();
			amount = amount + network.getRequestAmount();
			context.appendLog(null,"Network "+network.getNumber()+" requests and hits   "+network.getRequestAmount()+" and "+network.getHitAmount(),null);
		}

		float mfloat = (float)((float)hitCount/(float)amount);
		
		context.appendLog(null,"A total of "+amount+" request  hit "+hitCount+" hit rate is "+mfloat,null);
		context.appendLog(null,"A total of "+amount+" latency is " + resultData.getLatency() + " s",null);
		
		resultData.setHitRate(mfloat);
			
		context.getResultDataList().get(algorithm).add(resultData);
	}
	
	public void addLocalHobby(Iterator<State> mIt){
		while(mIt.hasNext()){
			State state = mIt.next();
			SingleLocalHobby requestedContent = state.getRequestSingleContent();
			requestedContent.addRequestedAmount();
			//基站增加该内容的流行度
			state.getNetwork().addHobbyByRequestContent(requestedContent.getSingleContent());
		}
	}
	
}

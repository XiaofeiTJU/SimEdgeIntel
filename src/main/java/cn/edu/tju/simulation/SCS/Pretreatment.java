package cn.edu.tju.simulation.SCS;

import java.util.List;

import cn.edu.tju.simulation.context.Context;
import cn.edu.tju.simulation.IF.devices.MobilityModel;
import cn.edu.tju.simulation.IF.edgenode.SameTypeWirelessNetwork;

/**
 * 
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 *
 */
public class Pretreatment {
	
	public static void process(){
		Context context = Context.getInstance();
		
		//网络进行流行度波动
		SameTypeWirelessNetwork BSs = context.getWirelessNetworkGroup().BS;
		for(int i = 0 ;i<BSs.getAmount();i++){
			BSs.getNetwork(i).fluctuatePopularity();;
		}
		
		//用户进行流行度波动
		List<MobilityModel> users = context.getUsers().getSimpleUsers();
		for(int j = 0 ;j<users.size();j++){
			users.get(j).fluctuatePopularity();
		}

		//生成队列
		context.getStateQueue().generateStateQueue();
	}
}

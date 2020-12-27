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
		
		//����������жȲ���
		SameTypeWirelessNetwork BSs = context.getWirelessNetworkGroup().BS;
		for(int i = 0 ;i<BSs.getAmount();i++){
			BSs.getNetwork(i).fluctuatePopularity();;
		}
		
		//�û��������жȲ���
		List<MobilityModel> users = context.getUsers().getSimpleUsers();
		for(int j = 0 ;j<users.size();j++){
			users.get(j).fluctuatePopularity();
		}

		//���ɶ���
		context.getStateQueue().generateStateQueue();
	}
}

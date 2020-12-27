package cn.edu.tju.simulation.IF.devices;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.edu.tju.simulation.context.Context;
import cn.edu.tju.simulation.IF.edgenode.SameTypeWirelessNetwork;
import cn.edu.tju.simulation.IF.edgenode.WirelessNetwork;

/**
 * Incoming devices id collection, generate devices
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 *
 */
public class MobilityModelGenerator {
	/**
	 * Generate users randomly
	 * @param number The number of users
	 * @param BSs Collection of base stations
	 * 
	 */
	public static void generateUser(){	
		Context context = Context.getInstance();
		//Clear all users
		context.getUsers().getSimpleUsers().clear();
		//Clear all users of network
		for(int i = 0; i< context.getWirelessNetworkGroup().BS.getAmount(); i++){
			context.getWirelessNetworkGroup().BS.getNetwork(i).getUserOfNetwork().clear();
		}
		
		//Generate Users
		WirelessNetwork network = null;
		Iterator<Integer> it = generateID(Integer.parseInt(context.getOperationPanel().getUserAmountText().getText())).iterator();
		while(it.hasNext()){
			int ID = it.next();			
			network = chooseNetwork();
			Point2D.Double point = generatePoint(network);
		
			SimpleMobilityModel sm = new SimpleMobilityModel(ID, point,network);
			context.getUsers().getSimpleUsers().add(sm);
			network.getUserOfNetwork().add(sm);
		}
	}
	
	public static Point2D.Double generatePoint(WirelessNetwork network){
		double angle =  Math.random()*Math.PI*2;

		double x_temp = 0;
		double y_temp = 0;
		double temp = Math.random();
		if(Math.cos(angle)<0){
			x_temp = Math.ceil(network.getRadius()*Math.cos(angle)*Math.sqrt(temp));
		}else{
			x_temp = Math.floor(network.getRadius()*Math.cos(angle)*Math.sqrt(temp));
		}
		if(Math.sin(angle)<0){
			y_temp = Math.ceil(network.getRadius()*Math.sin(angle)*Math.sqrt(temp));
		}else{
			y_temp = Math.floor(network.getRadius()*Math.sin(angle)*Math.sqrt(temp));
		}
		
		double x = network.getLocation().getX()+x_temp;
		double y = network.getLocation().getY()+y_temp;	

		return new Point2D.Double(x,y);
	}
	
	
	//���ѡ��һ����վ
	public static WirelessNetwork chooseNetwork(){
		SameTypeWirelessNetwork BSs = Context.getInstance().getWirelessNetworkGroup().BS;
		WirelessNetwork network = null;
		//���ѡ��һ����վ
		while(true){
			int a = (int)Math.floor(Math.random()*10);
			if(a<BSs.getAmount()){
				network = BSs.getNetwork(a);
				break;
			}	
		}
		return network;
	}	
	
	/**
	 * Generate a devices ID based on the number of users
	 * @param number The number of users
	 * @return
	 */
	public static List<Integer> generateID(int number){
		List<Integer> usersID = new ArrayList<Integer>();
		for(int i = 0; i<number ;i++){
			usersID.add(i+1);
		}
		return usersID;
	}
}

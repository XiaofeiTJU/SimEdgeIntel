package cn.edu.tju.simulation.algorithm;

import cn.edu.tju.simulation.IF.files.SingleLocalHobby;
import cn.edu.tju.simulation.IF.edgenode.WirelessNetwork;

/**
 * 
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 *
 */
public interface RealTimeAlgorithm{
	public void setCache(WirelessNetwork network,SingleLocalHobby requestContent);
}

package cn.edu.tju.simulation.tool;

import java.util.Iterator;

import cn.edu.tju.simulation.IF.files.CachingSingleContent;
import cn.edu.tju.simulation.context.Context;
import cn.edu.tju.simulation.IF.edgenode.SameTypeWirelessNetwork;
import cn.edu.tju.simulation.IF.edgenode.WirelessNetwork;

/**
 * Toolbox
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 * 
 */
public class ToolKit {
	public double pointRand(double Lamda) { // Poisson distribution
		double x = 0, b = 1, c = Math.exp(-Lamda), u;
		do {
			u = Math.random();
			b *= u;
			if (b >= c)
				x++;
		} while (b >= c);
		return x;
	}
	
	public static int getPossionVariable(double lamda) {  
        int x = 0;  
        double y = Math.random(), cdf = getPossionProbability(x, lamda);  
        while (cdf < y) {  
            x++;  
            cdf += getPossionProbability(x, lamda);  
        }  
        return x;  
    }  
  
    public static double getPossionProbability(int k, double lamda) {  
        double c = Math.exp(-lamda), sum = 1;  
        for (int i = 1; i <= k; i++) {  
            sum *= lamda / i;  
        }  
        return sum * c;  
    }  
    
    public static void printCache(Context context){
    	SameTypeWirelessNetwork BSs = context.getWirelessNetworkGroup().BS;
		for (int i = 0; i < BSs.getAmount(); i++) {
			WirelessNetwork network = BSs.getNetwork(i);
			//没有加日志
			context.appendLog(null,"Network " + network.getNumber()+ " cache is:",null);
			Iterator<CachingSingleContent> it = network.getCacheContent().iterator();
			StringBuffer str = new StringBuffer();
			while(it.hasNext()){
				CachingSingleContent cachingSingleContent = it.next();
				str.append(cachingSingleContent.getName());
				str.append(" ");
			}
			context.appendLog(null,str.toString(),null);
		}
    }
}

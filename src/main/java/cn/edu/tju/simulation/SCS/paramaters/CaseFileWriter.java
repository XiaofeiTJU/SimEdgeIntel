package cn.edu.tju.simulation.SCS.paramaters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import cn.edu.tju.simulation.context.Context;
import cn.edu.tju.simulation.IF.edgenode.WirelessNetwork;

/**
 * Save the case paramaters. The case paramaters is used to record the configuration of the network system.
 * For example, you designed the network topology and configured the radius, cache size, and other information for each base station. When you do the next simulation, you want to call this case directly, and you can choose to save it as a case paramaters.
 * 
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 *
 */
public class CaseFileWriter {
	
	public Boolean write(Context context, File file){
		
		FileOutputStream fos = null;
		OutputStreamWriter out = null;
		 try {  
	            fos = new FileOutputStream(file);  
	            out = new OutputStreamWriter(fos);
	            out.write("#WirelessNetwork\r\n");
	            
	            Iterator<WirelessNetwork> it = context.getWirelessNetworkGroup().BS.getIterator();
	            while(it.hasNext()){
	            	WirelessNetwork network = (WirelessNetwork) it.next();
	            	out.write(network.getNumber()+" "+network.getLocation().getX()+" "+network.getLocation().getY()+" "+network.getRadius()+" "+network.getCacheSize()+"\r\n");
	            }
	            out.write("#WirelessNetworkWave\r\n");
	            out.write(Parameter.BSMinWaveInterval+" "+Parameter.BSMaxWaveInterval);
	            
	            return true;
	        } catch (IOException e1) {   
	            e1.printStackTrace();  
	        }finally{
	        	try {
			        if(out!=null){
			        	out.close();
			        }if(fos!=null){
			        	fos.close(); 
			        }
				} catch (IOException e1) {
					e1.printStackTrace();
				}
	        }
		return false;
	}
}

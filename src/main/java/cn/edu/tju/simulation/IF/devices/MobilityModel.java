package cn.edu.tju.simulation.IF.devices;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.edu.tju.simulation.cache.Query;
import cn.edu.tju.simulation.IF.files.LocalHobby;
import cn.edu.tju.simulation.IF.files.SingleLocalHobby;
import cn.edu.tju.simulation.context.Context;
import cn.edu.tju.simulation.SCS.paramaters.Parameter;
import cn.edu.tju.simulation.SCS.state.State;
import cn.edu.tju.simulation.IF.edgenode.SameTypeWirelessNetwork;
import cn.edu.tju.simulation.IF.edgenode.WirelessNetwork;

/**
 * 
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 *
 */
public abstract class MobilityModel implements Query{
	/**
	 * User's ID
	 */
	protected int ID;
	/**
	 * User's coordinates
	 */
	protected Point2D.Double location;
	/**
	 * The current devices's base station
	 */
	protected WirelessNetwork wirelessNetwork;
	/**
	 * Distance to the current base station
	 */
	protected Double distance;
	/**
	 * The current devices's own popularity
	 */
	protected LocalHobby content;
		
	protected int sumOfPopularity = 0;
	
	protected List<Integer> ratio;
		
	public double getDownloadRate(){
		double k = Parameter.PATH_LOSS;
		double e = Parameter.EXPONENT;
		double Gbu = k*Math.pow(this.distance, -e);
		double Pb = Parameter.TRANSMISSION_POWER; // ��λ��W
		double temp = Parameter.GAUSSIAN_WHITE_NOISE_POWER; // ��λdBm/HZ
		double o = (Math.pow(10, temp/10)) * 0.001; // ��λW/HZ
		double SNR = Pb*Gbu/(o);
		double B = Parameter.BANDWIDTH * 1000000; // ��λHZ
		double w = (B/this.wirelessNetwork.getUserOfNetwork().size()) * (Math.log(1 + SNR) / Math.log(2));
		
		if(w<25165824){
			w = 25165824;
		}
		//return item is B
		return w / 8 / 1024;
	}

	public void checkLocationToAdjustNetwork(double x , double y){
		Point2D.Double networkLocation = this.wirelessNetwork.getLocation();
		double currentDistance = Point2D.distance(networkLocation.getX(), networkLocation.getY(), x, y);
		//������뵱ǰ������
		WirelessNetwork nextNetwork = null;
		if(currentDistance > this.wirelessNetwork.getRadius()){
			if(Math.random() < 0.1){
				double minDistance = 0;
				SameTypeWirelessNetwork BSs = Context.getInstance().getWirelessNetworkGroup().BS;
				for(int i = 0; i<BSs.getAmount(); i++){
					WirelessNetwork bs = BSs.getNetwork(i);
					double newDistance = Point2D.distance(bs.getLocation().getX(), bs.getLocation().getY(), x, y);
					if(newDistance <= bs.getRadius()){
						if(nextNetwork == null || newDistance < minDistance){
							nextNetwork = bs;
							minDistance = newDistance;
						}else if (newDistance >= minDistance){
							break;
						}
						//�����µľ���
						this.location.setLocation(x, y);
						//�����໥��ϵ
						this.wirelessNetwork.getUserOfNetwork().remove(this);
						this.wirelessNetwork = bs;
						this.wirelessNetwork.getUserOfNetwork().add(this);
					}
				}
				if(nextNetwork == null){
					//�������źŷ�Χ��Խ�練ת
					this.location.setLocation(MobilityModelGenerator.generatePoint(wirelessNetwork));
				}
			}else{
					//�û������ȥ�ˣ�Խ�練ת
				this.location.setLocation(MobilityModelGenerator.generatePoint(wirelessNetwork));
			}
		}else{
			this.location.setLocation(x, y);
		}
		
		// update distance 
		this.distance = Point2D.distance(this.wirelessNetwork.getLocation().getX(), this.wirelessNetwork.getLocation().getY(), x, y);
	}
	
	/**
	 * The law of movement
	 * @param location coordinate
	 */
	public abstract void move();
	
	public abstract State generateState();
	
	public void  fluctuatePopularity(){
		this.content.fluctuatePopularity(Parameter.UserMinWaveInterval, Parameter.UserMaxWaveInterval, this.wirelessNetwork.getContent().getContentList());
		generateProbabilityOfSingleContent();
	}
	
	public void generateProbabilityOfSingleContent(){
		if(this.ratio != null){
			this.ratio.clear();
		}else{
			this.ratio = new ArrayList<Integer>();
		}
		
		sumOfPopularity = 0;//The total popularity
		//Calculate the total popularity
		Iterator<SingleLocalHobby> it = this.content.getContentList().iterator();
		
		while(it.hasNext()){
			sumOfPopularity += it.next().getLocalHobbyValue();
		}
		
		for (int j =0;j<this.content.getContentList().size();j++) {
			SingleLocalHobby singleContent = content.getContentList().get(j);
			if(j!=0){
				ratio.add((singleContent.getLocalHobbyValue()+ratio.get(j-1)));
			}else if(j==0){
				ratio.add(singleContent.getLocalHobbyValue());
			}
		}
		
	}
	
	public Boolean query(SingleLocalHobby singleContent){
		return this.wirelessNetwork.dealQuery(this, singleContent);
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public Point2D.Double getLocation() {
		return location;
	}

	public void setLocation(Point2D.Double location) {
		this.location = location;
	}

	public WirelessNetwork getWirelessNetwork() {
		return wirelessNetwork;
	}

	public void setWirelessNetwork(WirelessNetwork wirelessNetwork) {
		this.wirelessNetwork = wirelessNetwork;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}


	public LocalHobby getContent() {
		return content;
	}

	public void setContent(LocalHobby content) {
		this.content = content;
	}

		
}

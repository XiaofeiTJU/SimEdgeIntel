package cn.edu.tju.simulation.SCS.state;

import cn.edu.tju.simulation.IF.files.SingleLocalHobby;
import cn.edu.tju.simulation.IF.devices.MobilityModel;
import cn.edu.tju.simulation.IF.edgenode.WirelessNetwork;

/**
 * Status
 * 
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin
 *         University
 * 
 */
public class State implements Cloneable {
	/**
	 * The devices to whom this status belongs
	 */
	private MobilityModel user;
	/**
	 * Whether this devices request files
	 */
	private Boolean request;
	/**
	 * Download files in this state
	 */
	private SingleLocalHobby requestSingleContent;
	/**
	 * Download time
	 */
	private double latency;

	private int remainingTimeSlotNumber;

	/**
	 * 
	 * @param mobilityModel
	 *            State belongs to the devices
	 * @param latency
	 *            The time this state occurred
	 * @param media
	 *            Download files in this state
	 */
	public State(MobilityModel user, SingleLocalHobby requestSingleContent) {
		this.user = user;
		this.latency = requestSingleContent.getSize() / user.getDownloadRate();
		System.out.println(requestSingleContent.getSize() + "KB " + user.getDownloadRate()+"KB/s");
		System.out.println(this.latency + "s");
		this.request = true;
		this.requestSingleContent = requestSingleContent;
		this.remainingTimeSlotNumber = requestSingleContent.getTimeSlotNumber();
	}

	@Override
	public Object clone() {
		State state = null;
		try {
			state = (State) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return state;
	}

	public void cutTimeSlotNumber() {
		this.remainingTimeSlotNumber--;
	}

	public double getLatency() {
		return latency;
	}

	public void setLatency(double time) {
		this.latency = time;
	}

	public SingleLocalHobby getRequestSingleContent() {
		return requestSingleContent;
	}

	public void setRequestSingleContent(SingleLocalHobby requestSingleContent) {
		this.requestSingleContent = requestSingleContent;
	}

	public WirelessNetwork getNetwork() {
		return this.user.getWirelessNetwork();
	}

	public MobilityModel getUser() {
		return user;
	}

	public Boolean getRequest() {
		return request;
	}

	public int getRemainingTimeSlotNumber() {
		return remainingTimeSlotNumber;
	}

	public void setRemainingTimeSlotNumber(int remainingTimeSlotNumber) {
		this.remainingTimeSlotNumber = remainingTimeSlotNumber;
	}

}

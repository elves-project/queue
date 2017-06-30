package cn.gyyx.elves.queue.pojo;

import java.io.Serializable;

public class Result implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	private String flag;
	private String dependTaskId;
	private String error;
	private int workerFlag;
	private String workerMessage;
	private int workerCosttime;
	private String status;
	private String queueId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public int getWorkerFlag() {
		return workerFlag;
	}
	public void setWorkerFlag(int workerFlag) {
		this.workerFlag = workerFlag;
	}
	public String getWorkerMessage() {
		return workerMessage;
	}
	public void setWorkerMessage(String workerMessage) {
		this.workerMessage = workerMessage;
	}
	public int getWorkerCosttime() {
		return workerCosttime;
	}
	public void setWorkerCosttime(int workerCosttime) {
		this.workerCosttime = workerCosttime;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getQueueId() {
		return queueId;
	}
	public void setQueueId(String queueId) {
		this.queueId = queueId;
	}
	
	@Override
	public String toString() {
		return "Result [id=" + id + ", flag=" + flag + ", error=" + error
				+ ", workerFlag=" + workerFlag + ", workerMessage="
				+ workerMessage + ", workerCosttime=" + workerCosttime
				+ ", status=" + status + ", queueId=" + queueId + "]";
	}
	public String getDependTaskId() {
		return dependTaskId;
	}
	public void setDependTaskId(String dependTaskId) {
		this.dependTaskId = dependTaskId;
	}
	
	
	
}

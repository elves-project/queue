package cn.gyyx.elves.queue.pojo;

public class Queue {

	private static final long serialVersionUID = 1L;

	private String queueId;
	private String app;
	private String createtime;
	private String committime;
	private String status;
	
	public String getQueueId() {
		return queueId;
	}
	public void setQueueId(String queueId) {
		this.queueId = queueId;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getCommittime() {
		return committime;
	}
	public void setCommittime(String committime) {
		this.committime = committime;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return "Queue [queueId=" + queueId + ", app=" + app + ", createtime="
				+ createtime + ", committime=" + committime + ", status="
				+ status + "]";
	}
	
	
	
}

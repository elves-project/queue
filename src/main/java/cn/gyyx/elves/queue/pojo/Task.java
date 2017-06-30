package cn.gyyx.elves.queue.pojo;

import java.io.Serializable;


public class Task implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String queueId;				//队列ID
	private String taskId;				//任务ID
	private String ip;						//IP
	private String mode;					//模式('p','np')
	private String app;					//APP
	private String func;					//FUNC
	private String param;					//PARAM
	private int timeout;					//超时时间
	private String proxy;					//代理器
	private String dependTaskId;			//依赖id

	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getQueueId() {
		return queueId;
	}
	public void setQueueId(String queueId) {
		this.queueId = queueId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getFunc() {
		return func;
	}
	public void setFunc(String func) {
		this.func = func;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public String getProxy() {
		return proxy;
	}
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}
	public String getDependTaskId() {
		return dependTaskId;
	}
	public void setDependTaskId(String dependTaskId) {
		this.dependTaskId = dependTaskId;
	}
	@Override
	public String toString() {
		return "Task [queueId=" + queueId + ", taskId=" + taskId + ", ip=" + ip
				+ ", mode=" + mode + ", app=" + app + ", func=" + func
				+ ", param=" + param + ", timeout=" + timeout + ", proxy="
				+ proxy + ", dependTaskId=" + dependTaskId + "]";
	}

}

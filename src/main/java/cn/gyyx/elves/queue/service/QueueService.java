package cn.gyyx.elves.queue.service;
import java.util.Map;

public interface QueueService {

	public Map<String,Object> createQueue(Map<String, Object> params);

	public Map<String,Object> addTask(Map<String, Object> params);
	
	public Map<String,Object> commitQueue(Map<String, Object> params);

	public Map<String,Object> stopQueue(Map<String, Object> params);
	
	public Map<String,Object> queueResult(Map<String, Object> params);
	
	public void taskResult(Map<String, Object> params);

}

package cn.gyyx.elves.queue.dao;

import java.util.List;

import cn.gyyx.elves.queue.pojo.Queue;
import cn.gyyx.elves.queue.pojo.Result;
import cn.gyyx.elves.queue.pojo.Task;

public interface QueueDao {

	public int createQueue(String queue_id, String app);

	public List<Queue> getQueue(String queue_id);
	
	public Task getTask(String task_id);
	
	public int addTask(Task t);
	
	public int commitQueue(String queue_id);
	
	public List<Task> getTasks(String queue_id);

	public List<Task> getNextTasks(String task_id);

	public void saveResult(Result r);
	
	public List<Result> getResults(String queue_id);

	public int stopQueue(String queue_id);
	
	public int stopTask(String queue_id);

	public int updateTaskStatus(String taskId,String status);
	
}

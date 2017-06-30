package cn.gyyx.elves.queue.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import cn.gyyx.elves.queue.dao.QueueDao;
import cn.gyyx.elves.queue.pojo.Queue;
import cn.gyyx.elves.queue.pojo.Result;
import cn.gyyx.elves.queue.pojo.Task;
import cn.gyyx.elves.queue.service.QueueService;
import cn.gyyx.elves.util.ExceptionUtil;
import cn.gyyx.elves.util.SecurityUtil;
import cn.gyyx.elves.util.mq.MessageProducer;

@Service("elvesConsumerService")
public class QueueServiceImpl implements QueueService{

	private static final Logger LOG=Logger.getLogger(QueueServiceImpl.class);
	
	@Autowired
	private QueueDao queueDao;
	
	@Autowired
	private MessageProducer messageProducer;

	@Override
	public Map<String, Object> createQueue(Map<String, Object> params)  {
		LOG.info("Create Queue Start...");
		Map<String, Object> rs=new HashMap<>();
		rs.put("flag", "false");
		rs.put("error", "");
		try{
			String QUEUEID = SecurityUtil.getUniqueKey();
			int flag = queueDao.createQueue(QUEUEID, params.get("app").toString());
			if(flag==1){
				rs.put("id", QUEUEID);
				rs.put("flag", "true");
			}else{
				rs.put("error", "[500]Create Queue Error!");
			}
		}catch(Exception e){
			String error =ExceptionUtil.getStackTraceAsString(e);
			LOG.error("[500]Internal ServerError "+error);
			rs.put("error","[500]Internal ServerError "+error);
		}
		LOG.info("Create Queue Finish...");
		return rs;
	}
	
	@Override
	public Map<String, Object> addTask(Map<String, Object> params){
		LOG.info("Add Task Start...");
		Map<String, Object> rs=new HashMap<>();
		rs.put("flag", "false");
		rs.put("error", "");
		try {
			List<Queue> qlist = queueDao.getQueue(params.get("id").toString());
			if (qlist.size() == 1) {
				if ("pendding".equals(qlist.get(0).getStatus())) {
					if(null!=params.get("depend_task_id")&&StringUtils.isNotBlank(params.get("depend_task_id").toString())){
						Task task =queueDao.getTask(params.get("depend_task_id").toString());
						if(task==null){
							rs.put("flag", "false");
							rs.put("error", "[412.4]Depend Task Not Found");
							return rs;
						}
					}
					String TASKID = SecurityUtil.getUniqueKey();
					Task t = new Task();
					t.setTaskId(TASKID);
					t.setQueueId(params.get("id").toString());
					t.setApp(qlist.get(0).getApp());
					t.setDependTaskId(params.get("depend_task_id").toString());
					t.setFunc(params.get("func").toString());
					t.setIp(params.get("ip").toString());
					t.setMode(params.get("mode").toString());
					t.setParam(params.get("param").toString());
					t.setProxy(params.get("proxy").toString());
					t.setTimeout((int) params.get("timeout"));
					int flag = queueDao.addTask(t);
					if (flag == 1) {
						rs.put("flag", "true");
						rs.put("id", TASKID);
					} else {
						rs.put("error", "[500]Create Task Error!");
					}
				} else {
					rs.put("error", "[412.2]Queue Status Incorrect!");
				}
			} else {
				rs.put("error", "[412.1]Queue Not Found!");
			}
		}catch(Exception e){
			String error =ExceptionUtil.getStackTraceAsString(e);
			LOG.error("[500]Internal ServerError "+error);
			rs.put("error","[500]Internal ServerError "+error);
		}
		LOG.info("Add Task Finish...");
		return rs;
	}

	@Override
	public Map<String, Object> commitQueue(Map<String, Object> params) {
		LOG.info("Commit Queue Start");
		Map<String, Object> rs=new HashMap<>();
		rs.put("flag", "false");
		rs.put("error", "");
		try {
			String queue_id = params.get("id").toString();
			List<Queue> qlist = queueDao.getQueue(params.get("id").toString());
			if (qlist.size() == 1) {
				if(!"pendding".equals(qlist.get(0).getStatus())){
					rs.put("error", "[412.2]Queue Status Incorrect!");
					return rs;
				}
				
				int flag = queueDao.commitQueue(queue_id);
				LOG.info("commit queue flag:"+ flag);
				List<Task> task_list = queueDao.getTasks(queue_id);
				for (Task t : task_list) {
					Map<String, Object> mqrequest = new HashMap<>();
					mqrequest.put("id", t.getTaskId());
					mqrequest.put("ip", t.getIp());
					mqrequest.put("type", "queue");
					mqrequest.put("mode", t.getMode());
					mqrequest.put("app", t.getApp());
					mqrequest.put("func", t.getFunc());
					mqrequest.put("param", t.getParam());
					mqrequest.put("timeout", t.getTimeout());
					mqrequest.put("proxy", t.getProxy());
					
					Map<String, Object> msg = new HashMap<>();
					msg.put("mqkey","queue.scheduler.asyncJob");
					msg.put("mqtype","cast");
					msg.put("mqbody",mqrequest);
					
					try {
						messageProducer.cast("queue.scheduler",JSON.toJSONString(msg));
						int tf = queueDao.updateTaskStatus(t.getTaskId(),"running");
						LOG.info("commit task job flag:"+tf);
					} catch (Exception e) {
						Result r = new Result();
						r.setId(t.getTaskId());
						r.setFlag("false");
						r.setError("[500] " + e.getMessage());
						r.setStatus("finish");
						//rs.put("error", "[500] taskID" + t.getTaskId() + " " + e.getMessage());
						queueDao.saveResult(r);
						LOG.error(ExceptionUtil.getStackTraceAsString(e));
					}
				}
				rs.put("flag", "true");
			} else {
				rs.put("error", "[412.1]Queue Not Found");
			}
		}catch(Exception e){
			String error =ExceptionUtil.getStackTraceAsString(e);
			LOG.error("[500]Internal ServerError "+error);
			rs.put("error","[500]Internal ServerError "+error);
		}
		LOG.info("Commit Queue Finish");
		return rs;
	}

	@Override
	public Map<String, Object> stopQueue(Map<String, Object> params) {
		LOG.info("Stop Queue Start...");
		Map<String, Object> rs=new HashMap<>();
		rs.put("flag", "false");
		rs.put("error", "");
		try {
			String queue_id = params.get("id").toString();
			List<Queue> qlist = queueDao.getQueue(params.get("id").toString());
			if (qlist.size() == 1) {
				int f = queueDao.stopQueue(queue_id);
				LOG.info("stopQueue flag:"+f);
				int tf = queueDao.stopTask(queue_id);
				LOG.info("stopTask flag:"+tf);
				rs.put("flag", "true");
			} else {
				rs.put("error", "[412.1]Queue Not Found");
			}
		}catch(Exception e){
			String error =ExceptionUtil.getStackTraceAsString(e);
			LOG.error("[500]Internal ServerError "+error);
			rs.put("error","[500]Internal ServerError "+error);
		}
		LOG.info("Stop Queue Finish");
		return rs;
	}

	@Override
	public Map<String, Object> queueResult(Map<String, Object> params){
		LOG.info("Get Queue Result Start...");
		Map<String, Object> rs=new HashMap<>();
		rs.put("flag", "false");
		rs.put("error", "");
		try {
			String queue_id = params.get("id").toString();
			
			List<Queue> qlist = queueDao.getQueue(params.get("id").toString());
			if(qlist.size()==1){
				List<Result> rList = queueDao.getResults(queue_id);
				Map<String, Object> rinfos = new HashMap<>();
				for (Result r : rList) {
					Map<String, Object> taskResult = new HashMap<>();
					taskResult.put("id",r.getId());
					taskResult.put("depend_task_id",r.getDependTaskId()==null?"":r.getDependTaskId());
					taskResult.put("flag",r.getFlag()==null?"":r.getFlag());
					taskResult.put("error",r.getError()==null?"":r.getError());
					taskResult.put("worker_message",r.getWorkerMessage()==null?"":r.getWorkerMessage());
					taskResult.put("status",r.getStatus());
					taskResult.put("worker_costtime",r.getWorkerCosttime());
					taskResult.put("worker_flag",r.getWorkerFlag());
					rinfos.put(r.getId(),taskResult);
				}
				rs.put("flag", "true");
				rs.put("result", rinfos);
			}else{
				rs.put("error", "[412.1]Queue Not Found");
			}
		}catch(Exception e){
			String error =ExceptionUtil.getStackTraceAsString(e);
			LOG.error("[500]Internal ServerError "+error);
			rs.put("error","[500]Internal ServerError "+error);
		}
		LOG.info("Get Queue Result Finish...");
		return rs;
	}

	@Override
	public void taskResult(Map<String, Object> params){
		LOG.info("Save Result Start...");
		try {
			Result r = new Result();
			r.setFlag(params.get("flag").toString());
			r.setError(params.get("error").toString());
			Map<String,Object> result =JSON.parseObject(params.get("result").toString(), new TypeReference<Map<String,Object>>(){});
			r.setId(result.get("id").toString());
			r.setStatus("finish");
			if("true".equals(params.get("flag").toString())){
				r.setWorkerCosttime(Integer.parseInt(result.get("worker_costtime").toString()));
				r.setWorkerFlag(Integer.parseInt(result.get("worker_flag").toString()));
				r.setWorkerMessage(result.get("worker_message").toString());
			}
			queueDao.saveResult(r);
			if("true".equals(params.get("flag").toString())&&null!=result.get("worker_flag")&&Integer.parseInt(result.get("worker_flag").toString())==1){
				List<Task> task_list = queueDao.getNextTasks(r.getId());
				for (Task t : task_list) {
					Map<String, Object> mqrequest = new HashMap<>();
					mqrequest.put("id", t.getTaskId());
					mqrequest.put("ip", t.getIp());
					mqrequest.put("type", "queue");
					mqrequest.put("mode", t.getMode());
					mqrequest.put("app", t.getApp());
					mqrequest.put("func", t.getFunc());
					mqrequest.put("param", t.getParam());
					mqrequest.put("timeout", t.getTimeout());
					mqrequest.put("proxy", t.getProxy());
					
					Map<String, Object> msg = new HashMap<>();
					msg.put("mqkey","queue.scheduler.asyncJob");
					msg.put("mqtype","cast");
					msg.put("mqbody",mqrequest);
					
					try {
						messageProducer.cast("queue.scheduler",JSON.toJSONString(msg));
						int tf = queueDao.updateTaskStatus(t.getTaskId(),"running");
						LOG.info("commit task job flag:"+tf);
					} catch (Exception e) {
						Result tr = new Result();
						tr.setId(t.getTaskId());
						tr.setFlag("false");
						tr.setError("[500] " + e.getMessage());
						tr.setStatus("finish");
						queueDao.saveResult(r);
						LOG.error(ExceptionUtil.getStackTraceAsString(e));
					}
				}
			}else{
				LOG.info("task depend job fail");
			}
			
			
		}catch(Exception e){
			String error =ExceptionUtil.getStackTraceAsString(e);
			LOG.error("[500]Internal ServerError "+error);
		}
		LOG.info("Save Result Finish...");
	}
}

package cn.gyyx.elves.queue.entrance;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import cn.gyyx.elves.util.ExceptionUtil;
import cn.gyyx.elves.util.SpringUtil;
import cn.gyyx.elves.util.mq.PropertyLoader;
import cn.gyyx.elves.util.zk.ZookeeperExcutor;


public class ProgramEntrance {

	private static final Logger LOG=Logger.getLogger(ProgramEntrance.class);
	
	/**
	 * 加载所有配置文件的路径
	 */
	private static void loadAllConfigFilePath(String configPath){
		SpringUtil.SPRING_CONFIG_PATH="file:"+configPath+File.separator+"conf"+File.separator+"spring.xml";
		SpringUtil.RABBITMQ_CONFIG_PATH="file:"+configPath+File.separator+"conf"+File.separator+"rabbitmq.xml";
		SpringUtil.PROPERTIES_CONFIG_PATH=configPath+File.separator+"conf"+File.separator+"conf.properties";
		SpringUtil.LOG4J_CONFIG_PATH=configPath+File.separator+"conf"+File.separator+"log4j.properties";
		SpringUtil.MYBATIS_CONFIG_PATH="file:"+configPath+File.separator+"conf"+File.separator+"mybatis.xml";
		LOG.info("loadAllConfigFilePath success!");
	}
	
	/**
	 * 加载日志配置文件
	 */
	private static void loadLogConfig() throws Exception{
		InputStream in=new FileInputStream(SpringUtil.LOG4J_CONFIG_PATH);// 自定义配置
		PropertyConfigurator.configure(in);
		LOG.info("loadLogConfig success!");
	}
	
	/**
	 * 加载Spring配置文件
	 */
	private static void loadApplicationXml() throws Exception{
		SpringUtil.app = new FileSystemXmlApplicationContext(SpringUtil.SPRING_CONFIG_PATH,SpringUtil.RABBITMQ_CONFIG_PATH);
		LOG.info("loadApplicationXml success!");
	}

	/**
	 * 注册模块到zk节点上
	 */
	private static void registerZooKeeper() throws Exception{
		if("true".equalsIgnoreCase(PropertyLoader.ZOOKEEPER_ENABLED)){
			LOG.info("regist zookeeper ....");
			ZookeeperExcutor zke=new ZookeeperExcutor(PropertyLoader.ZOOKEEPER_HOST,
					PropertyLoader.ZOOKEEPER_OUT_TIME, PropertyLoader.ZOOKEEPER_OUT_TIME);

			//创建模块根节点
			if(null==zke.getClient().checkExists().forPath(PropertyLoader.ZOOKEEPER_ROOT)){
				zke.getClient().create().creatingParentsIfNeeded().forPath(PropertyLoader.ZOOKEEPER_ROOT);
			}
			if(null==zke.getClient().checkExists().forPath(PropertyLoader.ZOOKEEPER_ROOT+"/queue")){
				zke.getClient().create().creatingParentsIfNeeded().forPath(PropertyLoader.ZOOKEEPER_ROOT+"/queue");
			}

			String nodeName=zke.createNode(PropertyLoader.ZOOKEEPER_ROOT+"/queue/", "");
			if(null!=nodeName){
				zke.addListener(PropertyLoader.ZOOKEEPER_ROOT+"/queue/", "");
			}
			LOG.info("register ZooKeeper success!");
		}
	}
	
	public static void main(String[] args) {
		if(null!=args&&args.length>0){
			try {
				loadAllConfigFilePath(args[0]);

		    	loadLogConfig();

				loadApplicationXml();

				registerZooKeeper();
			} catch (Exception e) {
				LOG.error("start queue error:"+ExceptionUtil.getStackTraceAsString(e));
				System.exit(1);
			}
    	}
	}
}

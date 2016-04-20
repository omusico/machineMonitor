package com.machineMonitor.general.contoller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.globaltek.machineLib.CurrentAlarm;
import com.globaltek.machineLib.CurrentExecuteNCInfo;
import com.globaltek.machineLib.GeneralResult;
import com.globaltek.machineLib.QueryMachineInfo;
import com.globaltek.machineLib.SpeedFeedRate;
import com.globaltek.machineLib.StatusInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
public class MonitorMainController {
	   
	 protected final transient Logger logger = Logger.getLogger(this.getClass());
	  
	   @Autowired	
	   MainSetController mainSet;
	  
	   @Value("${monitor.ip}")
	   String monitorIp;
	   
	   @Value("${monitor.machineSetPort}")
	   String machineSetPort;		   
	   
		
	   @Value("${monitor.machineAddMethod}")
	   String machineAddMethod;
	   
	   @Value("${monitor.queryMachineInfo}")
	   String queryMachineInfo;
	   
	   @Value("${monitor.statusInfo}")
	   String statusInfo;
	   
	   @Value("${monitor.feedrateSpeedInfo}")
	   String feedrateSpeedInfo;
	  
	   @Value("${monitor.curExecutePrgInfo}")
	   String curExecutePrgInfo;
	   
	   @Value("${monitor.makino.feedrateSpeedInfo}")
	   String makinoFeedrateSpeedInfo;
	 
	   @Value("${monitor.getCurrentAlarmInfo}")
	   String getCurrentAlarmInfo;  
	   /*新增機台資訊(Monitor)
	   /* parameters type
	    * 
	    * {"machineName":"CNC3"}
	    *
	   */
	   public GeneralResult  addMachine(String parameters) throws Exception {	 
			logger.debug("===== into AddMachine ======");				
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
		   //調用獲取機台資訊
			String  urlPath = monitorIp +":"+ machineSetPort + machineAddMethod;
			String result = mainSet.sendPost(urlPath, parameters);
			logger.debug("result:" +result);
			
			//to Objectjson							
			GeneralResult gResult= gson.fromJson(result, new TypeToken<GeneralResult>(){}.getType());		
			
			logger.debug("===== End AddMachine ======");
		    return gResult;
		 }
	   
	   
	   /*獲取機台資訊(Monitor)
	   /* parameters type
	    * 
	    * {"machineName":"CNC3"}
	    *
	   */
	   public QueryMachineInfo  queryMachineInfo(String parameters) throws Exception {	 
			logger.debug("===== into queryMachineInfo ======");				
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
		   //調用獲取機台資訊
			String  urlPath = monitorIp +":"+ machineSetPort + queryMachineInfo;
			String result = mainSet.sendPost(urlPath, parameters);
			logger.debug("result:" +result);
			
			//to Objectjson				
			QueryMachineInfo gResult= gson.fromJson(result, new TypeToken<QueryMachineInfo>(){}.getType());		
				
			logger.debug("===== End queryMachineInfo ======");
		    return gResult;
		 }
	   
	   /*抓取轉速進給(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public SpeedFeedRate  feedrateSpeedInfo(boolean isMakino,String port,String parameters) throws Exception {	 
		   	logger.debug("===== into feedrateSpeedInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();		
			String  urlPath = monitorIp +":"+ port + feedrateSpeedInfo;		
			
			//是否為makino
			if(isMakino){
				logger.debug("is makino:"+isMakino);
				urlPath = monitorIp +":"+ port + makinoFeedrateSpeedInfo;		
			 }
			
			//調用獲取機台資訊				
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
				
			//to Objectjson				
			SpeedFeedRate gResult= gson.fromJson(result, new TypeToken<SpeedFeedRate>(){}.getType());				
				
			logger.debug("===== End feedrateSpeedInfo ======");
		    return gResult;
		 }
	   
	   /*獲取機台狀態 (Monitor)
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/statusInfo
	    *
	    *parameters type
	    * {"sysNo":1}
	    * 
	    */
	   public StatusInfo  getStatusInfo( String port, String parameters) throws Exception {	 
			logger.debug("===== into getStatusInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + statusInfo;
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
				
			//to Objectjson				
			StatusInfo gResult= gson.fromJson(result, new TypeToken<StatusInfo>(){}.getType());				
				
			logger.debug("===== End getStatusInfo ======");
		    return gResult;
		 }
	   
	   /*抓取當前執行程式(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public CurrentExecuteNCInfo  curExecutePrgInfo(String port,String parameters) throws Exception {	 
		   	logger.debug("===== into curExecutePrgInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + curExecutePrgInfo;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
				
			//to Objectjson				
			CurrentExecuteNCInfo gResult= gson.fromJson(result, new TypeToken<CurrentExecuteNCInfo>(){}.getType());				
				
			logger.debug("===== End curExecutePrgInfo ======");
		    return gResult;
		 } 
	   
	   /*獲取報警訊息(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public CurrentAlarm  getCurrentAlarmInfo(String port,String parameters) throws Exception {	 
			logger.debug("===== into getCurrentAlarmInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + getCurrentAlarmInfo;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
				
			//to Objectjson				
			CurrentAlarm gResult= gson.fromJson(result, new TypeToken<CurrentAlarm>(){}.getType());				
				
			logger.debug("===== End getCurrentAlarmInfo ======");
		    return gResult;
		 }	
}

package com.machineMonitor.machineInfo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.globaltek.machineLib.GeneralResult;//數據封包
import com.globaltek.machineLib.QueryMachineInfo;
import com.globaltek.machineLib.StatusInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.machineMonitor.general.contoller.MainSetController;


@RestController
public class MachineInfoController {
	   protected final transient Logger logger = Logger.getLogger(this.getClass());
	  
	   @Value("${monitor.ip}")
	   String monitorIp;
	   
	   @Value("${monitor.machineSetPort}")
	   String machineSetPort;	  
	   
	   @Value("${monitor.queryMachineInfo}")
	   String queryMachineInfo;
	   
	   @Value("${monitor.statusInfo}")
	   String statusInfo;
	   
	  
	   /*獲取機台資訊*/
	   /* parameters type
	    * 
	    * {"machineName":"CNC3"}
	    *
	   */
	   @RequestMapping(value="/machineInfo/queryMachineInfo", method = RequestMethod.POST)
	   public QueryMachineInfo  queryMachineInfo(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into queryMachineInfo ======");				
			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
		   //調用獲取機台資訊
			String  urlPath = monitorIp +":"+ machineSetPort + queryMachineInfo;
			MainSetController mainSet = new MainSetController();
			String result = mainSet.sendPost(urlPath, parameters);
				
			//to Objectjson				
			QueryMachineInfo gResult= gson.fromJson(result, new TypeToken<QueryMachineInfo>(){}.getType());		
				
			logger.debug("===== End queryMachineInfo ======");
		    return gResult;
		 }
	   
	   /*獲取機台狀態 
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/statusInfo
	    *
	    *parameters type
	    * [{"machineName":"CNC3"},{"machineName":"CNC2"}]
	    * 
	    */
	   @RequestMapping(value="/machineInfo/statusInfo", method = RequestMethod.POST)
	   public List<Map<String,Object>>  getStatusInfo(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into getStatusInfo ======");			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();	
			
			List<Map<String,Object>> list = gson.fromJson(parameters,new TypeToken<List<Map<String,String>>>(){}.getType());
			for(Map<String,Object> obj :list){
				Map<String,String> map = new HashMap<String,String>();
				map.put("machineName", (String) obj.get("machineName"));
				QueryMachineInfo machineInfo= queryMachineInfo(gson.toJson(map));				
				int port =machineInfo.machine[0].urlPort;			
			   //調用獲取機台資訊
				String  urlPath = monitorIp +":"+ String.valueOf(port) + statusInfo;
				MainSetController mainSet = new MainSetController();
				String result = mainSet.sendPost(urlPath, parameters);
				logger.debug("urlPath:"+urlPath);	
				//to Objectjson				
				StatusInfo statusInfo= gson.fromJson(result, new TypeToken<StatusInfo>(){}.getType());	
				obj.put("url", urlPath);
				obj.put("resultCode", (String.valueOf(statusInfo.resultCode)));
				obj.put("errorInfo", statusInfo.errorInfo);
				obj.put("run", statusInfo.run);
				obj.put("status", statusInfo.status);
				obj.put("aut", statusInfo.aut);
				obj.put("mode", statusInfo.mode);
				obj.put("emergency", statusInfo.emergency);
				obj.put("alarm", statusInfo.alarm);
			}			
			logger.debug("===== End getStatusInfo ======");
		    return list;
		 }
	   
}

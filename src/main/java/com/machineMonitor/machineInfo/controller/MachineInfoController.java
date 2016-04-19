package com.machineMonitor.machineInfo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.globaltek.machineLib.QueryMachineInfo;
import com.globaltek.machineLib.StatusInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.machineMonitor.general.contoller.MainSetController;
import com.machineMonitor.general.contoller.MonitorMainController;


@RestController
public class MachineInfoController {
	 
	 protected final transient Logger logger = Logger.getLogger(this.getClass());

	 
	   @Value("${monitor.ip}")
	   String monitorIp;
	   
	   @Value("${monitor.machineSetPort}")
	   String machineSetPort;	  
	   
	   @Autowired
	   MonitorMainController monitorMainController;
	   
	   /*獲取機台狀態(Main)
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/statusInfo
	    *
	    *parameters type
	    * [{"machineName":"CNC3","machineId":123,"toolSetNum":2,"toolSetList":[0,1]},{....},...]
	    * 
	    */
	   @RequestMapping(value="/machineInfo/statusInfo", method = RequestMethod.POST)
	   public List<Map<String,Object>>  getStatusInfoMain(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into getStatusInfo ======");			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();	
			
			List<Map<String,Object>> list = gson.fromJson(parameters,new TypeToken<List<Map<String,Object>>>(){}.getType());
			
			for(Map<String,Object> obj :list){
				
				/*抓取機台port資訊*/
				Map<String,Object> mapQueryInfo = new HashMap<String,Object>();
				mapQueryInfo.put("machineName", obj.get("machineName"));
				QueryMachineInfo machineInfo= monitorMainController.queryMachineInfo(gson.toJson(mapQueryInfo));				
				int port =machineInfo.machine[0].urlPort;	
			
				logger.debug("port:"+port);
				/*抓取機台狀態資訊，多個path要抓取多次*/
				List<Object> toolSetList =  (List<Object>) obj.get("toolSetList");
				List<Map<String,Object>> toolSetListMap = new ArrayList<>();
				
				for(int i = 0 ; i < toolSetList.size() ; i++){				
					Map<String,Integer> mapToInfo = new HashMap<String,Integer>();
					mapToInfo.put("sysNo",  i+1 ); /*sysNo第一個是1*/
					StatusInfo statusInfo = monitorMainController.getStatusInfo(String.valueOf(port),gson.toJson(mapQueryInfo));			
					
					//to Objectjson				
					HashMap<String,Object> toolSetResult = new HashMap<>();
					toolSetResult.put("toolSetId",toolSetList.get(i));
					toolSetResult.put("sysNo", (i+1));
					toolSetResult.put("url", monitorIp +":"+ port + statusInfo);
					toolSetResult.put("resultCode", (String.valueOf(statusInfo.resultCode)));
					toolSetResult.put("errorInfo", statusInfo.errorInfo);
					toolSetResult.put("run", statusInfo.run);
					toolSetResult.put("status", statusInfo.status);
					toolSetResult.put("aut", statusInfo.aut);
					toolSetResult.put("mode", statusInfo.mode);
					toolSetResult.put("emergency", statusInfo.emergency);
					toolSetResult.put("alarm", statusInfo.alarm);
					toolSetListMap.add(toolSetResult);
				}
					obj.put("toolSetListMap",toolSetListMap);
			}			
			logger.debug("===== End getStatusInfo ======");
		    return list;
		 }
	   
	  
}

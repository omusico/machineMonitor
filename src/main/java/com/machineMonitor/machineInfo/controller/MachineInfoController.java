package com.machineMonitor.machineInfo.controller;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.globaltek.machineLib.CumulativeTimeInfo;
import com.globaltek.machineLib.CurrentAlarm;
import com.globaltek.machineLib.CurrentExecuteNCInfo;
import com.globaltek.machineLib.ExecutePrgContent;
import com.globaltek.machineLib.GCode;
import com.globaltek.machineLib.MachinePositionInfo;
import com.globaltek.machineLib.OtherCode;
import com.globaltek.machineLib.PartCount;
import com.globaltek.machineLib.QueryMachineInfo;
import com.globaltek.machineLib.SpeedFeedRate;
import com.globaltek.machineLib.StatusInfo;
import com.globaltek.machineLib.WorkOffset;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.machineMonitor.general.contoller.MonitorMainController;

import aj.org.objectweb.asm.Type;


@RestController
public class MachineInfoController {
	 
	 protected final transient Logger logger = Logger.getLogger(this.getClass());

	 
	   @Value("${monitor.ip}")
	   String monitorIp;
	   
	   @Value("${monitor.machineSetPort}")
	   String machineSetPort;	  
	   
	   @Value("${monitor.statusInfo}")
	   String statusInfo;	   
	  
	   @Autowired
	   MonitorMainController monitorMainController;
	   
	   @Autowired
	   MachineMainController machineMainController;
	   
	   /*獲取多個機台狀態(Main)
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/statusInfo
	    *
	    *parameters type
	    * [{"machineName":"CNC3","machineId":123,"toolSetNum":2,"toolSetList":[0,1]},{....},...]
	    * 
	    */
	   @RequestMapping(value="/machineInfo/getMachineListInfo", method = RequestMethod.POST)
	   public List<Map<String,Object>>  getMachineListInfo(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into getMachineListInfo ======");			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();	
			
			//list =  所有要抓資訊的機台
			List<Map<String,Object>> list = gson.fromJson(parameters,new TypeToken<List<Map<String,Object>>>(){}.getType());
			
			//抓單一機台資訊
			for(Map<String,Object> obj :list){				
				obj = getSingleMachineInfo(obj,"main");	//狀態、轉速進給、報警資訊						
			}			
			logger.debug("===== End getMachineListInfo ======");
		    return list;
		 }
	   
	   /*獲取單一機台狀態(Main)
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/statusInfo
	    *
	    *parameters type
	    * {"machineName":"CNC3","machineId":123,"toolSetNum":2,"toolSetList":[0,1]}
	    * 
	    */
	   @RequestMapping(value="/machineDetInfo/getSingleStatusInfo", method = RequestMethod.POST)
	   public Map<String,Object>  getSingleStatusInfoMain(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into getSingleStatusInfoMain ======");			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();	
			
			Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());				
			obj = getSingleMachineInfo(obj,"detail");	//get 大部分的方法	
			
			
			logger.debug("===== End getSingleStatusInfoMain ======");
		    return obj;
		 }
	   
	   /*獲取單一機台資訊(main)
	    * 
	    * type："main" "detail"
	    * */
	   public Map<String, Object> getSingleMachineInfo(Map<String,Object> obj,String type) throws Exception{
			logger.debug("===== into getSingleMachineInfo ======");	
		   Gson gson = new Gson();	
		   
		   /*抓取機台port資訊*/
			Map<String,Object> mapQueryInfo = new HashMap<String,Object>();
			mapQueryInfo.put("machineName", obj.get("machineName"));
			QueryMachineInfo machineInfo= monitorMainController.queryMachineInfo(gson.toJson(mapQueryInfo));
			boolean isConnect = machineInfo.machine[0].isConnect;				
			int port =machineInfo.machine[0].urlPort;			
			logger.debug("port:"+port);
			
		   /*抓取機台狀態資訊，多個path要抓取多次*/
			@SuppressWarnings("unchecked")
			List<Object> toolSetList =  (List<Object>) obj.get("toolSetList");
			List<Map<String,Object>> toolSetListMap = new ArrayList<>();
			logger.debug("toolSetList.size():"+toolSetList.size());
			
			if(!isConnect){
				  for(int i = 0 ; i < toolSetList.size() ; i++){
					  Map<String,Integer> mapToInfo = new HashMap<String,Integer>();
					  mapToInfo.put("sysNo",  i+1 ); /*sysNo第一個是1*/		
					  String sysNoarameter = gson.toJson(mapToInfo);
					  
					  StatusInfo statusObj = new StatusInfo();
					  statusObj.run = -3;
					  statusObj.resultCode = 0;
					  statusObj.errorInfo = "";
					  statusObj.alarm = false;
					  
					  HashMap<String,Object> toolSetResult = new HashMap<>();
					  toolSetResult.put("toolSetId",toolSetList.get(i));
					  toolSetResult.put("sysNo", (i+1));
						
					  toolSetResult.put("statusUrl", monitorIp +":"+ port + statusInfo+"?"+sysNoarameter);
					  toolSetResult.put("statusResultCode", (String.valueOf(statusObj.resultCode)));
					  toolSetResult.put("statusErrorInfo", statusObj.errorInfo);
					  toolSetResult.put("statusRun", statusObj.run);
					  toolSetResult.put("statusAlarm", statusObj.alarm);
					  toolSetListMap.add(toolSetResult);
				  }
			}
			
			if(isConnect){
				for(int i = 0 ; i < toolSetList.size() ; i++){	
					Map<String,Integer> mapToInfo = new HashMap<String,Integer>();
					mapToInfo.put("sysNo",  i+1 ); /*sysNo第一個是1*/		
					String sysNoarameter = gson.toJson(mapToInfo);
					HashMap<String,Object> toolSetResult = new HashMap<>();
					toolSetResult = machineMainController.getStatusInfo(port, sysNoarameter, i, (String)toolSetList.get(i), toolSetResult);
					toolSetResult = machineMainController.getspeedFeedRateMain(obj, port,sysNoarameter, toolSetResult);
					if(type.equals("detail")){
						toolSetResult = machineMainController.curExecutePrgInfo(port, sysNoarameter, toolSetResult);	
						toolSetResult = machineMainController.cumulativeTime(port, sysNoarameter, toolSetResult);	
						toolSetResult = machineMainController.getPartInfo(port, sysNoarameter, toolSetResult);	
						toolSetResult = machineMainController.otherCode(port, sysNoarameter, toolSetResult);	
						toolSetResult = machineMainController.gCodeInfo(port, sysNoarameter, toolSetResult);	
						toolSetResult = machineMainController.prgContentInfo(port, sysNoarameter, toolSetResult);	
						toolSetResult = machineMainController.getPositionInfo(port, sysNoarameter, toolSetResult);	
						toolSetResult = machineMainController.singleWorkOffsetInfo(port, sysNoarameter, toolSetResult);	

					}
					toolSetListMap.add(toolSetResult);
				}
			}
			//放置toolSet資訊至機台	
			obj.put("toolSetListMap",toolSetListMap);
			logger.debug("===== End getSingleMachineInfo ======");	
			return obj;
	   }
	   
	   
	   
	   
	   
}

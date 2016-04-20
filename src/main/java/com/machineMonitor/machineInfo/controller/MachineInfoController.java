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

import com.globaltek.machineLib.CurrentAlarm;
import com.globaltek.machineLib.CurrentExecuteNCInfo;
import com.globaltek.machineLib.QueryMachineInfo;
import com.globaltek.machineLib.SpeedFeedRate;
import com.globaltek.machineLib.StatusInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.machineMonitor.general.contoller.MonitorMainController;


@RestController
public class MachineInfoController {
	 
	 protected final transient Logger logger = Logger.getLogger(this.getClass());

	 
	   @Value("${monitor.ip}")
	   String monitorIp;
	   
	   @Value("${monitor.machineSetPort}")
	   String machineSetPort;	  
	   
	   @Value("${monitor.statusInfo}")
	   String statusInfo;
	   
	   @Value("${monitor.feedrateSpeedInfo}")
	   String feedrateSpeedInfo;
	   
	   @Value("${monitor.makino.feedrateSpeedInfo}")
	   String makinoFeedrateSpeedInfo;
	   
	   @Value("${monitor.makino.machineBrands}")
	   String[] makinoMachineBrands;
	   
	   @Value("${monitor.makino.machineTypes}")
	   String[] makinoMachineTypes;
	   
	   @Autowired
	   MonitorMainController monitorMainController;
	   
	   @Value("${monitor.curExecutePrgInfo}")
	   String curExecutePrgInfo;
	   
	   @Value("${monitor.getCurrentAlarmInfo}")
	   String getCurrentAlarmInfo;  
	   
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
				@SuppressWarnings("unchecked")
				List<Object> toolSetList =  (List<Object>) obj.get("toolSetList");
				List<Map<String,Object>> toolSetListMap = new ArrayList<>();
				
				for(int i = 0 ; i < toolSetList.size() ; i++){				
					Map<String,Integer> mapToInfo = new HashMap<String,Integer>();
					mapToInfo.put("sysNo",  i+1 ); /*sysNo第一個是1*/
					
					String sysNoarameter = gson.toJson(mapToInfo);
					StatusInfo statusObj = monitorMainController.getStatusInfo(String.valueOf(port),sysNoarameter);	
					
					//是否為makino
					String machineBrandId =(String)obj.get("machineBrandId");
					String machineTypeId =(String)obj.get("machineTypeId");
					List<String> makinoMachineBrandList = Arrays.asList(makinoMachineBrands);
					List<String> makinoMachineTypeList = Arrays.asList(makinoMachineTypes);
					
					boolean isMakino = false;
					if(makinoMachineBrandList.contains(machineBrandId) && makinoMachineTypeList.contains(machineTypeId)){
						logger.debug("is makino:" + isMakino);
						isMakino = true;
					 }					
					
					SpeedFeedRate speedFeedRateObj = monitorMainController.feedrateSpeedInfo(isMakino,String.valueOf(port),sysNoarameter);			
					CurrentExecuteNCInfo curExecutePrgInfoObj = monitorMainController.curExecutePrgInfo(String.valueOf(port),sysNoarameter);			
					CurrentAlarm  currentAlarm = null;
					if(statusObj.alarm){
					   currentAlarm= monitorMainController.getCurrentAlarmInfo(String.valueOf(port),sysNoarameter);
					}
					//to Objectjson				
					HashMap<String,Object> toolSetResult = new HashMap<>();
					toolSetResult.put("toolSetId",toolSetList.get(i));
					toolSetResult.put("sysNo", (i+1));
					
					toolSetResult.put("statusUrl", monitorIp +":"+ port + statusInfo+"?"+sysNoarameter);
					toolSetResult.put("statusResultCode", (String.valueOf(statusObj.resultCode)));
					toolSetResult.put("statusErrorInfo", statusObj.errorInfo);
					toolSetResult.put("statusRun", statusObj.run);
					toolSetResult.put("status", statusObj.status);
					toolSetResult.put("statusAut", statusObj.aut);
					toolSetResult.put("statusMode", statusObj.mode);
					toolSetResult.put("statusEmergency", statusObj.emergency);
					toolSetResult.put("statusAlarm", statusObj.alarm);				


					toolSetResult.put("speedFeedUrl", monitorIp +":"+ port + (isMakino ? makinoFeedrateSpeedInfo :feedrateSpeedInfo+"?"+sysNoarameter));
					toolSetResult.put("speedFeedResultCode", (String.valueOf(speedFeedRateObj.resultCode)));
					toolSetResult.put("speedFeedErrorInfo", speedFeedRateObj.errorInfo);
					toolSetResult.put("speedFeedOvFeed", speedFeedRateObj.OvFeed);
					toolSetResult.put("speedFeedOvSpeed", speedFeedRateObj.OvSpeed);
					toolSetResult.put("speedFeedActFeed", speedFeedRateObj.ActFeed);
					toolSetResult.put("speedFeedActSpeed", speedFeedRateObj.ActSpeed);
					toolSetResult.put("speedFeedFPCT", speedFeedRateObj.FPCT);
					toolSetResult.put("speedFeedSPCT", speedFeedRateObj.SPCT);
					toolSetResult.put("speedFeedQPCT", speedFeedRateObj.QPCT);

					toolSetResult.put("curExecutePrgInfoUrl", monitorIp +":"+ port + curExecutePrgInfo+"?"+sysNoarameter);
					toolSetResult.put("curExecutePrgInfoResultCode", (String.valueOf(curExecutePrgInfoObj.resultCode)));
					toolSetResult.put("curExecutePrgInfoErrorInfo", curExecutePrgInfoObj.errorInfo);
					toolSetResult.put("curExecutePrgInfoMacinPrg", curExecutePrgInfoObj.macinPrg);
					toolSetResult.put("curExecutePrgInfoRunPrg", curExecutePrgInfoObj.runPrg);
					toolSetResult.put("curExecutePrgInfoSequence", curExecutePrgInfoObj.sequence);
					
					if(currentAlarm!=null){
						toolSetResult.put("currentAlarmUrl", monitorIp +":"+ port + getCurrentAlarmInfo+"?"+sysNoarameter);
						toolSetResult.put("currentAlarmResultCode", (String.valueOf(currentAlarm.resultCode)));
						toolSetResult.put("currentAlarmErrorInfo", currentAlarm.errorInfo);
						toolSetResult.put("currentAlarmAlarmType", currentAlarm.alarmType);
						toolSetResult.put("currentAlarmAlarmNO", currentAlarm.alarmNO);
						toolSetResult.put("currentAlarmAlarmMsg", currentAlarm.alarmMsg);
					}				
					
					toolSetListMap.add(toolSetResult);
				}
					obj.put("toolSetListMap",toolSetListMap);
			}			
			logger.debug("===== End getStatusInfo ======");
		    return list;
		 }
	   
	  
}

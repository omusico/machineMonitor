package com.machineMonitor.machineSetting.controller;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.globaltek.machineLib.GeneralResult;//數據封包
import com.globaltek.machineLib.QueryMachineInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.machineMonitor.general.contoller.MainSetController;
import com.machineMonitor.general.contoller.MonitorMainController;


@RestController
public class MachineSetController {
	   protected final transient Logger logger = Logger.getLogger(this.getClass());
	  
	   @Value("${monitor.ip}")
	   String monitorIp;
	   
	   @Value("${monitor.machineSetPort}")
	   String machineSetPort;
	
	   @Autowired
	   MonitorMainController monitorMainController;
	   

	   /*新增機台*
	    * parameters type
		* 
		* {"machineIp":"10.1.21.52","controllerType":"31i","controllerManufacturer":"Fanuc","portNum":"8193","machineName":"CNC15"}
		*/
	   @RequestMapping(value="/machineSet/machineAdd", method = RequestMethod.POST)
	   public List<Map<String,String>>  machineAdd(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into machineAdd ======");				
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();			
			
			List<Map<String,String>> list = gson.fromJson(parameters,new TypeToken<List<Map<String,String>>>(){}.getType());
			
			for(Map<String,String> obj :list){
				String singleMachine = gson.toJson(obj);
				// 調用添加機台信息
				GeneralResult gResult= monitorMainController.addMachine(singleMachine);
				obj.put("resultCode", (String.valueOf(gResult.resultCode)));
				obj.put("errorInfo", gResult.errorInfo);
			}		
			logger.debug("===== End machineAdd ======");
		    return list;
		 }  
	   
	 
	   
}

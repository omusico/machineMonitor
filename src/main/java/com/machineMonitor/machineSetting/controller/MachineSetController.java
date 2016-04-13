package com.machineMonitor.machineSetting.controller;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.globaltek.machineLib.GeneralResult;//數據封包
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.machineMonitor.general.contoller.MainSetController;


@RestController
public class MachineSetController {
	   protected final transient Logger logger = Logger.getLogger(this.getClass());
	  
	   @Value("${monitor.ip}")
	   String monitorIp;
	   
	   @Value("${monitor.machineSetPort}")
	   String machineSetPort;
		
	   @Value("${monitor.machineAddMethod}")
	   String machineAddMethod;
	   
	   /*獲取機台供應商*/
	   @RequestMapping(value="/machineSet/machineAdd", method = RequestMethod.POST)
		  public List<Map<String,String>>  machineAdd(@RequestBody String data) throws Exception {	 
			logger.debug("===== into machineAdd ======");		
			
			String parameters = data;
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();			
			
			List<Map<String,String>> list = gson.fromJson(parameters,new TypeToken<List<Map<String,String>>>(){}.getType());
			for(Map<String,String> obj :list){
				String singleMachine = gson.toJson(obj);
				// 調用添加機台信息
				String  urlPath = monitorIp +":"+ machineSetPort + machineAddMethod;
				MainSetController mainSet = new MainSetController();
				String result = mainSet.sendPost(urlPath, singleMachine);
				
				//to Objectjson				
				GeneralResult gResult= gson.fromJson(result, new TypeToken<GeneralResult>(){}.getType());		

				obj.put("resultCode", (String.valueOf(gResult.resultCode)));
				obj.put("errorInfo", gResult.errorInfo);
			}
			
			
			
			logger.debug("===== End machineAdd ======");
		    return list;
		  }  
}

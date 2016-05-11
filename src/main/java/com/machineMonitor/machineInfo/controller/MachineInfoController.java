package com.machineMonitor.machineInfo.controller;

import java.net.URLEncoder;
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
import com.google.gson.internal.LinkedTreeMap;
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
	   
	   /*獲取單一機台狀態(detail)
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
	   
	   /*獲取單一機台資訊
	    * 
	    * type："main" "detail"
	    * */
	   public Map<String, Object> getSingleMachineInfo(Map<String,Object> obj,String type) throws Exception{
			logger.debug("===== into getSingleMachineInfo ======");	
		   Gson gson = new Gson();	
		   
		   /*抓取機台port資訊*/
		    QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
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
					  logger.debug(Integer.valueOf((String) toolSetList.get(i))+1);
					  mapToInfo.put("sysNo",  Integer.valueOf((String) toolSetList.get(i))+1 ); /*sysNo第一個是1*/		
					  String sysNoparameter = gson.toJson(mapToInfo);
					  
					  StatusInfo statusObj = new StatusInfo();
					  statusObj.run = -3;
					  statusObj.resultCode = 0;
					  statusObj.errorInfo = "";
					  statusObj.alarm = false;
					  
					  HashMap<String,Object> toolSetResult = new HashMap<>();
					  toolSetResult.put("toolSetId",toolSetList.get(i));
					  toolSetResult.put("sysNo", Integer.valueOf((String) toolSetList.get(i))+1);
						
					  toolSetResult.put("statusUrl", monitorIp +":"+ port + statusInfo+"?"+sysNoparameter);
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
					mapToInfo.put("sysNo",  Integer.valueOf((String) toolSetList.get(i))+1 ); /*sysNo第一個是1*/		
					String sysNoparameter = gson.toJson(mapToInfo);
					HashMap<String,Object> toolSetResult = new HashMap<>();
					toolSetResult = machineMainController.getStatusInfo(port, sysNoparameter, i, (String)toolSetList.get(i), toolSetResult);
					toolSetResult = machineMainController.getspeedFeedRateMain(obj, port,sysNoparameter, toolSetResult);
					toolSetResult = machineMainController.curExecutePrgInfo(port, sysNoparameter, toolSetResult);	
					toolSetResult = machineMainController.getPartInfo(port, sysNoparameter, toolSetResult);	

					if(type.equals("detail")){
						toolSetResult = machineMainController.cumulativeTime(port, sysNoparameter, toolSetResult);
						toolSetResult = machineMainController.otherCode(port, sysNoparameter, toolSetResult);	
						toolSetResult = machineMainController.gCodeInfo(port, sysNoparameter, toolSetResult);	
						toolSetResult = machineMainController.prgContentInfo(port, sysNoparameter, toolSetResult);	
						toolSetResult = machineMainController.getPositionInfo(port, sysNoparameter, toolSetResult);	

					}
					toolSetListMap.add(toolSetResult);
				}
			}
			//放置toolSet資訊至機台	
			obj.put("toolSetListMap",toolSetListMap);
			logger.debug("===== End getSingleMachineInfo ======");	
			return obj;
	   }
	   

	   /*獲取查詢的port及其他連線資訊
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/queryMachineInfo
	    *
	    *parameters type
	    * {"machineName":"CNC3","machineId":123,"toolSetId":1}
	    * 
	    */
	   public QueryMachineInfo getQueryInfo(String machineName) throws Exception{
		   Gson gson = new Gson();	
		    Map<String,Object> mapQueryInfo = new HashMap<String,Object>();
			mapQueryInfo.put("machineName", machineName);
			QueryMachineInfo machineInfo= monitorMainController.queryMachineInfo(gson.toJson(mapQueryInfo));
			return machineInfo;			
	   }
	  
	   /*獲取刀具資訊
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/getAllToolOffset
	    *
	    *parameters type
	    * {"machineName":"CNC3","machineId":123,"toolSetId":1}
	    * 
	    */
	   @RequestMapping(value="/machineDetInfo/getAllToolOffset", method = RequestMethod.POST)
	   public Map<String,Object>  getAllToolOffset(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into getAllToolOffset ======");			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();	
			//要抓資訊的機台
			Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());	
			 /*抓取機台port資訊*/
			QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
			int port =machineInfo.machine[0].urlPort;		
			
			Map<String,String> mapToInfo = new HashMap<String,String>();
			mapToInfo.put("sysNo", (String) obj.get("toolSetId"));
			String sysNoparameter = gson.toJson(mapToInfo);
			HashMap<String,Object> toolSetResult = new HashMap<>();
			
			logger.debug("port:"+port);
			toolSetResult = machineMainController.getAllToolOffset(port, sysNoparameter, toolSetResult);	
			toolSetResult = machineMainController.getPartInfo(port, sysNoparameter, toolSetResult);	

			obj.put("toolSetResult",toolSetResult);
			logger.debug("===== End getAllToolOffset ======");
		    return obj;
		 }
	   
	   

	   /*獲取工件補正資訊
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/allWorkOffsetInfo
	    *
	    *parameters type
	    * {"machineName":"CNC3","machineId":123,"toolSetId":1}
	    * 
	    */
	   @RequestMapping(value="/machineDetInfo/getSingleToolSetWorkPieceInfo", method = RequestMethod.POST)
	   public Map<String,Object>  getSingleToolSetWorkPieceInfo(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into getSingleToolSetWorkPieceInfo ======");			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();	
			//要抓資訊的機台
			Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());	
			 /*抓取機台port資訊*/
			QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
			int port =machineInfo.machine[0].urlPort;		
			
			Map<String,String> mapToInfo = new HashMap<String,String>();
			mapToInfo.put("sysNo", (String) obj.get("toolSetId"));
			String sysNoparameter = gson.toJson(mapToInfo);
			HashMap<String,Object> toolSetResult = new HashMap<>();
			
			logger.debug("port:"+port);
			toolSetResult = machineMainController.allWorkOffsetInfo(port, sysNoparameter, toolSetResult);

			obj.put("toolSetResult",toolSetResult);
			logger.debug("===== End getSingleToolSetWorkPieceInfo ======");
		    return obj;
		 }
	   
	   /*獲取Macro資料(多個)
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/allWorkOffsetInfo
	    *
	    *parameters type
	    * {"machineName":"CNC3","machineId":123,"toolSetId":1}
	    * 
	    */
	   @RequestMapping(value="/machineDetInfo/getMacroList", method = RequestMethod.POST)
	   public Map<String,Object>  getMacroList(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into getMacroList ======");	
			Gson gson = new Gson();	
			//要抓資訊的機台
			Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());	
			 /*抓取機台port資訊*/
			QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
			int port =machineInfo.machine[0].urlPort;		
			
			
			List<HashMap<String,Object>> macroResultList = new ArrayList<>();
			for(LinkedTreeMap<String,Object> macro : (List<LinkedTreeMap<String,Object>>)obj.get("macroList")){
				 /*產生查詢參數*/
				Map<String,String> mapToInfo = new HashMap<String,String>();			
				HashMap<String,Object> toolSetResult = new HashMap<>();

				mapToInfo.put("sysNo", (String) obj.get("toolSetId"));
				mapToInfo.put("macroId",(String) macro.get("macroId"));
				
				String parameter = gson.toJson(mapToInfo);
				
				toolSetResult = machineMainController.getSingleMacro(port, parameter, toolSetResult);
				toolSetResult.put("macroId",(String) macro.get("macroId"));				
				macroResultList.add(toolSetResult);
			}
			obj.put("macroResultList", macroResultList);
			logger.debug("===== End getMacroList ======");
		    return obj;
		 }
	   
	 
	   /*獲取內存程式清單
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/memoryNcInfo
	    *
	    *parameters type
	    * {"machineName":"CNC3","machineId":123,"toolSetId":1,"controllerBrandId":1,"folderPath":aaa}
	    * 
	    */
	   @RequestMapping(value="/machineDetInfo/getMemoryNcInfo", method = RequestMethod.POST)
	   public Map<String,Object>  getMemoryNcInfo(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into getMemoryNcInfo ======");			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();	
			//要抓資訊的機台
			Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());	
			 /*抓取機台port資訊*/
			QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
			int port =machineInfo.machine[0].urlPort;		
			
			 /*產生查詢參數*/
			Map<String,String> mapToInfo = new HashMap<String,String>();			
			HashMap<String,Object> toolSetResult = new HashMap<>();
			String controllerBrandId =(String)obj.get("controllerBrandsId");
			String folderPath = "";
			if(!controllerBrandId.equals("0")){
				folderPath = (String)obj.get("folderPath");
				folderPath= URLEncoder.encode(folderPath,"UTF-8");				
			}
			mapToInfo.put("sysNo", (String) obj.get("toolSetId"));
			mapToInfo.put("folderPath",folderPath);
			
			String parameter = gson.toJson(mapToInfo);
			logger.debug("port:"+port);
			toolSetResult = machineMainController.memoryNcInfo(port, parameter, toolSetResult);

			obj.put("toolSetResult",toolSetResult);
			logger.debug("===== End getMemoryNcInfo ======");
		    return obj;
		 }
	   
	/*上傳NC程式至內存MEM
    *
    *url
    * http://localhost:9501/globaltek/machine/service/uploadNcToMemory
    *
    *parameters type
    * {"machineName":"CNC3","machineId":123,"toolSetId":1,"filePath":aaa,"NCName":xxx}
    * 
    */
	@RequestMapping(value="/machineDetInfo/uploadMemoryNcInfo", method = RequestMethod.POST)
	public Map<String,Object>  uploadMemoryNcInfo(@RequestBody String parameters) throws Exception {	 
		logger.debug("===== into uploadMemoryNcInfo ======");			
		logger.debug("parameters:" +parameters);
		Gson gson = new Gson();	
		//要抓資訊的機台
		Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());	
		 /*抓取機台port資訊*/
		QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
		int port =machineInfo.machine[0].urlPort;		
		
		/*產生查詢參數*/
		Map<String,String> mapToInfo = new HashMap<String,String>();
		HashMap<String,Object> toolSetResult = new HashMap<>();
		
		String uploadfilePath = (String)obj.get("uploadfilePath");
		uploadfilePath= URLEncoder.encode(uploadfilePath,"UTF-8");
		
		String NCName = (String)obj.get("NCName");
		NCName= URLEncoder.encode(NCName,"UTF-8");
		
		mapToInfo.put("sysNo", (String) obj.get("toolSetId"));
		mapToInfo.put("filePath",uploadfilePath);
		mapToInfo.put("NCName",NCName);
		
		String parameter = gson.toJson(mapToInfo);
		logger.debug("port:"+port);
		toolSetResult = machineMainController.uploadNcToMemory(port, parameter, toolSetResult);

		obj.put("toolSetResult",toolSetResult);
		logger.debug("===== End uploadMemoryNcInfo ======");
	    return obj;
	 }
	    
	/*下載NC程式至本地
    *
    *url
    * http://localhost:9501/globaltek/machine/service/downloadMemoryNc
    *
    *parameters type
    * {"machineName":"CNC3","machineId":123,"toolSetId":1,"localPath":aaa,"NCName":xxx}
    * 
    */
	@RequestMapping(value="/machineDetInfo/downloadMemoryNc", method = RequestMethod.POST)
	public Map<String,Object>  downloadMemoryNc(@RequestBody String parameters) throws Exception {	 
		logger.debug("===== into downloadMemoryNc ======");			
		logger.debug("parameters:" +parameters);
		Gson gson = new Gson();	
		//要抓資訊的機台
		Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());	
		 /*抓取機台port資訊*/
		QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
		int port =machineInfo.machine[0].urlPort;		
		
		/*產生查詢參數*/
		Map<String,String> mapToInfo = new HashMap<String,String>();
		HashMap<String,Object> toolSetResult = new HashMap<>();
		
		String localPath = (String)obj.get("localPath");
		localPath= URLEncoder.encode(localPath,"UTF-8");
		
		String NCName = (String)obj.get("NCName");
		NCName= URLEncoder.encode(NCName,"UTF-8");
		
		mapToInfo.put("sysNo", (String) obj.get("toolSetId"));
		mapToInfo.put("localPath",localPath);
		mapToInfo.put("NCName",NCName);
		
		String parameter = gson.toJson(mapToInfo);
		logger.debug("port:"+port);
		toolSetResult = machineMainController.downloadMemoryNc(port, parameter, toolSetResult);

		obj.put("toolSetResult",toolSetResult);
		logger.debug("===== End downloadMemoryNc ======");
	    return obj;
	 }
	 
	
	/*刪除NC程式
    *
    *url
    * http://localhost:9501/globaltek/machine/service/deleteMemoryNc
    *
    *parameters type
    * {"machineName":"CNC3","machineId":123,"toolSetId":1,"NCName":xxx}
    * 
    */
	@RequestMapping(value="/machineDetInfo/deleteMemoryNc", method = RequestMethod.POST)
	public Map<String,Object>  deleteMemoryNc(@RequestBody String parameters) throws Exception {	 
		logger.debug("===== into downloadMemoryNc ======");			
		logger.debug("parameters:" +parameters);
		Gson gson = new Gson();	
		//要抓資訊的機台
		Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());	
		 /*抓取機台port資訊*/
		QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
		int port =machineInfo.machine[0].urlPort;		
		
		/*產生查詢參數*/
		Map<String,String> mapToInfo = new HashMap<String,String>();		
		HashMap<String,Object> toolSetResult = new HashMap<>();
		
		String NCName = (String)obj.get("NCName");
		NCName= URLEncoder.encode(NCName,"UTF-8");
		
		mapToInfo.put("sysNo", (String) obj.get("toolSetId"));
		mapToInfo.put("NCName",NCName);
		
		String parameter = gson.toJson(mapToInfo);
		logger.debug("port:"+port);
		toolSetResult = machineMainController.deleteMemoryNc(port, parameter, toolSetResult);

		obj.put("toolSetResult",toolSetResult);
		logger.debug("===== End deleteMemoryNc ======");
	    return obj;
	 }
	   
	   /*獲取FTP程式清單
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/ftpNcInfo
	    *
	    *parameters type
	    * {"userId":"","password":"","ftpPath":""}
	    * 
	    */
	   @RequestMapping(value="/machineDetInfo/getFtpNcInfo", method = RequestMethod.POST)
	   public Map<String,Object>  getFtpNcInfo(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into getFtpNcInfo ======");			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();	
			//要抓資訊的機台
			Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());	
			 /*抓取機台port資訊*/
			QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
			int port =machineInfo.machine[0].urlPort;		
			
			 /*產生查詢參數*/
			HashMap<String,Object> toolSetResult = new HashMap<>();
			Map<String,String> mapToInfo = new HashMap<String,String>();			
			mapToInfo.put("userId", (String) obj.get("ftpAccount"));;
			mapToInfo.put("password", (String) obj.get("ftpPassword"));
			mapToInfo.put("ftpPath",(String) obj.get("ftpPath"));
			
			String parameter = gson.toJson(mapToInfo);
			logger.debug("port:"+port);
			toolSetResult = machineMainController.ftpNcInfo(port, parameter, toolSetResult);

			obj.put("toolSetResult",toolSetResult);
			logger.debug("===== End getFtpNcInfo ======");
		    return obj;
		 }
	   
	/*上傳NC程式至FTP
	 *
	 *url
	 * http://localhost:9501/globaltek/machine/service/uoloadNcToFtp
	 *
	 *parameters type
	 * {"userId":"","password":"","ftpPath":"","localPath":"%5c%5clocalhost%5cNcTempFile%5cCNC%5cCNC1%5cO5000"}
	 * 
	 */
		@RequestMapping(value="/machineDetInfo/uploadNcToFtp", method = RequestMethod.POST)
		public Map<String,Object>  uploadNcToFtp(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into uploadNcToFtp ======");			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();	
			//要抓資訊的機台
			Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());	
			 /*抓取機台port資訊*/
			QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
			int port =machineInfo.machine[0].urlPort;		
			
			/*產生查詢參數*/
			HashMap<String,Object> toolSetResult = new HashMap<>();
			Map<String,String> mapToInfo = new HashMap<String,String>();			
			mapToInfo.put("userId", (String) obj.get("ftpAccount"));;
			mapToInfo.put("password", (String) obj.get("ftpPassword"));
			mapToInfo.put("ftpPath",(String) obj.get("ftpPath"));
			
			String uploadfilePath = (String)obj.get("uploadfilePath");
			uploadfilePath= URLEncoder.encode(uploadfilePath,"UTF-8");
			mapToInfo.put("localPath",uploadfilePath);
			
			String parameter = gson.toJson(mapToInfo);
			logger.debug("port:"+port);
			toolSetResult = machineMainController.uploadNcToFtp(port, parameter, toolSetResult);
	
			obj.put("toolSetResult",toolSetResult);
			logger.debug("===== End uploadNcToFtp ======");
		    return obj;
		 }
	    
		/*從FTP下載指定程式
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/downloadNcFromFtp
	    *
	    *parameters type
	    *{"userId":"","password":"","NCName":"O5000","ftpPath":"","localPath":"D%3a%5cJimmy"}
	    * 
	    */
		@RequestMapping(value="/machineDetInfo/downloadNcFromFtp", method = RequestMethod.POST)
		public Map<String,Object>  downloadNcFromFtp(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into downloadNcFromFtp ======");			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();	
			//要抓資訊的機台
			Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());	
			 /*抓取機台port資訊*/
			QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
			int port =machineInfo.machine[0].urlPort;		
			
			/*產生查詢參數*/
			HashMap<String,Object> toolSetResult = new HashMap<>();
			Map<String,String> mapToInfo = new HashMap<String,String>();			
			mapToInfo.put("userId", (String) obj.get("ftpAccount"));;
			mapToInfo.put("password", (String) obj.get("ftpPassword"));
			mapToInfo.put("ftpPath",(String) obj.get("ftpPath"));
			mapToInfo.put("NCName",(String) obj.get("NCName"));

			String localPath = (String)obj.get("localPath");
			localPath= URLEncoder.encode(localPath,"UTF-8");
			mapToInfo.put("localPath",localPath);
			
			String parameter = gson.toJson(mapToInfo);
			logger.debug("port:"+port);
			toolSetResult = machineMainController.downloadNcFromFtp(port, parameter, toolSetResult);

			obj.put("toolSetResult",toolSetResult);
			logger.debug("===== End downloadNcFromFtp ======");
		    return obj;
		 }
		 
	    
		/*刪除NC程式
	    *
	    *url
	    * http://localhost:9501/globaltek/machine/service/deleteNcFileInFtp
	    *
	    *parameters type
	    * {"userId":"","password":"","NCName":"O5000","ftpPath":""}
	    * 
	    */
		@RequestMapping(value="/machineDetInfo/deleteNcFileInFtp", method = RequestMethod.POST)
		public Map<String,Object>  deleteNcFileInFtp(@RequestBody String parameters) throws Exception {	 
			logger.debug("===== into deleteNcFileInFtp ======");			
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();	
			//要抓資訊的機台
			Map<String,Object> obj = gson.fromJson(parameters,new TypeToken<Map<String,Object>>(){}.getType());	
			 /*抓取機台port資訊*/
			QueryMachineInfo machineInfo= getQueryInfo((String)obj.get("machineName"));
			int port =machineInfo.machine[0].urlPort;		
			
			/*產生查詢參數*/
			HashMap<String,Object> toolSetResult = new HashMap<>();
			Map<String,String> mapToInfo = new HashMap<String,String>();			
			mapToInfo.put("userId", (String) obj.get("ftpAccount"));;
			mapToInfo.put("password", (String) obj.get("ftpPassword"));
			mapToInfo.put("ftpPath",(String) obj.get("ftpPath"));
			mapToInfo.put("NCName",(String) obj.get("NCName"));
			
			String parameter = gson.toJson(mapToInfo);
			logger.debug("port:"+port);
			toolSetResult = machineMainController.deleteNcFileInFtp(port, parameter, toolSetResult);

			obj.put("toolSetResult",toolSetResult);
			logger.debug("===== End deleteNcFileInFtp ======");
		    return obj;
		 }
}

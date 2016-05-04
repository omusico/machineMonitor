package com.machineMonitor.general.contoller;

import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.globaltek.machineLib.CumulativeTimeInfo;
import com.globaltek.machineLib.CurrentAlarm;
import com.globaltek.machineLib.CurrentExecuteNCInfo;
import com.globaltek.machineLib.ExecutePrgContent;
import com.globaltek.machineLib.FtpNCList;
import com.globaltek.machineLib.GCode;
import com.globaltek.machineLib.GeneralResult;
import com.globaltek.machineLib.MachinePositionInfo;
import com.globaltek.machineLib.MacroInfo;
import com.globaltek.machineLib.MemoryNCInfo;
import com.globaltek.machineLib.OtherCode;
import com.globaltek.machineLib.PartCount;
import com.globaltek.machineLib.QueryMachineInfo;
import com.globaltek.machineLib.SpeedFeedRate;
import com.globaltek.machineLib.StatusInfo;
import com.globaltek.machineLib.ToolInfo;
import com.globaltek.machineLib.WorkOffset;
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
	   
	   @Value("${monitor.nakamura.feedrateSpeedInfo}")
	   String nakamuraFeedrateSpeedInfo;
	   
	   @Value("${monitor.getCurrentAlarmInfo}")
	   String getCurrentAlarmInfo;     
	   
	   @Value("${monitor.cumulativeTime}")
	   String cumulativeTime;  
	   
	   @Value("${monitor.getPartInfo}")
	   String getPartInfo;  
	   
	   @Value("${monitor.otherCode}")
	   String otherCode;  
	   
	   @Value("${monitor.gCodeInfo}")
	   String gCodeInfo;  
	   
	   
	   @Value("${monitor.prgContentInfo}")
	   String prgContentInfo;  
	   
	   @Value("${monitor.getPositionInfo}")
	   String getPositionInfo; 
	   
	   
	   @Value("${monitor.singleWorkOffsetInfo}")
	   String singleWorkOffsetInfo;  
	     
	   @Value("${monitor.getAllToolOffset}")
	   String getAllToolOffset; 
	   
	   @Value("${monitor.allWorkOffsetInfo}")
	   String allWorkOffsetInfo; 
	   
	   @Value("${monitor.getSingleMacro}")
	   String getSingleMacro; 
	   
	   @Value("${monitor.memoryNcInfo}")
	   String memoryNcInfo;
	   
	   @Value("${monitor.uploadNcToMemory}")
	   String uploadNcToMemory;
	   
	   @Value("${monitor.downloadMemoryNc}")
	   String downloadMemoryNc;
	   
	   @Value("${monitor.deleteMemoryNc}")
	   String deleteMemoryNc;
	   
	   @Value("${monitor.ftpNcInfo}")
	   String ftpNcInfo;
	   
	   @Value("${monitor.uploadNcToFtp}")
	   String uploadNcToFtp;
	   
	   @Value("${monitor.downloadNcFromFtp}")
	   String downloadNcFromFtp;
	   
	   @Value("${monitor.deleteNcFileInFtp}")
	   String deleteNcFileInFtp;
	   
	   
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
	   public SpeedFeedRate  feedrateSpeedInfo(boolean isNakamura,boolean isMakino,String port,String parameters) throws Exception {	 
		   	logger.debug("===== into feedrateSpeedInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();		
			String  urlPath = monitorIp +":"+ port + feedrateSpeedInfo;		
			
			//是否為makino
			if(isMakino){
				logger.debug("is makino:"+isMakino);
				urlPath = monitorIp +":"+ port + makinoFeedrateSpeedInfo;		
			 }
			
			//是否為nakamura
			if(isNakamura){
				logger.debug("is isNakamura:"+isNakamura);
				urlPath = monitorIp +":"+ port + nakamuraFeedrateSpeedInfo;		
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
	   
	   /*獲取機台累計時間(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public CumulativeTimeInfo  cumulativeTime(String port,String parameters) throws Exception {	 
			logger.debug("===== into cumulativeTime ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + cumulativeTime;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
				
			//to Objectjson				
			CumulativeTimeInfo gResult= gson.fromJson(result, new TypeToken<CumulativeTimeInfo>(){}.getType());				
				
			logger.debug("===== End cumulativeTime ======");
		    return gResult;
		 }
	   
	   /*獲取工件數信息(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public PartCount  getPartInfo(String port,String parameters) throws Exception {	 
			logger.debug("===== into getPartInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + getPartInfo;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
				
			//to Objectjson				
			PartCount gResult= gson.fromJson(result, new TypeToken<PartCount>(){}.getType());				
				
			logger.debug("===== End getPartInfo ======");
		    return gResult;
		 }
	   
	   /*獲取OtherCode(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public OtherCode  otherCode(String port,String parameters) throws Exception {	 
			logger.debug("===== into otherCode ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + otherCode;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
				
			//to Objectjson				
			OtherCode gResult= gson.fromJson(result, new TypeToken<OtherCode>(){}.getType());				
				
			logger.debug("===== End otherCode ======");
		    return gResult;
		 }
	   
	   /*獲取GCode(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public GCode  gCodeInfo(String port,String parameters) throws Exception {	 
			logger.debug("===== into gCodeInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + gCodeInfo;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
				
			//to Objectjson				
			GCode gResult= gson.fromJson(result, new TypeToken<GCode>(){}.getType());				
				
			logger.debug("===== End gCodeInfo ======");
		    return gResult;
		 }
	   
	   /*獲取當前執行程式內容(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public ExecutePrgContent  prgContentInfo(String port,String parameters) throws Exception {	 
			logger.debug("===== into prgContentInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + prgContentInfo;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			result = URLDecoder.decode(result,"UTF-8");
			//to Objectjson				
			ExecutePrgContent gResult= gson.fromJson(result, new TypeToken<ExecutePrgContent>(){}.getType());				
				
			logger.debug("===== End prgContentInfo ======");
		    return gResult;
		 }
	   
	   /*獲取機台位置信息(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public MachinePositionInfo  getPositionInfo(String port,String parameters) throws Exception {	 
			logger.debug("===== into getPositionInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + getPositionInfo;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			MachinePositionInfo gResult= gson.fromJson(result, new TypeToken<MachinePositionInfo>(){}.getType());				
				
			logger.debug("===== End getPositionInfo ======");
		    return gResult;
		 }
	   
	   /*獲取單筆工件補正資料(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public WorkOffset  singleWorkOffsetInfo(String port,String parameters) throws Exception {	 
			logger.debug("===== into singleWorkOffsetInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + singleWorkOffsetInfo;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			WorkOffset gResult= gson.fromJson(result, new TypeToken<WorkOffset>(){}.getType());				
				
			logger.debug("===== End singleWorkOffsetInfo ======");
		    return gResult;
		 }
	   
	   /*獲取所有刀具補正信息(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public ToolInfo  getAllToolOffset(String port,String parameters) throws Exception {	 
			logger.debug("===== into getAllToolOffset ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + getAllToolOffset;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			ToolInfo gResult= gson.fromJson(result, new TypeToken<ToolInfo>(){}.getType());				
				
			logger.debug("===== End getAllToolOffset ======");
		    return gResult;
		 }
	   
	   
	   
	   /*獲取所有工件補正資料(Monitor)
	   /* parameters type
	    * 
	    * {"sysNo":1}
	    *
	   */
	   public WorkOffset  allWorkOffsetInfo(String port,String parameters) throws Exception {	 
			logger.debug("===== into allWorkOffsetInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + allWorkOffsetInfo;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			WorkOffset gResult= gson.fromJson(result, new TypeToken<WorkOffset>(){}.getType());				
				
			logger.debug("===== End allWorkOffsetInfo ======");
		    return gResult;
		 }
	   
	   /*獲取單筆Macro變數資料(Monitor)
	   /* parameters type
	    * 
	    *{"sysNo":1,"macroId":4109}
	    *
	   */
	   public MacroInfo  getSingleMacro(String port,String parameters) throws Exception {	 
			logger.debug("===== into getSingleMacro ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + getSingleMacro;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			MacroInfo gResult= gson.fromJson(result, new TypeToken<MacroInfo>(){}.getType());				
				
			logger.debug("===== End getSingleMacro ======");
		    return gResult;
		 }
	   
	   /*獲取內存程式清單(Monitor)
	   /* parameters type
	    * 
	    *{"sysNo":1}
	    *
	   */
	   public MemoryNCInfo  memoryNcInfo(String port,String parameters) throws Exception {	 
			logger.debug("===== into memoryNcInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + memoryNcInfo;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			MemoryNCInfo gResult= gson.fromJson(result, new TypeToken<MemoryNCInfo>(){}.getType());				
				
			logger.debug("===== End memoryNcInfo ======");
		    return gResult;
		 }

	   
	   /*上傳NC程式至內存(Monitor)
	   /* parameters type
	    * 
	    *{"sysNo":1}
	    *
	   */
	   public GeneralResult  uploadNcToMemory(String port,String parameters) throws Exception {	 
			logger.debug("===== into uploadNcToMemory ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + uploadNcToMemory;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			GeneralResult gResult= gson.fromJson(result, new TypeToken<GeneralResult>(){}.getType());				
				
			logger.debug("===== End uploadNcToMemory ======");
		    return gResult;
		 }
	   
	   /*下載NC程式至內存(Monitor)
	   /* parameters type
	    * 
	    *{"sysNo":1}
	    *
	   */
	   public GeneralResult  downloadMemoryNc(String port,String parameters) throws Exception {	 
			logger.debug("===== into downloadMemoryNc ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + downloadMemoryNc;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			GeneralResult gResult= gson.fromJson(result, new TypeToken<GeneralResult>(){}.getType());				
				
			logger.debug("===== End downloadMemoryNc ======");
		    return gResult;
		 }
	   
	   
	   /*刪除NC程式至內存(Monitor)
	   /* parameters type
	    * 
	    *{"sysNo":1}
	    *
	   */
	   public GeneralResult  deleteMemoryNc(String port,String parameters) throws Exception {	 
			logger.debug("===== into deleteMemoryNc ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + deleteMemoryNc;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			GeneralResult gResult= gson.fromJson(result, new TypeToken<GeneralResult>(){}.getType());				
				
			logger.debug("===== End deleteMemoryNc ======");
		    return gResult;
		 }
	   
	   /*獲取FTP程式清單(Monitor)
	   /* parameters type
	    * 
	    *{"userId":"","password":"","ftpPath":""}
	    *
	   */
	   public FtpNCList  ftpNcInfo(String port,String parameters) throws Exception {	 
			logger.debug("===== into ftpNcInfo ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + ftpNcInfo;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			FtpNCList gResult= gson.fromJson(result, new TypeToken<FtpNCList>(){}.getType());				
				
			logger.debug("===== End ftpNcInfo ======");
		    return gResult;
		 }

	   
	   /*上傳程式至FTP(Monitor)
	   /* parameters type
	    * 
	    *{"userId":"","password":"","ftpPath":"","localPath":"%5c%5clocalhost%5cNcTempFile%5cCNC%5cCNC1%5cO5000"}
	    *
	   */
	   public GeneralResult  uploadNcToFtp(String port,String parameters) throws Exception {	 
			logger.debug("===== into uploadNcToFtp ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + uploadNcToFtp;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			GeneralResult gResult= gson.fromJson(result, new TypeToken<GeneralResult>(){}.getType());				
				
			logger.debug("===== End uploadNcToFtp ======");
		    return gResult;
		 }
	   
	   /*從FTP下載指定程式(Monitor)
	   /* parameters type
	    * 
	    *{"userId":"","password":"","NCName":"O5000","ftpPath":"","localPath":"D%3a%5cJimmy"}
	    *
	   */
	   public GeneralResult  downloadNcFromFtp(String port,String parameters) throws Exception {	 
			logger.debug("===== into downloadNcFromFtp ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + downloadNcFromFtp;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			GeneralResult gResult= gson.fromJson(result, new TypeToken<GeneralResult>(){}.getType());				
				
			logger.debug("===== End downloadNcFromFtp ======");
		    return gResult;
		 }
	   
	   
	   /*刪除FTP程式(Monitor)
	   /* parameters type
	    * 
	    *{"userId":"","password":"","NCName":"O5000","ftpPath":""}
	    *
	   */
	   public GeneralResult  deleteNcFileInFtp(String port,String parameters) throws Exception {	 
			logger.debug("===== into deleteNcFileInFtp ======");
			logger.debug("parameters:" +parameters);
			Gson gson = new Gson();					
			   
			//調用獲取機台資訊
			String  urlPath = monitorIp +":"+ port + deleteNcFileInFtp;			
			String result = mainSet.sendPost(urlPath,parameters);
			logger.debug("result:" +result);
			//to Objectjson				
			GeneralResult gResult= gson.fromJson(result, new TypeToken<GeneralResult>(){}.getType());				
				
			logger.debug("===== End deleteNcFileInFtp ======");
		    return gResult;
		 }
}

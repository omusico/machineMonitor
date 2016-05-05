package com.machineMonitor.machineInfo.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

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
import com.globaltek.machineLib.SpeedFeedRate;
import com.globaltek.machineLib.StatusInfo;
import com.globaltek.machineLib.ToolInfo;
import com.globaltek.machineLib.WorkOffset;
import com.machineMonitor.general.contoller.MonitorMainController;


@RestController
public class MachineMainController {
	 
	 protected final transient Logger logger = Logger.getLogger(this.getClass());

	   @Autowired
	   MonitorMainController monitorMainController;
	   
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
	   
	   @Value("${monitor.nakamura.machineBrands}")
	   String[] nakamuraMachineBrands;
	   
	   @Value("${monitor.nakamura.machineTypes}")
	   String[] nakamuraMachineTypes;
	   
	  
	   @Value("${monitor.getCurrentAlarmInfo}")
	   String getCurrentAlarmInfo; 	   
	   
	   @Value("${monitor.curExecutePrgInfo}")
	   String curExecutePrgInfo;	   
	   
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
	   
	   /*獲取單ToolSet的狀態*/
	   public HashMap<String,Object> getStatusInfo(int port,String sysNoparameter,int i ,String toolSetId,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into getStatusInfo=====");			
			StatusInfo statusObj = monitorMainController.getStatusInfo(String.valueOf(port),sysNoparameter);	
			toolSetResult.put("toolSetId",toolSetId);
			toolSetResult.put("sysNo", (i+1));					
			toolSetResult.put("statusUrl", monitorIp +":"+ port + statusInfo+"?"+sysNoparameter);
			toolSetResult.put("statusResultCode", (String.valueOf(statusObj.resultCode)));
			toolSetResult.put("statusErrorInfo", statusObj.errorInfo);
			toolSetResult.put("statusRun", statusObj.run);
			toolSetResult.put("statusAlarm", statusObj.alarm);		
			if(statusObj.alarm){
				toolSetResult = getCurrentAlarmInfo(port, sysNoparameter, toolSetResult);
			}	
			
			logger.debug("====== End getStatusInfo=====");
			return toolSetResult;
	   }
	   /*獲取單ToolSet的報警資訊*/
	   public HashMap<String,Object> getCurrentAlarmInfo(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into getCurrentAlarmInfo=====");
		   CurrentAlarm currentAlarm= monitorMainController.getCurrentAlarmInfo(String.valueOf(port),sysNoparameter);
			toolSetResult.put("currentAlarmUrl", monitorIp +":"+ port + getCurrentAlarmInfo+"?"+sysNoparameter);
			toolSetResult.put("currentAlarmResultCode", (String.valueOf(currentAlarm.resultCode)));
			toolSetResult.put("currentAlarmErrorInfo", currentAlarm.errorInfo);
			toolSetResult.put("currentAlarmAlarmType", currentAlarm.alarmType);
			toolSetResult.put("currentAlarmAlarmNO", currentAlarm.alarmNO);
			toolSetResult.put("currentAlarmAlarmMsg", currentAlarm.alarmMsg);
			logger.debug("====== End getCurrentAlarmInfo=====");
			return toolSetResult;
	   }
	   /*獲取單ToolSet的轉速進給資訊*/
	   public HashMap<String,Object> getspeedFeedRateMain(Map<String,Object> obj,int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into getspeedFeedRateMain=====");
		   
		   String machineBrandId =(String)obj.get("machineBrandId");
		   String machineTypeId =(String)obj.get("machineTypeId");
		   logger.debug("machineBrandId:"+machineBrandId);
		   logger.debug("machineTypeId:"+machineTypeId);
		    //是否為makino
			List<String> makinoMachineBrandList = Arrays.asList(makinoMachineBrands);
			List<String> makinoMachineTypeList = Arrays.asList(makinoMachineTypes);			
			boolean isMakino = false;
			logger.debug("makinoMachineBrandList.contains(machineBrandId):" + makinoMachineBrandList.contains(machineBrandId));
			logger.debug("makinoMachineTypeList.contains(machineTypeId)：" + makinoMachineTypeList.contains(machineTypeId));
			if(makinoMachineBrandList.contains(machineBrandId) && makinoMachineTypeList.contains(machineTypeId)){				
				isMakino = true;
			 }
			logger.debug("is makino:" + isMakino);
			//是否為nakamura
			List<String> nakamuraMachineBrandList = Arrays.asList(nakamuraMachineBrands);
			List<String> nakamuraMachineTypeList = Arrays.asList(nakamuraMachineTypes);		
			boolean isNakamura = false;
			if(nakamuraMachineBrandList.contains(machineBrandId) && nakamuraMachineTypeList.contains(machineTypeId)){
				logger.debug("is nakamura:" + isNakamura);
				isNakamura = true;
			 }
			
			SpeedFeedRate speedFeedRateObj = monitorMainController.feedrateSpeedInfo(isNakamura,isMakino,String.valueOf(port),sysNoparameter);			
			toolSetResult.put("speedFeedUrl", monitorIp +":"+ port + (isMakino ? makinoFeedrateSpeedInfo :feedrateSpeedInfo+"?"+sysNoparameter));
			toolSetResult.put("speedFeedResultCode", (String.valueOf(speedFeedRateObj.resultCode)));
			toolSetResult.put("speedFeedErrorInfo", speedFeedRateObj.errorInfo);
			toolSetResult.put("speedFeedOvFeed", speedFeedRateObj.OvFeed);
			toolSetResult.put("speedFeedOvSpeed", speedFeedRateObj.OvSpeed);
			toolSetResult.put("speedFeedActFeed", speedFeedRateObj.ActFeed);
			toolSetResult.put("speedFeedActSpeed", speedFeedRateObj.ActSpeed);
			toolSetResult.put("speedFeedFPCT", speedFeedRateObj.FPCT);
			toolSetResult.put("speedFeedSPCT", speedFeedRateObj.SPCT);
			toolSetResult.put("speedFeedQPCT", speedFeedRateObj.QPCT);
			   logger.debug("====== End getspeedFeedRateMain=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的執行程式*/
	   public HashMap<String,Object> curExecutePrgInfo(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into curExecutePrgInfo=====");
		   CurrentExecuteNCInfo curExecutePrgInfoObj = monitorMainController.curExecutePrgInfo(String.valueOf(port),sysNoparameter);			
			toolSetResult.put("curExecutePrgInfoUrl", monitorIp +":"+ port + curExecutePrgInfo+"?"+sysNoparameter);
			toolSetResult.put("curExecutePrgInfoResultCode", (String.valueOf(curExecutePrgInfoObj.resultCode)));
			toolSetResult.put("curExecutePrgInfoErrorInfo", curExecutePrgInfoObj.errorInfo);
			toolSetResult.put("curExecutePrgInfoMacinPrg", curExecutePrgInfoObj.macinPrg);
			toolSetResult.put("curExecutePrgInfoRunPrg", curExecutePrgInfoObj.runPrg);
			toolSetResult.put("curExecutePrgInfoSequence", curExecutePrgInfoObj.sequence);

			logger.debug("====== End curExecutePrgInfo=====");
			return toolSetResult;
	   }
	   
	  
	   
	   /*獲取單ToolSet的機台累計時間*/
	   public HashMap<String,Object> cumulativeTime(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into cumulativeTime=====");
		   CumulativeTimeInfo cmulativeTimeInfoObj= monitorMainController.cumulativeTime(String.valueOf(port), sysNoparameter);
			toolSetResult.put("cmulativeTimeInfoUrl", monitorIp +":"+ port + cumulativeTime+"?"+sysNoparameter);
			toolSetResult.put("cmulativeTimeInfoResultCode", (String.valueOf(cmulativeTimeInfoObj.resultCode)));
			toolSetResult.put("cmulativeTimeInfoErrorInfo", cmulativeTimeInfoObj.errorInfo);
			toolSetResult.put("cmulativeTimeInfoTimeInfo", cmulativeTimeInfoObj.timeInfo);
			logger.debug("====== End cumulativeTime=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的獲取工件數信息*/
	   public HashMap<String,Object> getPartInfo(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into getPartInfo=====");
		   PartCount partCountObj= monitorMainController.getPartInfo(String.valueOf(port), sysNoparameter);
			toolSetResult.put("partCountUrl", monitorIp +":"+ port + getPartInfo+"?"+sysNoparameter);
			toolSetResult.put("partCountResultCode", (String.valueOf(partCountObj.resultCode)));
			toolSetResult.put("partCountErrorInfo", partCountObj.errorInfo);
			toolSetResult.put("partCountPartCount", partCountObj.partCount);
			toolSetResult.put("partCountPartTotal", partCountObj.partTotal);
			toolSetResult.put("partCountPartRequire", partCountObj.partRequire);
			logger.debug("====== End getPartInfo=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的獲取otherCode*/
	   public HashMap<String,Object> otherCode(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into otherCode=====");
		   OtherCode otherCodeObj= monitorMainController.otherCode(String.valueOf(port), sysNoparameter);
			toolSetResult.put("otherCodeUrl", monitorIp +":"+ port + otherCode+"?"+sysNoparameter);
			toolSetResult.put("otherCodeResultCode", (String.valueOf(otherCodeObj.resultCode)));
			toolSetResult.put("otherCodeErrorInfo", otherCodeObj.errorInfo);		
			toolSetResult.put("otherCodeHCode", otherCodeObj.HCode);
			toolSetResult.put("otherCodeDCode", otherCodeObj.DCode);
			toolSetResult.put("otherCodeTCode", otherCodeObj.TCode);
			toolSetResult.put("otherCodeMCode", otherCodeObj.MCode);
			toolSetResult.put("otherCodeBCode", otherCodeObj.BCode);
			toolSetResult.put("otherCodeFCode", otherCodeObj.FCode);
			toolSetResult.put("otherCodeSCode", otherCodeObj.SCode);
			logger.debug("====== End otherCode=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的獲取G Code*/
	   public HashMap<String,Object> gCodeInfo(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into gCodeInfo=====");
		   	GCode gcodeObj= monitorMainController.gCodeInfo(String.valueOf(port), sysNoparameter);
			toolSetResult.put("gcodeUrl", monitorIp +":"+ port + gCodeInfo+"?"+sysNoparameter);
			toolSetResult.put("gcodeResultCode", (String.valueOf(gcodeObj.resultCode)));
			toolSetResult.put("gcodeErrorInfo", gcodeObj.errorInfo);		
			toolSetResult.put("gcodeGdata", gcodeObj.GData);			
			logger.debug("====== End gCodeInfo=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的當前執行程式內容*/
	   public HashMap<String,Object> prgContentInfo(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into prgContentInfo=====");
		   	ExecutePrgContent executePrgContentObj= monitorMainController.prgContentInfo(String.valueOf(port), sysNoparameter);
			toolSetResult.put("executePrgContentUrl", monitorIp +":"+ port + prgContentInfo+"?"+sysNoparameter);
			toolSetResult.put("executePrgContentResultCode", (String.valueOf(executePrgContentObj.resultCode)));
			toolSetResult.put("executePrgContentErrorInfo", executePrgContentObj.errorInfo);	
			toolSetResult.put("executePrgContentPrgContent", executePrgContentObj.prgContent);	
			logger.debug("====== End prgContentInfo=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的機台位置信息*/
	   public HashMap<String,Object> getPositionInfo(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into prgContentInfo=====");
		   	MachinePositionInfo machinePositionInfoObj= monitorMainController.getPositionInfo(String.valueOf(port), sysNoparameter);
			toolSetResult.put("machinePositionInfoUrl", monitorIp +":"+ port + getPositionInfo+"?"+sysNoparameter);
			toolSetResult.put("machinePositionInfoResultCode", (String.valueOf(machinePositionInfoObj.resultCode)));
			toolSetResult.put("machinePositionInfoErrorInfo", machinePositionInfoObj.errorInfo);
			toolSetResult.put("machinePositionInfoAxisName", machinePositionInfoObj.axisName);	
			toolSetResult.put("machinePositionInfoPosition", machinePositionInfoObj.position);	

			logger.debug("====== End prgContentInfo=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的單筆工件補正資料*/
	   public HashMap<String,Object> singleWorkOffsetInfo(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into singleWorkOffsetInfo=====");
		   WorkOffset workOffsetObj= monitorMainController.singleWorkOffsetInfo(String.valueOf(port), sysNoparameter);
			toolSetResult.put("offsetUrl", monitorIp +":"+ port + singleWorkOffsetInfo+"?"+sysNoparameter);
			toolSetResult.put("offsetResultCode", (String.valueOf(workOffsetObj.resultCode)));
			toolSetResult.put("offsetErrorInfo", workOffsetObj.errorInfo);
			toolSetResult.put("offsetAxisName", workOffsetObj.axisName);	
			toolSetResult.put("offsetPosition", workOffsetObj.offset);	
			logger.debug("====== End singleWorkOffsetInfo=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的所有刀具補正資料*/
	   public HashMap<String,Object> getAllToolOffset(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into getAllToolOffset=====");
		    ToolInfo toolOffsetObj= monitorMainController.getAllToolOffset(String.valueOf(port), sysNoparameter);
			toolSetResult.put("toolOffsetUrl", monitorIp +":"+ port + getAllToolOffset+"?"+sysNoparameter);
			toolSetResult.put("toolOffsetResultCode", (String.valueOf(toolOffsetObj.resultCode)));
			toolSetResult.put("toolOffsetErrorInfo", toolOffsetObj.errorInfo);
			toolSetResult.put("toolOffsetToolTitle", toolOffsetObj.toolTitle);	
			toolSetResult.put("toolOffsetToolValue", toolOffsetObj.toolValue);	
			logger.debug("====== End getAllToolOffset=====");
			return toolSetResult;
	   }
	   /*獲取單ToolSet的所有工件補正資料*/
	   public HashMap<String,Object> allWorkOffsetInfo(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into allWorkOffsetInfo=====");
		   WorkOffset workOffsetObj= monitorMainController.allWorkOffsetInfo(String.valueOf(port), sysNoparameter);
			toolSetResult.put("offsetUrl", monitorIp +":"+ port + allWorkOffsetInfo+"?"+sysNoparameter);
			toolSetResult.put("offsetResultCode", (String.valueOf(workOffsetObj.resultCode)));
			toolSetResult.put("offsetErrorInfo", workOffsetObj.errorInfo);
			toolSetResult.put("offsetAxisName", workOffsetObj.axisName);	
			toolSetResult.put("offsetPosition", workOffsetObj.offset);	
			logger.debug("====== End allWorkOffsetInfo=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的單一Macro資料 {"sysNo":1,"macroId":4109}*/
	   public HashMap<String,Object> getSingleMacro(int port,String parameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into getSingleMacro=====");
		   MacroInfo macroObj= monitorMainController.getSingleMacro(String.valueOf(port), parameter);
			toolSetResult.put("macroUrl", monitorIp +":"+ port + getSingleMacro+"?"+parameter);
			toolSetResult.put("macroResultCode", (String.valueOf(macroObj.resultCode)));
			toolSetResult.put("macroErrorInfo", macroObj.errorInfo);
			toolSetResult.put("macroAxisName", macroObj.macro);	
			logger.debug("====== End getSingleMacro=====");
			return toolSetResult;
	   }
	   
	   /*獲取內存程式清單
	   /* parameters type
	    * 
	    *{"sysNo":1}
	    *
	   */
	   public  HashMap<String,Object>  memoryNcInfo(int port,String sysNoparameter,HashMap<String,Object> toolSetResult) throws Exception {	 
			logger.debug("===== into memoryNcInfo ======");
			MemoryNCInfo gResult= monitorMainController.memoryNcInfo(String.valueOf(port), sysNoparameter);
			toolSetResult.put("memoryNcUrl", monitorIp +":"+ port + memoryNcInfo+"?"+sysNoparameter);
			toolSetResult.put("memoryNcResultCode", (String.valueOf(gResult.resultCode)));
			toolSetResult.put("memoryNcErrorInfo", gResult.errorInfo);
			toolSetResult.put("memoryNcMemNc", gResult.memNC);	
			logger.debug("===== End memoryNcInfo ======");
		    return toolSetResult;
		 }

	   
	   /*上傳NC程式至內存
	   /* parameters type
	    * {"sysNo":1,"filePath":"%5c%5clocalhost%5cNcTempFile%5cCNC%5cCNC1%5cO5000","NCName":"O5000"}
	    *
	   */
	   public  HashMap<String,Object>  uploadNcToMemory(int port,String parameter,HashMap<String,Object> toolSetResult) throws Exception {	 
			logger.debug("===== into uploadNcToMemory ======");
			GeneralResult gResult= monitorMainController.uploadNcToMemory(String.valueOf(port), parameter);
			toolSetResult.put("uploadNcUrl", monitorIp +":"+ port + uploadNcToMemory+"?"+parameter);
			toolSetResult.put("uploadNcResultCode", (String.valueOf(gResult.resultCode)));
			toolSetResult.put("uploadNcErrorInfo", gResult.errorInfo);
			logger.debug("===== End uploadNcToMemory ======");
		    return toolSetResult;
		 }
	   
	
	   /*下載NC程式至內存
	   /* parameters type
	    * {"sysNo":1,"localPath":"D%3a%5cJimmy","NCName":"O5000"}
	    *
	   */
	   public  HashMap<String,Object>  downloadMemoryNc(int port,String parameter,HashMap<String,Object> toolSetResult) throws Exception {	 
			logger.debug("===== into downloadMemoryNc ======");
			GeneralResult gResult= monitorMainController.downloadMemoryNc(String.valueOf(port), parameter);
			toolSetResult.put("downloadNcUrl", monitorIp +":"+ port + downloadMemoryNc+"?"+parameter);
			toolSetResult.put("downloadNcResultCode", (String.valueOf(gResult.resultCode)));
			toolSetResult.put("downloadNcErrorInfo", gResult.errorInfo);
			logger.debug("===== End downloadMemoryNc ======");
		    return toolSetResult;
		 }
	   
	   /*刪除NC程式至內存
	   /* parameters type
	    * 
	    *{"sysNo":1,"NCName":"O5000"}
	    *
	   */
	   public HashMap<String,Object>  deleteMemoryNc(int port,String parameter,HashMap<String,Object> toolSetResult) throws Exception {	 
			logger.debug("===== into deleteMemoryNc ======");
			GeneralResult gResult= monitorMainController.deleteMemoryNc(String.valueOf(port), parameter);
			toolSetResult.put("deleteNcUrl", monitorIp +":"+ port + deleteMemoryNc+"?"+parameter);
			toolSetResult.put("deleteNcResultCode", (String.valueOf(gResult.resultCode)));
			toolSetResult.put("deleteNcErrorInfo", gResult.errorInfo);
			logger.debug("===== End deleteMemoryNc ======");
		    return toolSetResult;
		 }
	
	   
	   /*獲取FTP程式清單
	   /* parameters type
	    * 
	    *{"userId":"","password":"","ftpPath":""}
	    *
	   */
	   public  HashMap<String,Object>  ftpNcInfo(int port,String parameter,HashMap<String,Object> toolSetResult) throws Exception {	 
			logger.debug("===== into ftpNcInfo ======");
			FtpNCList gResult= monitorMainController.ftpNcInfo(String.valueOf(port), parameter);
			toolSetResult.put("ftpNcUrl", monitorIp +":"+ port + ftpNcInfo+"?"+parameter);
			toolSetResult.put("ftpNcResultCode", (String.valueOf(gResult.resultCode)));
			toolSetResult.put("ftpNcErrorInfo", gResult.errorInfo);
			toolSetResult.put("ftpNcNCName", gResult.NCName);	
			logger.debug("===== End ftpNcInfo ======");
		    return toolSetResult;
		 }

	   
	   /*上傳程式至FTP
	   /* parameters type
	    * {"userId":"","password":"","ftpPath":"","localPath":"%5c%5clocalhost%5cNcTempFile%5cCNC%5cCNC1%5cO5000"}
	    *
	   */
	   public  HashMap<String,Object>  uploadNcToFtp(int port,String parameter,HashMap<String,Object> toolSetResult) throws Exception {	 
			logger.debug("===== into uploadNcToFtp ======");
			GeneralResult gResult= monitorMainController.uploadNcToFtp(String.valueOf(port), parameter);
			toolSetResult.put("uploadFtpNcUrl", monitorIp +":"+ port + uploadNcToFtp+"?"+parameter);
			toolSetResult.put("uploadFtpNcResultCode", (String.valueOf(gResult.resultCode)));
			toolSetResult.put("uploadFtpNcErrorInfo", gResult.errorInfo);
			logger.debug("===== End uploadNcToFtp ======");
		    return toolSetResult;
		 }
	   
	
	   /*從FTP下載指定程式
	   /* parameters type
	    * {"userId":"","password":"","NCName":"O5000","ftpPath":"","localPath":"D%3a%5cJimmy"}
	    *
	   */
	   public  HashMap<String,Object>  downloadNcFromFtp(int port,String parameter,HashMap<String,Object> toolSetResult) throws Exception {	 
			logger.debug("===== into downloadNcFromFtp ======");
			GeneralResult gResult= monitorMainController.downloadNcFromFtp(String.valueOf(port), parameter);
			toolSetResult.put("downloadFtpNcUrl", monitorIp +":"+ port + downloadNcFromFtp+"?"+parameter);
			toolSetResult.put("downloadFtpNcResultCode", (String.valueOf(gResult.resultCode)));
			toolSetResult.put("downloadFtpNcErrorInfo", gResult.errorInfo);
			logger.debug("===== End downloadNcFromFtp ======");
		    return toolSetResult;
		 }
	   
	   /*刪除FTP程式
	   /* parameters type
	    * 
	    *{"userId":"","password":"","NCName":"O5000","ftpPath":""}
	    *
	   */
	   public HashMap<String,Object>  deleteNcFileInFtp(int port,String parameter,HashMap<String,Object> toolSetResult) throws Exception {	 
			logger.debug("===== into deleteNcFileInFtp ======");
			GeneralResult gResult= monitorMainController.deleteNcFileInFtp(String.valueOf(port), parameter);
			toolSetResult.put("deleteFtpNcUrl", monitorIp +":"+ port + deleteNcFileInFtp+"?"+parameter);
			toolSetResult.put("deleteFtpNcResultCode", (String.valueOf(gResult.resultCode)));
			toolSetResult.put("deleteFtpNcErrorInfo", gResult.errorInfo);
			logger.debug("===== End deleteNcFileInFtp ======");
		    return toolSetResult;
		 } 
	   
}

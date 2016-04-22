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
import com.globaltek.machineLib.GCode;
import com.globaltek.machineLib.MachinePositionInfo;
import com.globaltek.machineLib.OtherCode;
import com.globaltek.machineLib.PartCount;
import com.globaltek.machineLib.SpeedFeedRate;
import com.globaltek.machineLib.StatusInfo;
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
	   
	   /*獲取單ToolSet的狀態*/
	   public HashMap<String,Object> getStatusInfo(int port,String sysNoarameter,int i ,String toolSetId,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into getStatusInfo=====");			
			StatusInfo statusObj = monitorMainController.getStatusInfo(String.valueOf(port),sysNoarameter);	
			toolSetResult.put("toolSetId",toolSetId);
			toolSetResult.put("sysNo", (i+1));					
			toolSetResult.put("statusUrl", monitorIp +":"+ port + statusInfo+"?"+sysNoarameter);
			toolSetResult.put("statusResultCode", (String.valueOf(statusObj.resultCode)));
			toolSetResult.put("statusErrorInfo", statusObj.errorInfo);
			toolSetResult.put("statusRun", statusObj.run);
			toolSetResult.put("statusAlarm", statusObj.alarm);		
			if(statusObj.alarm){
				toolSetResult = getCurrentAlarmInfo(port, sysNoarameter, toolSetResult);
			}	
			
			logger.debug("====== End getStatusInfo=====");
			return toolSetResult;
	   }
	   /*獲取單ToolSet的報警資訊*/
	   public HashMap<String,Object> getCurrentAlarmInfo(int port,String sysNoarameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into getCurrentAlarmInfo=====");
		   CurrentAlarm currentAlarm= monitorMainController.getCurrentAlarmInfo(String.valueOf(port),sysNoarameter);
			toolSetResult.put("currentAlarmUrl", monitorIp +":"+ port + getCurrentAlarmInfo+"?"+sysNoarameter);
			toolSetResult.put("currentAlarmResultCode", (String.valueOf(currentAlarm.resultCode)));
			toolSetResult.put("currentAlarmErrorInfo", currentAlarm.errorInfo);
			toolSetResult.put("currentAlarmAlarmType", currentAlarm.alarmType);
			toolSetResult.put("currentAlarmAlarmNO", currentAlarm.alarmNO);
			toolSetResult.put("currentAlarmAlarmMsg", currentAlarm.alarmMsg);
			logger.debug("====== End getCurrentAlarmInfo=====");
			return toolSetResult;
	   }
	   /*獲取單ToolSet的轉速進給資訊*/
	   public HashMap<String,Object> getspeedFeedRateMain(Map<String,Object> obj,int port,String sysNoarameter,HashMap<String,Object> toolSetResult) throws Exception{	
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
			
			SpeedFeedRate speedFeedRateObj = monitorMainController.feedrateSpeedInfo(isNakamura,isMakino,String.valueOf(port),sysNoarameter);			
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
			   logger.debug("====== End getspeedFeedRateMain=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的執行程式*/
	   public HashMap<String,Object> curExecutePrgInfo(int port,String sysNoarameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into curExecutePrgInfo=====");
		   CurrentExecuteNCInfo curExecutePrgInfoObj = monitorMainController.curExecutePrgInfo(String.valueOf(port),sysNoarameter);			
			toolSetResult.put("curExecutePrgInfoUrl", monitorIp +":"+ port + curExecutePrgInfo+"?"+sysNoarameter);
			toolSetResult.put("curExecutePrgInfoResultCode", (String.valueOf(curExecutePrgInfoObj.resultCode)));
			toolSetResult.put("curExecutePrgInfoErrorInfo", curExecutePrgInfoObj.errorInfo);
			toolSetResult.put("curExecutePrgInfoMacinPrg", curExecutePrgInfoObj.macinPrg);
			toolSetResult.put("curExecutePrgInfoRunPrg", curExecutePrgInfoObj.runPrg);
			logger.debug("====== End curExecutePrgInfo=====");
			return toolSetResult;
	   }
	   
	  
	   
	   /*獲取單ToolSet的機台累計時間*/
	   public HashMap<String,Object> cumulativeTime(int port,String sysNoarameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into cumulativeTime=====");
		   CumulativeTimeInfo cmulativeTimeInfoObj= monitorMainController.cumulativeTime(String.valueOf(port), sysNoarameter);
			toolSetResult.put("cmulativeTimeInfoUrl", monitorIp +":"+ port + cumulativeTime+"?"+sysNoarameter);
			toolSetResult.put("cmulativeTimeInfoResultCode", (String.valueOf(cmulativeTimeInfoObj.resultCode)));
			toolSetResult.put("cmulativeTimeInfoErrorInfo", cmulativeTimeInfoObj.errorInfo);
			toolSetResult.put("cmulativeTimeInfoTimeInfo", cmulativeTimeInfoObj.timeInfo);
			logger.debug("====== End cumulativeTime=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的獲取工件數信息*/
	   public HashMap<String,Object> getPartInfo(int port,String sysNoarameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into getPartInfo=====");
		   PartCount partCountObj= monitorMainController.getPartInfo(String.valueOf(port), sysNoarameter);
			toolSetResult.put("partCountUrl", monitorIp +":"+ port + getPartInfo+"?"+sysNoarameter);
			toolSetResult.put("partCountResultCode", (String.valueOf(partCountObj.resultCode)));
			toolSetResult.put("partCountErrorInfo", partCountObj.errorInfo);
			toolSetResult.put("partCountPartCount", partCountObj.partCount);
			toolSetResult.put("partCountPartTotal", partCountObj.partTotal);
			toolSetResult.put("partCountPartRequire", partCountObj.partRequire);
			logger.debug("====== End getPartInfo=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的獲取otherCode*/
	   public HashMap<String,Object> otherCode(int port,String sysNoarameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into otherCode=====");
		   OtherCode otherCodeObj= monitorMainController.otherCode(String.valueOf(port), sysNoarameter);
			toolSetResult.put("otherCodeUrl", monitorIp +":"+ port + otherCode+"?"+sysNoarameter);
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
	   public HashMap<String,Object> gCodeInfo(int port,String sysNoarameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into gCodeInfo=====");
		   	GCode gcodeObj= monitorMainController.gCodeInfo(String.valueOf(port), sysNoarameter);
			toolSetResult.put("gcodeUrl", monitorIp +":"+ port + gCodeInfo+"?"+sysNoarameter);
			toolSetResult.put("gcodeResultCode", (String.valueOf(gcodeObj.resultCode)));
			toolSetResult.put("gcodeErrorInfo", gcodeObj.errorInfo);		
			toolSetResult.put("gcodeGdata", gcodeObj.GData);			
			logger.debug("====== End gCodeInfo=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的當前執行程式內容*/
	   public HashMap<String,Object> prgContentInfo(int port,String sysNoarameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into prgContentInfo=====");
		   	ExecutePrgContent executePrgContentObj= monitorMainController.prgContentInfo(String.valueOf(port), sysNoarameter);
			toolSetResult.put("executePrgContentUrl", monitorIp +":"+ port + prgContentInfo+"?"+sysNoarameter);
			toolSetResult.put("executePrgContentResultCode", (String.valueOf(executePrgContentObj.resultCode)));
			toolSetResult.put("executePrgContentErrorInfo", executePrgContentObj.errorInfo);	
			toolSetResult.put("executePrgContentPrgContent", executePrgContentObj.prgContent);	
			logger.debug("====== End prgContentInfo=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的機台位置信息*/
	   public HashMap<String,Object> getPositionInfo(int port,String sysNoarameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into prgContentInfo=====");
		   	MachinePositionInfo machinePositionInfoObj= monitorMainController.getPositionInfo(String.valueOf(port), sysNoarameter);
			toolSetResult.put("machinePositionInfoUrl", monitorIp +":"+ port + getPositionInfo+"?"+sysNoarameter);
			toolSetResult.put("machinePositionInfoResultCode", (String.valueOf(machinePositionInfoObj.resultCode)));
			toolSetResult.put("machinePositionInfoErrorInfo", machinePositionInfoObj.errorInfo);
			toolSetResult.put("machinePositionInfoAxisName", machinePositionInfoObj.axisName);	
			toolSetResult.put("machinePositionInfoPosition", machinePositionInfoObj.position);	

			logger.debug("====== End prgContentInfo=====");
			return toolSetResult;
	   }
	   
	   /*獲取單ToolSet的單筆工件補正資料*/
	   public HashMap<String,Object> singleWorkOffsetInfo(int port,String sysNoarameter,HashMap<String,Object> toolSetResult) throws Exception{	
		   logger.debug("====== into singleWorkOffsetInfo=====");
		   WorkOffset workOffsetObj= monitorMainController.singleWorkOffsetInfo(String.valueOf(port), sysNoarameter);
			toolSetResult.put("offsetUrl", monitorIp +":"+ port + singleWorkOffsetInfo+"?"+sysNoarameter);
			toolSetResult.put("offsetResultCode", (String.valueOf(workOffsetObj.resultCode)));
			toolSetResult.put("offsetErrorInfo", workOffsetObj.errorInfo);
			toolSetResult.put("offsetAxisName", workOffsetObj.axisName);	
			toolSetResult.put("offsetPosition", workOffsetObj.offset);	
			logger.debug("====== End singleWorkOffsetInfo=====");
			return toolSetResult;
	   }
}

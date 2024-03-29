package com.machineMonitor.general.contoller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

@Controller
public class MainSetController {
	   
	 protected final transient Logger logger = Logger.getLogger(this.getClass());
		
			// HTTP POST request
			public String sendPost(String urlPath,String parameters) throws Exception {
//				logger.debug("===== into Monitor MainSetController sendPost ======");
				logger.debug("urlPath："+urlPath);
				try {
					URL url = new URL(urlPath);
					URLConnection urlconnection = url.openConnection();
					HttpURLConnection con = (HttpURLConnection) urlconnection;
	
					//add reuqest header
					con.setRequestMethod("POST");			
					con.setRequestProperty("Content-type","application/json");
					
					// Send post request
					con.setDoOutput(true);
					OutputStream ou = con.getOutputStream();
					ou.write(parameters.getBytes("utf8"));
					ou.flush();
					ou.close();
					int responseCode = con.getResponseCode();
	
					String sTotalString = "";
					logger.debug("responseCode:"+responseCode);
					logger.debug("sendPost ："+urlPath+",parameters:"+parameters);
					if (responseCode == HttpURLConnection.HTTP_OK) {
						InputStream urlStream = con.getInputStream();
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(urlStream));
						String sCurrentLine = "";
						while ((sCurrentLine = bufferedReader.readLine()) != null) {
							sTotalString += sCurrentLine;
						}
						urlStream.close();
					}	
//					logger.debug("===== End Monitor MainSetController sendPost ======");
					return sTotalString;
				} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("sendPost failed："+urlPath+",parameters:"+parameters);
				}				
			}
}

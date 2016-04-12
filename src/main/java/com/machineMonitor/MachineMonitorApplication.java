package com.machineMonitor;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MachineMonitorApplication {

	private static Logger logger = Logger.getLogger(MachineMonitorApplication.class);

	public static void main(String[] args) {
		logger.debug("===== Restart MachineMonitorApplication main ====="); 
		SpringApplication.run(MachineMonitorApplication.class, args);
		logger.debug("===== End MachineMonitorApplication main ====="); 
	}
}

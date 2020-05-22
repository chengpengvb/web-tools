package com.cp.www;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cp.www.util.StopWatch;

/**
 *
 *
 * @author cp
 * @version 创建时间：2019年12月27日 下午3:00:20
 */
@SpringBootApplication
public class ToolsApplication {

	private static Logger logger = LogManager.getLogger();

	private static void printActive(double timeSecond) {
		int sunSize = 100;
		StringBuffer sb = new StringBuffer(3 * sunSize);
		StringBuffer endSb = new StringBuffer(sunSize);
		sb.append("\n");
		String timeStr = "ToolsApplication startUp in " + timeSecond + "s";
		if (timeStr.length() % 2 != 0) {
			timeStr += " ";
		}
		int timeSize = timeStr.length(), blankSize = (sunSize - timeSize - 2) / 2;
		StringBuffer blankSb = new StringBuffer(blankSize);
		for (int i = 0; i < sunSize; i++) {
			sb.append("*");
			endSb.append("*");
		}
		for (int i = 0; i < blankSize; i++) {
			blankSb.append(" ");
		}
		sb.append("\n*").append(blankSb).append(timeStr).append(blankSb).append("*\n").append(endSb);
		logger.info(sb);
	}

	public static void main(String[] args) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		SpringApplication.run(ToolsApplication.class, args);
		stopWatch.stop();
		printActive(stopWatch.timeSecond());
	}
}

package com.example.bankingsystem.core.utilities.logging.kafka;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
	private String filePathString = "/home/canyilmaz/eclipse-workspace/";
	
	@KafkaListener(topics = "logs", groupId = "logs_group")
	public void saveBankAccountLogs2File(
			@Payload String message, 
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition
			) 
	{
		//System.out.println("Message : " + message + ", Partition : " + partition + ", Key : " + key);
		//System.out.println("Message : " + message + ", Partition : " + partition);
		try {
			File file = new File(filePathString + "logs.txt");
			FileWriter fileWriter = new FileWriter(file, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(message);
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

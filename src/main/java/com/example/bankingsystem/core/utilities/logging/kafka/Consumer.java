package com.example.bankingsystem.core.utilities.logging.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.bankingsystem.repositories.BankAccountRepository;

@Component
public class Consumer {
	@Autowired
	private BankAccountRepository bankAccountRepository;

	@KafkaListener(topics = "logs", groupId = "logs_group")
	public void saveBankAccountLog(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
		try {
			bankAccountRepository.createBankAccountLog(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

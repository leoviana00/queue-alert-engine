package io.viana.queue_alert_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling  // 👉 necessário para ativar os schedulers
@SpringBootApplication
public class QueueAlertEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueueAlertEngineApplication.class, args);
	}

}

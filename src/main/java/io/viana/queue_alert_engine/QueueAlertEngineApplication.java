package io.viana.queue_alert_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principal da aplicação Queue Alert Engine.
 * É o ponto de entrada para iniciar o aplicativo Spring Boot.
 */
@EnableScheduling // Habilita o suporte a métodos agendados (schedulers) como o QueueMonitorScheduler
@SpringBootApplication // Define esta classe como uma aplicação Spring Boot, ativando autoconfigurações
public class QueueAlertEngineApplication {

  /**
   * Método principal que inicializa e executa a aplicação Spring Boot.
   *
   * @param args Argumentos de linha de comando.
   */
  public static void main(String[] args) {
    SpringApplication.run(QueueAlertEngineApplication.class, args);
  }

}
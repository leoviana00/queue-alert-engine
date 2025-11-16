// package io.viana.queue_alert_engine.service;

// import io.viana.queue_alert_engine.domain.AlertRule;
// import io.viana.queue_alert_engine.domain.QueueStatus;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;

// @Slf4j
// @Service
// public class QueueMonitorService {

//     /**
//      * Avalia o lag e retorna o status da fila
//      */
//     public QueueStatus evaluateLag(long lag, AlertRule rule) {
//         long warn = rule.lagWarning();
//         long critical = rule.lagCritical();

//         if (lag >= critical) {
//             log.error("🚨 CRITICAL topic='{}' part={} lag={} (critical={})",
//                     rule.topic(), rule.partition(), lag, critical);
//             return QueueStatus.CRITICAL;
//         }

//         if (lag >= warn) {
//             log.warn("⚠️ WARNING topic='{}' part={} lag={} (warn={}, critical={})",
//                     rule.topic(), rule.partition(), lag, warn, critical);
//             return QueueStatus.WARNING;
//         }

//         log.info("✅ topic='{}' part={} OK (lag={} < warn={})", rule.topic(), rule.partition(), lag, warn);
//         return QueueStatus.OK;
//     }
// }


/*
 * Avalia lag de acordo com thresholds da regra e loga mensagens; 
 * Atualmente apenas lógica de avaliação de status isolada (pode ser usada pelo LagChecker para separar lógica de cálculo do envio de alertas).
 */
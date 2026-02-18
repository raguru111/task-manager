
package com.example.taskservice.audit;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {
 private static final Logger log=LoggerFactory.getLogger(AuditLogger.class);
 public void log(String msg){ log.info("AUDIT:"+msg);}
}

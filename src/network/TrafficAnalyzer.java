package network;

import core.SDASLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TrafficAnalyzer {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Path trafficLog;
    private final SDASLogger logger;
    private final TrafficRuleSet rules = new TrafficRuleSet();
    private final Random rnd = new Random();

    
    private String lastIp = null;
    private LocalDateTime lastTime = null;

    public TrafficAnalyzer(Path trafficLog, SDASLogger logger) {
        this.trafficLog = trafficLog;
        this.logger = logger;
        ensure();
    }

    private void ensure() {
        try {
            Path parent = trafficLog.getParent();
            if (parent != null) Files.createDirectories(parent);
            if (!Files.exists(trafficLog)) Files.createFile(trafficLog);
        } catch (Exception e) {
            logger.log("RED", "Error creando traffic.log: " + e.getMessage());
        }
    }

    public void analyze() {
        // generar una línea de tráfico simple
        var now = LocalDateTime.now();
        String srcIp = randomIp();
        int dstPort = randomPort();

        writeTraffic(now, srcIp, dstPort);

        // regla 1: puertos sospechosos
        if (rules.isSuspiciousPort(dstPort)) {
            logger.log("RED", "Conexión sospechosa desde " + srcIp + " al puerto " + dstPort);
        }

        // regla 2: IP repetida 
        if (lastIp != null && lastIp.equals(srcIp)) {
            logger.log("RED", "La IP " + srcIp + " realiza conexiones repetidas.");
        }

        // regla 3: intervalo (simple)
        if (lastTime != null) {
            long diff = java.time.Duration.between(lastTime, now).getSeconds();
            if (diff <= 1) {
                logger.log("RED", "Intervalo muy corto entre conexiones (" + diff + "s) desde " + srcIp);
            }
        }

        // actualizar estado
        lastIp = srcIp;
        lastTime = now;
    }

    private void writeTraffic(LocalDateTime time, String srcIp, int port) {
        try (BufferedWriter bw = Files.newBufferedWriter(trafficLog, StandardOpenOption.APPEND)) {
            bw.write("[" + time.format(FMT) + "] SRC=" + srcIp + " DPORT=" + port);
            bw.newLine();
        } catch (IOException e) {
            logger.log("RED", "Error escribiendo traffic.log: " + e.getMessage());
        }
    }

    private String randomIp() {
        String[] ips = {"192.168.1.10", "192.168.1.20", "10.0.0.5", "23.87.55.120"};
        return ips[rnd.nextInt(ips.length)];
    }

    private int randomPort() {
        int[] ports = {80, 443, 22, 23, 4444, 135};
        return ports[rnd.nextInt(ports.length)];
    }
}

package process;

import core.SDASLogger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProcessAnalyzer {
    private final SDASLogger logger;

    private final double cpuThreshold = 80.0;
    private final long persistenceSeconds = 20;

    private final Set<String> blacklist = Set.of(
            "keylogger.exe", "ransomware.exe"
    );

    private final Set<String> suspicious = Set.of(
            "keylogger.exe", "miner.exe", "ransomware.exe"
    );

    // Tracking por PID (para persistencia real)
    private final Map<Integer, LocalDateTime> firstSeen = new HashMap<>();

    public ProcessAnalyzer(SDASLogger logger) {
        this.logger = logger;
    }

    public void analyze(List<SimProcess> processes) {
        LocalDateTime now = LocalDateTime.now();

        // Marcamos vistos
        Set<Integer> currentPids = new HashSet<>();
        for (SimProcess p : processes) {
            currentPids.add(p.getPid());
            firstSeen.putIfAbsent(p.getPid(), now);

            // Lista negra
            if (blacklist.contains(p.getName())) {
                logger.log("PROCESOS",
                        "Proceso \"" + p.getName() + "\" está en lista negra (pid=" + p.getPid() + ").");
            }

            // CPU excesiva
            if (p.getCpu() >= cpuThreshold) {
                logger.log("PROCESOS",
                        "Proceso \"" + p.getName() + "\" excede uso de CPU (" +
                                String.format("%.1f", p.getCpu()) + "%, pid=" + p.getPid() + ").");
            }

            // Persistencia (solo si es sospechoso)
            if (suspicious.contains(p.getName())) {
                long aliveSecs = Duration.between(firstSeen.get(p.getPid()), now).getSeconds();
                if (aliveSecs >= persistenceSeconds) {
                    logger.log("PROCESOS",
                            "Proceso sospechoso \"" + p.getName() + "\" persiste " +
                                    aliveSecs + "s (pid=" + p.getPid() + ").");
                }
            }
        }

        firstSeen.keySet().removeIf(pid -> !currentPids.contains(pid));
    }
}

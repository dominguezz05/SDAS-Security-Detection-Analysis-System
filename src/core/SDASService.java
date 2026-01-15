package core;

import integrity.IntegrityMonitor;
import network.TrafficAnalyzer;
import process.ProcessAnalyzer;
import process.ProcessSimulator;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SDASService {

    private final SDASLogger logger;

    private final IntegrityMonitor integrity;
    private final TrafficAnalyzer traffic;

    private final ProcessSimulator simulator;
    private final ProcessAnalyzer procAnalyzer;

    private final ScheduledExecutorService scheduler;

    public SDASService(Path watchFolder, Path trafficLog, Path processesLog, SDASLogger logger) {
        this.logger = logger;

        this.integrity = new IntegrityMonitor(watchFolder, logger);
        this.traffic = new TrafficAnalyzer(trafficLog, logger);

        this.simulator = new ProcessSimulator(processesLog, logger);
        this.procAnalyzer = new ProcessAnalyzer(logger);

        this.scheduler = Executors.newScheduledThreadPool(3);
    }

    public void start() {
        logger.log("CORE", "SDAS arrancado.");

        // Módulo 1: Integridad
        scheduler.scheduleAtFixedRate(() -> safeRun("INTEGRIDAD", integrity::scan),
                0, 5, TimeUnit.SECONDS);

        // Módulo 2: Tráfico simulado
        scheduler.scheduleAtFixedRate(() -> safeRun("RED", traffic::analyze),
                0, 5, TimeUnit.SECONDS);

        // Módulo 3: Procesos simulados + detección
        scheduler.scheduleAtFixedRate(() -> safeRun("PROCESOS", () -> {
            var processes = simulator.generate();
            procAnalyzer.analyze(processes);
        }), 0, 5, TimeUnit.SECONDS);
    }

    public void stop() {
        logger.log("CORE", "Parando SDAS...");
        scheduler.shutdownNow();
    }

    private void safeRun(String module, Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            logger.log(module, "ERROR: " + e.getMessage());
        }
    }
}

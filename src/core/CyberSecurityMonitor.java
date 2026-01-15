package core;

import java.nio.file.Path;

public class CyberSecurityMonitor {
    public static void main(String[] args) {
        Path watchFolder  = Path.of("data/watch");
        Path trafficLog   = Path.of("data/traffic.log");
        Path processesLog = Path.of("data/processes.log");

        SDASLogger logger = new SDASLogger(Path.of("log_sdas.txt"));
        SDASService service = new SDASService(watchFolder, trafficLog, processesLog, logger);

        Runtime.getRuntime().addShutdownHook(new Thread(service::stop));
        service.start();
    }
}

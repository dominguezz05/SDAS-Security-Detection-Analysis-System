package process;

import core.SDASLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProcessSimulator {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Path outLog;
    private final SDASLogger logger;

    private final Random rnd = new Random();
    private final Map<Integer, SimProcess> alive = new HashMap<>();
    private int nextPid = 1000;

    // Nombres “normales” + algunos típicos sospechosos
    private final String[] names = {
            "chrome.exe", "explorer.exe", "java.exe", "python.exe", "svchost.exe",
            "backup_agent.exe", "updater.exe", "keylogger.exe", "miner.exe", "ransomware.exe"
    };

    public ProcessSimulator(Path outLog, SDASLogger logger) {
        this.outLog = outLog;
        this.logger = logger;
        ensureLogFile();
    }

    private void ensureLogFile() {
        try {
            Path parent = outLog.getParent();
            if (parent != null) Files.createDirectories(parent);
            if (!Files.exists(outLog)) Files.createFile(outLog);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el log de procesos: " + outLog, e);
        }
    }

    /** Genera/actualiza procesos y devuelve la foto actual (lista) */
    public List<SimProcess> generate() {
        // A veces nacen procesos nuevos
        int spawns = rnd.nextInt(3); // 0..2
        for (int i = 0; i < spawns; i++) spawnOne();

        // A veces mueren procesos 
        maybeKillSome();

        // Actualizamos CPU 
        for (SimProcess p : alive.values()) {
            double base = clamp(p.getCpu() + rnd.nextGaussian() * 10.0, 0, 100);
            // pico ocasional
            if (rnd.nextDouble() < 0.10) base = clamp(base + 50, 0, 100);
            p.setCpu(base);
        }

        //Escribimos “snapshot” al log de procesos 
        writeSnapshot(alive.values());

        return new ArrayList<>(alive.values());
    }

    private void spawnOne() {
        String name = names[rnd.nextInt(names.length)];

        // CPU inicial: si es malware “típico”, más probabilidad de alta CPU
        double cpu = rnd.nextDouble() * 40;
        if (name.contains("miner") || name.contains("ransomware")) cpu = 50 + rnd.nextDouble() * 50;

        int pid = nextPid++;
        SimProcess p = new SimProcess(pid, name, cpu, LocalDateTime.now());
        alive.put(pid, p);
    }

    private void maybeKillSome() {
        if (alive.isEmpty()) return;

        // kill 0..20% de los vivos
        int maxKills = Math.max(0, alive.size() / 5);
        int kills = rnd.nextInt(maxKills + 1);

        List<Integer> pids = new ArrayList<>(alive.keySet());
        Collections.shuffle(pids, rnd);

        for (int i = 0; i < kills && i < pids.size(); i++) {
            // se evita matar demasiado a menudo los sospechosos para que “persistan”
            SimProcess p = alive.get(pids.get(i));
            if (p.getName().contains("keylogger") && rnd.nextDouble() < 0.8) continue;
            if (p.getName().contains("miner") && rnd.nextDouble() < 0.6) continue;

            alive.remove(pids.get(i));
        }
    }

    private void writeSnapshot(Collection<SimProcess> processes) {
        try (BufferedWriter bw = Files.newBufferedWriter(outLog, StandardOpenOption.APPEND)) {
            for (SimProcess p : processes) {
                bw.write(String.format("[%s] pid=%d name=%s cpu=%.1f start=%s",
                        LocalDateTime.now().format(FMT),
                        p.getPid(),
                        p.getName(),
                        p.getCpu(),
                        p.getStartTime().format(FMT)
                ));
                bw.newLine();
            }
        } catch (IOException e) {
            logger.log("PROCESOS", "ERROR escribiendo processes.log: " + e.getMessage());
        }
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}

package process;

import java.time.LocalDateTime;

public class SimProcess {
    private final int pid;
    private final String name;
    private double cpu; 
    private final LocalDateTime startTime;

    public SimProcess(int pid, String name, double cpu, LocalDateTime startTime) {
        this.pid = pid;
        this.name = name;
        this.cpu = cpu;
        this.startTime = startTime;
    }

    public int getPid() { return pid; }
    public String getName() { return name; }
    public double getCpu() { return cpu; }
    public LocalDateTime getStartTime() { return startTime; }

    public void setCpu(double cpu) { this.cpu = cpu; }
}

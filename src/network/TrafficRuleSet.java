package network;

import java.util.Set;

public class TrafficRuleSet {

    private final Set<Integer> suspiciousPorts = Set.of(22, 23, 4444, 135, 3389);

    public boolean isSuspiciousPort(int port) {
        return suspiciousPorts.contains(port);
    }
}

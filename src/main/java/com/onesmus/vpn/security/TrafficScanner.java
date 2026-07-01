package com.onesmus.vpn.security;

import com.onesmus.vpn.SecurityFinding;
import com.onesmus.vpn.VPnNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TrafficScanner implements SecurityScanner {

    @Override
    public String getName() {
        return "TrafficScanner";
    }

    @Override
    public List<SecurityFinding> scan(VPnNode node) throws ScanError {
        List<SecurityFinding> findings = new ArrayList<>();
        detectAnomalies(node, findings);
        return findings;
    }

    private void detectAnomalies(VPnNode node, List<SecurityFinding> findings) {
        // TODO: hook into node's flow logs to detect unusual traffic patterns
    }

    @Override
    public ScanResult scanAsync(VPnNode node) {
        try {
            return new ScanResult(node, scan(node));
        } catch (ScanError err) {
            return new ScanResult(node, err.getMessage());
        }
    }
}
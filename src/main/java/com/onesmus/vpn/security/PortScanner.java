package com.onesmus.vpn.security;

import com.onesmus.vpn.SecurityFinding;
import com.onesmus.vpn.VPnNode;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class PortScanner implements SecurityScanner {

    private static final int[] EXPECTED_PORTS = {51820, 8080, 9090};

    @Override
    public String getName() {
        return "PortScanner";
    }

    @Override
    public List<SecurityFinding> scan(VPnNode node) throws ScanError {
        List<SecurityFinding> findings = new ArrayList<>();
        String host = node.getPublicIp();

        try {
            List<Integer> open = runNmap(host);
            for (int port : open) {
                if (!isExpectedPort(port)) {
                    findings.add(new SecurityFinding(node.getId(),
                            SecurityFinding.FindingType.OPEN_PORT,
                            SecurityFinding.Severity.MEDIUM,
                            "Unexpected open port: " + port));
                }
            }
            return findings;
        } catch (Exception err) {
            throw new ScanError("nmap failed on " + host, err);
        }
    }

    private List<Integer> runNmap(String host) throws Exception {
        List<Integer> open = new ArrayList<>();
        Process proc = Runtime.getRuntime().exec(
                new String[]{"nmap", "-p", "1-1024", "--open", host});
        proc.waitFor();

        try (BufferedReader rdr = new BufferedReader(
                new InputStreamReader(proc.getInputStream()))) {
            String line;
            while ((line = rdr.readLine()) != null) {
                if (line.contains("/tcp")) {
                    open.add(Integer.parseInt(line.split("/")[0].trim()));
                }
            }
        }
        return open;
    }

    private boolean isExpectedPort(int port) {
        for (int p : EXPECTED_PORTS) if (p == port) return true;
        return false;
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
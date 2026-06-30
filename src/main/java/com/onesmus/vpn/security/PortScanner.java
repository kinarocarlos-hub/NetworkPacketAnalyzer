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
    private static final int WG_PORT = 51820;
    private static final int API_PORT = 8080;
    private static final int MANAGEMENT_PORT = 9090;
    
    @Override
    public String getScannerName() {
        return "PortScanner";
    }
    
    @Override
    public List<SecurityFinding> scan(VPnNode node) throws ScanException {
        List<SecurityFinding> findings = new ArrayList<>();
        String target = node.getPublicIp();
        
        try {
            List<Integer> openPorts = scanPorts(target);
            
            for (Integer port : openPorts) {
                if (port != WG_PORT && port != API_PORT && port != MANAGEMENT_PORT) {
                    findings.add(new SecurityFinding(
                        node.getId(),
                        SecurityFinding.FindingType.OPEN_PORT,
                        SecurityFinding.Severity.MEDIUM,
                        "Unexpected open port detected: " + port
                    ));
                }
            }
            
            return findings;
        } catch (Exception e) {
            throw new ScanException("Port scan failed for node " + node.getName(), e);
        }
    }
    
    private List<Integer> scanPorts(String target) throws Exception {
        List<Integer> openPorts = new ArrayList<>();
        
        Process process = Runtime.getRuntime().exec(
            new String[]{"nmap", "-p", "1-1024", "--open", target}
        );
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("/tcp")) {
                    String port = line.split("/")[0].trim();
                    openPorts.add(Integer.parseInt(port));
                }
            }
        }
        
        process.waitFor();
        return openPorts;
    }
    
    @Override
    public ScanResult scanAsync(VPnNode node) {
        try {
            return new ScanResult(node, scan(node));
        } catch (Exception e) {
            return new ScanResult(node, e.getMessage());
        }
    }
}
package com.onesmus.vpn;

import com.onesmus.vpn.security.SecurityScanner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class SecurityScannerService {
    private final List<SecurityScanner> scanners;
    private final VPNNodeRepository nodeRepository;
    private final SecurityFindingRepository findingRepository;
    private final TaskScheduler taskScheduler;
    
    public SecurityScannerService(List<SecurityScanner> scanners, VPNNodeRepository nodeRepository,
                                   SecurityFindingRepository findingRepository, TaskScheduler taskScheduler) {
        this.scanners = scanners;
        this.nodeRepository = nodeRepository;
        this.findingRepository = findingRepository;
        this.taskScheduler = taskScheduler;
    }
    
    public void scheduleScans() {
        for (VPnNode node : nodeRepository.findAll()) {
            scheduleNodeScans(node);
        }
    }
    
    private void scheduleNodeScans(VPnNode node) {
        for (SecurityScanner scanner : scanners) {
            final var sc = scanner;
            final var nd = node;
            taskScheduler.scheduleAtFixedRate(() -> runScan(nd, sc), Duration.ofMinutes(5));
        }
    }
    
    private void runScan(VPnNode node, SecurityScanner scanner) {
        try {
            var result = scanner.scanAsync(node);
            if (result.isCompleted()) {
                for (SecurityFinding finding : result.getFindings()) {
                    findingRepository.save(finding);
                }
            }
        } catch (Exception e) {
            // Log but don't crash the scheduler
        }
    }
}
package com.onesmus.vpn;

import com.onesmus.vpn.security.SecurityScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class SecurityScannerService {

    private static final Logger log = LoggerFactory.getLogger(SecurityScannerService.class);

    private final List<SecurityScanner> scanners;
    private final VPNNodeRepository nodeRepo;
    private final SecurityFindingRepository findingRepo;
    private final TaskScheduler scheduler;

    public SecurityScannerService(List<SecurityScanner> scanners, VPNNodeRepository nodeRepo,
                                  SecurityFindingRepository findingRepo, TaskScheduler scheduler) {
        this.scanners = scanners;
        this.nodeRepo = nodeRepo;
        this.findingRepo = findingRepo;
        this.scheduler = scheduler;
    }

    public void scheduleScans() {
        for (VPnNode node : nodeRepo.findAll()) {
            scheduleScansForNode(node);
        }
    }

    private void scheduleScansForNode(VPnNode node) {
        for (SecurityScanner scanner : scanners) {
            VPnNode nd = node; // capture for lambda
            SecurityScanner sc = scanner;
            scheduler.scheduleAtFixedRate(() -> runScanner(nd, sc), Duration.ofMinutes(5));
        }
    }

    private void runScanner(VPnNode node, SecurityScanner scanner) {
        try {
            SecurityScanner.ScanResult result = scanner.scanAsync(node);
            if (result.isSuccess()) {
                for (SecurityFinding f : result.getFindings()) {
                    findingRepo.save(f);
                }
            } else {
                log.warn("Scanner {} failed on {}: {}", scanner.getName(), node.getName(), result.getError());
            }
        } catch (Exception err) {
            log.error("Scanner {} crashed: {}", scanner.getName(), err.getMessage());
        }
    }
}
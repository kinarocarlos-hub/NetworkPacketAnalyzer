package com.onesmus.vpn.security;

import com.onesmus.vpn.SecurityFinding;
import com.onesmus.vpn.VPnNode;

import java.util.List;

public interface SecurityScanner {
    String getScannerName();
    
    List<SecurityFinding> scan(VPnNode node) throws ScanException;
    
    ScanResult scanAsync(VPnNode node);
    
    class ScanException extends Exception {
        public ScanException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    class ScanResult {
        private final VPnNode node;
        private final List<SecurityFinding> findings;
        private final boolean completed;
        private final String error;
        
        public ScanResult(VPnNode node, List<SecurityFinding> findings) {
            this.node = node;
            this.findings = findings;
            this.completed = true;
            this.error = null;
        }
        
        public ScanResult(VPnNode node, String error) {
            this.node = node;
            this.findings = List.of();
            this.completed = false;
            this.error = error;
        }
        
        public VPnNode getNode() { return node; }
        public List<SecurityFinding> getFindings() { return findings; }
        public boolean isCompleted() { return completed; }
        public String getError() { return error; }
    }
}
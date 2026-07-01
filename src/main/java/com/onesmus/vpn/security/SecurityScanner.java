package com.onesmus.vpn.security;

import com.onesmus.vpn.SecurityFinding;
import com.onesmus.vpn.VPnNode;

import java.util.List;

public interface SecurityScanner {

    String getName();

    List<SecurityFinding> scan(VPnNode node) throws ScanError;

    ScanResult scanAsync(VPnNode node);

    class ScanError extends Exception {
        public ScanError(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    class ScanResult {
        private final VPnNode node;
        private final List<SecurityFinding> findings;
        private final String error;

        public ScanResult(VPnNode node, List<SecurityFinding> findings) {
            this.node = node;
            this.findings = findings;
            this.error = null;
        }

        public ScanResult(VPnNode node, String error) {
            this.node = node;
            this.findings = List.of();
            this.error = error;
        }

        public VPnNode getNode() { return node; }
        public List<SecurityFinding> getFindings() { return findings; }
        public boolean isSuccess() { return error == null; }
        public String getError() { return error; }
    }
}
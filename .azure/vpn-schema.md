# NetPulse VPN - Database Schema

## Zero-PII No-Logs Design

All tables use UUID identifiers. No PII (emails, names, IPs beyond VPN assignment) is stored.

User identity links exist only via `user_id` field (username hash in production) with no PII stored in VPN tables.

---

## Tables

### vpn_nodes

| Column | Type | Description |
|--------|------|-------------|
| id | UUID (PK) | Unique node identifier |
| name | VARCHAR(255) | Node name (e.g., "us-east-1") |
| region | VARCHAR(100) | Geographic region |
| public_endpoint | VARCHAR(255) | Public hostname |
| public_ip | INET | Public IP address |
| wg_port | INT | WireGuard UDP port |
| api_port | INT | Internal API port |
| status | ENUM | ONLINE, OFFLINE, DRAINING, MAINTENANCE |
| last_heartbeat | TIMESTAMP | Last health check |
| active_peers | INT | Current peer count |
| max_peers | INT | Capacity limit |
| listen_subnet | CIDR | VPN subnet (e.g., "10.42.0") |
| supported_crypto | VARCHAR | JSON list of crypto algorithms |

**Indexes:** `idx_node_status`, `idx_node_region`

---

### vpn_peers

| Column | Type | Description |
|--------|------|-------------|
| id | UUID (PK) | Peer identifier |
| user_id | VARCHAR(255) | User identifier (hashed in production) |
| public_key | TEXT | Base64 encoded public key |
| private_key_enc | BYTEA | Encrypted private key (never plaintext) |
| node_id | UUID (FK) | Assigned VPN node |
| ip_address | INET | Assigned VPN IP |
| crypto_algorithm | ENUM | CURVE25519, KYBER, DILITHIUM, etc. |
| status | ENUM | ACTIVE, REVOKED, SUSPENDED |
| assigned_at | TIMESTAMP | When peer was provisioned |
| revoked_at | TIMESTAMP | When peer was revoked |
| last_handshake | TIMESTAMP | Last WireGuard handshake |
| bytes_transferred | BIGINT | Total traffic counter |

**Indexes:** `idx_peer_node`, `idx_peer_user`, `idx_peer_status`, `idx_peer_assigned_at`

---

### connection_logs

| Column | Type | Description |
|--------|------|-------------|
| id | UUID (PK) | Log entry identifier |
| peer_id | UUID (FK) | Associated peer (never user_id) |
| node_id | UUID (FK) | Associated node |
| duration_seconds | BIGINT | Connection duration |
| bytes_up | BIGINT | Upload bytes |
| bytes_down | BIGINT | Download bytes |
| timestamp | TIMESTAMP | Log timestamp |
| log_date | DATE | Partition key (daily rotation) |
| user_id_hash | VARCHAR(64) | Optional for anonymized stats |

**No PII stored** - Connection logs only reference UUIDs and aggregate counters.

**Indexes:** `idx_conn_peer_time`, `idx_conn_timestamp`, `idx_conn_node`

---

### security_findings

| Column | Type | Description |
|--------|------|-------------|
| id | UUID (PK) | Finding identifier |
| node_id | UUID (FK) | Node where finding detected |
| finding_type | ENUM | OPEN_PORT, VULNERABILITY, MALICIOUS_IP, TRAFFIC_SPIKE, PORT_SCAN |
| severity | ENUM | CRITICAL, HIGH, MEDIUM, LOW, INFO |
| title | VARCHAR(500) | Short description |
| description | TEXT | Full details |
| raw_output | TEXT | Scanner raw output |
| port | INT | Affected port (if applicable) |
| cve_id | VARCHAR(50) | CVE identifier |
| resolved | BOOLEAN | Resolution status |
| created_at | TIMESTAMP | Creation time |
| resolved_at | TIMESTAMP | Resolution time |

**Indexes:** `idx_finding_node`, `idx_finding_type`, `idx_finding_severity`, `idx_finding_time`

---

## No-Logs Audit Compliance

1. **PII Separation**: `user_id` in `vpn_peers` table can be a hash or opaque ID - never email/name
2. **Ephemeral Data**: Actual connection metadata (source IPs, timestamps) exist only in WireGuard kernel memory, not persisted
3. **Aggregate-Only**: Connection logs store only byte counters and duration after session ends
4. **Retention Policy**: `connection_logs.log_date` enables automated daily partition dropping
5. **Auditor Verification**: Query joining `vpn_peers` to `connection_logs` produces only UUID/IP counters, no user identity

```sql
-- Auditor query to verify no PII correlation
SELECT cl.peer_id, cl.bytes_up, cl.bytes_down, p.ip_address
FROM connection_logs cl
JOIN vpn_peers p ON cl.peer_id = p.id
WHERE cl.log_date = '2026-06-30';
-- Result: anonymous counters only, no user identity
```
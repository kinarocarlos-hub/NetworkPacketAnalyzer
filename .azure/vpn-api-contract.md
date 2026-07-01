# VPN Control Plane API Contract

## Authentication
All endpoints authenticated via JWT or session token. Node-to-control-plane uses mTLS with node certificates.

---

## Peer Management

### POST /api/vpn/peers
Provision a new WireGuard peer for the authenticated user.

**Request:**
```json
{
  "algorithm": "CURVE25519|KYBER|DILITHIUM|FALCON"  // Optional, defaults to CURVE25519
}
```

**Response 200:**
```json
{
  "peerId": "uuid-v4",
  "ipAddress": "10.42.0.10",
  "publicKey": "base64-encoded-public-key",
  "privateKey": "base64-encoded-private-key",  // Only returned once on provision
  "endpoint": "vpn-us-east.example.com:51820",
  "algorithm": "CURVE25519"
}
```

**Response 400:**
```json
{ "error": "No available nodes" | "Unsupported algorithm" }
```

---

### DELETE /api/vpn/peers/{peerId}
Revoke a peer. Immediately propagates to node.

**Response 200:**
```json
{
  "status": "revoked",
  "peerId": "uuid-v4"
}
```

**Response 403/404:**
```json
{ "error": "Not authorized" | "Peer not found" }
```

---

### GET /api/vpn/peers
List active peers for user.

**Response 200:**
```json
[
  {
    "id": "uuid",
    "ipAddress": "10.42.0.10",
    "publicKey": "...",
    "endpoint": "vpn-us-east.example.com:51820",
    "crypto": "CURVE25519",
    "nodeName": "us-east-1"
  }
]
```

---

### GET /api/vpn/sessions
List active sessions (connection status).

**Response 200:**
```json
[
  {
    "peerId": "uuid",
    "ipAddress": "10.42.0.10",
    "bytesUp": 1234567,
    "bytesDown": 2345678,
    "lastHandshake": "2026-06-30T10:30:00Z",
    "status": "ACTIVE"
  }
]
```

---

## Node Registration (Data Plane)

### POST /api/vpn/nodes/register
Register a new VPN node. Requires pre-shared registration key.

**Request:**
```json
{
  "name": "us-west-1",
  "region": "us-west",
  "endpoint": "vpn-us-west.example.com",
  "publicIp": "203.0.113.42",
  "listenSubnet": "10.43",
  "wgPort": 51820,
  "apiPort": 8080,
  "maxPeers": 1000,
  "registrationKey": "secret-key-from-bootstrap"
}
```

**Response 200:**
```json
{
  "nodeId": "uuid",
  "status": "registered",
  "maxPeers": 1000
}
```

---

### POST /api/vpn/nodes/{nodeId}/heartbeat
Node health check with metrics.

**Request:**
```json
{
  "activePeers": 42,
  "cpuLoad": 0.45,
  "memoryUsedMB": 512,
  "bytesTransferred": 1073741824
}
```

**Response 200:**
```json
{ "status": "healthy" }
```

---

### POST /api/vpn/nodes/{nodeId}/config/pull
Pull latest config delta. Used by nodes in pull-based architecture.

**Request:**
```json
{
  "currentConfigVersion": "uuid-or-timestamp",
  "supportedAlgorithms": ["CURVE25519", "KYBER"]
}
```

**Response 200:**
```json
{
  "configVersion": "uuid-v4",
  "peers": {
    "add": [{"id": "uuid", "publicKey": "...", "ipAddress": "..."}],
    "remove": ["uuid-of-revoked-peer"],
    "update": []
  }
}
```

---

## Security Findings

### GET /api/vpn/admin/findings?severity=HIGH&resolved=false
List security findings (admin only).

**Response 200:**
```json
[
  {
    "id": "uuid",
    "nodeId": "node-uuid",
    "findingType": "OPEN_PORT|VULNERABILITY|MALICIOUS_IP|TRAFFIC_SPIKE|PORT_SCAN",
    "severity": "CRITICAL|HIGH|MEDIUM|LOW|INFO",
    "title": "Open SSH port detected",
    "port": 22,
    "createdAt": "2026-06-30T10:00:00Z"
  }
]
```

---

### POST /api/vpn/admin/findings/{findingId}/resolve
Resolve a finding (admin only).

**Response 200:**
```json
{ "status": "resolved", "findingId": "uuid" }
```

---

## Error Responses

All errors follow:
```json
{ "error": "description", "code": "ERROR_CODE" }
```

Common codes:
- `NO_CAPACITY` - No VPN nodes available
- `INVALID_ALGORITHM` - Unsupported crypto algorithm
- `NODE_OFFLINE` - Node not reachable
- `UNAUTHORIZED` - Not permitted to action
- `NOT_FOUND` - Resource not found
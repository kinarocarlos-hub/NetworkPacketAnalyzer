# Control Plane Push vs Pull Architecture

## Overview

Two patterns for distributing peer configurations from control plane to VPN nodes:

**A) Control Plane Pushes Config → Nodes**
- Control plane initiates HTTP/gRPC connection to each node
- Nodes expose provisioning API endpoint

**B) Nodes Pull Config ← Control Plane**
- Nodes poll control plane API on interval
- Control plane validates node identity and returns delta config

---

## Security Tradeoffs

| Aspect | Push (A) | Pull (B) |
|--------|----------|----------|
| **Node Authentication** | Control plane must maintain node credentials and rotate them | Nodes authenticate with long-lived token or mTLS cert |
| **Network Exposure** | Nodes must accept inbound provisioning connections (higher attack surface) | Only outbound connections from nodes (easier firewall rules) |
| **Credential Compromise** | Compromised node credential → attacker can push config to ALL nodes | Compromised node credential → attacker can only read config for that node |
| **Lateral Movement** | Higher risk via provisioning port | Lower risk, read-only access per node |

**Winner: Pull (B)** - Reduces node attack surface, limits blast radius of credential compromise.

---

## Failure Handling

| Scenario | Push (A) | Pull (B) |
|----------|----------|----------|
| **Node Offline** | Provisioning fails, must retry with back-off | Missed polls, catches up on reconnect |
| **Network Partition** | Unreachable nodes diverge from config | Nodes detect stale config on reconnect |
| **Control Plane Down** | No way to revoke peers on offline nodes | Nodes continue operating until reconnect |
| **Duplicate Config** | Requires idempotency keys | Last-write-wins with version vector |

**Winner: Push (A)** - More immediate revocation, but Pull (B) provides natural eventual consistency.

---

## Protocol Transition Deployment

**Scenario**: Rolling out post-quantum key exchange (e.g., KYBER) across fleet.

### Push (A):
```
Problem: Must push new config to each node
- Requires control plane to track node protocol support
- Cannot push config to offline nodes during maintenance window
- Risk of partial rollout if some nodes fail to apply
- Rollback requires coordinated push to revert configs

Process:
1. Update control plane with KYBER support
2. Push new peer configs to nodes supporting KYBER
3. For nodes in maintenance: config drift until they come online
4. Rollback: push old configs back (timing window for data loss)
```

### Pull (B):
```
Advantage: Natural versioned config distribution
- Each config has version number
- Nodes pull latest config they support
- Offline nodes get correct config on reconnect
- Rollback via version rollback in DB (instant, safe)
- Protocol negotiation happens client-side during pull

Process:
1. Add KYBER config variants to DB with version N+1
2. Nodes pull config, see version bump, apply if supported
3. Unsupported nodes continue with current version
4. Rollback: mark version N+1 as deprecated, nodes fallback naturally
```

**Winner: Pull (B)** - Enables zero-downtime protocol transitions with natural rollback.

---

## Hybrid Recommendation

Use **Pull (B)** as primary with **Push (A) fallback** for critical revocations:

```
Node Behavior:
- Poll control plane every 30s for config updates
- Accept out-of-band revocation push via dedicated endpoint
- Self-heal by reapplying pulled config on boot

Control Plane Behavior:
- Primary: Serve config deltas via /api/node/config
- Fallback: Push critical revocations to all reachable nodes
- Queue revocations for offline nodes in database
```

This provides:
- Best security posture (default pull)
- Immediate revocation capability (push for emergencies)
- Clean protocol transition story

---

## Implementation Notes

1. **Node Identity**: Each node has persistent UUID issued at registration
2. **Config Versioning**: Include `config_version: UUID` in all config payloads
3. **Mutual TLS**: Both patterns require mTLS, but pull only needs node→control auth
4. **Health Check**: Nodes push heartbeats regardless (separate concern from config)
5. **Migration Path**: Current implementation uses push; migrate to pull for production
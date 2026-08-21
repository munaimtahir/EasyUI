# EasyUI Backend — Production Infrastructure & Deployment Guide

## 1. Executive Overview

The **EasyUI Backend** (`com.easyui.backend`) is a lightweight asynchronous Kotlin service built on **Ktor 2.3.11** and the **Netty** NIO engine. It handles:
- Caregiver-to-senior single-use 8-digit pairing token exchange.
- Scoped bearer token authentication (`battery`, `checkin`, `config`, `alerts`).
- Periodic battery and charging status reports.
- Voluntary "I'm OK" check-in notifications.
- High-priority SOS emergency alert broadcasts.
- Remote medication reminder configuration synchronization.
- Instant pairing revocation and total device/account data purging.

---

## 2. Infrastructure Requirements

### 2.1 Compute Sizing

Because the service uses non-blocking coroutines on Netty, resource requirements are minimal:

| Deployment Tier | Minimum Sizing | Recommended Sizing | Estimated Capacity |
| --------------- | -------------- | ------------------ | ------------------ |
| **Staging / Pilot** | 0.5 vCPU, 512 MB RAM | 1 vCPU, 1 GB RAM | 1,000 active devices |
| **Production** | 1 vCPU, 1 GB RAM | 2 vCPU, 2 GB RAM | 10,000+ active devices |

### 2.2 Storage & Persistence

The backend persists device pairing state, active tokens, status snapshots, check-ins, and pending configs to a persistent JSON snapshot file defined by `EASYUI_STORAGE_FILE`.
- **Directory**: `/data` (mount a persistent Docker volume or cloud disk here).
- **Snapshot File**: `/data/easyui_store.json`.
- **Disk Space**: 10 GB SSD is sufficient for persistent state and server logs.

### 2.3 Networking & Domains

Android release builds strictly reject cleartext HTTP traffic. All production communication requires valid HTTPS:
- **Domain Name**: e.g., `api.easyui.app` (Production) or `staging-api.easyui.app` (Staging).
- **Public Inbound Ports**:
  - `80/tcp` (HTTP — redirects to HTTPS)
  - `443/tcp` (HTTPS — TLS termination with valid CA certificate)
- **TLS Certificate**: Automatic provisioning and renewal via Let's Encrypt (using Caddy, Nginx + Certbot, or Cloudflare/AWS ACM).

---

## 3. Environment Variables Reference

| Variable Name | Default Value | Recommended Production Value | Description |
| ------------- | ------------- | ---------------------------- | ----------- |
| `PORT` | `8088` | `8080` (or `8088`) | Port the Netty server binds to inside the container. |
| `HOST` | `0.0.0.0` | `0.0.0.0` | IP address to bind to. |
| `EASYUI_ENV` | `development` | `production` | Masks internal stack traces in HTTP error responses. |
| `EASYUI_STORAGE_FILE` | *(none)* | `/data/easyui_store.json` | Path to persistent store snapshot on disk. |
| `EASYUI_SEED_DEV_TOKENS` | `true` (if dev) | `false` | When `false`, prevents seeding hardcoded dev test tokens. |

---

## 4. Deployment Architecture

```
                  ┌──────────────────────────────────────────────────────────┐
                  │                 Internet / Public Clients                 │
                  └─────────────┬──────────────────────────────┬─────────────┘
                                │ (HTTPS :443)                 │ (HTTPS :443)
                                ▼                              ▼
                  ┌───────────────────────────┐  ┌───────────────────────────┐
                  │   EasyUI Senior Launcher  │  │  EasyUI Caregiver App     │
                  └─────────────┬─────────────┘  └─────────────┬─────────────┘
                                │                              │
                                └──────────────┬───────────────┘
                                               ▼
                              ┌──────────────────────────────────┐
                              │ Reverse Proxy / TLS Termination  │
                              │ (Caddy / Nginx / Cloud Ingress)  │
                              └────────────────┬─────────────────┘
                                               │ (HTTP :8080)
                                               ▼
                              ┌──────────────────────────────────┐
                              │       EasyUI Ktor Backend        │
                              │       (Netty / JVM 17)           │
                              └────────────────┬─────────────────┘
                                               │
                                               ▼
                              ┌──────────────────────────────────┐
                              │ Persistent Disk Mount (`/data`)  │
                              │   `/data/easyui_store.json`      │
                              └──────────────────────────────────┘
```

---

## 5. Deployment Options & Step-by-Step Guides

### Option A: Docker Compose with Automatic HTTPS via Caddy (Recommended for VPS)

This method provides a single-command setup on any cloud VPS (DigitalOcean, Hetzner, AWS Lightsail, Linode, etc.) with automatic Let's Encrypt SSL.

#### 1. Setup Server
Provision an Ubuntu 22.04/24.04 VPS ($4–$6/month), install Docker and Docker Compose:
```bash
sudo apt update && sudo apt install -y docker.io docker-compose-v2
```

#### 2. Configure DNS
Create an **A Record** pointing your domain (e.g. `api.easyui.app`) to your VPS public IPv4 address.

#### 3. Deploy Stack
Clone the repository and launch the stack:
```bash
git clone https://github.com/munaimtahir/easyui.git /opt/easyui
cd /opt/easyui

# Set your domain in Caddy environment
export DOMAIN=api.easyui.app

# Build and start services in background
docker compose up -d --build
```

#### 4. Verify Health
```bash
curl -i https://api.easyui.app/health
# Expected Output:
# HTTP/2 200
# {"status": "healthy"}
```

---

### Option B: Google Cloud Run (Serverless Container)

Cloud Run offers zero server maintenance and automatic HTTPS scaling.

#### 1. Build and Push Container
```bash
gcloud builds submit --tag gcr.io/YOUR_PROJECT_ID/easyui-backend
```

#### 2. Deploy to Cloud Run with Persistent Cloud Storage / Volume Mount
```bash
gcloud run deploy easyui-backend \
  --image gcr.io/YOUR_PROJECT_ID/easyui-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars EASYUI_ENV=production,EASYUI_SEED_DEV_TOKENS=false,EASYUI_STORAGE_FILE=/data/easyui_store.json \
  --port 8080
```

#### 3. Map Custom Domain
In the Cloud Run Console, navigate to **Custom Domains** and map `api.easyui.app`.

---

### Option C: Fly.io (Fast Global Edge Deployment)

#### 1. Launch Fly App
```bash
fly launch --name easyui-backend --no-deploy
```

#### 2. Create Persistent Storage Volume
```bash
fly volumes create easyui_data --size 1 --region ord
```

#### 3. Configure `fly.toml`
```toml
app = "easyui-backend"
primary_region = "ord"

[build]
  dockerfile = "Dockerfile"

[env]
  PORT = "8080"
  HOST = "0.0.0.0"
  EASYUI_ENV = "production"
  EASYUI_SEED_DEV_TOKENS = "false"
  EASYUI_STORAGE_FILE = "/data/easyui_store.json"

[mounts]
  source = "easyui_data"
  destination = "/data"

[http_service]
  internal_port = 8080
  force_https = true
  auto_stop_machines = false
  auto_start_machines = true
```

#### 4. Deploy
```bash
fly deploy
fly certs add api.easyui.app
```

---

## 6. Operational Maintenance & Backups

### 6.1 Backing Up Store State
To backup the persistent state file:
```bash
# Docker volume backup
docker cp $(docker compose ps -q backend):/data/easyui_store.json /backups/easyui_store_$(date +%F).json
```

### 6.2 Viewing Production Logs
```bash
docker compose logs -f backend
```

### 6.3 Restarting the Backend
```bash
docker compose restart backend
```

---

## 7. Active Deployment Configuration (Current Infrastructure)

- **Dedicated Backend VM**: Google Cloud VM (Internal: `vps-clone`, Public IP: `34.46.17.200`)
  - **Role**: designated hosting runner running the EasyUI Ktor server backend on port `8088`.
  - **DNS Resolution**: `easyui.alshifalab.pk` and `api.easyui.alshifalab.pk` are routed through the Caddy proxy on this VM.
- **Signed Release Builds**:
  - Other developer machines are responsible for pulling updates from `main` and generating production-signed client releases (APKs).
  - This ensures that VM CPU and memory are reserved exclusively for the live Ktor server runtime.


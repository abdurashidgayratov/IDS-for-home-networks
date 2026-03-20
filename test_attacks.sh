#!/bin/bash
# ===== SUNIY ATTACK SIMULYATSIYA =====
# Bu script database ga to'g'ridan test alertlar qo'shadi

echo "🚨 Test alertlar qo'shilmoqda..."

DB="ids_home_network"
USER="postgres"

# Funksiya: alert qo'shish
add_alert() {
  local severity=$1
  local signature=$2
  local category=$3
  local src_ip=$4
  local dst_ip=$5
  local protocol=$6

  psql -U $USER -d $DB -c "
    INSERT INTO alerts (severity, signature, category, source_ip, source_port, destination_ip, destination_port, protocol, timestamp, is_read)
    VALUES ($severity, '$signature', '$category', '$src_ip', $((RANDOM % 60000 + 1024)), '$dst_ip', $((RANDOM % 1000 + 1)), '$protocol', NOW(), false);
  " 2>/dev/null && echo "✅ Added: [$severity] $signature" || echo "❌ Failed: $signature"
}

# CRITICAL (severity=1) attacks
add_alert 1 "ET EXPLOIT EternalBlue SMB Remote Code Execution" "Exploit" "192.168.1.105" "10.0.0.1" "TCP"
add_alert 1 "MALWARE-CNC Win.Trojan.Zeus variant outbound connection" "Malware CnC" "10.20.76.69" "8.8.8.8" "TCP"
add_alert 1 "ET SCAN Nmap Scripting Engine User-Agent Detected" "Network Scan" "192.168.1.200" "10.0.0.5" "TCP"

# HIGH (severity=2) attacks
add_alert 2 "ET SCAN Potential SSH Scan OUTBOUND" "Network Scan" "172.16.0.50" "192.168.1.1" "TCP"
add_alert 2 "ET DOS Potential DDoS UDP Flood" "Denial of Service" "203.0.113.42" "10.0.0.1" "UDP"
add_alert 2 "ET POLICY RDP connection attempt" "Policy Violation" "192.168.1.77" "10.0.0.10" "TCP"

# MEDIUM (severity=3) attacks
add_alert 3 "ET INFO DNS Query for Suspicious TLD" "Misc activity" "10.20.76.69" "8.8.8.8" "UDP"
add_alert 3 "ET INFO Observed Cloudflare DNS over HTTPS Domain" "Misc activity" "172.19.14.243" "162.159.61.4" "TCP"
add_alert 3 "SURICATA HTTP suspicious user-agent" "Protocol Command Decode" "192.168.1.50" "104.21.0.1" "TCP"

# LOW (severity=4) attacks
add_alert 4 "ET INFO ICMP Ping Sweep" "Network Scan" "192.168.1.1" "192.168.1.255" "ICMP"
add_alert 4 "ET INFO TLS Handshake Failure" "Misc activity" "140.82.121.3" "10.0.0.5" "TCP"
add_alert 4 "ET POLICY Outbound NTP Version Request" "Policy Violation" "10.0.0.5" "129.6.15.28" "UDP"

echo ""
echo "✅ Hammasi qo'shildi!"
echo "Dashboard da Refresh tugmasini bosing 🔄"

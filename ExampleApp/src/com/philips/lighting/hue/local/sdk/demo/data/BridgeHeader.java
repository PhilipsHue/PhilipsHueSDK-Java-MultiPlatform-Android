package com.philips.lighting.hue.local.sdk.demo.data;

/**
 * Helper data class access point and bridge list.
 * 
 * @author Manmath R
 */
public class BridgeHeader {
    private String ip;
    private String status;
    private long lastHeartbeat;

    /**
     * Instantiates BridgeHeader class.
     * 
     * @param ip
     *            the IP address of the bridge.
     * @param status
     *            the "Connected" or "Disconnected" status of the bridge.
     */
    public BridgeHeader(String ip, String status, long lastHeartbeat) {
        this.ip = ip;
        this.status = status;
        this.lastHeartbeat = lastHeartbeat;
    }

    /**
     * @return the ip
     */
    public String getIPAddress() {
        return ip;
    }

    /**
     * @param ip
     *            the ip to set
     */
    public void setIPAddress(String ip) {
        this.ip = ip;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return last heartbeat for
     */
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

}

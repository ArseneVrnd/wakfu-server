package com.wakfu.emulator.protocol.model;

public class WorldServerInfo {
    private int id;
    private String name;
    private String address;
    private int port;
    private int playerCount;
    private int status; // 0 = offline, 1 = online, 2 = maintenance

    public WorldServerInfo(int id, String name, String address, int port, int playerCount, int status) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.port = port;
        this.playerCount = playerCount;
        this.status = status;
    }

    // Getters et setters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getPort() { return port; }
    public int getPlayerCount() { return playerCount; }
    public int getStatus() { return status; }
}
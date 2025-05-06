package com.wakfu.emulator.world.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapManager {
    private static final Logger logger = LogManager.getLogger(MapManager.class);

    private final Map<Integer, GameMap> maps = new ConcurrentHashMap<>();
    private GameMap defaultMap;

    public MapManager() {
        // Créer une carte par défaut
        GameMap map1 = new GameMap(1, "Zone de départ", 100, 100);
        addMap(map1);
        defaultMap = map1;

        // Ajouter d'autres cartes d'exemple
        addMap(new GameMap(2, "Forêt d'Astrub", 200, 200));
        addMap(new GameMap(3, "Village d'Astrub", 150, 150));

        logger.info("Gestionnaire de cartes initialisé avec {} cartes", maps.size());
    }

    public void addMap(GameMap map) {
        maps.put(map.getId(), map);
    }

    public GameMap getMap(int mapId) {
        return maps.get(mapId);
    }

    public GameMap getDefaultMap() {
        return defaultMap;
    }

    public void setDefaultMap(GameMap defaultMap) {
        this.defaultMap = defaultMap;
    }

    public Collection<GameMap> getAllMaps() {
        return Collections.unmodifiableCollection(maps.values());
    }
}
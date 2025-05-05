import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectStructureCreator {

    private static final String BASE_PATH = "wakfu-server";

    public static void main(String[] args) {
        try {
            // Créer le dossier principal
            createDirectory(BASE_PATH);

            // Créer les modules et leurs fichiers
            createAuthServerModule();
            createWorldServerModule();
            createCommonModule();
            createProtocolModule();
            createDatabaseModule();

            // Créer le pom.xml principal
            createRootPomXml();

            System.out.println("Structure du projet créée avec succès !");
        } catch (IOException e) {
            System.err.println("Erreur lors de la création de la structure du projet : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createAuthServerModule() throws IOException {
        String modulePath = BASE_PATH + "/auth-server";
        createDirectory(modulePath);

        // Structure du module
        String javaPath = modulePath + "/src/main/java/com/wakfu/emulator/auth";
        createDirectory(javaPath);

        // Fichiers Java
        createFile(javaPath + "/AuthServer.java", getAuthServerContent());

        // Créer le pom.xml du module
        createFile(modulePath + "/pom.xml", getAuthServerPomContent());
    }

    private static void createWorldServerModule() throws IOException {
        String modulePath = BASE_PATH + "/world-server";
        createDirectory(modulePath);

        // Structure du module
        String javaPath = modulePath + "/src/main/java/com/wakfu/emulator/world";
        createDirectory(javaPath);

        // Créer un fichier exemple pour le world-server
        createFile(javaPath + "/WorldServer.java", getWorldServerContent());

        // Créer le pom.xml du module
        createFile(modulePath + "/pom.xml", getWorldServerPomContent());
    }

    private static void createCommonModule() throws IOException {
        String modulePath = BASE_PATH + "/common";
        createDirectory(modulePath);

        // Structure du module
        String javaPath = modulePath + "/src/main/java/com/wakfu/emulator/common";
        createDirectory(javaPath);

        // Créer un fichier exemple pour le module common
        createFile(javaPath + "/Constants.java", getConstantsContent());

        // Créer le pom.xml du module
        createFile(modulePath + "/pom.xml", getCommonPomContent());
    }

    private static void createProtocolModule() throws IOException {
        String modulePath = BASE_PATH + "/protocol";
        createDirectory(modulePath);

        // Structure du module
        String javaPath = modulePath + "/src/main/java/com/wakfu/emulator/protocol";
        createDirectory(javaPath);

        // Fichiers Java
        createFile(javaPath + "/Message.java", getMessageContent());
        createFile(javaPath + "/MessageRegistry.java", getMessageRegistryContent());

        // Créer le pom.xml du module
        createFile(modulePath + "/pom.xml", getProtocolPomContent());
    }

    private static void createDatabaseModule() throws IOException {
        String modulePath = BASE_PATH + "/database";
        createDirectory(modulePath);

        // Structure du module
        String resourcesPath = modulePath + "/src/main/resources";
        createDirectory(resourcesPath);

        // Fichier SQL
        createFile(resourcesPath + "/schema.sql", getSchemaContent());

        // Créer le pom.xml du module
        createFile(modulePath + "/pom.xml", getDatabasePomContent());
    }

    private static void createDirectory(String path) throws IOException {
        Path dirPath = Paths.get(path);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
            System.out.println("Dossier créé : " + path);
        }
    }

    private static void createFile(String path, String content) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs(); // Assure que le dossier parent existe

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }

        System.out.println("Fichier créé : " + path);
    }

    private static void createRootPomXml() throws IOException {
        String pomContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n\n" +
                "    <groupId>com.wakfu.emulator</groupId>\n" +
                "    <artifactId>wakfu-server</artifactId>\n" +
                "    <version>0.1.0</version>\n" +
                "    <packaging>pom</packaging>\n\n" +
                "    <modules>\n" +
                "        <module>auth-server</module>\n" +
                "        <module>world-server</module>\n" +
                "        <module>common</module>\n" +
                "        <module>protocol</module>\n" +
                "        <module>database</module>\n" +
                "    </modules>\n\n" +
                "    <properties>\n" +
                "        <maven.compiler.source>17</maven.compiler.source>\n" +
                "        <maven.compiler.target>17</maven.compiler.target>\n" +
                "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
                "    </properties>\n\n" +
                "    <dependencies>\n" +
                "        <!-- Netty pour la gestion des connexions réseau -->\n" +
                "        <dependency>\n" +
                "            <groupId>io.netty</groupId>\n" +
                "            <artifactId>netty-all</artifactId>\n" +
                "            <version>4.1.87.Final</version>\n" +
                "        </dependency>\n" +
                "        \n" +
                "        <!-- JDBC pour PostgreSQL -->\n" +
                "        <dependency>\n" +
                "            <groupId>org.postgresql</groupId>\n" +
                "            <artifactId>postgresql</artifactId>\n" +
                "            <version>42.5.1</version>\n" +
                "        </dependency>\n" +
                "        \n" +
                "        <!-- Logging -->\n" +
                "        <dependency>\n" +
                "            <groupId>org.apache.logging.log4j</groupId>\n" +
                "            <artifactId>log4j-api</artifactId>\n" +
                "            <version>2.19.0</version>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.apache.logging.log4j</groupId>\n" +
                "            <artifactId>log4j-core</artifactId>\n" +
                "            <version>2.19.0</version>\n" +
                "        </dependency>\n" +
                "        \n" +
                "        <!-- JSON -->\n" +
                "        <dependency>\n" +
                "            <groupId>com.fasterxml.jackson.core</groupId>\n" +
                "            <artifactId>jackson-databind</artifactId>\n" +
                "            <version>2.14.1</version>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "</project>";

        createFile(BASE_PATH + "/pom.xml", pomContent);
    }

    private static String getAuthServerContent() {
        return "package com.wakfu.emulator.auth;\n\n" +
                "import org.apache.logging.log4j.LogManager;\n" +
                "import org.apache.logging.log4j.Logger;\n\n" +
                "public class AuthServer {\n" +
                "    private static final Logger logger = LogManager.getLogger(AuthServer.class);\n" +
                "    \n" +
                "    public static void main(String[] args) {\n" +
                "        logger.info(\"Démarrage du serveur d'authentification Wakfu...\");\n" +
                "        \n" +
                "        // Initialiser la configuration\n" +
                "        loadConfiguration();\n" +
                "        \n" +
                "        // Connexion à la base de données\n" +
                "        initDatabase();\n" +
                "        \n" +
                "        // Démarrer le serveur réseau\n" +
                "        startNetworkServer();\n" +
                "        \n" +
                "        logger.info(\"Serveur d'authentification démarré avec succès !\");\n" +
                "    }\n" +
                "    \n" +
                "    private static void loadConfiguration() {\n" +
                "        logger.info(\"Chargement de la configuration...\");\n" +
                "        // TODO: Implémenter le chargement de la configuration\n" +
                "    }\n" +
                "    \n" +
                "    private static void initDatabase() {\n" +
                "        logger.info(\"Initialisation de la connexion à la base de données...\");\n" +
                "        // TODO: Implémenter la connexion à la base de données\n" +
                "    }\n" +
                "    \n" +
                "    private static void startNetworkServer() {\n" +
                "        logger.info(\"Démarrage du serveur réseau...\");\n" +
                "        // TODO: Implémenter le serveur réseau avec Netty\n" +
                "    }\n" +
                "}";
    }

    private static String getWorldServerContent() {
        return "package com.wakfu.emulator.world;\n\n" +
                "import org.apache.logging.log4j.LogManager;\n" +
                "import org.apache.logging.log4j.Logger;\n\n" +
                "public class WorldServer {\n" +
                "    private static final Logger logger = LogManager.getLogger(WorldServer.class);\n" +
                "    \n" +
                "    public static void main(String[] args) {\n" +
                "        logger.info(\"Démarrage du serveur de monde Wakfu...\");\n" +
                "        \n" +
                "        // TODO: Implémenter le serveur de monde\n" +
                "        \n" +
                "        logger.info(\"Serveur de monde démarré avec succès !\");\n" +
                "    }\n" +
                "}";
    }

    private static String getConstantsContent() {
        return "package com.wakfu.emulator.common;\n\n" +
                "public class Constants {\n" +
                "    // Configuration du serveur\n" +
                "    public static final int AUTH_SERVER_PORT = 5558;\n" +
                "    public static final int WORLD_SERVER_PORT = 5556;\n" +
                "    \n" +
                "    // Autres constantes communes\n" +
                "    public static final int PROTOCOL_VERSION = 1;\n" +
                "}";
    }

    private static String getMessageContent() {
        return "package com.wakfu.emulator.protocol;\n\n" +
                "import io.netty.buffer.ByteBuf;\n\n" +
                "public abstract class Message {\n" +
                "    private final int id;\n" +
                "    \n" +
                "    public Message(int id) {\n" +
                "        this.id = id;\n" +
                "    }\n" +
                "    \n" +
                "    public int getId() {\n" +
                "        return id;\n" +
                "    }\n" +
                "    \n" +
                "    public abstract void serialize(ByteBuf buffer);\n" +
                "    \n" +
                "    public abstract void deserialize(ByteBuf buffer);\n" +
                "}";
    }

    private static String getMessageRegistryContent() {
        return "package com.wakfu.emulator.protocol;\n\n" +
                "import java.util.HashMap;\n" +
                "import java.util.Map;\n" +
                "import java.util.function.Supplier;\n\n" +
                "public class MessageRegistry {\n" +
                "    private static final Map<Integer, Supplier<Message>> messageFactories = new HashMap<>();\n" +
                "    \n" +
                "    public static void registerMessage(int id, Supplier<Message> factory) {\n" +
                "        messageFactories.put(id, factory);\n" +
                "    }\n" +
                "    \n" +
                "    public static Message createMessage(int id) {\n" +
                "        Supplier<Message> factory = messageFactories.get(id);\n" +
                "        if (factory == null) {\n" +
                "            throw new IllegalArgumentException(\"Message avec ID \" + id + \" non enregistré\");\n" +
                "        }\n" +
                "        return factory.get();\n" +
                "    }\n" +
                "    \n" +
                "    // Initialiser les messages connus\n" +
                "    static {\n" +
                "        // TODO: Enregistrer les messages connus\n" +
                "        // Exemple : registerMessage(1, VersionMessage::new);\n" +
                "    }\n" +
                "}";
    }

    private static String getSchemaContent() {
        return "-- Créer la base de données et l'utilisateur\n" +
                "-- CREATE DATABASE wakfu_server;\n" +
                "-- CREATE USER wakfu_user WITH PASSWORD 'votre_mot_de_passe';\n" +
                "-- GRANT ALL PRIVILEGES ON DATABASE wakfu_server TO wakfu_user;\n\n" +
                "-- Table des comptes\n" +
                "CREATE TABLE IF NOT EXISTS accounts (\n" +
                "    id SERIAL PRIMARY KEY,\n" +
                "    username VARCHAR(50) UNIQUE NOT NULL,\n" +
                "    password VARCHAR(255) NOT NULL,\n" +
                "    email VARCHAR(100) UNIQUE NOT NULL,\n" +
                "    last_login TIMESTAMP,\n" +
                "    is_banned BOOLEAN DEFAULT FALSE,\n" +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                ");\n\n" +
                "-- Table des personnages\n" +
                "CREATE TABLE IF NOT EXISTS characters (\n" +
                "    id SERIAL PRIMARY KEY,\n" +
                "    account_id INTEGER REFERENCES accounts(id),\n" +
                "    name VARCHAR(50) UNIQUE NOT NULL,\n" +
                "    level INTEGER DEFAULT 1,\n" +
                "    experience BIGINT DEFAULT 0,\n" +
                "    class_id SMALLINT NOT NULL,\n" +
                "    gender SMALLINT NOT NULL,\n" +
                "    appearance JSONB,\n" +
                "    stats JSONB,\n" +
                "    position JSONB,\n" +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                ");";
    }

    private static String getAuthServerPomContent() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <parent>\n" +
                "        <artifactId>wakfu-server</artifactId>\n" +
                "        <groupId>com.wakfu.emulator</groupId>\n" +
                "        <version>0.1.0</version>\n" +
                "    </parent>\n" +
                "    <modelVersion>4.0.0</modelVersion>\n\n" +
                "    <artifactId>auth-server</artifactId>\n\n" +
                "    <dependencies>\n" +
                "        <dependency>\n" +
                "            <groupId>com.wakfu.emulator</groupId>\n" +
                "            <artifactId>protocol</artifactId>\n" +
                "            <version>${project.version}</version>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>com.wakfu.emulator</groupId>\n" +
                "            <artifactId>common</artifactId>\n" +
                "            <version>${project.version}</version>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "</project>";
    }

    private static String getWorldServerPomContent() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <parent>\n" +
                "        <artifactId>wakfu-server</artifactId>\n" +
                "        <groupId>com.wakfu.emulator</groupId>\n" +
                "        <version>0.1.0</version>\n" +
                "    </parent>\n" +
                "    <modelVersion>4.0.0</modelVersion>\n\n" +
                "    <artifactId>world-server</artifactId>\n\n" +
                "    <dependencies>\n" +
                "        <dependency>\n" +
                "            <groupId>com.wakfu.emulator</groupId>\n" +
                "            <artifactId>protocol</artifactId>\n" +
                "            <version>${project.version}</version>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>com.wakfu.emulator</groupId>\n" +
                "            <artifactId>common</artifactId>\n" +
                "            <version>${project.version}</version>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "</project>";
    }

    private static String getCommonPomContent() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <parent>\n" +
                "        <artifactId>wakfu-server</artifactId>\n" +
                "        <groupId>com.wakfu.emulator</groupId>\n" +
                "        <version>0.1.0</version>\n" +
                "    </parent>\n" +
                "    <modelVersion>4.0.0</modelVersion>\n\n" +
                "    <artifactId>common</artifactId>\n" +
                "</project>";
    }

    private static String getProtocolPomContent() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <parent>\n" +
                "        <artifactId>wakfu-server</artifactId>\n" +
                "        <groupId>com.wakfu.emulator</groupId>\n" +
                "        <version>0.1.0</version>\n" +
                "    </parent>\n" +
                "    <modelVersion>4.0.0</modelVersion>\n\n" +
                "    <artifactId>protocol</artifactId>\n\n" +
                "    <dependencies>\n" +
                "        <dependency>\n" +
                "            <groupId>com.wakfu.emulator</groupId>\n" +
                "            <artifactId>common</artifactId>\n" +
                "            <version>${project.version}</version>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "</project>";
    }

    private static String getDatabasePomContent() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <parent>\n" +
                "        <artifactId>wakfu-server</artifactId>\n" +
                "        <groupId>com.wakfu.emulator</groupId>\n" +
                "        <version>0.1.0</version>\n" +
                "    </parent>\n" +
                "    <modelVersion>4.0.0</modelVersion>\n\n" +
                "    <artifactId>database</artifactId>\n" +
                "</project>";
    }
}
package src.KnowledgeDatabase;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.GraphDatabase;
public class KnowledgeDatabase {
    private boolean activeConnection = false;
    private boolean activeSession = false;

    public knowledgeDatabase() {

    }

    public void establishConnection(String dbUri, String dbUser, String dbPassword) {
        try(var driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword))) {
            driver.verifyConnectivity();
            this.activeConnection = true;
        }
    }

    public void closeConnection() {
        if (activeConnection) {

        }
    }

    public

}

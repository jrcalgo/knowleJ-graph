package ai.knowlej.Database;

import java.util.ArrayList;

import org.neo4j.driver.AuthTokens;

public class kbDatabaseClient {
    private boolean activeConnection = false;
    private boolean activeSession = false;
    private ArrayList<String> sessions;

    public kbDatabaseClient() {
        super();
    }

    public kbDatabaseClient(String dbUri, String dbUser, String dbPassword) {
        establishConnection(dbUri, dbUser, dbPassword);
        this.sessions = null;
    }

    public kbDatabaseClient(String dbUri, String dbUser, String dbPassword, String newSessionName)  {
        if (establishConnection(dbUri, dbUser, dbPassword)) {
            if (addSession(newSessionName)) {
                this.sessions = new ArrayList<String>();
                this.sessions.add(newSessionName);
            } else {
                System.out.println("Session creation failed");
            }
        } else {
            System.out.println("Connection failed");
        }
    }

    public boolean establishConnection(String dbUri, String dbUser, String dbPassword) {
        if (activeConnection) {
            System.out.println("Connection already established");
            return true;
        }
        try (var driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword))) {
            driver.verifyConnectivity();
            this.activeConnection = true;
        } catch (Exception e) {
            System.out.println("Connection failed");
            return false;
        }
        return false;
    }

    public boolean addSession(String name) {
        if (!sessions.contains(name)) {
            try (var session = driver.session()) {
                session.writeTransaction(tx -> tx.run("MERGE (a:Person {name: $x})", parameters("x", name)));
                this.activeSession = true;
            } catch (Exception e) {
                System.out.println("Session creation failed");
                return false;
            }
        } else {
            System.out.println("Session name already exists");
            return false;
        }
        return this.activeSession;
    }

    public void closeConnection() {
        if (activeConnection) {
            driver.close();
            this.activeConnection = false;
        }
    }

    public boolean closeSession(String name) {
        if (activeSession && sessions.contains(name)) {
            try (var session = driver.session()) {
                session.writeTransaction(tx -> tx.run("MATCH (a:Person {name: $x}) DETACH DELETE a", parameters("x", name)));
                return true;
            } catch (Exception e) {
                System.out.println("Session deletion failed");
                return false;
            }
        }
        return false;
    }

    public void addNewDomain() {

    }

    public void addNewModel() {
        
    }

    public void addInferenceChain() {

    }
}

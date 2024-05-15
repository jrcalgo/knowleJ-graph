package ai.knowlej.Database;

import org.neo4j.driver.*;

public class BuildGraphDatabase {
    static private Driver driver;
    static private AuthToken authenticationToken;
    static private Session session;
    static private boolean activeSession = false;

    public BuildGraphDatabase() {
        super();
    }

    public Driver initDriver(String dbUri, String dbUser, String dbPassword) {
        this.authenticationToken = AuthTokens.basic(dbUser, dbPassword);
        this.driver = GraphDatabase.driver(dbUri, authenticationToken);
        this.driver.verifyConnectivity();
        return driver;
    }

    public Session openSession(char readOrWrite, String dbName) {
        if (activeSession != true) {
            SessionConfig sessionConfig = null;
            if (readOrWrite != Character.toLowerCase('r') && readOrWrite != Character.toLowerCase('w')) {
                return null;
            } else if (readOrWrite == 'r') {
                sessionConfig = SessionConfig.builder()
                        .withDefaultAccessMode(AccessMode.READ)
                        .withDatabase(dbName)
                        .build();
            } else if (readOrWrite == 'w') {
                sessionConfig = SessionConfig.builder()
                        .withDefaultAccessMode(AccessMode.WRITE)
                        .withDatabase(dbName)
                        .build();
            }
            this.session = driver.session(sessionConfig);
            this.activeSession = true;
        }
        return this.session;
    }

    public void closeSession() {
        if (this.activeSession == true) {
            this.session.close();
            this.session = null;
            this.activeSession = false;
        }
    }

    protected class BuildDomain {

        public void createDomain(String domainName) {

        }

        public void pointDomainToDomain() {

        }
    }

    protected class BuildSubdomain {

        public void createSubdomain(String subdomainName) {

        }

        public void pointSubdomainToSubdomain() {

        }
    }

    protected class BuildKnowledgeBase {

        public void createStaticKB(String sKBName) {

        }

        public void createInferenceKB(String iKBName) {

        }

        public void createKBNode(String nodeName) {

        }

        public void pointKBNodeToKBNode() {

        }

        public void addInferenceChain() {

        }
    }
}

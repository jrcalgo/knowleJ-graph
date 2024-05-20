package ai.knowlej.Database

import ai.knowlej.DataStructures.DeductionGraphNode
import ai.knowlej.DataStructures.DirectedDeductionGraph
import ai.knowlej.Database.Models.*

import org.neo4j.driver.*

/*
* provides methods to connect to neo4j knowledge database(s) and write new nodes/pointers to nodes. Useful in tandem with
* algorithmic graph population.
*/
open class BuildNeo4jDatabase {
    fun initDriver(database: String?, dbUri: String?, dbUser: String?, dbPassword: String?): Driver? {
        authenticationToken = AuthTokens.basic(dbUser, dbPassword)
        try {
            driver = GraphDatabase.driver(dbUri, authenticationToken)
            driver!!.verifyConnectivity()
        }
        catch (e: Exception) {
            return null
        }
        dbName = database
        return driver
    }

    fun closeDriver() {
        driver!!.close()
    }

    fun openSession(readOrWrite: Char): Session? {
        if (!activeSession) {
            var sessionConfig: SessionConfig? = null
            if (readOrWrite != 'r'.lowercaseChar() && readOrWrite != 'w'.lowercaseChar()) {
                return null
            } else if (readOrWrite == 'r') {
                sessionConfig = SessionConfig.builder()
                    .withDefaultAccessMode(AccessMode.READ)
                    .withDatabase(dbName)
                    .build()
            } else if (readOrWrite == 'w') {
                sessionConfig = SessionConfig.builder()
                    .withDefaultAccessMode(AccessMode.WRITE)
                    .withDatabase(dbName)
                    .build()
            }
            session = driver!!.session(sessionConfig)
            activeSession = true
        }
        return session
    }

    fun closeSession() {
        if (activeSession) {
            session!!.close()
            session = null
            activeSession = false
        }
    }

    fun createNewDatabase(newDatabaseName: String?) {
        // check if database already exists
        try {
            openSession('r').use { _ ->

            }
        } catch (e: Exception) {

        } finally {
            closeSession()
        }
        // create new database
        try {
            openSession('w').use { _ ->

            }
        } catch (e: Exception) {

        } finally {
            closeSession()
        }
    }

    open inner class BuildDomainNodes : BuildNeo4jDatabase() {
        fun checkForDomain(domainName: String?): Boolean {
            // check if domain already exists
            var domainExistence: Boolean = false
            try {
                openSession('r').use { _ ->
                    val result = session!!.run("MATCH (d:Domain) WHERE d.name = \$domainName RETURN d", mapOf("domainName" to domainName))
                    if (result.hasNext()) {
                        domainExistence = true
                    }
                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
            return domainExistence
        }

        fun createDomainNode(domainName: String?, domainLabels: Array<String?>?, domainProperties: Map<String, String>?) {
            if (!checkForDomain(domainName)) {
                return
            }
            // create new domain node
            val domainNode = DomainNode(domainName, domainLabels, domainProperties)
            try {
                openSession('w').use { _ ->

                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
        }

        fun createDomainRelationship(domainName1: String?, domainName2: String?) {
            if (!checkForDomain(domainName1) || !checkForDomain(domainName2)) {
                return
            }
            // check if relationship already exists
            try {
                openSession('r').use { _ ->

                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }

        }
    }

     open inner class BuildSubdomainNodes : BuildDomainNodes() {
         fun checkForSubdomain(domainName: String?, subdomainName: String?): Boolean {
             // first, check if domain exists
             if (!checkForDomain(domainName)) {
                 return false
             }
             // then check if subdomain already exists
             openSession('r').use { _ -> }
         }

        fun createSubdomainNode(domainName: String?, subdomainName: String?, subdomainLabels: Array<String?>?, subdomainProperties: Map<String, String>?) {
            // check if subdomain already exists
            if (!checkForSubdomain(domainName, subdomainName)) {
                return
            }
            // create new subdomain node
            val subdomainNode = SubdomainNode(subdomainName, subdomainLabels, subdomainProperties)
            try {
                openSession('w').use { _ ->

                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
        }

        fun createSubdomainRelationship(domainFromName: String?, subdomainFromName: String?, domainToName: String?, subdomainToName: String?) {
            if (!checkForSubdomain(domainFromName, subdomainFromName) || !checkForSubdomain(domainToName, subdomainToName)) {
                return
            }
            // check if relationship already exists
            try {
                openSession('r').use { _ ->

                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }

            try {
                openSession('w', )
            }
        }
    }

    open inner class BuildKnowledgeBaseNodes : BuildSubdomainNodes() {
        fun createConceptualKB(domainName: String?, subdomainName: String?, nodes: ArrayList<String?>) {
            if (!checkForSubdomain(domainName, subdomainName)) {
                return
            }
            // check if domain already exists
            var subDomainExistence: Boolean = false
            try {
                openSession('r').use { _ ->
                    val result = session!!.run("MATCH (d:Domain) WHERE d.name = \$domainName RETURN d", mapOf("domainName" to domainName))
                    if (result.hasNext()) {
                        domainExistence = true
                    }
                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
            return domainExistence
        }

        fun createLogicalKB(domainName: String?, subdomainName: String?, premiseNodes: ArrayList<String?>) {
            if (!checkForSubdomain(domainName, subdomainName)) {
                return
            }
            for (premise in premiseNodes) {
                try {
                    openSession('w').use { _ ->

                    }
                } catch (e: Exception) {

                } finally {
                    closeSession()
                }
            }
        }

        private fun createKBNode(domainName: String?, subdomainName: String?) {
        }

        fun createKBNodeRelationship(
            fromNode: String?,
            toNode: String?,
            connectionLabels: Array<String?>?,
            connectionProperties: HashMap<String?, String?>?
        ) {
        }

        fun mergeInferenceChain(
            iKBName: String?,
            graph: DirectedDeductionGraph?,
            inferenceChain: ArrayList<DeductionGraphNode?>?
        ) {
        }
    }

    companion object {
        private var dbName: String? = null
        private var driver: Driver? = null
        private var authenticationToken: AuthToken? = null
        private var session: Session? = null
        private var activeSession = false
    }
}

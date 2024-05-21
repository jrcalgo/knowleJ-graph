package ai.knowlej.Database

import ai.knowlej.PropositionalLogic.Logic.Proposition

import ai.knowlej.DataStructures.DeductionGraphNode
import ai.knowlej.DataStructures.DirectedDeductionGraph
import ai.knowlej.Database.Models.*

import org.neo4j.driver.*
import kotlin.run

/*
* provides methods to connect to neo4j knowledge database(s) and write new nodes/pointers to nodes. Useful in tandem with
* algorithmic graph population.
*/
open class BuildNeo4jDatabase {
    fun initDriver(databaseName: String?, dbUri: String?, dbUser: String?, dbPassword: String?): Driver? {
        authenticationToken = AuthTokens.basic(dbUser, dbPassword)
        if (driver == null) {
            try {
                driver = GraphDatabase.driver(dbUri, authenticationToken)
                driver!!.verifyConnectivity()
            }
            catch (e: Exception) {
                return null
            }
            dbName = databaseName
            sessionReadConfig = SessionConfig.builder()
                .withDefaultAccessMode(AccessMode.READ)
                .withDatabase(dbName)
                .build()
            sessionWriteConfig = SessionConfig.builder()
                .withDefaultAccessMode(AccessMode.WRITE)
                .withDatabase(dbName)
                .build()
        }
        return driver
    }

    fun openSession(readOrWrite: Char): Session? {
        if (session == null) {
            try {
                if (readOrWrite != 'r'.lowercaseChar() && readOrWrite != 'w'.lowercaseChar()) {
                    return null
                } else if (readOrWrite == 'r') {
                    session = driver!!.session(sessionReadConfig)
                } else if (readOrWrite == 'w') {
                    session = driver!!.session(sessionWriteConfig)
                }
            } catch (e: Exception) {
                return null
            }
        }
        return session
    }

    fun closeSession(): Session? {
        if (session != null) {
            session!!.close()
            session = null
        }
        return session
    }

    fun closeDriver() {
        if (session != null)
            closeSession()
        driver!!.close()
        driver = null
    }

    fun createNewDatabase(newDatabaseName: String?): Boolean {
        // check if database already exists
        var databaseExistence: Boolean = false
        try {
            openSession('r').use { _ ->
                val result = session!!.run("SHOW DATABASES")
                while (result.hasNext()) {
                    val databases = result.next()
                    if (databases["name"].asString() == newDatabaseName)
                        databaseExistence = true
                }
            }
        } catch (e: Exception) {
            throw Exception("Check for existing database failed!")
        } finally {
            closeSession()
        }

        var databaseCreated: Boolean = false
        if (!databaseExistence) {
            // create new database
            try {
                openSession('w').use { _ ->
                    session!!.run("CREATE DATABASE \$newDatabaseName", mapOf("newDatabaseName" to newDatabaseName))
                    databaseCreated = true
                }
            } catch (e: Exception) {
                throw Exception("New database creation failed!")
            } finally {
                closeSession()
            }
        }
        return databaseCreated
    }

    open inner class BuildDomainNodes : BuildNeo4jDatabase() {
        fun checkForDomain(domainName: String?, continueReadSession: Boolean = false): Boolean {
            // check if domain already exists
            var domainExistence: Boolean = false
            try {
                if (session == null) 
                    openSession('r')
                val result = session!!.run("MATCH (d:Domain) WHERE d.name = \$domainName RETURN d", mapOf("domainName" to domainName))
                if (result.hasNext()) 
                    domainExistence = true      
            } catch (e: Exception) {
                throw Exception("checkForDomain failed!")
            } finally {
                if (!continueReadSession && session != null)
                    closeSession()
            }
            return domainExistence
        }

        fun createDomainNode(domainNode: DomainNode) {
            if (!checkForDomain(domainNode.domainName)) {
                return
            }
            // create new domain node
            try {
                openSession('w').use { _ ->
                    val parameters = mapOf("domainName" to domainNode.domainName, "domainLabels" to domainNode.domainLabels, "domainProperties" to domainNode.domainProperties)
                    val cypher = "CREATE (d:Domain {name: \$domainName} )"
                    val result = session!!.run(cypher, parameters)
                    if (result.hasNext()) {

                    }
                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
        }

        fun createDomainRelationship(domainNode1: DomainNode, domainNode2: DomainNode) {
            if (!checkForDomain(domainNode1.domainName, true) || !checkForDomain(domainNode2.domainName, false)) {
                return
            }
            // check if relationship already exists
            try {
                openSession('w').use { _ ->
                    val parameters = mapOf("domainName1" to domainNode1.domainName, "domainName2" to domainNode2.domainName)
                    val cypher = 
                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
        }
    }

     open inner class BuildSubdomainNodes : BuildDomainNodes() {
         fun checkForSubdomain(domainName: String, subdomainName: String, continueReadSession: Boolean = false): Boolean {
            //  check if subdomain and parent domain already exists
            var subdomainExistence: Boolean = false
            try {
                if (checkForDomain(domainName, true)) {
                    val result = session!!.run(
                        "MATCH (d:Domain)-[:DOMAIN_OF]-(s:Subdomain) WHERE d.name = \$domainName AND s.name = \$subdomainName RETURN s",
                        mapOf("domainName" to domainName, "subdomainName" to subdomainName)
                    )
                    if (result.hasNext())
                        subdomainExistence = true
                }
            } catch (e: Exception) {
                throw Exception("checkForSubdomain failed!")
            } finally {
                if (!continueReadSession && session != null)
                    closeSession()
            }
            return subdomainExistence
         }

        fun createSubdomainNode(domainName: String, sbdNode: SubdomainNode): Boolean? {
            // check if subdomain already exists
            if (!checkForSubdomain(domainName, sbdNode.subdomainName)) {
                return null
            }
            // check and create new subdomain node
            var writeSuccessful = false
            try {
                openSession('w').use { _ ->
                    val parameters = mapOf("domainName" to domainName, "subdomainName" to sbdNode.subdomainName, "subdomainLabels" to sbdNode.subdomainLabels, "subdomainProperties" to sbdNode.subdomainProperties)
                    val cypher = "MERGE (d:Domain {name})-[:DOMAIN_OF]-(s:Subdomain: \$subdomainLabels {name: \$subdomainName} ON CREATE SET s += \$subdomainProperties) ON MATCH RETURN s"
                    val result = session!!.run(cypher, parameters)
                    if (result.hasNext())
                        writeSuccessful = true
                }
            } catch (e: Exception) {
                throw Exception("createSubdomainNode failed!")
            } finally {
                closeSession()
            }
            return writeSuccessful
        }

        fun createSubdomainRelationship(domainFromName: String, subdomainFromName: String, domainToName: String, subdomainToName: String, relationshipLabels: Array<String>?, relationshipProperties: HashMap<String, String>?): Boolean? {
            if (!checkForSubdomain(domainFromName, subdomainFromName, true) || !checkForSubdomain(domainToName, subdomainToName, false)) {
                return null
            }
            // check and create new subdomain relationship
            var relationshipExistence = false
            try {
                openSession('w').use { _ ->
                    val parameters = mapOf("domainFromName" to domainFromName, "subdomainFromName" to subdomainFromName, "domainToName" to domainToName, "subdomainToName" to subdomainToName)
                    val cypher = "MERGE (d1:Domain {name: \$domainFromName})-[:DOMAIN_OF]->(s1:Subdomain {name: \$subdomainFromName})-(s2:Subdomain {name: \$subdomainToName})<-[:DOMAIN_OF]-(d2:Domain {name: \$domainToName})"
                    val result = session!!.run(cypher, parameters)
                    if (result.hasNext()) {
                        relationshipExistence = true
                    }
                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
            return relationshipExistence
        }
    }

    open inner class BuildKnowledgeBaseNodes : BuildSubdomainNodes() {
        fun createAbstractKB(domainName: String, subdomainName: String, abstractNodes: ArrayList<String>) {
            if (!checkForSubdomain(domainName, subdomainName)) {
                return
            }
            // check and create knowledge base nodes
            try {
                openSession('w').use { _ ->
                    
                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
        }

        fun createLogicalKB(domainName: String, subdomainName: String, premiseNodes: Array<LogicNode>) {
            if (!checkForSubdomain(domainName, subdomainName, true)) {
                return
            }

            try {
                openSession('w').use { _ ->

                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
            
        }

        private fun createKBNode(domainName: String, subdomainName: String, lN: LogicNode): Boolean? {
            if (!checkForSubdomain(domainName, subdomainName)) {
                return null
            }
            // check and create knowledge base node
            try {
                openSession('w').use { _ ->

                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
        }

        fun createKBNodeRelationship(fromNode: String, toNode: String, relationshipLabels: Array<String>?, relationshipProperties: HashMap<String, String>?) {

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
        private var sessionReadConfig: SessionConfig? = null
        private var sessionWriteConfig: SessionConfig? = null
    }
}

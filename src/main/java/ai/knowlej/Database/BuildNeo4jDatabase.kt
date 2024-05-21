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
        fun checkForDomain(domainNode: DomainGroupNode, continueReadSession: Boolean = false): Boolean {
            // check if domain already exists
            var domainExistence: Boolean = false
            try {
                if (session == null) 
                    openSession('r')
                
                val parameters = mapOf("domainName" to domainNode.domainName, "labels" to domainNode.domainLabels.joinToString(":"), "domainProperties" to domainNode.domainProperties)
                val cypher = "MATCH (d:Domain:\$labels {name: \$domainName, properties: \$domainProperties}) WHERE d.name = \$domainName RETURN d"
                val result = session!!.run(cypher, parameters)
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

        fun createDomainNode(domainNode: DomainGroupNode): org.neo4j.driver.types.Node? {
            if (!checkForDomain(domainNode)) {
                return null
            }
            // create new domain node
            var createdNode: org.neo4j.driver.types.Node? = null
            try {
                openSession('w').use { _ ->
                    val parameters = mapOf("domainName" to domainNode.domainName, "labels" to domainNode.domainLabels.joinToString(":"), "domainProperties" to domainNode.domainProperties)
                    val cypher = "CREATE (d:Domain:\$labels {name: \$domainName, properties: \$domainProperties}) RETURN d"
                    val result = session!!.run(cypher, parameters)
                    if (result.hasNext()) {
                        createdNode = result.next().get("d").asNode()
                    }
                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
            return createdNode
        }

        fun createDomainRelationship(domainNode1: DomainGroupNode, domainNode2: DomainGroupNode): org.neo4j.driver.types.Relationship? {
            if (!checkForDomain(domainNode1, true) || !checkForDomain(domainNode2, false)) {
                return null
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
         fun checkForSubdomain(domainNode: DomainGroupNode, subdomainNode: SubdomainGroupNode, continueReadSession: Boolean = false): Boolean {
            //  check if subdomain and parent domain already exists
            var subdomainExistence: Boolean = false
            try {
                if (checkForDomain(domainNode, true)) {
                    val domainParameters = mapOf("domainName" to domainNode.domainName, "domainLabels" to if (domainNode.domainLabels.isNotEmpty()) domainNode.domainLabels.joinToString(":") else "", "domainProperties" to if (domainNode.domainProperties.isNotEmpty()) domainNode.domainProperties else "")
                    val subdomainParameters = mapOf("subdomainName" to subdomainNode.subdomainName, "subdomainLabels" to subdomainNode.subdomainLabels.joinToString(":"), "subdomainProperties" to subdomainNode.subdomainProperties)
                    val cypher = "MATCH (d:Domain:\$domainLabels {name: \$domainName, properties: \$domainProperties})-[:DOMAIN_OF]-(s:Subdomain:\$subdomainLabels {name: \$subdomainName, properties: \$subdomainProperties}) RETURN s"
                    val result = session!!.run(cypher, domainParameters+subdomainParameters)
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

        fun createSubdomainNode(domainNode: DomainGroupNode, subdomainNode: SubdomainGroupNode): org.neo4j.driver.types.Node? {
            // check if subdomain already exists
            if (!checkForSubdomain(domainNode, subdomainNode)) {
                return null
            }
            // check and create new subdomain node
            var createdNode: org.neo4j.driver.types.Node? = null
            try {
                openSession('w').use { _ ->
                    val domainParameters = mapOf("domainName" to domainNode.domainName, "domainLabels" to domainNode.domainLabels.joinToString(":"), "domainProperties" to domainNode.domainProperties)
                    val subdomainParameters = mapOf("subdomainName" to subdomainNode.subdomainName, "subdomainLabels" to subdomainNode.subdomainLabels.joinToString(":"), "subdomainProperties" to subdomainNode.subdomainProperties)
                    val parameters = domainParameters+subdomainParameters
                    val cypher = "MERGE (d:Domain {name})-[:DOMAIN_OF]-(s:Subdomain: \$subdomainLabels {name: \$subdomainName, properties: \$subdomainProperties} RETURN s"
                    val result = session!!.run(cypher, parameters)
                    if (result.hasNext())
                        createdNode = result.next().get("s").asNode()
                }
            } catch (e: Exception) {
                throw Exception("createSubdomainNode failed!")
            } finally {
                closeSession()
            }
            return createdNode
        }

        fun createSubdomainRelationship(domainNode1: DomainGroupNode, subdomainNode1: SubdomainGroupNode, domainNode2: DomainGroupNode, subdomainNode2: SubdomainGroupNode, relationshipLabels: Array<String>?, relationshipProperties: HashMap<String, String>?): Boolean? {
            if (!checkForSubdomain(domainNode1, subdomainNode1, true) || !checkForSubdomain(domainNode2, subdomainNode2, false)) {
                return null
            }
            // check and create new subdomain relationship
            var relationshipExistence = false
            try {
                openSession('w').use { _ ->
                    val fromDomainParameters = mapOf("fromDomainName" to domainNode1.domainName, "fromDomainLabels" to domainNode1.domainLabels.joinToString(":"), "fromDomainProperties" to domainNode1.domainProperties)
                    val fromSubdomainParameters = mapOf("fromSubdomainName" to subdomainNode1.subdomainName, "fromSubdomainLabels" to subdomainNode1.subdomainLabels.joinToString(":"), "fromSubdomainProperties" to subdomainNode1.subdomainProperties)
                    val toDomainParameters = mapOf("toDomainName" to domainNode2.domainName, "toDomainLabels" to domainNode2.domainLabels.joinToString(":"), "toDomainProperties" to domainNode2.domainProperties)
                    val toSubdomainParameters = mapOf("toSubdomainName" to subdomainNode2.subdomainName, "toSubdomainLabels" to subdomainNode2.subdomainLabels.joinToString(":"), "toSubdomainProperties" to subdomainNode2.subdomainProperties)
                    val parameters = fromDomainParameters+fromSubdomainParameters+toDomainParameters+toSubdomainParameters
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
        fun createAbstractKB(domainNode: DomainGroupNode, subdomainNode: SubdomainGroupNode, abstractKBNode: AbstractKBNode): org.neo4j.driver.types.Node? {
            if (!checkForSubdomain(domainNode, subdomainNode)) {
                return null
            }
            // check and create knowledge base nodes
            try {
                openSession('w').use { _ ->
                    val parameters = mapOf("")
                    var cypher = 
                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
        }

        fun createLogicalKB(domainNode: DomainGroupNode, subdomainNode: SubdomainGroupNode, logicKBNode: LogicKBNode): org.neo4j.driver.types.Node? {
            if (!checkForSubdomain(domainNode, subdomainNode)) {
                return null
            }
            var createdKB: org.neo4j.driver.types.Node? = null
            try {
                openSession('w').use { _ ->
                    val parameters = mapOf("domainName" to domainName, "subdomainName" to subdomainName, "logicGroupName" to logicKBNode.logicGroupName, "logicGroupLabels" to logicKBNode.logicGroupLabels, "logicGroupProperties" to logicKBNode.logicGroupProperties)
                    var cypher = "MERGE (d:Domain {name:})"
                    for (node in logicKBNode.logicNodes) {
                        val nodeParameters = mapOf("logicSentence" to node.logicSentence, "nodeLabels" to node.logicLabels, "nodeProperties" to node.logicProperties)
                        cypher = 
                        val result
                        if (result.hasNext()) {

                        }
                    }
                }
            } catch (e: Exception) {

            } finally {
                closeSession()
            }
            return createdKB
        }

        private fun createAbstractNode(domainNode: DomainGroupNode, subdomainNode: SubdomainGroupNode,  abstractNode: AbstractKnowledgeNode): org.neo4j.driver.types.Node? {
            if (!checkForSubdomain(domainNode, subdomainNode)) {
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
            return null
        }

        fun createKBNodeRelationship(fromNode: Any, toNode: Any, relationshipLabels: Array<String>?, relationshipProperties: HashMap<String, String>?): org.neo4j.driver.types.Relationship? {
            if (!(fromNode is LogicNode) && !(fromNode is AbstractKnowledgeNode)) {
                return null
            } else if (!(toNode is LogicNode) && !(toNode is AbstractKnowledgeNode)) {
                return null
            }

            return null
        }

        fun mergeInferenceChain(logicGroup: LogicGroupNode, graph: DirectedDeductionGraph?, inferenceChain: ArrayList<DeductionGraphNode?>?) {

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

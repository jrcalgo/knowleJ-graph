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
                throw Exception("createDomainNode failed!")
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
            var createdRelationship: org.neo4j.driver.types.Relationship? = null
            try {
                openSession('w').use { _ ->
                    val domain1Parameters = mapOf("domainName1" to domainNode1.domainName, "domainLabels1" to domainNode1.domainLabels.joinToString(":"), "domainProperties1" to domainNode1.domainProperties)
                    val domain2Parameters = mapOf("domainName2" to domainNode2.domainName, "domainLabels2" to domainNode2.domainLabels.joinToString(":"), "domainProperties2" to domainNode2.domainProperties)
                    val parameters = domain1Parameters+domain2Parameters
                    val cypher = "MERGE (d1:Domain:\$domainLabels1 {name: \$domainName1, properties: \$domainProperties1})-[:RELATES_TO]-(d2:Domain:\$domainLabels2 {name: \$domainName2, properties: \$domainProperties2}) RETURN d1, d2"
                    val result = session!!.run(cypher, parameters)
                    if (result.hasNext()) {
                        createdRelationship = result.next().get("d1").asRelationship()
                    }
                }
            } catch (e: Exception) {
                throw Exception("createDomainRelationship failed!")
            } finally {
                closeSession()
            }
            return createdRelationship
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

        fun createSubdomainRelationship(domainNode1: DomainGroupNode, subdomainNode1: SubdomainGroupNode, domainNode2: DomainGroupNode, subdomainNode2: SubdomainGroupNode, relationshipLabels: Array<String>?, relationshipProperties: HashMap<String, String>?): org.neo4j.driver.types.Relationship? {
            if (!checkForSubdomain(domainNode1, subdomainNode1, true) || !checkForSubdomain(domainNode2, subdomainNode2, false)) {
                return null
            }
            // check and create new subdomain relationship
            var relationshipExistence: org.neo4j.driver.types.Relationship? = null
            try {
                openSession('w').use { _ ->
                    val fromDomainParameters = mapOf("fromDomainName" to domainNode1.domainName, "fromDomainLabels" to domainNode1.domainLabels.joinToString(":"), "fromDomainProperties" to domainNode1.domainProperties)
                    val fromSubdomainParameters = mapOf("fromSubdomainName" to subdomainNode1.subdomainName, "fromSubdomainLabels" to subdomainNode1.subdomainLabels.joinToString(":"), "fromSubdomainProperties" to subdomainNode1.subdomainProperties)
                    val toDomainParameters = mapOf("toDomainName" to domainNode2.domainName, "toDomainLabels" to domainNode2.domainLabels.joinToString(":"), "toDomainProperties" to domainNode2.domainProperties)
                    val toSubdomainParameters = mapOf("toSubdomainName" to subdomainNode2.subdomainName, "toSubdomainLabels" to subdomainNode2.subdomainLabels.joinToString(":"), "toSubdomainProperties" to subdomainNode2.subdomainProperties)
                    val parameters = fromDomainParameters+fromSubdomainParameters+toDomainParameters+toSubdomainParameters
                    val cypher = "MERGE (d1:Domain:\$fromDomainLabels {name: \$fromDomainName, properties: \$fromDomainProperties})-[:DOMAIN_OF]->(s1:Subdomain:\$fromSubdomainLabels {name: \$fromSubdomainName, properties: \$fromSubdomainProperties})--(s2:Subdomain:\$toSubdomainLabels {name: \$toSubdomainName, properties: \$toSubdomainProperties})<-[:DOMAIN_OF]-(d2:Domain:\$toDomainLabels {name: \$toDomainName, properties: \$toDomainProperties}) return s1, s2"
                    val result = session!!.run(cypher, parameters)
                    if (result.hasNext()) {
                        relationshipExistence = result.next().get("d1").asRelationship()
                    }
                }
            } catch (e: Exception) {
                throw Exception("createSubdomainRelationship failed!")
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
            var createdKBNode: org.neo4j.driver.types.Node? = null
            var createdChildNodes: Int = 0

            val childNodeCount: Int = abstractKBNode.abstractKnowledgeNodes.size
            try {
                openSession('w').use { _ ->
                    val domainParameters = mapOf("domainName" to domainNode.domainName, "domainLabels" to domainNode.domainLabels.joinToString(":"), "domainProperties" to domainNode.domainProperties)
                    val subdomainParameters = mapOf("subdomainName" to subdomainNode.subdomainName, "subdomainLabels" to subdomainNode.subdomainLabels.joinToString(":"), "subdomainProperties" to subdomainNode.subdomainProperties)
                    val abstractKBParameters = mapOf("abstractKBName" to abstractKBNode.abstractKBName, "abstractKBLabels" to abstractKBNode.abstractKBLabels.joinToString(":"), "abstractKBProperties" to abstractKBNode.abstractKBProperties)
                    val parameters = domainParameters+subdomainParameters+abstractKBParameters
                    var cypher = "MERGE (d:Domain:\$domainLabels {name: \$domainName, properties: \$domainProperties})-[:DOMAIN_OF]->(s:Subdomain:\$subdomainLabels {name: \$subdomainName, properties: \$subdomainProperties})-[:SUBDOMAIN_OF]->(a:AbstractKB:\$abstractKBLabels {name: \$abstractKBName, properties: \$abstractKBProperties}) ON CREATE RETURN a ON MATCH RETURN null"
                    var result = session!!.run(cypher, parameters)
                    if (result.hasNext()) {
                        createdKBNode = result.next().get("a").asNode()
                        for (node in abstractKBNode.abstractKnowledgeNodes) {
                            val childParameters = mapOf("sentence" to node.concept, "sentenceLabels" to node.conceptLabels.joinToString(":"), "sentenceProperties" to node.conceptProperties)
                            cypher = "MERGE (a:AbstractKB:\$abstractKBLabels {name: \$abstractKBName, properties: \$abstractKBProperties)-[:KB_OF]->(c:Concept:\$sentenceLabels {name: \$sentence, properties: \$sentenceProperties}) ON CREATE RETURN c ON MATCH RETURN null"
                            result = session!!.run(cypher, childParameters)
                            if (result.hasNext()) {
                                createdChildNodes++
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                throw Exception("createAbstractKB failed!")
            } finally {
                closeSession()
            }
            return createdKBNode; (createdChildNodes/childNodeCount)
        }

        fun createLogicalKB(domainNode: DomainGroupNode, subdomainNode: SubdomainGroupNode, logicKBNode: LogicKBNode): org.neo4j.driver.types.Node? {
            if (!checkForSubdomain(domainNode, subdomainNode)) {
                return null
            }
            // check and create knowledge base nodes
            var createdKBNode: org.neo4j.driver.types.Node? = null
            var createdChildNodes: Int = 0

            val childNodeCount: Int = logicKBNode.logicNodes.size
            try {
                openSession('w').use { _ ->
                    val domainParameters = mapOf("domainName" to domainNode.domainName, "domainLabels" to domainNode.domainLabels.joinToString(":"), "domainProperties" to domainNode.domainProperties)
                    val subdomainParameters = mapOf("subdomainName" to subdomainNode.subdomainName, "subdomainLabels" to subdomainNode.subdomainLabels.joinToString(":"), "subdomainProperties" to subdomainNode.subdomainProperties)
                    val logicKBParameters = mapOf("logicKBName" to logicKBNode.logicKBName, "logicKBLabels" to logicKBNode.logicKBLabels.joinToString(":"), "logicKBProperties" to logicKBNode.logicKBProperties)
                    val parameters = domainParameters+subdomainParameters+logicKBParameters
                    var cypher = "MERGE (d:Domain:\$domainLabels {name: \$domainName, properties: \$domainProperties})-[:DOMAIN_OF]->(s:Subdomain:\$subdomainLabels {name: \$subdomainName, properties: \$subdomainProperties})-[:SUBDOMAIN_OF]->(a:logicKB:\$logicKBLabels {name: \$logicKBName, properties: \$logicKBProperties}) ON CREATE RETURN a ON MATCH RETURN null"
                    var result = session!!.run(cypher, parameters)
                    if (result.hasNext()) {
                        createdKBNode = result.next().get("a").asNode()
                        for (node in logicKBNode.logicNodes) {
                            val childParameters = mapOf("sentence" to node.logicSentence, "sentenceLabels" to node.logicLabels.joinToString(":"), "sentenceProperties" to node.logicProperties)
                            cypher = "MERGE (a:logicKB:\$logicKBLabels {name: \$logicKBName, properties: \$logicKBProperties)-[:KB_OF]->(c:Concept:\$sentenceLabels {name: \$sentence, properties: \$sentenceProperties}) ON CREATE RETURN c ON MATCH RETURN null"
                            result = session!!.run(cypher, childParameters)
                            if (result.hasNext()) {
                                createdChildNodes++
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                throw Exception("createLogicKB failed!")
            } finally {
                closeSession()
            }
            return createdKBNode; (createdChildNodes/childNodeCount)
        }

        fun createAbstractNode(domainNode: DomainGroupNode, subdomainNode: SubdomainGroupNode, abstractKBNode: AbstractKBNode, abstractNode: AbstractKnowledgeNode): org.neo4j.driver.types.Node? {
            if (!checkForSubdomain(domainNode, subdomainNode)) {
                return null
            }
            // check and create knowledge base node
            var createdNode: org.neo4j.driver.types.Node? = null
            try {
                openSession('w').use { _ ->
                    val domainParameters = mapOf("domainName" to domainNode.domainName, "domainLabels" to domainNode.domainLabels.joinToString(":"), "domainProperties" to domainNode.domainProperties)
                    val subdomainParameters = mapOf("subdomainName" to subdomainNode.subdomainName, "subdomainLabels" to subdomainNode.subdomainLabels.joinToString(":"), "subdomainProperties" to subdomainNode.subdomainProperties)
                    val abstractKBParameters = mapOf("abstractKBName" to abstractKBNode.abstractKBName, "abstractKBLabels" to abstractKBNode.abstractKBLabels.joinToString(":"), "abstractKBProperties" to abstractKBNode.abstractKBProperties)
                    val abstractNodeParameters = mapOf("abstractNodeName" to abstractNode.concept, "abstractNodeLabels" to abstractNode.conceptLabels.joinToString(":"), "abstractNodeProperties" to abstractNode.conceptProperties)
                    val parameters = domainParameters+subdomainParameters+abstractKBParameters+abstractNodeParameters
                    val cypher = "MERGE (d:Domain:\$domainLabels {name: \$domainName, properties: \$domainProperties})-[:DOMAIN_OF]->(s:Subdomain:\$subdomainLabels {name: \$subdomainName, properties: \$subdomainProperties})-[:SUBDOMAIN_OF]->(a:AbstractKB:\$abstractKBLabels {name: \$abstractKBName, properties: \$abstractKBProperties})-[:KB_OF]->(c:Concept:\$abstractNodeLabels {name: \$abstractNodeName, properties: \$abstractNodeProperties}) ON CREATE RETURN c ON MATCH RETURN null"
                    val result = session!!.run(cypher, parameters)
                    if (result.hasNext()) {
                        createdNode = result.next().get("c").asNode()
                    }
                }
            } catch (e: Exception) {
                throw Exception("createAbstractNode failed!")
            } finally {
                closeSession()
            }
            return createdNode
        }

        fun createLogicNode(domainNode: DomainGroupNode, subdomainNode: SubdomainGroupNode, logicKBNode: LogicKBNode, logicNode: LogicNode): org.neo4j.driver.types.Node? {
            if (!checkForSubdomain(domainNode, subdomainNode)) {
                return null
            }
            // check and create knowledge base node
            var createdNode: org.neo4j.driver.types.Node? = null
            try {
                openSession('w').use { _ ->
                    val domainParameters = mapOf("domainName" to domainNode.domainName, "domainLabels" to domainNode.domainLabels.joinToString(":"), "domainProperties" to domainNode.domainProperties)
                    val subdomainParameters = mapOf("subdomainName" to subdomainNode.subdomainName, "subdomainLabels" to subdomainNode.subdomainLabels.joinToString(":"), "subdomainProperties" to subdomainNode.subdomainProperties)
                    val logicKBParameters = mapOf("logicKBName" to logicKBNode.logicKBName, "logicKBLabels" to logicKBNode.logicKBLabels.joinToString(":"), "logicKBProperties" to logicKBNode.logicKBProperties)
                    val logicNodeParameters = mapOf("logicNodeName" to logicNode.logicSentence, "logicNodeLabels" to logicNode.logicLabels.joinToString(":"), "logicNodeProperties" to logicNode.logicProperties)
                    val parameters = domainParameters+subdomainParameters+logicKBParameters+logicNodeParameters
                    val cypher = "MERGE (d:Domain:\$domainLabels {name: \$domainName, properties: \$domainProperties})-[:DOMAIN_OF]->(s:Subdomain:\$subdomainLabels {name: \$subdomainName, properties: \$subdomainProperties})-[:SUBDOMAIN_OF]->(a:logicKB:\$logicKBLabels {name: \$logicKBName, properties: \$logicKBProperties})-[:KB_OF]->(c:Concept:\$logicNodeLabels {name: \$logicNodeName, properties: \$logicNodeProperties}) ON CREATE RETURN c ON MATCH RETURN null"
                    val result = session!!.run(cypher, parameters)
                    if (result.hasNext()) {
                        createdNode = result.next().get("c").asNode()
                    }
                }
            } catch (e: Exception) {
                throw Exception("createLogicNode failed!")
            } finally {
                closeSession()
            }
            return createdNode
        }

        fun createKBNodeRelationship(fromNode: Any, toNode: Any, relationshipName: String?, relationshipProperties: Map<String, String>?): org.neo4j.driver.types.Relationship? {
            if (fromNode !is LogicNode && fromNode !is AbstractKnowledgeNode) {
                return null
            } else if (toNode !is LogicNode && toNode !is AbstractKnowledgeNode) {
                return null
            }
            // check if relationship already exists
            var createdRelationship: org.neo4j.driver.types.Relationship? = null
            try {
                openSession('w').use { _ ->
                    val relationshipParameters = mapOf("relationshipName" to relationshipName, "relationshipProperties" to relationshipProperties)
                    val fromNodeParameters: Map<String, Any> = if (fromNode is LogicNode) {
                        mapOf("fromLogicNodeName" to fromNode.logicSentence, "fromLogicNodeLabels" to fromNode.logicLabels.joinToString(":"), "fromLogicNodeProperties" to fromNode.logicProperties) as Map<String, LogicNode>
                    } else {
                        val fromAbstractNode = fromNode as AbstractKnowledgeNode
                        mapOf("fromAbstractNodeName" to fromAbstractNode.concept, "fromAbstractNodeLabels" to fromAbstractNode.conceptLabels.joinToString(":"), "fromAbstractNodeProperties" to fromAbstractNode.conceptProperties) as Map<String, AbstractKnowledgeNode>
                    }
                    val toNodeParameters: Map<String, Any> = if (toNode is LogicNode) {
                        mapOf("toLogicNodeName" to toNode.logicSentence, "toLogicNodeLabels" to toNode.logicLabels.joinToString(":"), "toLogicNodeProperties" to toNode.logicProperties) as Map<String, LogicNode>
                    } else {
                        val toAbstractNode = toNode as AbstractKnowledgeNode
                        mapOf("toAbstractNodeName" to toAbstractNode.concept, "toAbstractNodeLabels" to toAbstractNode.conceptLabels.joinToString(":"), "toAbstractNodeProperties" to toAbstractNode.conceptProperties) as Map<String, AbstractKnowledgeNode>
                    }
                    val parameters = relationshipParameters+fromNodeParameters+toNodeParameters
                    val cypher: String = when {
                        fromNodeParameters["fromLogicNodeName"] != null && toNodeParameters["toLogicNodeName"] != null -> {
                            "MERGE (from:LogicNode:\${parameters[\"fromLogicNodeLabels\"]} {name: \${parameters[\"fromLogicNodeSentence\"]}, properties: \${parameters[\"fromLogicNodeProperties\"]}})-[r:\${parameters[\"relationshipName\"]} {properties: \$relationshipProperties}]->(to:LogicNode:\${parameters[\"toLogicNodeLabels\"]} {name: \${parameters[\"toLogicNodeName\"]}, properties: \${parameters[\"toLogicNodeProperties\"]}}) ON CREATE RETURN r ON MATCH RETURN"
                        }
                        fromNodeParameters["fromLogicNodeName"] != null && toNodeParameters["toAbstractNodeName"] != null -> {
                            "MERGE (from:LogicNode:\${parameters[\"fromLogicNodeLabels\"]} {name: \${parameters[\"fromLogicNodeName\"]}, properties: \${parameters[\"fromLogicNodeProperties\"]}})- [r:\${parameters[\"relationshipName\"]} {properties: \$relationshipProperties}]->(to:AbstractNode:\${toNodeParameters[\"toAbstractNodeLabels\"]} {name: \${toNodeParameters[\"toAbstractNodeName\"]}, properties: \${toNodeParameters[\"toAbstractNodeProperties\"]}}) ON CREATE RETURN r ON MATCH RETURN null"
                        }
                        fromNodeParameters["fromAbstractNodeName"] != null && toNodeParameters["toLogicNodeName"] != null -> {
                            "MERGE (from:AbstractNode:\${fromNodeParameters[\"abstractNodeLabels\"]} {name: \${fromNodeParameters[\"abstractNodeName\"]}, properties: \${fromNodeParameters[\"fromAbstractNodeProperties\"]}})-[r:\${parameters[\"relationshipName\"]} {properties: \$relationshipProperties}]->(to:LogicNode:\${toNodeParameters[\"toLogicNodeLabels\"]} {name: \${toNodeParameters[\"toLogicNodeName\"]}, properties: \${toNodeParameters[\"toLogicNodeProperties\"]}}) ON CREATE RETURN r ON MATCH RETURN null"
                        }
                        fromNodeParameters["fromAbstractNodeName"] != null && toNodeParameters["toAbstractNodeName"] != null -> {
                            "MERGE (from:AbstractNode:\${fromNodeParameters[\"fromAbstractNodeLabels\"]} {name: \${fromNodeParameters[\"fromAbstractNodeName\"]}, properties: \${fromNodeParameters[\"fromAbstractNodeProperties\"]}})-[r:\${parameters[\"relationshipName\"]} {properties: \$relationshipProperties}]->(to:AbstractNode:\${toNodeParameters[\"toAbstractNodeLabels\"]} {name: \${toNodeParameters[\"toAbstractNodeName\"]}, properties: \${toNodeParameters[\"toAbstractNodeProperties\"]}}) ON CREATE RETURN r ON MATCH RETURN null"
                        }
                        else -> {
                            throw Exception("createDomainRelationship cyphertext failed!")
                        }
                    }
                    val result = session!!.run(cypher, parameters)
                    if (result.hasNext()) {
                        createdRelationship = result.next().get("r").asRelationship()
                    }
                }
            } catch (e: Exception) {
                throw Exception("createDomainRelationship failed!")
            } finally {
                closeSession()
            }
            return createdRelationship
        }

        fun mergeInferenceChain(logicGroup: LogicKBNode, graph: DirectedDeductionGraph?, inferenceChain: ArrayList<DeductionGraphNode>) {

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

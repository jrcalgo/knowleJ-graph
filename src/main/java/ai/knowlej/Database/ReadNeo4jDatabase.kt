package ai.knowlej.Database

import org.neo4j.driver.*
import kotlin.run

open class ReadNeo4jDatabase {
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

    open inner class ReadDomain {
        
    }

    open inner class ReadSubdomain {

    }

    open inner class ReadLogicBase {

    }

    open inner class ReadAbstractBase {

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
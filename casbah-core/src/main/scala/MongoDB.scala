/**
 * Copyright (c) 2010 10gen, Inc. <http://10gen.com>
 * Copyright (c) 2009, 2010 Novus Partners, Inc. <http://novus.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For questions and comments about this product, please see the project page at:
 *
 * http://github.com/mongodb/casbah
 *
 */

package com.mongodb.casbah

import scala.collection.JavaConverters._

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.map_reduce.{MapReduceCommand, MapReduceResult}
import com.mongodb.{DBCollection, CommandResult}
import scala.collection.mutable
import com.mongodb


/**
 * Wrapper for the Mongo <code>DB</code> object providing scala-friendly functionality.
 *
 * @since 2.0
 * @see com.mongodb.DB
 */
object MongoDB {
  def apply(connection: com.mongodb.MongoClient, dbName: String): MongoDB =
    connection.asScala.apply(dbName)

  def apply(connection: MongoClient, dbName: String): MongoDB =
    connection(dbName)
}

// scalastyle:off number.of.methods
/**
 * Wrapper for the Mongo <code>DB</code> object providing scala-friendly functionality.
 *
 * @since 1.0
 * @see com.mongodb.DB
 */
class MongoDB(val underlying: com.mongodb.DB) {
  /**
   * Apply method to proxy  getCollection, to allow invocation of
   * <code>dbInstance("collectionName")</code>
   * instead of getCollection
   *
   * @param collection a  string for the collection name
   * @return MongoCollection A wrapped instance of a Mongo DBCollection Class returning generic DBObjects
   */
  def apply(collection: String): MongoCollection = underlying.getCollection(collection).asScala

  // TODO - write tests and make so!
  // /**
  //  * Creates a Mongo instance based on a (single) mongodb node (default port).
  //  *
  //  * @param collection a string for the collection name
  //  * @param  c (Class[A])
  //  * @return MongoTypedCollection[A]
  //  */
  // def apply(collection: String, objectClass: Class[DBObject]) = underlying.getCollection(collection).asScala.setObjectClass(objectClass)

  def addUser(username: String, passwd: String): WriteResult = underlying.addUser(username, passwd.toArray)

  /**
   * Authenticates connection/db with given name and password
   *
   * @param username  name of user for this database
   * @param passwd password of user for this database
   * @return true if authenticated, false otherwise
   */
  @deprecated("Please use MongoClient to create a client, which will authenticate all connections to server.", "2.7")
  def authenticate(username: String, passwd: String): Boolean = underlying.authenticate(username, passwd.toArray)

  /** Execute a database command directly.
    * @see <a href="http://mongodb.onconfluence.com/display/DOCS/List+of+Database+Commands">List of Commands</a>
    * @return the result of the command from the database
    */
  def command(cmd: DBObject): CommandResult = underlying.command(cmd)

  /** Execute a database command directly.
    * @see <a href="http://mongodb.onconfluence.com/display/DOCS/List+of+Database+Commands">List of Commands</a>
    * @return the result of the command from the database
    */
  def command(cmd: String): CommandResult = underlying.command(cmd)

  /** Execute a database command directly.
    * @see <a href="http://mongodb.onconfluence.com/display/DOCS/List+of+Database+Commands">List of Commands</a>
    * @return the result of the command from the database
    */
  def command(cmd: DBObject, options: Int): CommandResult = underlying.command(cmd, options)

  /** Execute a database command directly.
    * @see <a href="http://mongodb.onconfluence.com/display/DOCS/List+of+Database+Commands">List of Commands</a>
    * @return the result of the command from the database
    */
  def command(cmd: DBObject, options: Int, pref: ReadPreference): CommandResult = underlying.command(cmd, options, pref)

  /** Creates a collection with a given name and options.
    * If the collection does not exist, a new collection is created.
    * Possible options:
    * <dl>
    * <dt>capped</dt><dd><i>boolean</i>: if the collection is capped</dd>
    * <dt>size</dt><dd><i>int</i>: collection size</dd>
    * <dt>max</dt><dd><i>int</i>: max number of documents</dd>
    * </dl>
    * @param name the name of the collection to return
    * @param o options
    * @return the collection
    */
  def createCollection(name: String, o: DBObject): DBCollection = underlying.createCollection(name, o)

  def doEval(code: String, args: AnyRef*): CommandResult = underlying.doEval(code, args: _*)

  /**
   * Drops this database.  Removes all data on disk.  Use with caution.
   */
  def dropDatabase(): Unit = underlying.dropDatabase()

  def eval(code: String, args: AnyRef*): AnyRef = underlying.eval(code, args: _*)

  /**
   * For testing purposes only - this method forces an error to help test error handling
   */
  def forceError(): Unit = underlying.forceError()

  /** Gets a collection with a given name.
    * If the collection does not exist, a new collection is created.
    * @param name (String) the name of the collection to return
    * @return the collection
    */
  def getCollection(name: String): DBCollection = underlying.getCollection(name)

  /** Returns a collection matching a given string.
    * @param s the name of the collection
    * @return the collection
    */
  def getCollectionFromString(s: String): DBCollection = underlying.getCollectionFromString(s)

  /** Returns a set of the names of collections in this database.
    * @return the names of collections in this database
    */
  def getCollectionNames(): mutable.Set[String] = underlying.getCollectionNames.asScala /* calls the db */

  /** Returns a set of the names of collections in this database.
    * @return the names of collections in this database
    */
  def collectionNames(): mutable.Set[String] = getCollectionNames() /* calls the db */

  /**
   * Gets the the error (if there is one) from the previous operation.  The result of
   * this command will look like
   *
   * <pre>
   * { "err" :  errorMessage  , "ok" : 1.0 }
   * </pre>
   *
   * The value for errorMessage will be null if no error occurred, or a description otherwise.
   *
   * Care must be taken to ensure that calls to getLastError go to the same connection as that
   * of the previous operation. See com.mongodb.Mongo.requestStart for more information.
   *
   * @return DBObject with error and status information
   */
  def getLastError(): CommandResult = underlying.getLastError() /* calls the db */

  def lastError(): CommandResult = getLastError() /* calls the db */

  def getLastError(writeConcern: WriteConcern): CommandResult =
    underlying.getLastError(writeConcern)

  def lastError(writeConcern: WriteConcern): CommandResult =
    getLastError(writeConcern)

  def getLastError(w: Int, wTimeout: Int, fsync: Boolean): CommandResult =
    underlying.getLastError(w, wTimeout, fsync)

  def lastError(w: Int, wTimeout: Int, fsync: Boolean): CommandResult =
    getLastError(w, wTimeout, fsync)

  def name: String = getName

  def getName: String = underlying.getName

  /**
   * Returns the last error that occurred since start of database or a call to <code>resetError()</code>
   *
   * The return object will look like
   *
   * <pre>
   * { err : errorMessage, nPrev : countOpsBack, ok : 1 }
   * </pre>
   *
   * The value for errormMessage will be null of no error has ocurred, or the message.  The value of
   * countOpsBack will be the number of operations since the error occurred.
   *
   * Care must be taken to ensure that calls to getPreviousError go to the same connection as that
   * of the previous operation. See com.mongodb.Mongo.requestStart for more information.
   *
   * @deprecated The `getPreviousError()` and `resetError()` commands are deprecated and may be removed in future versions of MongoDB
   * @return DBObject with error and status information
   */
  @deprecated("may be removed in future versions of MongoDB", "2.7")
  def getPreviousError(): CommandResult = underlying.getPreviousError() /* calls the db */

  /**
   * Resets the error memory for this database.  Used to clear all errors such that getPreviousError()
   * will return no error.
   *
   * @deprecated The `getPreviousError()` and `resetError()` commands are deprecated and may be removed in future versions of MongoDB
   */
  @deprecated("may be removed in future versions of MongoDB", "2.7")
  def resetError(): Unit = underlying.resetError()

  def getSisterDB(name: String): MongoDB = underlying.getSisterDB(name).asScala

  /**
   * Returns true if this DB is authenticated
   *
   * @return true if authenticated, false otherwise
   */
  @deprecated("Use MongoClient to create an authenticated connection.", "2.7")
  def isAuthenticated: Boolean = underlying.isAuthenticated

  def stats(): CommandResult = getStats()

  def getStats(): CommandResult = underlying.getStats() /* calls the db */

  def requestDone(): Unit = underlying.requestDone()

  def requestEnsureConnection(): Unit = underlying.requestEnsureConnection()

  def requestStart(): Unit = underlying.requestStart()

  /** Makes this database read-only
    *
    * @param b if the database should be read-only
    */
  @deprecated("Avoid making database read-only via this method. Use a read-only user with MongoClient instead.", "2.7")
  def setReadOnly(b: Boolean): Unit = underlying.setReadOnly(b)

  /** Makes this database read-only
    *
    * @param b if the database should be read-only
    */
  @deprecated("Avoid making database read-only via this method. Use a read-only user with MongoClient instead.", "2.7")
  def readOnly_=(b: Boolean): Unit = setReadOnly(b)

  /**
   * Sets queries to be OK to run on slave nodes.
   */
  @deprecated("Replaced with ReadPreference.SECONDARY.", "2.3.0")
  def slaveOk(): Unit = underlying.slaveOk() // use parens because this side-effects

  /**
   *
   * Set the write concern for this database.
   * Will be used for writes to any collection in this database.
   * See the documentation for [[com.mongodb.WriteConcern]] for more info.
   *
   * @param concern (WriteConcern) The write concern to use
   * @see WriteConcern
   * @see http://www.thebuzzmedia.com/mongodb-single-server-data-durability-guide/
   */
  def setWriteConcern(concern: WriteConcern): Unit = underlying.setWriteConcern(concern)

  /**
   *
   * Set the write concern for this database.
   * Will be used for writes to any collection in this database.
   * See the documentation for [[com.mongodb.WriteConcern]] for more info.
   *
   * @param concern (WriteConcern) The write concern to use
   * @see WriteConcern
   * @see http://www.thebuzzmedia.com/mongodb-single-server-data-durability-guide/
   */
  def writeConcern_=(concern: WriteConcern): Unit = setWriteConcern(concern)

  /**
   *
   * get the write concern for this database,
   * which is used for writes to any collection in this database.
   * See the documentation for [[com.mongodb.WriteConcern]] for more info.
   *
   * @see WriteConcern
   */
  def getWriteConcern: WriteConcern = underlying.getWriteConcern

  /**
   *
   * get the write concern for this database,
   * which is used for writes to any collection in this database.
   * See the documentation for [[com.mongodb.WriteConcern]] for more info.
   *
   * @see WriteConcern
   * @see http://www.thebuzzmedia.com/mongodb-single-server-data-durability-guide/
   */
  def writeConcern: WriteConcern = getWriteConcern

  /*
   * Sets the read preference for this database. Will be used as default for
   * reads from any collection in this database. See the
   * documentation for [[com.mongodb.ReadPreference]] for more information.
   *
   * @param preference Read Preference to use
   */
  def readPreference_=(pref: ReadPreference): Unit = setReadPreference(pref)

  /**
   * Sets the read preference for this database. Will be used as default for
   * reads from any collection in this database. See the
   * documentation for [[com.mongodb.ReadPreference]] for more information.
   *
   * @param pref Read Preference to use
   */
  def setReadPreference(pref: ReadPreference): Unit = underlying.setReadPreference(pref)

  /**
   * Gets the read preference for this database. Will be used as default for
   * reads from any collection in this database. See the
   * documentation for [[com.mongodb.ReadPreference]] for more information.
   */
  def readPreference: ReadPreference = getReadPreference

  /**
   * Gets the read preference for this database. Will be used as default for
   * reads from any collection in this database. See the
   * documentation for [[com.mongodb.ReadPreference]] for more information.
   */
  def getReadPreference: ReadPreference = underlying.getReadPreference

  /**
   * Checks to see if a collection by name %lt;name&gt; exists.
   * @param collectionName The collection to test for existence
   * @return false if no collection by that name exists, true if a match to an existing collection was found
   */
  def collectionExists(collectionName: String): Boolean =
    underlying.collectionExists(collectionName)

  /**
   * The Java Driver is a bit outdated and is missing the finalize option.
   * Additionally, it returns ZERO information about the actual results of the mapreduce,
   * just a cursor to the result collection.
   * This is less than ideal.  So I've wrapped it in something more useful.
   *
   * @param cmd An instance of MapReduceCommand representing the required MapReduce
   * @return MapReduceResult a wrapped result object.  This contains the returns success, counts etc, but implements iterator and can be iterated directly
   */
  def mapReduce(cmd: MapReduceCommand): MapReduceResult = MapReduceResult(command(cmd.toDBObject))(this)

  /**
   * write concern aware write op block.
   *
   * Checks getLastError after the last write.
   * If  you run multiple ops you'll only get the final
   * error.
   *
   * Your op function gets a copy of this MongoDB instance.
   *
   * This is for write ops only - you cannot return data from it.
   *
   * Your function must return WriteResult, which is the
   * return type of any mongo write operation like insert/save/update/remove
   *
   * If you have set a connection or DB level WriteConcern,
   * it will be inherited.
   *
   * @throws MongoException if error
   */
  def request(op: MongoDB => WriteResult): Unit = op(this).getLastError(writeConcern).throwOnError()

  /**
   * write concern aware write op block.
   *
   * Checks getLastError after the last write.
   * If  you run multiple ops you'll only get the final
   * error.
   *
   * Your op function gets a copy of this MongoDB instance.
   *
   * This is for write ops only - you cannot return data from it.
   *
   * Your function must return WriteResult, which is the
   * return type of any mongo write operation like insert/save/update/remove
   *
   * @throws MongoException if error
   */
  def request(w: Int, wTimeout: Int = 0, fsync: Boolean = false)(op: MongoDB => WriteResult): Unit =
    op(this).getLastError(WriteConcern(w, wTimeout, fsync)).throwOnError()

  /**
   * write concern aware write op block.
   *
   * Checks getLastError after the last write.
   * If  you run multiple ops you'll only get the final
   * error.
   *
   * Your op function gets a copy of this MongoDB instance.
   *
   * This is for write ops only - you cannot return data from it.
   *
   * Your function must return WriteResult, which is the
   * return type of any mongo write operation like insert/save/upd§ate/remove
   *
   * @throws MongoException if error
   */
  def request(writeConcern: WriteConcern)(op: MongoDB => WriteResult): Unit =
    op(this).getLastError(writeConcern).throwOnError()

  def checkedWrite(op: MongoDB => WriteResult): Unit =
    op(this).getLastError.throwOnError()

  /**
   * Manipulate Network Options
   *
   * @see com.mongodb.Mongo
   * @see com.mongodb.Bytes
   */
  def addOption(option: Int): Unit = underlying.addOption(option)

  /**
   * Manipulate Network Options
   *
   * @see com.mongodb.Mongo
   * @see com.mongodb.Bytes
   */
  def resetOptions(): Unit = underlying.resetOptions() // use parens because this side-effects

  /**
   * Manipulate Network Options
   *
   * @see com.mongodb.Mongo
   * @see com.mognodb.Bytes
   */
  def getOptions: Int = underlying.getOptions

  /**
   * Manipulate Network Options
   *
   * @see com.mongodb.Mongo
   * @see com.mognodb.Bytes
   */
  def options: Int = getOptions

  override def toString(): String = underlying.toString

}

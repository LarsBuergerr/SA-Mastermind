//import controller.ControllerComponent.ControllerMockImpl.Controller

import scala.util.{Failure, Success, Try}
import MongoDB.MongoDAO
import SlickDB.SlickDAO

/**
 * Module for persistence related components and configurations.
 */
object PersistenceModule:
  //you can plug in the Mongo DAO or the SlickDAO to the DAOInterface
  given DAOInterface = new MongoDAO()
  //  given DAOInterface = new SlickDAO()
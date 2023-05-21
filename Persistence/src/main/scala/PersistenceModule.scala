//****************************************************************************** PACKAGE  

//****************************************************************************** IMPORTS

//import controller.ControllerComponent.ControllerMockImpl.Controller

import scala.util.{Failure, Success, Try}
import MongoDB.MongoDAO
import SlickDB.SlickDAO

//****************************************************************************** OBJECT DEFINITION
object PersistenceModule:
  given DAOInterface = new MongoDAO()
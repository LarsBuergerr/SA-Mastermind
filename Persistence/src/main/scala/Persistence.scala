import FileIOComponent.RestPersistenceAPI
import PersistenceModule.given

/**
 * Main entry point for the persistence module.
 */
object PersistenceMain {
    @main def run() =
        val restPersistenceAPI = new RestPersistenceAPI()
        restPersistenceAPI.start()
}
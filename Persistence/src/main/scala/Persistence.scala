
import FileIOComponent.RestPersistenceAPI

object PersistenceMain {
    @main def run() = 
        val restPersistenceAPI = new RestPersistenceAPI()
        restPersistenceAPI.start()
}
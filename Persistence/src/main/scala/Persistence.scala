
import FileIOComponent.RestPersistenceAPI
import PersistenceModule.given

object PersistenceMain {
    @main def run() = 
        val restPersistenceAPI = new RestPersistenceAPI()
        restPersistenceAPI.start()
}
package controller

import controller.ControllerComponent.RestControllerAPI
import controller.CoreModule.given

/**
 * This object represents the entry point of the CoreMain application.
 * It initializes the RestControllerAPI and starts the application.
 */
object CoreMain {
    @main def run() = {
        // Creates an instance of RestControllerAPI.
        val restControllerApi = new RestControllerAPI()
        // Starts the application.
        restControllerApi.start() 
    }
}

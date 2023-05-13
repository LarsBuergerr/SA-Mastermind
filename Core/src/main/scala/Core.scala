package controller

import controller.ControllerComponent.RestControllerAPI
import controller.CoreModule.given

object CoreMain {
    @main def run() = 
        val restControllerApi = new RestControllerAPI()
        restControllerApi.start()
}
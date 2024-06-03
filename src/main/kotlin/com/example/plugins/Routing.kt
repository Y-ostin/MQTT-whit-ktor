package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


data class User(val id: Int, var name: String, var email: String)
val users = mutableListOf<User>()

val mqttService = MqttService() // Crear una instancia de MqttService

fun Application.configureRouting() {
    routing {
        // Rutas para los usuarios
        createUserRoute()
        getUsersRoute()
        getUserRoute()
        updateUserRoute()
        deleteUserRoute()
    }
}

// Ruta para crear un usuario
fun Route.createUserRoute() {
    post("/users/create") {
        val user = call.receive<User>()
        users.add(user.copy(id = users.size + 1))
        mqttService.publish("users/create", "User created: ${user.name}") // Publicar en MQTT
        call.respond(mapOf("status" to "User added", "user" to user))
    }
}

// Ruta para obtener todos los usuarios
fun Route.getUsersRoute() {
    get("/users") {
        call.respond(users)
    }
}

// Ruta para obtener un usuario por ID
fun Route.getUserRoute() {
    get("/users/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        val user = users.find { it.id == id }
        if (user == null) {
            call.respond(mapOf("error" to "User not found"))
        } else {
            call.respond(user)
        }
    }
}

// Ruta para actualizar un usuario
fun Route.updateUserRoute() {
    put("/users/update/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        val updatedUser = call.receive<User>()
        val userIndex = users.indexOfFirst { it.id == id }
        if (userIndex == -1) {
            call.respond(mapOf("error" to "User not found"))
        } else {
            users[userIndex].apply {
                name = updatedUser.name
                email = updatedUser.email
            }
            mqttService.publish("users/update", "User updated: ${updatedUser.name}") // Publicar en MQTT
            call.respond(mapOf("status" to "User updated"))
        }
    }
}

// Ruta para eliminar un usuario
fun Route.deleteUserRoute() {
    delete("/users/delete/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        val userIndex = users.indexOfFirst { it.id == id }
        if (userIndex == -1) {
            call.respond(mapOf("error" to "User not found"))
        } else {
            val user = users.removeAt(userIndex)
            mqttService.publish("users/delete", "User deleted: ${user.name}") // Publicar en MQTT
            call.respond(mapOf("status" to "User deleted"))
        }
    }
}

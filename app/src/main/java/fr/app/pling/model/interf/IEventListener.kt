package fr.app.pling.model.interf

interface IEventListener {
    fun onSuccess()
    fun onFailure(message: String)
}
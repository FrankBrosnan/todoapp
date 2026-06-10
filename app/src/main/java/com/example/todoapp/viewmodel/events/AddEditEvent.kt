package com.example.todoapp.viewmodel.events

sealed class AddEditEvent {
    data class TitleChanged(val value: String) : AddEditEvent()
    data class ContentChanged(val value: String) : AddEditEvent()
    object SaveClicked : AddEditEvent()
    object DeleteClicked : AddEditEvent()
}
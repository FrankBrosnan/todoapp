Architecture Main Points
github URL : https://github.com/FrankBrosnan/todoapp (public)
current working branch: main
current_development branch: v4_branch

modules. single module app

structure
app
 |----> data        (room database setup)
 |----> gui         (composables)
 |----> model       (domain entities)
 |----> viewmodel   (viewmodel and factory)
MainActivity.kt     (main entry point for app)

design

data:-
Room based database.
NoteRepository singleton. Dao object sent as parameter. Can pass in real or 
in memory test db
NoteDao, NoteDatabase
Dao provides a flow of all notes in the db in fun getAllNotes(): Flow<List<Note>>

composables:-
NotesListScreen: Shows notes and a floating action bar to add notes, swipe to delete feature
delete undo snackbar.gets data from view model. NavController and ViewModel passed as parameters.
click to edit existing note.

NotesAddEditScreen: Allow to add or edit note. passed navController: NavController, noteId: Long,
viewModel: NotesViewModel. Delete Icon shown to remove a note with snackbar shown in NotesListScreen

model:-
main domain entity stored in SQLLite db, data class. has unique id.

viewmodel:-
NotesViewModel inherits from ViewModel. Get all notes from the repository. Uses channel to 
communicate event that delete is requested to support undo delete feature. Operations are 
suspend functions so we know the operation completes before doing navigation

NotesViewModelFactory:-
returns different viewmodels for different repository instances. supports in memory db for test
and real persistent db for production.

MainActivity:-
defines and creates composables and defines navigation classes with routes.

Summary:
Single module app
Uses Room
Manual DI
Events use one shot channels to communicate between screens
2 Screens with navigation routes
ViewModel functions are suspend functions
NotesViewModelFactory creates ViewModel using appropriate 
Undo delete creates new note with recently deleted note
Repository is a singleton to provide access to it.
NavGraph scoped models
In memory db instrumentation tests.
Moved from kapt to kts with Room.
UI Reacting on Class NotesUiState which encapsulated the state of the Model.


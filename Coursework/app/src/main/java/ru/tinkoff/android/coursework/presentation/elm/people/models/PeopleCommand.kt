package ru.tinkoff.android.coursework.presentation.elm.people.models

internal sealed class PeopleCommand {

    object LoadPeopleListFromApi : PeopleCommand()

    object LoadPeopleListFromDb : PeopleCommand()

}

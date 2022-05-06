package ru.tinkoff.android.coursework.data.mappers

import ru.tinkoff.android.coursework.data.api.model.UserDto
import ru.tinkoff.android.coursework.data.db.model.UserDb
import ru.tinkoff.android.coursework.domain.model.User

internal object UserMapper {

    fun usersToUsersDbList(users: List<User>): List<UserDb> =
        users.map { user -> userToUserDb(user) }

    fun userDbToUser(userDb: UserDb): User {
        return User(
            userId = userDb.userId,
            fullName = userDb.fullName,
            email = userDb.email,
            avatarUrl = userDb.avatarUrl,
            presence = userDb.presence
        )
    }

    fun usersDbToUsersList(users: List<UserDb>): List<User> =
        users.map { user -> userDbToUser(user)}

    fun userDtoToUser(userDto: UserDto): User {
        return User(
            userId = userDto.userId,
            fullName = userDto.fullName,
            email = userDto.email,
            avatarUrl = userDto.avatarUrl,
            presence = userDto.presence
        )
    }

    private fun userToUserDb(user: User): UserDb {
        return UserDb(
            userId = user.userId,
            fullName = user.fullName,
            email = user.email,
            avatarUrl = user.avatarUrl,
            presence = user.presence
        )
    }

}

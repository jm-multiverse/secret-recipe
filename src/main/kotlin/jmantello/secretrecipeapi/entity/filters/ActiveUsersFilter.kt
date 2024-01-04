package jmantello.secretrecipeapi.entity.filters

class ActiveUsersFilter {
    companion object {
        const val NAME = "activeUsersFilter"
        const val PARAMETER_NAME = "userStatus"
        const val COLUMN = "status"
        const val CONDITION = "$COLUMN = :$PARAMETER_NAME"
    }
}
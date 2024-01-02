package jmantello.secretrecipeapi.entity.filters

class ActiveUsersFilter {
    companion object {
        const val NAME = "activeUsersFilter"
        const val PARAM = "userStatus"
        const val COLUMN = "status"
        const val CONDITION = "$COLUMN = :$PARAM"
    }
}
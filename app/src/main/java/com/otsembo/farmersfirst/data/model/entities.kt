package com.otsembo.farmersfirst.data.model

/**
 * Data class representing a user entity.
 * @property id The unique identifier of the user (default value: 0).
 * @property email The email address of the user.
 */
data class User(
    var id: Int = 0,
    val email: String
)

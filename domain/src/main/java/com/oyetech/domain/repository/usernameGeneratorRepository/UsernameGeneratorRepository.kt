package com.oyetech.domain.repository.usernameGeneratorRepository

interface UsernameGeneratorRepository {

    /**
     * Generates a single random username.
     * @return A random username as a String.
     */
    fun generateUsername(): String

    /**
     * Generates a list of random usernames.
     * @param count The number of usernames to generate.
     * @return A list of random usernames.
     */
    fun generateUsernames(count: Int): List<String>
}

class UsernameGeneratorImp : UsernameGeneratorRepository {

    private val adjectives =
        listOf("Brave", "Clever", "Quick", "Lucky", "Cool", "Happy", "Bold", "Bright")
    private val nouns =
        listOf("Tiger", "Wizard", "Phoenix", "Knight", "Fox", "Lion", "Falcon", "Shark")

    override fun generateUsername(): String {
        return "${getRandomAdjective()}${getRandomNoun()}${getRandomNumber()}"
    }

    override fun generateUsernames(count: Int): List<String> {
        return List(count) { generateUsername() }
    }

    private fun getRandomAdjective(): String {
        return adjectives.random()
    }

    private fun getRandomNoun(): String {
        return nouns.random()
    }

    private fun getRandomNumber(): Int {
        return (100..999).random()
    }
}
package com.example.codeforcesanalyser

data class responseDataClass(
    val status: String,
    val result: List<CodeforcesResult>
)

data class CodeforcesResult(
    val contestId: Int,
    val contestName: String,
    val handle: String,
    val rank: Int,
    val ratingUpdateTimeSeconds: Int,
    val oldRating: Int,
    val newRating: Int
)
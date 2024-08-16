package com.example.codeforcesanalyzer

data class UserInfoResponse(
    val status: String,
    val result: List<UserInfo>
)

data class UserInfo(
    val handle: String,
    val email: String?,
    val rank: String,
    val rating: Int,
    val maxRank: String,
    val maxRating: Int,
    val lastOnlineTimeSeconds: Long,
    val registrationTimeSeconds: Long,
    val friendOfCount: Int,
    val avatar: String,
    val titlePhoto: String
)

data class responseDataClass(
    val status: String,
    val result: List<ContestResult>
)

data class ContestResult(
    val contestId: Int,
    val contestName: String,
    val handle: String,
    val rank: Int,
    val ratingUpdateTimeSeconds: Long,
    val oldRating: Int,
    val newRating: Int
)

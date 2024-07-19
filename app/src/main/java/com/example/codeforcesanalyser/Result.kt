data class responseDataClass(
    val status: String,
    val result: List<Result>
)

data class Result(
    val contestId: Int,
    val contestName: String,
    val handle: String,
    val rank: Int,
    val ratingUpdateTimeSeconds: Int,
    val oldRating: Int,
    val newRating: Int
)
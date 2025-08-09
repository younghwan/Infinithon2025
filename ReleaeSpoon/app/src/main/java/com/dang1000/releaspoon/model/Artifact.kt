import kotlinx.serialization.Serializable

@Serializable
data class Artifacts(
    val artifacts: List<Artifact>
)

@Serializable
data class Artifact(
    val name: String,
    val versions: List<ArtifactFeed>
)

@Serializable
data class ArtifactFeed(
    val version: String,
    val text: String,
    val impact: String,
    val links: List<String> = emptyList()
) {
    var isBookmarked = false
}

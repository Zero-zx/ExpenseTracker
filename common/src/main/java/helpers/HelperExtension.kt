package helpers

fun String.standardize(): String {
    return this[0].uppercase() + this.substring(1)
}
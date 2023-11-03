package fgietzen.simpleruntracker.utils

fun displayTime(seconds: Long): String {
    if (seconds > 60) {
        val minutes = seconds / 60;
        val secondsLeft = seconds % 60;
        return "$minutes m $secondsLeft s"
    }
    return "$seconds s";
}

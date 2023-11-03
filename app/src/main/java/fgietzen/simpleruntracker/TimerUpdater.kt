package fgietzen.simpleruntracker

import java.util.TimerTask

class TimerUpdater (
    private val update: () -> Unit
) : TimerTask() {

    override fun run() {
        update();
    }
}
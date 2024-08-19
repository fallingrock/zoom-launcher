package net.fallingrock.zoomlaunch

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.swing.JOptionPane

class ZoomLauncherK {
    private fun execute() {
        log.atInfo().log("Starting Zoom Launcher")

        val matcher = validZoomUrl ?: run {
            log.atWarn().log("No valid Zoom URL found. Exiting.")
            return
        }

        // The substituted value will be contained in the result variable
        val zoomUri = matcher.replaceAll(ZOOM_URI_FORMAT)

        log.atInfo().log("Launching URL: $zoomUri")

        launchZoomMeeting(zoomUri)
    }

    private val validZoomUrl: Matcher?
        get() {
            while (true) {
                val url = JOptionPane.showInputDialog(
                    null,
                    "URL",
                    "Zoom Launcher",
                    JOptionPane.QUESTION_MESSAGE
                )

                if (url == null) {
                    return null
                }

                val matcher = WEBLINK_PATTERN.matcher(url)

                if (matcher.matches()) {
                    return matcher
                } else {
                    log.atWarn().log("Invalid URL: $url")
                    showErrorDialog("Invalid URL: $url")
                }
            }
        }

    private fun launchZoomMeeting(zoomUri: String) {
        val cmd = listOf("xdg-open", zoomUri) // "xdg-open $zoomUri"

        try {
            log.atInfo().log("Executing command: $cmd")
            val pb = ProcessBuilder(cmd)
            pb.start()
        } catch (e: IOException) {
            log.atError().log("Error executing command $cmd", e)
            showErrorDialog("Error launching Zoom: ${e.message}")
        }
    }

    private fun showErrorDialog(message: String) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Error launching Zoom",
            JOptionPane.ERROR_MESSAGE
        )
    }

    companion object {
        private const val WEBLINK_REGEX = "^https://(.+\\.zoom\\.us)/j/(\\d+)\\?pwd=(.+)"
        private val WEBLINK_PATTERN: Pattern = Pattern.compile(WEBLINK_REGEX)

        private const val ZOOM_URI_FORMAT = "zoommtg://$1/join?action=join&confno=$2&pwd=$3"
        private val log: Logger = LoggerFactory.getLogger(ZoomLauncherK::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            val zl = ZoomLauncherK()
            zl.execute()
        }
    }
}

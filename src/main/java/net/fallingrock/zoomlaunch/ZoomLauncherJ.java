package net.fallingrock.zoomlaunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZoomLauncherJ {

    private static final String WEBLINK_REGEX = "^https://(.+\\.zoom\\.us)/j/(\\d+)\\?pwd=(.+)";
    private static final Pattern WEBLINK_PATTERN = Pattern.compile(WEBLINK_REGEX);

    private static final String ZOOM_URI_FORMAT = "zoommtg://$1/join?action=join&confno=$2&pwd=$3";
    private static final Logger log = LoggerFactory.getLogger(ZoomLauncherJ.class);

    private void execute() {
        log.atInfo().log("Starting Zoom Launcher");

        Matcher matcher = getValidZoomUrl();

        if (matcher != null) {

            // The substituted value will be contained in the result variable
            final String zoomUri = matcher.replaceAll(ZOOM_URI_FORMAT);

            log.atInfo().log("Launching URL: {}", zoomUri);

            launchZoomMeeting(zoomUri);
        }
    }

    private Matcher getValidZoomUrl() {
        while (true) {
            final String url = JOptionPane.showInputDialog(null,
                    "URL",
                    "Zoom Launcher",
                    JOptionPane.QUESTION_MESSAGE);

            if (url == null) {
                log.atWarn().log("No url provided");
                return null;
            }

            Matcher matcher = WEBLINK_PATTERN.matcher(url);

            if (matcher.matches()) {
                return matcher;
            } else {
                log.atWarn().log("Invalid URL: {}", url);
                showErrorDialog("Invalid URL: " + url);
            }
        }
    }

    private void launchZoomMeeting(String zoomUri) {
        final String cmd = String.format("xdg-open %s", zoomUri);

        try {
            log.atInfo().log("Executing command: {}", cmd);
            final ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.start();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            showErrorDialog("Error launching Zoom: " + e.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "Error launching Zoom",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        ZoomLauncherJ zl = new ZoomLauncherJ();
        zl.execute();
    }
}

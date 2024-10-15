package net.fallingrock.zoomlaunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZoomLauncherJ {

    private static final String WEBLINK_REGEX = "^https://(.+\\.zoom\\.us)/j/(\\d+)\\?pwd=(.+)";
    private static final Pattern WEBLINK_PATTERN = Pattern.compile(WEBLINK_REGEX);

    private static final String ZOOM_URI_FORMAT = "zoommtg://$1/join?action=join&confno=$2&pwd=$3";
    private static final Logger log = LoggerFactory.getLogger(ZoomLauncherJ.class);

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("ZoomLauncher");

    private void execute() {
        log.atInfo().log("Starting Zoom Launcher");

        if (!isOsValid()) {
            log.atError().log("Os not supported");
            System.exit(1);
        }

        Matcher matcher = getValidZoomUrl();

        if (matcher != null) {

            // The substituted value will be contained in the result variable
            final String zoomUri = matcher.replaceAll(ZOOM_URI_FORMAT);

            log.atInfo().log("Launching URL: {}", zoomUri);

            launchZoomMeeting(zoomUri);
        }
    }

    private boolean isOsValid() {
        String os = System.getProperty("os.name");

        return os.toLowerCase().contains("linux");
    }

    private Matcher getValidZoomUrl() {
        while (true) {
            final String url = JOptionPane.showInputDialog(null,
                    RESOURCE_BUNDLE.getString("url"),
                    RESOURCE_BUNDLE.getString("zoom.launcher"),
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
                showErrorDialog(MessageFormat.format(RESOURCE_BUNDLE.getString("invalid.url"), url));
            }
        }
    }

    private void launchZoomMeeting(String zoomUri) {
        final List<String> cmd = Arrays.asList("xdg-open", zoomUri);

        try {
            log.atInfo().log("Executing command: {}", cmd);
            final ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.start();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            showErrorDialog(e.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                RESOURCE_BUNDLE.getString("error.launching.zoom"),
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        ZoomLauncherJ zl = new ZoomLauncherJ();
        zl.execute();
    }
}

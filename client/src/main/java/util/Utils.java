package util;

public class Utils {
    public static String formatErrorMessage(String message) {
        if (message == null) {
            return "<html></html>";
        }

        int maxWidth = 80;
        StringBuilder formatted = new StringBuilder("<html>");
        StringBuilder currentLine = new StringBuilder();

        String[] words = message.split("\\s+");
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxWidth) {
                if (!currentLine.isEmpty()) {
                    formatted.append(currentLine).append("<br>");
                    currentLine.setLength(0);
                }
            }
            if (!currentLine.isEmpty()) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }

        if (!currentLine.isEmpty()) {
            formatted.append(currentLine);
        }

        formatted.append("</html>");
        return formatted.toString();
    }
}
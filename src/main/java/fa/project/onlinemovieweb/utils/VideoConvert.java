package fa.project.onlinemovieweb.utils;

public class VideoConvert {

    public static String extractFileId(String shareLink) {
        // Extract the file ID from the URL using known format
        String regex = "/d/([a-zA-Z0-9_-]{25,})";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(shareLink);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null; // invalid link
    }

    public static String toEmbedLink(String shareLink) {
        String fileId = extractFileId(shareLink);
        if (fileId == null) return "Invalid Google Drive share link.";
        return "https://drive.google.com/file/d/" + fileId + "/preview";
    }

    public static String toDownloadLink(String shareLink) {
        String fileId = extractFileId(shareLink);
        if (fileId == null) return "Invalid Google Drive share link.";
        return "https://drive.google.com/uc?export=download&id=" + fileId;
    }

    public static void main(String[] args) {
        String shareLink = "  ";
        System.out.println("Embed Link: " + toEmbedLink(shareLink));
        System.out.println("Download Link: " + toDownloadLink(shareLink));
    }
}

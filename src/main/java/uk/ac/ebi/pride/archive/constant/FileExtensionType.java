package uk.ac.ebi.pride.archive.constant;


public enum FileExtensionType {
    GPG(".gpg"), MD5(".md5"), JAR(".jar");

    private final String fileExtension;

    FileExtensionType(final String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public static boolean containsFileExtension(final String fileExtensionToBeChecked) {
        for (final FileExtensionType fileExtensionType : FileExtensionType.values()) {
            if (fileExtensionToBeChecked.toLowerCase().endsWith(fileExtensionType.getFileExtension())) {
                return true;
            }
        }
        return false;
    }

}

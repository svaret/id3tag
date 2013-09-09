package se.nextit.id3tag

import static groovy.io.FileType.FILES
import static java.util.logging.Level.OFF
import static java.util.logging.Logger.getLogger

getLogger("org.jaudiotagger").setLevel(OFF)

def dir = new File("/Users/pastorn/Dropbox/Bergstrom/music/reggaeAndMisc/reggaeNet/N/Nicodemus")
def numberOfFiles = 0
def skippedFiles = []

dir.traverse(type: FILES, nameFilter: ~/.*\.mp3$/) {
    def myMP3File = new MyMP3File(it)
    myMP3File.setId3v1TagFields()
    myMP3File.setId3v2TagFields()
}

skippedFiles.each {
    println it
}

boolean filenameInvalid(List<String> artistAndTitle, List<String> skippedFiles) {
    if (artistAndTitle.size() < 2) {
        skippedFiles.add artistAndTitle + ". Separator not found in filename."
        return false
    }
    if (artistAndTitle.size() > 2) {
        skippedFiles.add artistAndTitle + ". Too many separators in filename."
        return false
    }
}
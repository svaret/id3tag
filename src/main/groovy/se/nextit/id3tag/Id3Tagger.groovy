package se.nextit.id3tag

import static groovy.io.FileType.FILES
import static java.util.logging.Level.OFF
import static java.util.logging.Logger.getLogger

getLogger("org.jaudiotagger").setLevel(OFF)

def dir = new File("/Users/pastorn/Dropbox/Bergstrom/music/reggaeAndMisc/miscNet/M/Monica Zetterlund")
def FILENAME = "fileNamesFile.txt"
def numberOfFiles = 0
def skippedFiles = []

def filenamesFile = new File(FILENAME)
Set processedFilenames = []
if (filenamesFile.exists())
    processedFilenames = filenamesFile.readLines()

dir.traverse(type: FILES, nameFilter: ~/.*\.mp3$/) {
    println "Found file '" + it.name + "'"
    if (processedFilenames.contains(it.name))
        return

    println "Processing '" + it.name + "'"
    try {
        def myMP3File = new MyMP3File(it)
        myMP3File.setId3v1TagFields()
        myMP3File.setId3v2TagFields()
    } catch (FilenameInvalidException e) {
        skippedFiles += "Filename '" + it.name + "' is invalid."
        return
    } catch (FileNotFoundException e) {
        skippedFiles += "Filename '" + it.name + "' not found."
        return
    }

    numberOfFiles++
    processedFilenames.add(it.name)
}

File processedFilenamesFile = new File(FILENAME)
processedFilenamesFile.delete()
processedFilenamesFile.withWriter { out ->
    processedFilenames.each { out.println it }
}

println numberOfFiles + " files processed"

if (skippedFiles.size() > 0) {
    println "Skipped files:"
    skippedFiles.each {
        println it

    }
}
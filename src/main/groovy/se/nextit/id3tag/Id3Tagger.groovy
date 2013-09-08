package se.nextit.id3tag

import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.id3.ID3v11Tag
import org.jaudiotagger.tag.id3.ID3v24Tag

import static groovy.io.FileType.FILES
import static org.jaudiotagger.tag.FieldKey.ARTIST
import static org.jaudiotagger.tag.FieldKey.TITLE

def dir = new File("/Users/pastorn/Dropbox/Bergstrom/music/reggaeAndMisc/reggaeNet/N/Nicodemus")
def numberOfFiles = 0
def skippedFiles = []

dir.traverse(type: FILES, nameFilter: ~/.*\.mp3$/) {
    def file = it
    def filenameWithoutExtension = file.name.lastIndexOf('.').with { it != -1 ? file.name[0..<it] : file.name }
    def artistAndTitle = filenameWithoutExtension.split(" - ") as List

    if (filenameInvalid(artistAndTitle, skippedFiles)) return

    def mp3File = new MP3File(file)

    processId3v1Tag(mp3File, artistAndTitle.get(0).trim(), artistAndTitle.get(1).trim())
    processId3v2Tag(mp3File, artistAndTitle.get(0).trim(), artistAndTitle.get(1).trim())

    mp3File.commit()
    numberOfFiles++
}

private void processId3v1Tag(MP3File mp3File, String artist, String title) {
    if (mp3File.hasID3v1Tag()) {
        if (!mp3File.ID3v1Tag.getFirst(ARTIST).equals(artist) ||
                !mp3File.ID3v1Tag.getFirst(TITLE).equals(title)) {
            mp3File.ID3v1Tag.setField ARTIST, artist
            mp3File.ID3v1Tag.setField TITLE, title
        }
    } else {
        def ID3v1Tag = new ID3v11Tag()
        ID3v1Tag.artist = artist
        ID3v1Tag.title = title
        mp3File.ID3v1Tag = ID3v1Tag
    }
}

private void processId3v2Tag(MP3File mp3File, String artist, String title) {
    if (mp3File.hasID3v2Tag()) {
        if (!mp3File.ID3v2Tag.getFirst(ARTIST).equals(artist)) {
            mp3File.ID3v2Tag.setField ARTIST, artist
        } else {
            def ID3v2Tag = new ID3v24Tag()
            ID3v2Tag.setField ARTIST, artist
            mp3File.ID3v2Tag = ID3v2Tag
        }

        if (!mp3File.ID3v2Tag.getFirst(TITLE).equals(title)) {
            mp3File.ID3v2Tag.setField TITLE, title
        } else {
            def ID3v2Tag = new ID3v24Tag()
            ID3v2Tag.setField TITLE, title
            mp3File.ID3v2Tag = ID3v2Tag
        }
    }
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
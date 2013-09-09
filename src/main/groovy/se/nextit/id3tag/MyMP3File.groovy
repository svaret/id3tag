package se.nextit.id3tag

import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.id3.ID3v11Tag
import org.jaudiotagger.tag.id3.ID3v24Tag

import static org.jaudiotagger.tag.FieldKey.ARTIST
import static org.jaudiotagger.tag.FieldKey.TITLE

class MyMP3File {
    private static final String SEPARATOR = " - "
    private MP3File mp3File

    MyMP3File(File file) {
        def filenameWithoutExtension = file.name.subSequence(0, file.name.lastIndexOf('.'))
        def artistAndTitle = filenameWithoutExtension.split(SEPARATOR as Closure) as List
        if (artistAndTitle.size() < 2)
            throw new SeparatorMissingException("Separator not found in filename " + file.name)
        if (artistAndTitle.size() > 2)
            throw new TooManySeparatorsException("Too many separators in filename" + file.name)

        mp3File = new MP3File(file)
        setId3v1TagFields(artistAndTitle.get(0), artistAndTitle.get(1))
        setId3v2TagFields(artistAndTitle.get(0), artistAndTitle.get(1))
        mp3File.commit()
    }

    private def setId3v1TagFields(artist, title) {
        if (!mp3File.hasID3v1Tag())
            mp3File.ID3v1Tag = new ID3v11Tag()

        if (artist != mp3File.ID3v1Tag.getFirst(ARTIST))
            mp3File.ID3v1Tag.setField(ARTIST, artist)

        if (title != mp3File.ID3v1Tag.getFirst(TITLE))
            mp3File.ID3v1Tag.setField(TITLE, title)
    }

    private def setId3v2TagFields(artist, title) {
        if (!mp3File.hasID3v2Tag())
            mp3File.ID3v2Tag = new ID3v24Tag()

        if (artist != mp3File.ID3v2Tag.getFirst(ARTIST))
            mp3File.ID3v2Tag.setField(ARTIST, artist)

        if (title != mp3File.ID3v2Tag.getFirst(TITLE))
            mp3File.ID3v2Tag.setField(TITLE, title)
    }


    def getArtistV1Tag() {
        mp3File.ID3v1Tag.getFirst(ARTIST)
    }

    def getTitleV1Tag() {
        mp3File.ID3v1Tag.getFirst(TITLE)
    }

    def getArtistV2Tag() {
        mp3File.ID3v2Tag.getFirst(ARTIST)
    }

    def getTitleV2Tag() {
        mp3File.ID3v1Tag.getFirst(TITLE)
    }
}
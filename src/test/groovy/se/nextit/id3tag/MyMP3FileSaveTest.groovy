package se.nextit.id3tag

import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.id3.ID3v11Tag
import org.jaudiotagger.tag.id3.ID3v24Tag
import spock.lang.Specification

import java.util.logging.Level
import java.util.logging.Logger

import static java.net.URLDecoder.decode
import static java.util.logging.Level.OFF
import static java.util.logging.Logger.getLogger
import static org.jaudiotagger.tag.FieldKey.ARTIST
import static org.jaudiotagger.tag.FieldKey.FBPM
import static org.jaudiotagger.tag.FieldKey.TITLE

class MyMP3FileSaveTest extends Specification {
    { getLogger("org.jaudiotagger").setLevel(OFF); }

    private static final String CORRECT_FILENAME = "/Ronnie Davis - Hooligan.mp3"

    def "Creating MyMP3File with no separators should throw an exception"() {
        given:
        def file = new File("/RonnieDavisIRoyHolligan.mp3")

        when:
        new MyMP3File(file)

        then:
        thrown SeparatorMissingException
    }

    def "Creating MyMP3File with too many separators should throw an exception"() {
        given:
        def file = new File("/Ronnie - Davis - IRoyHooligan.mp3")

        when:
        new MyMP3File(file)

        then:
        thrown TooManySeparatorsException
    }

    def "MP3 file with existing incorrect id3v1 tag should get correct artist and title tagged"() {
        given:
        def mp3File = new MP3File(openFile(CORRECT_FILENAME))
        mp3File.ID3v1Tag = addId3v1Tag("Incorrect artist", "Incorrect title")
        mp3File.commit()

        when:
        def myMP3File = new MyMP3File(openFile(CORRECT_FILENAME))

        then:
        myMP3File.artistV1Tag == "Ronnie Davis"
        myMP3File.titleV1Tag == "Hooligan"
    }

    def "MP3 file with no id3v1 tag should get correct artist and title tagged"() {
        given:
        def mp3File = new MP3File(openFile(CORRECT_FILENAME))
        mp3File.delete(mp3File.ID3v1Tag)
        mp3File.commit()

        when:
        def myMP3File = new MyMP3File(openFile(CORRECT_FILENAME))

        then:
        myMP3File.artistV1Tag == "Ronnie Davis"
        myMP3File.titleV1Tag == "Hooligan"
    }

    def "MP3 file with existing incorrect id3v2 tag should get correct artist and title tagged"() {
        given:
        def mp3File = new MP3File(openFile(CORRECT_FILENAME))
        mp3File.ID3v2Tag = addId3v2Tag("Incorrect artist", "Incorrect title")
        mp3File.commit()

        when:
        def myMP3File = new MyMP3File(openFile(CORRECT_FILENAME))

        then:
        myMP3File.artistV2Tag == "Ronnie Davis"
        myMP3File.titleV2Tag == "Hooligan"
    }

    def "MP3 file with no id3v2 tag should get correct artist and title tagged"() {
        given:
        def mp3File = new MP3File(openFile(CORRECT_FILENAME))
        mp3File.delete(mp3File.ID3v2Tag)
        mp3File.commit()

        when:
        def myMP3File = new MyMP3File(openFile(CORRECT_FILENAME))

        then:
        myMP3File.artistV2Tag == "Ronnie Davis"
        myMP3File.titleV2Tag == "Hooligan"
    }

    private File openFile(filename) {
        def url = getClass().getResource(decode(filename, "utf-8"))
        new File(decode(url.getFile(), "utf-8"))
    }

    private ID3v11Tag addId3v1Tag(def artist, def title) {
        def ID3v1Tag = new ID3v11Tag()
        ID3v1Tag.artist = artist
        ID3v1Tag.title = title
        ID3v1Tag
    }

    private ID3v24Tag addId3v2Tag(def artist, def title) {
        def ID3v2Tag = new ID3v24Tag()
        ID3v2Tag.setField(ARTIST, artist)
        ID3v2Tag.setField(TITLE, title)
        ID3v2Tag
    }
}
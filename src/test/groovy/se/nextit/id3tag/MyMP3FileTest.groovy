package se.nextit.id3tag

import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.id3.ID3v11Tag
import org.jaudiotagger.tag.id3.ID3v24Tag
import spock.lang.Specification

import static java.net.URLDecoder.decode
import static java.util.logging.Level.OFF
import static java.util.logging.Logger.getLogger
import static org.jaudiotagger.tag.FieldKey.ARTIST
import static org.jaudiotagger.tag.FieldKey.TITLE

class MyMP3FileTest extends Specification {
    { getLogger("org.jaudiotagger").setLevel(OFF); }

    private static final String CORRECT_FILENAME = "/Ronnie Davis - Hooligan.mp3"

    def "Missing separator should throw an exception upon creation"() {
        given:
        def file = new File("/RonnieDavisIRoyHooligan.mp3")

        when:
        new MyMP3File(file)

        then:
        thrown SeparatorMissingException
    }

    def "Too many separators should throw an exception upon creation"() {
        given:
        def file = new File("/Ronnie - Davis - IRoyHooligan.mp3")

        when:
        new MyMP3File(file)

        then:
        thrown TooManySeparatorsException
    }

    def "Incorrect id3v1 tag should get correct artist and title tagged"() {
        given:
        def mp3File = new MP3File(openFile(CORRECT_FILENAME))
        mp3File.ID3v1Tag = addId3v1Tag("Incorrect artist", "Incorrect title")
        mp3File.commit()
        def myMP3File = new MyMP3File(openFile(CORRECT_FILENAME))

        when:
        myMP3File.setId3v1TagFields()

        then:
        myMP3File.artistV1Tag == "Ronnie Davis"
        myMP3File.titleV1Tag == "Hooligan"
    }

    def "Missing id3v1 tag should get correct artist and title tagged"() {
        given:
        def mp3File = new MP3File(openFile(CORRECT_FILENAME))
        mp3File.delete(mp3File.ID3v1Tag)
        mp3File.commit()
        def myMP3File = new MyMP3File(openFile(CORRECT_FILENAME))

        when:
        myMP3File.setId3v1TagFields()

        then:
        myMP3File.artistV1Tag == "Ronnie Davis"
        myMP3File.titleV1Tag == "Hooligan"
    }

    def "Incorrect id3v2 tag should get correct artist and title tagged"() {
        given:
        def mp3File = new MP3File(openFile(CORRECT_FILENAME))
        mp3File.ID3v2Tag = addId3v2Tag("Incorrect artist", "Incorrect title")
        mp3File.commit()
        def myMP3File = new MyMP3File(openFile(CORRECT_FILENAME))

        when:
        myMP3File.setId3v2TagFields()

        then:
        myMP3File.artistV2Tag == "Ronnie Davis"
        myMP3File.titleV2Tag == "Hooligan"
    }

    def "Missing id3v2 tag should get correct artist and title tagged"() {
        given:
        def mp3File = new MP3File(openFile(CORRECT_FILENAME))
        mp3File.delete(mp3File.ID3v2Tag)
        mp3File.commit()
        def myMP3File = new MyMP3File(openFile(CORRECT_FILENAME))

        when:
        myMP3File.setId3v2TagFields()

        then:
        myMP3File.artistV2Tag == "Ronnie Davis"
        myMP3File.titleV2Tag == "Hooligan"
    }

    private File openFile(filename) {
        def url = getClass().getResource(decode(filename, "utf-8"))
        new File(decode(url.getFile(), "utf-8"))
    }

    private static ID3v11Tag addId3v1Tag(def artist, def title) {
        def ID3v1Tag = new ID3v11Tag()
        ID3v1Tag.artist = artist
        ID3v1Tag.title = title
        ID3v1Tag
    }

    private static ID3v24Tag addId3v2Tag(def artist, def title) {
        def ID3v2Tag = new ID3v24Tag()
        ID3v2Tag.setField(ARTIST, artist)
        ID3v2Tag.setField(TITLE, title)
        ID3v2Tag
    }
}
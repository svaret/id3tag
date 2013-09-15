package se.nextit.id3tag

import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.id3.ID3v11Tag
import org.jaudiotagger.tag.id3.ID3v24Tag
import spock.lang.Specification

import static java.net.URLDecoder.decode
import static java.util.Arrays.equals
import static java.util.logging.Level.OFF
import static java.util.logging.Logger.getLogger
import static org.jaudiotagger.tag.FieldKey.ARTIST
import static org.jaudiotagger.tag.FieldKey.TITLE
import static org.jaudiotagger.tag.datatype.Artwork.createArtworkFromFile

class MyMP3FileTest extends Specification {
    { getLogger("org.jaudiotagger").setLevel(OFF); }

    private static final String RONNIE_DAVIS = "Ronnie Davis"
    private static final String HOOLIGAN = "Hooligan"
    private static final String RONNIE_DAVIS_DIR = "ronniedavis"
    private static final String RONNIE_DAVIS_FILENAME = "/" + RONNIE_DAVIS_DIR + "/" + RONNIE_DAVIS + " - " + HOOLIGAN + ".mp3"
    private static final String JPG_ARTWORK_FILENAME = "/" + RONNIE_DAVIS_DIR + "/" + RONNIE_DAVIS_DIR + ".jpg"

    private static final String LEON = "Leon & The Revolutionaries"
    private static final String ROOTS = "Roots"
    private static final String LEON_DIR = "leon"
    private static final String LEON_FILENAME = "/" + LEON_DIR + "/" + LEON + " - " + ROOTS + ".mp3"
    private static final String PNG_ARTWORK_FILENAME = "/" + LEON_DIR + "/" + LEON_DIR + ".png"

    private static final String GARY_MINOTT = "Gary Minott"
    private static final String SEEK_GOD = "Seek God"
    private static final String GARY_MINOTT_DIR = "garyminott"
    private static final String GARY_MINOTT_FILENAME = "/" + GARY_MINOTT_DIR + "/" + GARY_MINOTT + " - " + SEEK_GOD + ".mp3"
    private static final String MISSING_ARTWORK_FILENAME = "/" + GARY_MINOTT_DIR + "/" + GARY_MINOTT_DIR + ".png"

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
        def mp3File = new MP3File(openFile(RONNIE_DAVIS_FILENAME))
        mp3File.ID3v1Tag = addId3v1Tag("Incorrect artist", "Incorrect title")
        mp3File.commit()
        def myMP3File = new MyMP3File(openFile(RONNIE_DAVIS_FILENAME))

        when:
        myMP3File.setId3v1TagFields()

        then:
        myMP3File.artistV1Tag == "Ronnie Davis"
        myMP3File.titleV1Tag == "Hooligan"
    }

    def "Missing id3v1 tag should get correct artist and title tagged"() {
        given:
        def mp3File = new MP3File(openFile(RONNIE_DAVIS_FILENAME))
        mp3File.delete(mp3File.ID3v1Tag)
        mp3File.commit()
        def myMP3File = new MyMP3File(openFile(RONNIE_DAVIS_FILENAME))

        when:
        myMP3File.setId3v1TagFields()

        then:
        myMP3File.artistV1Tag == "Ronnie Davis"
        myMP3File.titleV1Tag == "Hooligan"
    }

    def "Incorrect id3v2 tag should get correct artist and title tagged"() {
        given:
        def mp3File = new MP3File(openFile(RONNIE_DAVIS_FILENAME))
        mp3File.ID3v2Tag = addId3v2Tag("Incorrect artist", "Incorrect title")
        mp3File.commit()
        def myMP3File = new MyMP3File(openFile(RONNIE_DAVIS_FILENAME))

        when:
        myMP3File.setId3v2TagFields()

        then:
        myMP3File.artistV2Tag == "Ronnie Davis"
        myMP3File.titleV2Tag == "Hooligan"
    }

    def "Missing id3v2 tag should get correct artist and title tagged"() {
        given:
        def mp3File = new MP3File(openFile(RONNIE_DAVIS_FILENAME))
        mp3File.delete(mp3File.ID3v2Tag)
        mp3File.commit()
        def myMP3File = new MyMP3File(openFile(RONNIE_DAVIS_FILENAME))

        when:
        myMP3File.setId3v2TagFields()

        then:
        myMP3File.artistV2Tag == "Ronnie Davis"
        myMP3File.titleV2Tag == "Hooligan"
    }

    def "JPG Artwork with same name as folder will be added to mp3"() {
        given:
        def mp3File = new MP3File(openFile(RONNIE_DAVIS_FILENAME))
        mp3File.ID3v2Tag = addId3v2Tag(RONNIE_DAVIS, HOOLIGAN)
        mp3File.commit()
        def myMP3File = new MyMP3File(openFile(RONNIE_DAVIS_FILENAME))

        when:
        myMP3File.setId3v2TagFields()

        then:
        equals(myMP3File.artwork.getBinaryData(), createArtworkFromFile(openFile(JPG_ARTWORK_FILENAME)).getBinaryData())
    }

    def "PNG Artwork with same name as folder will be added to mp3"() {
        given:
        def mp3File = new MP3File(openFile(LEON_FILENAME))
        mp3File.ID3v2Tag = addId3v2Tag(LEON, ROOTS)
        mp3File.commit()
        def myMP3File = new MyMP3File(openFile(LEON_FILENAME))

        when:
        myMP3File.setId3v2TagFields()

        then:
        equals(myMP3File.artwork.getBinaryData(), createArtworkFromFile(openFile(PNG_ARTWORK_FILENAME)).getBinaryData())
    }

    def "Missing artwork file should work"() {
        given:
        def myMP3File = new MyMP3File(openFile(GARY_MINOTT_FILENAME))

        when:
        myMP3File.setId3v2TagFields()

        then:
        myMP3File.artwork == null
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
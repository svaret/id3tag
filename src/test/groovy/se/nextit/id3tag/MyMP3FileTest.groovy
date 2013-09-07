package se.nextit.id3tag

import spock.lang.Specification

import static java.net.URLDecoder.decode

class MyMP3FileTest extends Specification {
    def "Creating MyMP3File with no separators should throw an exception"() {
        given:
        def file = new File(decode("/RonnieDavisIRoyHolligan.mp3", "utf-8"))

        when:
        new MyMP3File(file)

        then:
        thrown SeparatorMissingException
    }

    def "Creating MyMP3File with too many separators should throw an exception"() {
        given:
        def file = new File(decode("/Ronnie - Davis - IRoyHooligan.mp3", "utf-8"))

        when:
        new MyMP3File(file)

        then:
        thrown TooManySeparatorsException
    }
}
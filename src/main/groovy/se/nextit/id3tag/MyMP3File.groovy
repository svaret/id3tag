package se.nextit.id3tag

import org.jaudiotagger.audio.mp3.MP3File

class MyMP3File {
    private MP3File mp3File
    MyMP3File(File file) {
        def artistAndTitle = file.name.split(" - ") as List
        if (artistAndTitle.size() < 2)
            throw new SeparatorMissingException("Separator not found in filename " + file.name)
        if (artistAndTitle.size() > 2)
            throw new TooManySeparatorsException("Too many separators in filename" + file.name)

        mp3File = new MP3File(file)
    }
}
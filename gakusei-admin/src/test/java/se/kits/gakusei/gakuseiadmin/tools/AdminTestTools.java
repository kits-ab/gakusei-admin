package se.kits.gakusei.gakuseiadmin.tools;

import se.kits.gakusei.content.model.WordType;

import java.util.ArrayList;

public class AdminTestTools {

    public static Iterable<WordType> generateWordTypes() {
        Iterable<WordType> wordTypes = new ArrayList<WordType>();
        for (int i = 0; i < 5; i++) {
            WordType wordType = new WordType();
            wordType.setType("type" + i);
        }

        return wordTypes;
    }
}

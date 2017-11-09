package se.kits.gakusei.gakuseiadmin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kits.gakusei.content.model.Inflection;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.repository.InflectionRepository;
import se.kits.gakusei.content.repository.LessonRepository;
import se.kits.gakusei.content.repository.NuggetRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminGrammarHandler {

    @Autowired
    InflectionRepository inflectionRepository;

    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    NuggetRepository nuggetRepository;

    public List<HashMap<String, Object>> createGrammarLists() {
        List<HashMap<String, Object>> grammarLists = new ArrayList<>();

        for(Lesson lesson : lessonRepository.findVocabularyLessons()){
            grammarLists.add(getGrammarList(lesson));
        }

        return grammarLists;
    }

    private HashMap<String, Object> getGrammarList(Lesson lesson) {
        HashMap<String, Object> grammarList = new HashMap<>();
        HashMap<String, List<Inflection>> inflections = new HashMap<>();

        List<Nugget> nuggets = nuggetRepository.getNuggetsWithWordType("verb", "english", "swedish");

        inflections.put("used", inflectionRepository.findByLesson_Id(lesson.getId()));
        inflections.put("unused", getUnusedInflectionMethods(lesson));

        grammarList.put("lesson", lesson);
        grammarList.put("inflections", inflections);
        grammarList.put("nuggets", nuggets);

        return grammarList;
    }

    public List<Inflection> getUnusedInflectionMethods(Lesson lesson) {
        List<Inflection> unusedInflectionMethods = new ArrayList<>();
        List<String> usedInflectionMethods = extractInflectionStrings(inflectionRepository.findByLesson_Id(lesson.getId()));
        List<String> allInflectionMethods = Inflection.getAllInflectionMethods();

        for(String inflectionMethod : allInflectionMethods){
            if(!usedInflectionMethods.contains(inflectionMethod)){
                Inflection inflection = new Inflection();
                inflection.setInflectionMethod(inflectionMethod);
                inflection.setLesson(lesson);

                unusedInflectionMethods.add(inflection);
            }
        }

        return unusedInflectionMethods;
    }

    private List<String> extractInflectionStrings(List<Inflection> inflections){
        return inflections.stream().map(inflection -> inflection.getInflectionMethod()).collect(Collectors.toList());
    }

}

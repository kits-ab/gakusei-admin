package se.kits.gakusei.gakuseiadmin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kits.gakusei.content.model.Inflection;
import se.kits.gakusei.content.model.Lesson;
import se.kits.gakusei.content.repository.InflectionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminGrammarHandler {

    @Autowired
    InflectionRepository inflectionRepository;

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

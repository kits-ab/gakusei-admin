package se.kits.gakusei.gakuseiadmin.controllers;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.gakuseiadmin.content.WordTypeRepository;
import se.kits.gakusei.gakuseiadmin.Controllers.WordTypeController;
import se.kits.gakusei.gakuseiadmin.tools.AdminTestTools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class WordTypeControllerTest {

    @InjectMocks
    private WordTypeController wordTypeController;

    @Mock
    private WordTypeRepository wordTypeRepository;

    private String type;
    private String newType;
    private Iterable<WordType> wordTypes;
    private WordType wordType;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        type = "noun";
        newType = "verb";

        wordType = new WordType();
        wordType.setType(type);

        wordTypes = AdminTestTools.generateWordTypes();

    }


    @Test
    public void testGetWordTypeOK() {
        when(wordTypeRepository.findByType(type)).thenReturn(wordType);

        ResponseEntity<WordType> re = wordTypeController.getWordType(type);

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(wordType, re.getBody());
    }

    @Test
    public void testGetWordTypesOK() {
        when(wordTypeRepository.findAll()).thenReturn(wordTypes);

        ResponseEntity<Iterable<WordType>> re = wordTypeController.getWordTypes();

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(wordTypes, re.getBody());

    }

    @Test
    public void testGetWordTypeNotFound() {
        when(wordTypeRepository.findByType(newType)).thenReturn(null);

        ResponseEntity<WordType> re = wordTypeController.getWordType(newType);

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
        assertEquals(null, re.getBody());
    }

    @Test
    public void testCreateWordTypeOK() {
        when(wordTypeRepository.findByType(type)).thenReturn(null);
        when(wordTypeRepository.save(any(WordType.class))).thenReturn(wordType);

        ResponseEntity<?> re = wordTypeController.createWordType(type);

        assertEquals(HttpStatus.CREATED, re.getStatusCode());
        assertEquals(wordType, re.getBody());

    }

    @Test
    public void testCreateWordTypeBadRequest() {
        when(wordTypeRepository.findByType(type)).thenReturn(wordType);

        ResponseEntity<?> re = wordTypeController.createWordType(type);

        assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
        assertTrue(re.getBody().getClass() == String.class);
    }


    @Test
    public void testUpdateWordTypeOK() {
        when(wordTypeRepository.findByType(type)).thenReturn(wordType);
        when(wordTypeRepository.save(any(WordType.class))).thenReturn(null);
        wordType.setType(newType);

        ResponseEntity<WordType> re = wordTypeController.updateWordType(type, newType);

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(wordType, re.getBody());

    }

    @Test
    public void testUpdateWordTypeNotFound() {
        when(wordTypeRepository.findByType(type)).thenReturn(null);

        ResponseEntity<WordType> re = wordTypeController.updateWordType(type, newType);

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
        assertEquals(null, re.getBody());

    }

    @Test
    public void testDeleteTypeOK() {
        when(wordTypeRepository.findByType(type)).thenReturn(wordType);

        ResponseEntity<WordType> re = wordTypeController.deleteWordType(type);

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(null, re.getBody());

    }

    @Test
    public void testDeleteTypeNotFound() {
        when(wordTypeRepository.findByType(type)).thenReturn(null);

        ResponseEntity<WordType> re = wordTypeController.deleteWordType(type);

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
        assertEquals(null, re.getBody());
    }
}

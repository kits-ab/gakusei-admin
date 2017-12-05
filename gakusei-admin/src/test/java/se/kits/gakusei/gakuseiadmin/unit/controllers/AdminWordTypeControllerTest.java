package se.kits.gakusei.gakuseiadmin.unit.controllers;

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
import se.kits.gakusei.content.repository.WordTypeRepository;
import se.kits.gakusei.gakuseiadmin.controllers.AdminWordTypeController;
import se.kits.gakusei.gakuseiadmin.tools.AdminTestTools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AdminWordTypeControllerTest {

    @InjectMocks
    private AdminWordTypeController adminWordTypeController;

    @Mock
    private WordTypeRepository wordTypeRepository;

    private String type;
    private String newType;
    private Iterable<WordType> wordTypes;
    private WordType wordType;
    private WordType updatedType;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        type = "noun";
        newType = "verb";

        wordType = AdminTestTools.generateTestWordType(type);
        updatedType = AdminTestTools.updateTestWordType(wordType, newType);

        wordTypes = AdminTestTools.generateWordTypes();

    }


    @Test
    public void testGetWordTypeOK() {
        when(wordTypeRepository.findOne(wordType.getId())).thenReturn(wordType);

        ResponseEntity<WordType> re = adminWordTypeController.getWordType(wordType.getId());

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(wordType, re.getBody());
    }

    @Test
    public void testGetWordTypesOK() {
        when(wordTypeRepository.findAll()).thenReturn(wordTypes);

        ResponseEntity<Iterable<WordType>> re = adminWordTypeController.getWordTypes();

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(wordTypes, re.getBody());

    }

    @Test
    public void testGetWordTypeNotFound() {
        when(wordTypeRepository.findOne(wordType.getId())).thenReturn(null);

        ResponseEntity<WordType> re = adminWordTypeController.getWordType(wordType.getId());

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
        assertEquals(null, re.getBody());
    }

    @Test
    public void testCreateWordTypeOK() {
        when(wordTypeRepository.findByType(type)).thenReturn(null);
        when(wordTypeRepository.save(any(WordType.class))).thenReturn(wordType);

        ResponseEntity<?> re = adminWordTypeController.createWordType(type);

        assertEquals(HttpStatus.CREATED, re.getStatusCode());
        assertEquals(wordType, re.getBody());

    }

    @Test
    public void testCreateWordTypeBadRequest() {
        when(wordTypeRepository.findByType(type)).thenReturn(wordType);

        ResponseEntity<?> re = adminWordTypeController.createWordType(type);

        assertEquals(HttpStatus.BAD_REQUEST, re.getStatusCode());
        assertTrue(re.getBody().getClass() == String.class);
    }


    @Test
    public void testUpdateWordTypeOK() {
        when(wordTypeRepository.exists(updatedType.getId())).thenReturn(true);
        when(wordTypeRepository.save(updatedType)).thenReturn(updatedType);

        ResponseEntity<WordType> re = adminWordTypeController.updateWordType(updatedType);

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(updatedType, re.getBody());

    }

    @Test
    public void testUpdateWordTypeNotFound() {
        when(wordTypeRepository.exists(wordType.getId())).thenReturn(false);

        ResponseEntity<WordType> re = adminWordTypeController.updateWordType(updatedType);

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
        assertEquals(null, re.getBody());

    }

    @Test
    public void testDeleteTypeOK() {
        when(wordTypeRepository.exists(wordType.getId())).thenReturn(true);

        ResponseEntity<String> re = adminWordTypeController.deleteWordType(wordType.getId());

        assertEquals(HttpStatus.OK, re.getStatusCode());
        assertEquals(null, re.getBody());

    }

    @Test
    public void testDeleteTypeNotFound() {
        when(wordTypeRepository.exists(wordType.getId())).thenReturn(false);

        ResponseEntity<String> re = adminWordTypeController.deleteWordType(wordType.getId());

        assertEquals(HttpStatus.NOT_FOUND, re.getStatusCode());
        assertEquals(null, re.getBody());
    }
}

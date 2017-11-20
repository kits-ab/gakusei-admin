package se.kits.gakusei.gakuseiadmin.unit.controllers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import se.kits.gakusei.content.model.Book;
import se.kits.gakusei.content.model.Nugget;
import se.kits.gakusei.content.model.WordType;
import se.kits.gakusei.content.repository.BookRepository;
import se.kits.gakusei.gakuseiadmin.content.AdminNuggetRepository;
import se.kits.gakusei.gakuseiadmin.content.AdminWordTypeRepository;
import se.kits.gakusei.gakuseiadmin.tools.AdminTestTools;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminNuggetControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private AdminNuggetRepository adminNuggetRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AdminWordTypeRepository wordTypeRepository;

    private MockMvc mockMvc;

    private List<Long> bookIds;
    private Nugget nugget;
    private int offset;
    private int pageSize;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        bookIds = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        Book book = bookRepository.save(AdminTestTools.generateTestBook("book"));
        books.add(book);
        bookIds.add(book.getId());
        WordType wordType = wordTypeRepository.save(AdminTestTools.generateTestWordType("type"));
        long wordTypeId = wordType.getId();
        nugget = AdminTestTools.generateNugget(wordType, books);
        offset = 0;
        pageSize = 5;

    }

    @After
    public void tearDown() {
        adminNuggetRepository.deleteAll();
        bookRepository.deleteAll();
        wordTypeRepository.deleteAll();
    }

    @Test
    public void testCreateNuggetOk() throws Exception {
        String nuggetString = AdminTestTools.generateNuggetString(nugget);

        mockMvc.perform(post("/api/nuggets")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(nuggetString))
                .andExpect(status().isCreated());

        Assert.assertTrue(adminNuggetRepository.count() > 0);
    }

    @Test
    public void testCreateNuggetBadRequest() throws Exception {
        adminNuggetRepository.save(nugget);
        nugget.setId("testId2");
        String nuggetString = AdminTestTools.generateNuggetString(nugget);

        mockMvc.perform(post("/api/nuggets")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(nuggetString))
                .andExpect(status().isBadRequest());

        Assert.assertTrue(adminNuggetRepository.count() == 1);
    }

    @Test
    public void testDeleteNuggetOk() throws Exception {
        Nugget savedNugget = adminNuggetRepository.save(nugget);

        mockMvc.perform(delete("/api/nuggets/" + savedNugget.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        Assert.assertTrue(adminNuggetRepository.count() == 0);
    }

    @Test
    public void testDeleteNuggetNotFound() throws Exception {
        mockMvc.perform(delete("/api/nuggets/" + nugget.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetNuggetPage() throws Exception {
        adminNuggetRepository.save(nugget);

        String requestUrl = String.format("/api/nuggets/%d?pageSize=%d", offset, pageSize);
        mockMvc.perform(get(requestUrl)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"totalElements\":1")));

    }

    @Test
    public void testGetNuggetPageFilterBySwedish() throws Exception {
        adminNuggetRepository.save(nugget);

        String requestUrl = String.format("/api/nuggets/%d/search?pageSize=%d&swedish="
                + nugget.getSwedish(), offset, pageSize);

        mockMvc.perform(get(requestUrl)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"totalElements\":1")));
    }

    @Test
    public void testGetNuggetPageFilterBySwedishAndWordTypeId() throws Exception {
        adminNuggetRepository.save(nugget);

        String requestUrl = String.format("/api/nuggets/%d/search?pageSize=%d&swedish="
                + nugget.getSwedish() + "&wordTypeId=%d", offset, pageSize, nugget.getWordType().getId());

        mockMvc.perform(get(requestUrl)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"totalElements\":1")));
    }

    @Test
    public void testGetNuggetPageFilterBySwedishAndBooksIn() throws Exception {
        adminNuggetRepository.save(nugget);

        String requestUrl = String.format("/api/nuggets/%d/search?pageSize=%d&swedish="
                + nugget.getSwedish(), offset, pageSize);

        mockMvc.perform(get(requestUrl)
                .requestAttr("bookIds", bookIds)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"totalElements\":1")));
    }

    @Test
    public void testGetNuggetPageFilterBySwedishAndWordTypeIdAndBooksIn() throws Exception {
        adminNuggetRepository.save(nugget);

        String requestUrl = String.format("/api/nuggets/%d/search?pageSize=%d&swedish="
                + nugget.getSwedish() + "&wordTypeId=%d", offset, pageSize, nugget.getWordType().getId());

        System.out.println(requestUrl);
        mockMvc.perform(get(requestUrl)
                .requestAttr("bookIds", bookIds)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"totalElements\":1")));
    }
}

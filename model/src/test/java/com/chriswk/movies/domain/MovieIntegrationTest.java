package com.chriswk.movies.domain;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Configurable;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.chriswk.movies.config.ModelConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ModelConfig.class})
public class MovieIntegrationTest {

	@Autowired
    private MovieDataOnDemand dod;
    
    @Test
    public void testCountMovies() {
        Assert.assertNotNull("Data on demand for 'Movie' failed to initialize correctly", dod.getRandomMovie());
        long count = Movie.countMovies();
        Assert.assertTrue("Counter for 'Movie' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void testFindMovie() {
        Movie obj = dod.getRandomMovie();
        Assert.assertNotNull("Data on demand for 'Movie' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Movie' failed to provide an identifier", id);
        obj = Movie.findMovie(id);
        Assert.assertNotNull("Find method for 'Movie' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Movie' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void testFindAllMovies() {
        Assert.assertNotNull("Data on demand for 'Movie' failed to initialize correctly", dod.getRandomMovie());
        long count = Movie.countMovies();
        Assert.assertTrue("Too expensive to perform a find all test for 'Movie', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Movie> result = Movie.findAllMovies();
        Assert.assertNotNull("Find all method for 'Movie' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Movie' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void testFindMovieEntries() {
        Assert.assertNotNull("Data on demand for 'Movie' failed to initialize correctly", dod.getRandomMovie());
        long count = Movie.countMovies();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Movie> result = Movie.findMovieEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Movie' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Movie' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void testFlush() {
        Movie obj = dod.getRandomMovie();
        Assert.assertNotNull("Data on demand for 'Movie' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Movie' failed to provide an identifier", id);
        obj = Movie.findMovie(id);
        Assert.assertNotNull("Find method for 'Movie' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyMovie(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'Movie' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void testMergeUpdate() {
        Movie obj = dod.getRandomMovie();
        Assert.assertNotNull("Data on demand for 'Movie' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Movie' failed to provide an identifier", id);
        obj = Movie.findMovie(id);
        boolean modified =  dod.modifyMovie(obj);
        Integer currentVersion = obj.getVersion();
        Movie merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'Movie' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void testPersist() {
        Assert.assertNotNull("Data on demand for 'Movie' failed to initialize correctly", dod.getRandomMovie());
        Movie obj = dod.getNewTransientMovie(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Movie' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Movie' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        Assert.assertNotNull("Expected 'Movie' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void testRemove() {
        Movie obj = dod.getRandomMovie();
        Assert.assertNotNull("Data on demand for 'Movie' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Movie' failed to provide an identifier", id);
        obj = Movie.findMovie(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'Movie' with identifier '" + id + "'", Movie.findMovie(id));
    }
	
    
}
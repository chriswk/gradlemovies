package com.chriswk.movies.domain;

import org.springframework.beans.factory.annotation.Configurable;

@Configurable
@Component
public class MovieDataOnDemand {
	private Random rnd = new SecureRandom();
    
    private List<Movie> data;
    
    public Movie getNewTransientMovie(int index) {
        Movie obj = new Movie();
        setRelease_date(obj, index);
        setTitle(obj, index);
        return obj;
    }
    
    public void setRelease_date(Movie obj, int index) {
        Date release_date = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND) + new Double(Math.random() * 1000).intValue()).getTime();
        obj.setRelease_date(release_date);
    }
    
    public void setTitle(Movie obj, int index) {
        String title = "title_" + index;
        obj.setTitle(title);
    }
    
    public Movie getSpecificMovie(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Movie obj = data.get(index);
        Long id = obj.getId();
        return Movie.findMovie(id);
    }
    
    public Movie getRandomMovie() {
        init();
        Movie obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return Movie.findMovie(id);
    }
    
    public boolean modifyMovie(Movie obj) {
        return false;
    }
    
    public void init() {
        int from = 0;
        int to = 10;
        data = Movie.findMovieEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Movie' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Movie>();
        for (int i = 0; i < 10; i++) {
            Movie obj = getNewTransientMovie(i);
            try {
                obj.persist();
            } catch (ConstraintViolationException e) {
                StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getConstraintDescriptor()).append(":").append(cv.getMessage()).append("=").append(cv.getInvalidValue()).append("]");
                }
                throw new RuntimeException(msg.toString(), e);
            }
            obj.flush();
            data.add(obj);
        }
    }
}
package dataAccess;
import model.*;

import java.util.Collection;

public interface DataAccess {
    void createUser(User user) throws DataAccessException;

    User getUser(User user) throws DataAccessException;
}
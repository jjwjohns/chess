package dataAccess;

import model.*;

import java.util.HashMap;
import java.util.UUID;

public class DataMemory implements DataAccess {
    final private HashMap<String, User> users = new HashMap<>();
    final private HashMap<String, Authtoken> authdata = new HashMap<>();
    final private HashMap<String, Game> games = new HashMap<>();

    public void createUser(User user) {
        users.put(user.username(), user);
    }

    public User getUser(User user) {
        return users.get(user.username());
    }

    public Authtoken createAuth (String username) {
        String token = UUID.randomUUID().toString();
        Authtoken auth = new Authtoken(token, username);
        authdata.put(token, auth);
        return auth;
    }

    public void deleteAuths() throws DataAccessException {
        authdata.clear();
    }

    public void deleteUsers() throws DataAccessException {
        users.clear();
    }

    public void deleteGames() throws DataAccessException {
        games.clear();
    }
}

//    public Collection<Pet> listPets() {
//        return pets.values();
//    }
//
//
//    public Pet getPet(int id) {
//        return pets.get(id);
//    }
//
//    public void deletePet(Integer id) {
//        pets.remove(id);
//    }
//
//    public void deleteAllPets() {
//        pets.clear();
//    }

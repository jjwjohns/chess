package dataAccess;

import model.*;

import java.util.HashMap;

public class DataMemory implements DataAccess {
    final private HashMap<String, User> users = new HashMap<>();

    public void createUser(User user) {
        users.put(user.username(), user);
    }

    public User getUser(User user) {
        return users.get(user.username());
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

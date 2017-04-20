package com.ibm.websphere.jsonsupport.test;

import java.util.List;
import java.util.Map;

public class SuperUser extends User {
    public User[] managedUsers;
    List<User> managedUsersList;
    Map<Object, User> userMap;
    Map<Object, Object> testMap;

    public List<User> getManagedUsersList() {
        return managedUsersList;
    }

    @SuppressWarnings("unused")
    private void setManagedUsersList(List<User> value) {
        managedUsersList = value;
    }

    public Map<Object, User> getUserMap() {
        return userMap;
    }

    @SuppressWarnings("unused")
    private void setUserMap(Map<Object, User> value) {
        userMap = value;
    }

    public Map<Object, Object> getTestMap() {
        return testMap;
    }

    @SuppressWarnings("unused")
    private void setTestMap(Map<Object, Object> value) {
        testMap = value;
    }
}

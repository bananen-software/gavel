package software.bananen.gavel.staticanalysis.examples.lcom4;

public class UserManagement {

    private int userId = 0;
    private String password = "secret";

    private String name = "John";
    private String email = "john@example.com";
    private String bio = "Lorem ipsum dolor sit amet";

    public void login() {
        System.out.println(userId + " " + password);
    }

    public void logout() {
        System.out.println(userId);
    }

    public void checkPassword() {
        System.out.println(password);
    }

    public void updateProfile() {
        System.out.println(name + " " + email + " " + bio);
    }

    public void getProfile() {
        System.out.println(name + " " + email + " " + bio);
    }
}

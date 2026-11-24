package model;

public class Worker {
    private int workerId;
    private String name;
    private String role;
    private String phone;

    public Worker(int workerId, String name, String role, String phone) {
        this.workerId = workerId;
        this.name = name;
        this.role = role;
        this.phone = phone;
    }

    public int getWorkerId() { return workerId; }
    public String getName()  { return name; }
    public String getRole()  { return role; }
    public String getPhone() { return phone; }

    public void display() {
        System.out.printf("%-4d %-20s %-22s %s%n", workerId, name, role, phone);
    }
}
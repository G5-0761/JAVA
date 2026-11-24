package storage;

import model.Worker;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO = Data Access Object: all SQL for the workers table lives here
public class WorkerDAO {

    public void addWorker(Worker w) throws SQLException {
        String sql = "INSERT INTO workers (name, role, phone) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, w.getName());
            ps.setString(2, w.getRole());
            ps.setString(3, w.getPhone());
            ps.executeUpdate();
        }
    }

    public List<Worker> getAllWorkers() throws SQLException {
        List<Worker> list = new ArrayList<>();
        String sql = "SELECT * FROM workers";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Worker(rs.getInt("worker_id"), rs.getString("name"),
                                    rs.getString("role"), rs.getString("phone")));
            }
        }
        return list;
    }

    public boolean deleteWorker(int id) throws SQLException {
        String sql = "DELETE FROM workers WHERE worker_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
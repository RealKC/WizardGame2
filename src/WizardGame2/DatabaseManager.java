package WizardGame2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class that mediates interactions with a database
 */
public class DatabaseManager {
    public static class ScoreEntry {
        private final String obtainedAt;
        private final int score;

        private ScoreEntry(String obtainedAt, int score) {
            this.obtainedAt = obtainedAt;
            this.score = score;
        }

        public String getObtainedAt() {
            return obtainedAt;
        }

        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            return "ScoreEntry{" +
                    "obtainedAt='" + obtainedAt + '\'' +
                    ", score=" + score +
                    '}';
        }
    }

    private static DatabaseManager instance = null;

    private Connection conn;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");

            conn = DriverManager.getConnection("jdbc:sqlite:saves.db");
            initializeTables();
        } catch (ClassNotFoundException e) {
            Utils.logException(getClass(), e, "[FATAL] 'sqlite-jdbc' is missing");
        } catch (SQLException e) {
            Utils.logException(getClass(), e, "failed to initialize connection");
        }
    }

    private void initializeTables() {
        try {
            var stmt = conn.createStatement();
            stmt.executeUpdate("""
CREATE TABLE IF NOT EXISTS scores(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    obtainedAt TEXT NOT NULL,
    value INTEGER NOT NULL,
    level STRING NOT NULL
);

CREATE TABLE IF NOT EXISTS lastFinishedLevel(
    id INTEGER UNIQUE,
    level INTEGER
);

INSERT OR IGNORE INTO lastFinishedLevel(id, level)
VALUES (1, 0);
""");
            stmt.close();
        } catch (SQLException e) {
            Utils.logException(getClass(), e, "failed to initialize tables");
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }

    public void addNewScoreEntry(String level, String time, int score) {
        try {
            var stmt = conn.prepareStatement("""
INSERT INTO scores(obtainedAt, value, level)
VALUES (?, ?, ?)
""");
            stmt.setString(1, time);
            stmt.setInt(2, score);
            stmt.setString(3, level);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            Utils.logException(getClass(), e, "failed to initialize tables");
        }
    }

    public ArrayList<ScoreEntry> getTopScoresFor(String level) {
        try {
            var stmt = conn.prepareStatement("""
SELECT obtainedAt, value, level
FROM scores
WHERE level = ?
ORDER BY value DESC
LIMIT 10
""");
            stmt.setString(1, level);
            var rs = stmt.executeQuery();

            var result = new ArrayList<ScoreEntry>();

            while (rs.next()) {
                result.add(new ScoreEntry(rs.getString(1), rs.getInt(2)));
            }

            stmt.close();

            return result;
        } catch (SQLException e) {
            Utils.logException(getClass(), e, "failed to retrieve score entries");
        }

        return null;
    }

    public void setLastBeatLevel(int level) {
        try {
            var stmt = conn.prepareStatement("""
UPDATE lastFinishedLevel
SET level = ?
WHERE level < ?
""");
            stmt.setInt(1, level);
            stmt.setInt(2, level);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Utils.logException(getClass(), e, "failed to set last beat level");
        }
    }

    public int getNextPlayableLevel() {
        try {
            var stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("""
SELECT level
FROM lastFinishedLevel
WHERE id == 1
""");
            return rs.getInt("level") + 1;
        } catch (SQLException e) {
            Utils.logException(getClass(), e, "failed to get last beat level");
        }

        return 1;
    }
}

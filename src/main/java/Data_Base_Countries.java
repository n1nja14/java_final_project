import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.sql.SQLException;

public class Data_Base_Countries {
    private static Connection connection;
    private static Statement statement;

    public Data_Base_Countries() {
    }

    // Подключение к базе данных
    public void connectDB() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:B:\\IntelliJ IDEA 2024.2.1\\java_final_project\\src\\main\\resources\\Country.db");
        statement = connection.createStatement();
    }

    public void createTables() throws SQLException {
        String createRegionsTable = "CREATE TABLE IF NOT EXISTS Regions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "region VARCHAR(50) NOT NULL);";

        String createSubregionsTable = "CREATE TABLE IF NOT EXISTS Subregions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subregion VARCHAR(50) NOT NULL, " +
                "region_id INTEGER, " +
                "FOREIGN KEY (region_id) REFERENCES Regions(id));";

        String createCountriesTable = "CREATE TABLE IF NOT EXISTS Countries (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "country VARCHAR(50) NOT NULL, " +
                "subregion_id INTEGER, " +
                "internet_users INT, " +
                "population INT, " +
                "FOREIGN KEY (subregion_id) REFERENCES Subregions(id));";

        try (var stmt = connection.createStatement()) {
            stmt.execute(createRegionsTable);
            stmt.execute(createSubregionsTable);
            stmt.execute(createCountriesTable);
        }
    }

    public void saveCountries(List<Country> countries) throws SQLException {
        // SQL-запросы для вставки
        String insertRegion = "INSERT INTO Regions (region) VALUES (?);";
        String insertSubregion = "INSERT INTO Subregions (subregion, region_id) VALUES (?, ?);";
        String insertCountry = "INSERT INTO Countries (country, subregion_id, internet_users, population) VALUES (?, ?, ?, ?);";

        for (Country country : countries) {
            int regionId = getRegionId(country.getRegion()); // Получаем идентификатор региона или создаем новый
            int subregionId = getSubregionId(country.getSubregion(), regionId); // Получаем идентификатор субрегиона или создаем новый

            // Вставляем данные о стране
            try (PreparedStatement pstmtCountry = connection.prepareStatement(insertCountry)) {
                pstmtCountry.setString(1, country.getNameCountry());
                pstmtCountry.setInt(2, subregionId);
                pstmtCountry.setInt(3, country.getInternetUsers());
                pstmtCountry.setInt(4, country.getPopulation());
                pstmtCountry.executeUpdate(); // Выполнение обновления
            }
        }
    }

    private int getRegionId(String region) throws SQLException {
        String query = "SELECT id FROM Regions WHERE region = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, region);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Возвращаем идентификатор существующего региона
            } else {
                // Если регион не найден, то добавляем его
                try (PreparedStatement insertPstmt = connection.prepareStatement("INSERT INTO Regions (region) VALUES (?);", PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insertPstmt.setString(1, region);
                    insertPstmt.executeUpdate();
                    var generatedKeys = insertPstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Возвращаем только что созданный идентификатор региона
                    }
                }
            }
        }
        return -1; // Возвращаем -1 в случае ошибки
    }

    private int getSubregionId(String subregion, int regionId) throws SQLException {
        String query = "SELECT id FROM Subregions WHERE subregion = ? AND region_id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, subregion);
            pstmt.setInt(2, regionId);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Возвращаем идентификатор существующего субрегиона
            } else {
                // Если субрегион не найден, то добавляем его
                try (PreparedStatement insertPstmt = connection.prepareStatement("INSERT INTO Subregions (subregion, region_id) VALUES (?, ?);", PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insertPstmt.setString(1, subregion);
                    insertPstmt.setInt(2, regionId);
                    insertPstmt.executeUpdate();
                    var generatedKeys = insertPstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Возвращаем только что созданный идентификатор субрегиона
                    }
                }
            }
        }
        return -1; // Возвращаем -1 в случае ошибки
    }
    public String getCountryWithLeastInternetUsersInEasternEurope() throws SQLException {
        String query = "SELECT country " +
                "FROM Countries C " +
                "JOIN Subregions S ON C.subregion_id = S.id " +
                "JOIN Regions R ON S.region_id = R.id " +
                "WHERE R.region = 'Eastern Europe' " +
                "ORDER BY C.internet_users ASC;";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.print(rs);
            if (rs.next()) {
                return rs.getString("country"); // Возвращаем название страны
            }
        }
        return null; // Возвращаем null, если страна не найдена
    }
    public HashMap<String, Double> displayCountriesWithInternetUsagePercentage() throws SQLException {
        String query = "SELECT C.country, " +
                "(C.internet_users * 100.0 / C.population) AS internet_usage_percentage " +
                "FROM Countries C " +
                "WHERE (C.internet_users * 100.0 / C.population) BETWEEN 75 AND 85;";
        HashMap<String, Double> dataset = new HashMap<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Выводим заголовок
            System.out.printf("%-30s %-25s%n", "Country", "Internet Usage Percentage");
            System.out.println("-------------------------------------------------------------");

            while (rs.next()) {
                String country = rs.getString("country");
                double internetUsagePercentage = rs.getDouble("internet_usage_percentage");

                // Выводим данные
                dataset.put(country, internetUsagePercentage);
            }
        }
        return dataset;
    }

    // Удаление данных
    public void deleteData() throws SQLException {
        statement.execute("DROP TABLE IF EXISTS Region");
        statement.execute("DROP TABLE IF EXISTS Countries");
    }

    // Чтение данных из таблицы
    public void readData() throws SQLException {
        String query = "SELECT C.country AS 'Country or area', " +
                "S.subregion AS 'Subregion', " +
                "R.region AS 'Region', " +
                "C.internet_users AS 'Internet users', " +
                "C.population AS 'Population' " +
                "FROM Countries C " +
                "JOIN Subregions S ON C.subregion_id = S.id " +
                "JOIN Regions R ON S.region_id = R.id;";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            // Выводим заголовки
            System.out.printf("%-30s %-20s %-20s %-15s %-10s%n", "Country or area", "Subregion", "Region", "Internet users", "Population");
            System.out.println("-------------------------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                String country = rs.getString("Country or area");
                String subregion = rs.getString("Subregion");
                String region = rs.getString("Region");
                int internetUsers = rs.getInt("Internet users");
                int population = rs.getInt("Population");

                // Форматируем и выводим данные
                System.out.printf("%-30s %-20s %-20s %-15d %-10d%n", country, subregion, region, internetUsers, population);
            }

        }
    }
    public List<Country> getCountriesData() throws SQLException {
        String query = "SELECT C.country AS 'Country or area', " +
                "S.subregion AS 'Subregion', " +
                "R.region AS 'Region', " +
                "C.internet_users AS 'Internet users', " +
                "C.population AS 'Population' " +
                "FROM Countries C " +
                "JOIN Subregions S ON C.subregion_id = S.id " +
                "JOIN Regions R ON S.region_id = R.id;";

        List<Country> countryDataList = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String country = rs.getString("Country or area");
                String subregion = rs.getString("Subregion");
                String region = rs.getString("Region");
                int internetUsers = rs.getInt("Internet users");
                int population = rs.getInt("Population");

                // Создаем объект CountryData и добавляем его в список
                Country countryData = new Country(country, subregion, region, internetUsers, population);
                countryDataList.add(countryData);
            }
        }

        // Преобразуем список в массив
        return countryDataList;
    }

    public void closeDb() throws SQLException {
        statement.close();
        connection.close();
    }
}
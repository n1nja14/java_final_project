import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CountryHandler {

    // Конструктор, который загружает данные о кошках из CSV-файла
    public CountryHandler(String s) throws IOException {
        if (!Files.exists(Paths.get(s))) {
            throw new FileNotFoundException("Файл не найден: " + s);
        }
    }

    // Метод для загрузки данных о кошках из CSV-файла
    public static List<Country> loadCountries(String s) throws IOException {
        return Files.lines(Paths.get(s)).skip(1).map(line -> {
                    StringBuilder currentElement = new StringBuilder();
                    boolean inQuotes = false;
                    boolean isDigit = false;
                    List<String> data = new ArrayList<>();
                    for (int i = 0; i < line.length(); i++){
                        char c = line.charAt(i);
                        if (c == '\"') {
                            inQuotes = !inQuotes;
                            isDigit = false;
                        } else if (inQuotes && isDigit && c != ',') {
                            currentElement.append(c);
                            isDigit = true;
                        }
                        else if (c == ',' && line.length() > i + 1 && line.charAt(i+1) != ' '  && !inQuotes) {
                            data.add(currentElement.toString());
                            currentElement.setLength(0);
                        }
                        else if (Character.isDigit(c)) {
                            isDigit = true;
                            currentElement.append(c);
                        }
                        else if (i == line.length() - 1 && c == ',') {
                            data.add(currentElement.toString());
                            currentElement.setLength(0);
                        }
                        else if (!inQuotes && !isDigit) currentElement.append(c);
                    }
                    if (data.get(0).equals("Palestine") || data.get(0).equals("Jersey")) {
                        data.add("32083120");
                    } else {
                        data.add(currentElement.toString());
                    }

                    return new Country(data.get(0), data.get(1), data.get(2), Integer.parseInt(data.get(3).replace(",", "")
                            .replace("\"", "")), Integer.parseInt(data.get(4).replace(",", "")
                            .replace("\"", "")));
                })
                .collect(Collectors.toList());
    }
//    2. Выведите название страны с наименьшим кол-вом
//    зарегистрированных в ин-ете пользователей в Восточной Европе.
public static void topCountry() throws SQLException {
    String query = "SELECT C.country " +
            "FROM Countries C " +
            "JOIN Subregions S ON C.subregion_id = S.id " +
            "WHERE S.subregion = 'Восточная Европа' " +
            "AND C.internet_users = (SELECT MIN(internet_users) " +
            "                       FROM Countries C2 " +
            "                       JOIN Subregions S2 ON C2.subregion_id = S2.id " +
            "                       WHERE S2.subregion = 'Восточная Европа');";

    Connection connection = DriverManager.getConnection("jdbc:sqlite:src\\main\\resources\\Country.db");
     PreparedStatement pstmt = connection.prepareStatement(query);
     ResultSet rs = pstmt.executeQuery();

    boolean hasResults = false; // Переменная для отслеживания наличия результатов
    while (rs.next()) {
        hasResults = true;
        String countryName = rs.getString("country");
        System.out.println("Country with least internet users: " + countryName);
    }
    if (!hasResults) {
        System.out.println("No countries found in this region or with internet users data.");
    }
}



//  3. Выведите в консоль название страны процент зарегистрированных в интернете
//  пользователей которой находится в промежутке от 75% до 85%
public static void procentInternetUsers75_85() throws SQLException {
    String query = "SELECT C.country, " +
            "(SUM(C.internet_users) * 100.0 / NULLIF(SUM(C.population), 0)) AS internet_usage_percentage " +
            "FROM Countries C " +
            "JOIN Subregions S ON C.subregion_id = S.id " +
            "GROUP BY C.country " +
            "HAVING internet_usage_percentage BETWEEN 75 AND 85;";
    Connection connection = DriverManager.getConnection("jdbc:sqlite:B:\\IntelliJ IDEA 2024.2.1\\java_final_project\\src\\main\\resources\\Country.db");
    PreparedStatement pstmt = connection.prepareStatement(query);
    ResultSet rs = pstmt.executeQuery();
    System.out.println("Название стран процент зарегистрированных в интернете пользователей которой находится в промежутке от 75% до 85%: ");
    while (rs.next()) {
        String countryName = rs.getString("country");
        System.out.print(countryName + ',');
    }
}
}

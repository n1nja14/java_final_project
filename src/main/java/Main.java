import org.jfree.chart.*;
import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] arg) throws IOException, SQLException {
        String path = "src/main/resources/Country.csv";
        List<Country> countries = CountryHandler.loadCountries(path);
        System.out.print(countries);

        Data_Base_Countries DB_Countries = new Data_Base_Countries();
        DB_Countries.connectDB();
        DB_Countries.createTables();
        DB_Countries.readData();
        DB_Countries.saveCountries(countries);
        System.out.print(DB_Countries.getCountryWithLeastInternetUsersInEasternEurope());
        SwingUtilities.invokeLater(() -> {
            Graph graph = new Graph(countries);
            graph.setVisible(true);
        });
        CountryHandler.topCountry();
        CountryHandler.procentInternetUsers75_85();
    }
}
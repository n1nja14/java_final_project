import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;


public class Graph extends JFrame {
    private final int DEFAULT_PADDING = 15;

    public Graph(List<Country> countries) {
        init(countries);
    }

    private void init(List<Country> countries) {
        CategoryDataset dataset = createDataset(countries);
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING));
        chartPanel.setBackground(Color.WHITE);
        add(chartPanel);
        pack();
        setTitle("Процент пользователей интернета по субрегионам");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JFreeChart createChart(CategoryDataset dataset) {
        return ChartFactory.createBarChart(
                "Процент пользователей интернета по субрегионам",
                "Субрегион",
                "Процент пользователей",
                dataset
        );
    }

    private CategoryDataset createDataset(List<Country> countries) {
        Map<String, Integer> internetUsersMap = new HashMap<>();
        Map<String, Integer> populationMap = new HashMap<>();

        for (Country country : countries) {
            String subregion = country.getSubregion();
            internetUsersMap.put(subregion, internetUsersMap.getOrDefault(subregion, 0) + country.getInternetUsers());
            populationMap.put(subregion, populationMap.getOrDefault(subregion, 0) + country.getPopulation());
        }

        var dataset = new DefaultCategoryDataset();
        for (String subregion : internetUsersMap.keySet()) {
            int internetUsers = internetUsersMap.get(subregion);
            int population = populationMap.get(subregion);
            double percentage = (double) internetUsers / population * 100;
            dataset.setValue(percentage, "Пользователи интернета", subregion);
        }

        return dataset;
    }
}
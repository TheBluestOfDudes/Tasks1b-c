package eksamenTestFX;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

public class GraphController {

    @FXML
    private MenuButton currency;

    @FXML
    private LineChart<String, Number> chart;

    private ExchangeRates rates;

    private String chosenCurrency;

    @FXML
    public void initialize(){

        try {
            URL url = new URL("https://data.norges-bank.no/api/data/EXR/B..NOK.SP?startPeriod=2019-09-27&endPeriod=2019-10-04&format=sdmx-json&locale=no");
            rates = new ExchangeRates(url);
            fillMenu();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void fillMenu(){
        String[] currencies = rates.getCurrencies();
        chosenCurrency = currencies[0];
        for(int i = 0; i < currencies.length; i++){
            MenuItem m = new MenuItem(currencies[i]);
            m.setOnAction(v ->{
                chosenCurrency = m.getText();
                currency.setText(chosenCurrency + " (" + rates.getCurrencyDescription(chosenCurrency) + ")");
                chart.getData().clear();
                fillGraph(chosenCurrency);
            });
            currency.getItems().add(m);
        }
        currency.setText(chosenCurrency + " (" + rates.getCurrencyDescription(chosenCurrency) + ")");
        fillGraph(chosenCurrency);
    }

    private void fillGraph(String curr){
        try {
            Date[] dates = rates.getDates();
            double[] values = rates.getExchangeRates(curr);

            XYChart.Series<String, Number> s = new XYChart.Series<>();
            s.setName("Observation Data for " + rates.getCurrencyDescription(curr));
            for(int i = 0; i < values.length; i++){
                s.getData().add(new XYChart.Data(dates[i].toString(), values[i]));
            }
            chart.getData().add(s);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
    }

}

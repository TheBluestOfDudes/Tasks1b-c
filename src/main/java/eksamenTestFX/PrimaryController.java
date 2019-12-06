package eksamenTestFX;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.net.MalformedURLException;
import java.net.URL;

public class PrimaryController {

    @FXML
    private TextField nokVal;

    @FXML
    private MenuButton menuButton;

    @FXML
    private TextField currenVal;

    private ExchangeRates rates;

    private static int TO = 0;
    private static int FROM = 1;
    private int exchange;

    private String chosenCurrency;

    @FXML
    public void initialize() {

        try {
            URL url = new URL("https://data.norges-bank.no/api/data/EXR/B..NOK.SP?startPeriod=2019-09-27&endPeriod=2019-10-04&format=sdmx-json&locale=no");
            rates = new ExchangeRates(url);
            fillMenu();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        exchange = TO;

        //Changes here means we're going from NOK to a different currency
        nokVal.textProperty().addListener((ObservableValue<? extends String> observableValue, String oldValue, String newValue) -> {
            if (checkNewValue(newValue)) {
                if(!newValue.equalsIgnoreCase("")){
                    exchange = TO;
                    exchangeTo(newValue, chosenCurrency);
                }
                else{
                    currenVal.setText("");
                }
            }
        });

        //Changes here means we're going from a currency to NOK
        currenVal.textProperty().addListener((ObservableValue<? extends String> observableValue, String oldValue, String newValue) -> {
            if (checkNewValue(newValue)) {
                if(!newValue.equalsIgnoreCase("")){
                    exchange = FROM;
                    exchangeFrom(newValue, chosenCurrency);
                }
                else{
                    nokVal.setText("");
                }
            }
        });
    }

    private void fillMenu() {
        String[] currencies = rates.getCurrencies();
        chosenCurrency = currencies[0];
        for (int i = 0; i < currencies.length; i++) {
            MenuItem m = new MenuItem(currencies[i]);
            m.setOnAction(v -> {
                chosenCurrency = m.getText();
                menuButton.setText(chosenCurrency + " (" + rates.getCurrencyDescription(chosenCurrency) + ")");
                if(exchange == TO && !nokVal.getText().equalsIgnoreCase("")){
                    exchangeTo(nokVal.getText(), chosenCurrency);
                }
                else if(exchange == FROM && !nokVal.getText().equalsIgnoreCase("")){
                    exchangeFrom(currenVal.getText(), chosenCurrency);
                }
            });
            menuButton.getItems().add(m);
        }
        menuButton.setText(chosenCurrency + " (" + rates.getCurrencyDescription(chosenCurrency) + ")");
    }

    private Boolean checkNewValue(String newValue) {
        boolean ok = true;
        try {
            Double.parseDouble(newValue);
        } catch (NumberFormatException|NullPointerException e) {
            ok = false;
        }
        if(newValue.equalsIgnoreCase("")){
            ok = true;
        }
        return ok;
    }

    private void exchangeTo(String value, String currency){
        Double converstion = rates.exchangeTo(Double.parseDouble(value), currency);
        currenVal.setText(converstion.toString());
    }

    private void exchangeFrom(String value, String currency){
        Double converstion = rates.exchangeFrom(Double.parseDouble(value), currency);
        nokVal.setText(converstion.toString());
    }

}

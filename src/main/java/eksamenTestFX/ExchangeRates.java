package eksamenTestFX;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class controls the logic of exchange rates
 */
public class ExchangeRates {

    private JSONObject jsObj;
    private LinkedList<Currency> currencies;
    private ArrayList<ObservationDate> observeDates;

    /**
     * Constructor laster inn medsent JSON data og fyller inn liste med valutaer
     * @param js Json data
     */
    public ExchangeRates(String js){

        String cName = "";
        String cId = "";
        int cPos = 0;

        String dStart = "";
        String dName = "";
        String dEnd = "";
        String dId = "";

        currencies = new LinkedList<>();
        observeDates = new ArrayList<>();

        jsObj = new JSONObject(js);
        JSONArray currencyArr = jsObj.getJSONObject("structure").getJSONObject("dimensions").getJSONArray("series").getJSONObject(1).getJSONArray("values");
        JSONArray dateArr = jsObj.getJSONObject("structure").getJSONObject("dimensions").getJSONArray("observation").getJSONObject(0).getJSONArray("values");
        for(Object o : currencyArr){
            cName = ((JSONObject) o).getString("name");
            cId = ((JSONObject) o).getString("id");
            cPos = ((JSONObject) o).getInt("position");
            currencies.add(new Currency(cName, cPos, cId));
        }

        for(Object o : dateArr){
            dStart = ((JSONObject)o).getString("start");
            dName = ((JSONObject) o).getString("name");
            dEnd = ((JSONObject) o).getString("end");
            dId = ((JSONObject) o).getString("id");
            observeDates.add(new ObservationDate(dStart, dName, dEnd, dId));
        }

    }

    public ExchangeRates(URL url) throws MalformedURLException {

        String json = "";

        String cName = "";
        String cId = "";
        int cPos = 0;

        String dStart = "";
        String dName = "";
        String dEnd = "";
        String dId = "";

        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String str = "";
            while ((str = br.readLine()) != null){
                json += str;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        currencies = new LinkedList<>();
        observeDates = new ArrayList<>();

        jsObj = new JSONObject(json);

        JSONArray currencyArr = jsObj.getJSONObject("structure").getJSONObject("dimensions").getJSONArray("series").getJSONObject(1).getJSONArray("values");
        JSONArray dateArr = jsObj.getJSONObject("structure").getJSONObject("dimensions").getJSONArray("observation").getJSONObject(0).getJSONArray("values");
        for(Object o : currencyArr){
            cName = ((JSONObject) o).getString("name");
            cId = ((JSONObject) o).getString("id");
            cPos = ((JSONObject) o).getInt("position");
            currencies.add(new Currency(cName, cPos, cId));
        }

        for(Object o : dateArr){
            dStart = ((JSONObject)o).getString("start");
            dName = ((JSONObject) o).getString("name");
            dEnd = ((JSONObject) o).getString("end");
            dId = ((JSONObject) o).getString("id");
            observeDates.add(new ObservationDate(dStart, dName, dEnd, dId));
        }

    }

    /**
     * Checks if a given currency exists in memory
     * @param currency The id of the currency
     * @return True/False whether it exists
     */
    public boolean currencyExists(String currency){

        boolean exists = false;

        Iterator iter = currencies.iterator();
        while (iter.hasNext()){
            Currency c = (Currency) iter.next();
            if(c.getId().equalsIgnoreCase(currency)){
                exists = true;
            }
        }

        return exists;
    }

    /**
     * Returns the full name description of the currency, given its id
     * @param currency The currency's id
     * @return The full name description of the currency
     */
    public String getCurrencyDescription(String currency){
        String result = "";
        Iterator iter = currencies.iterator();
        while (iter.hasNext()){
            Currency c = (Currency) iter.next();
            if(c.getId().equalsIgnoreCase(currency)){
                result = c.getName();
            }
        }

        return result;
    }

    /**
     * Returns an array of dates
     * @return The array of the dates
     * @throws ParseException Exception if the parsing didn't work
     */
    public Date[] getDates() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date[] dates = new Date[observeDates.size()];
        for(int i = 0; i < dates.length; i++){
            dates[i] = format.parse(observeDates.get(i).getName());
        }
        return dates;
    }

    /**
     * Returns an array of floats that show the exchange rates
     * @param currency The currency we are getting the rates from
     * @return The array of the rates
     */
    public double[] getExchangeRates(String currency){

        Iterator iter = currencies.iterator();
        int count = 0;
        double[] results = new double[observeDates.size()];

        while (iter.hasNext()){
            Currency c = (Currency)iter.next();
            if(c.getId().equalsIgnoreCase(currency)){
                JSONObject observe = jsObj.getJSONArray("dataSets").getJSONObject(0).getJSONObject("series").getJSONObject("0:" + count + ":0:0").getJSONObject("observations");
                for(Integer i = 0; i < observeDates.size(); i++){
                    JSONArray temp = observe.getJSONArray(i.toString());
                    results[i] = Double.parseDouble(temp.getString(0));
                }
            }
            count++;
        }
        return results;
    }

    /**
     * Exchanges a value of NOK to a given currency
     * @param value The amount of NOK
     * @param currency The currency we are exchanging to
     * @return The equivalent value in the exchanged currency
     */
    public double exchangeTo(double value, String currency){
        Iterator iter = currencies.iterator();
        int count = 0;
        double result = 0.0;
        int multi = 0;
        while(iter.hasNext()){
            Currency c = (Currency)iter.next();
            if(c.getId().equalsIgnoreCase(currency)){
                JSONObject currencyRate = jsObj.getJSONArray("dataSets").getJSONObject(0).getJSONObject("series").getJSONObject("0:" + count + ":0:0");
                JSONObject observe = currencyRate.getJSONObject("observations");
                JSONArray attributes = currencyRate.getJSONArray("attributes");
                JSONArray rate = observe.getJSONArray(((Integer)(observeDates.size()-1)).toString());
                if(attributes.getInt(2) == 0){
                    result = value / Double.parseDouble(rate.getString(0));
                }
                else{
                    multi = attributes.getInt(2);
                    value *= (Math.pow(10, multi));
                    result = value / ((Double.parseDouble(rate.getString(0))) / (Math.pow(10, multi)));
                }
            }
            count++;
        }
        return result;
    }

    /**
     * Exchanges a value from a given currency to NOK
     * @param value The value in the currency
     * @param currency The currency
     * @return The exchanged value in NOK
     */
    public double exchangeFrom(double value, String currency){
        Iterator iter = currencies.iterator();
        int count = 0;
        double result = 0.0;
        int multi = 0;
        while (iter.hasNext()){
            Currency c = (Currency)iter.next();
            if(c.getId().equalsIgnoreCase(currency)){
                JSONObject currencyRate = jsObj.getJSONArray("dataSets").getJSONObject(0).getJSONObject("series").getJSONObject("0:" + count + ":0:0");
                JSONObject observe = currencyRate.getJSONObject("observations");
                JSONArray attributes = currencyRate.getJSONArray("attributes");
                JSONArray rate = observe.getJSONArray(((Integer)(observeDates.size()-1)).toString());
                if(attributes.getInt(2) == 0){
                    result = value * Double.parseDouble(rate.getString(0));
                }
                else{
                    multi = attributes.getInt(2);
                    result = (value / (Math.pow(10, multi))) * (Double.parseDouble(rate.getString(0)) / (Math.pow(10, multi)));
                }
            }
            count++;
        }
        return result;
    }

    /**
     * Gets all the currency names stored in memory
     * @return An array with all the currency names
     */
    public String[] getCurrencies(){
        String[] result = new String[currencies.size()];
        for(int i = 0; i < result.length; i++){
            Currency c = currencies.get(i);
            result[i] = c.getId();
        }
        return result;
    }
}
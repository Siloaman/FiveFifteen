

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.webkit.WebChromeClient;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    

    boolean AsyncTaskOn;
    boolean ONLINE = false;
    boolean PRE_PRE_TIMESTAMP_VALID; // 3rd minute and 8th minute
    boolean PRE_TIMESTAMP_VALID; // 4th minute and 9th minute
    boolean TIMESTAMP_VALID;
    ProgressDialog pd;
    int PROCESS_STAGE = 0;

    int new_tick_count = 0;

    boolean DEMO_MODE, LIMITED_SUBSCRIPTION, FULL_SUBSCRIPTION, RICH_MAN_SUBSCRIPTION;

    boolean BEFORE_MARKET_OPEN, FIVE_PERCENT_PENNY, N_FIFTEEN_RED, FIFTEEN_PERCENT_MANY;

    MediaPlayer mediaPlayer;

    private BroadcastReceiver HTMLReceiver;
    int AutomatedTrackerMode = -404;
    int quarterHourCounter = 1;
    int CurrentMinute;

    int RowClickNumber = 0;

    Button TrackingButton;
    EditText AccountButton, CashBalanceET, StockExtras;
    TextToSpeech textToSpeech;
    String ticker_updates = "";



    List<String> momentumStocks_TickerList;
    MomentumStock momentumStock;                                                                    // an object containing all ticker data

    List<MomentumStock> momentumStocks_List, momentumStocks_List_Temp;                              // contains all MomentumStock data for all tickers for a given minute
    int momentumStocks_List_Counter;

    // every 1 min tracking
    List<MomentumStock> momentumStocks_List_1, momentumStocks_List_2, momentumStocks_List_3;        
    
    // every 5 min tracking
    List<MomentumStock> ML_5A, ML_5B, ML_5C, ML_5D, ML_5E;
    
    boolean HighYield = false;
    boolean GreenRunning = false;
    boolean RedRunning = false;
    boolean GreenTrendInterruption3 = false;
    boolean GreenTrendInterruption4 = false;



    LinkedList<List<MomentumStock>> momentumStocks_LinkedList;                                            // contains all Lists of MomentumStock over the course of the trading day

    public String[] csvValuesInfo;
    public String[] csvHeaderInfo;
    int tickerColumn = -1;
    int tickerCount = 0;

    AsyncTask MARKETWATCHSEARCH;

    // SHAREDPREFERENCES
    private static final String PREF_NAME = "my_prefs";
    private static final String ACCOUNT_KEY = "accountKey";
    private static final String STOCK_KEY = "stockKey";
    private SharedPreferences shared_prefs;
    private double Trading_AccountBalance;
    private double Trading_StockBalance;
    boolean TradeExecutionInProgress = false;
    String StockBuyInLineUp = "";
    double StockBuyInPriceLineUp = 0.00;


    


    // TABLELAYOUT + SIZES
    int textSize = 35, smallTextSize = 35, mediumTextSize = 45;



    int leftRowMargin = 5, topRowMargin = 5, rightRowMargin = 5, bottomRowMargin = 5;
    LinearLayout stockListLayout;
    TableLayout stockListTable;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);                      

        // get the VIBRATOR_SERVICE system service
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize SharedPreferences
        shared_prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
      
        // Load the saved double value
        Trading_AccountBalance = shared_prefs.getFloat(ACCOUNT_KEY, 3000);
        Trading_StockBalance = shared_prefs.getFloat(STOCK_KEY, 0);

        // TODO: SUBSCRIPTION MODE

        DEMO_MODE               = true;
        LIMITED_SUBSCRIPTION    = false;
        FULL_SUBSCRIPTION       = false;
        RICH_MAN_SUBSCRIPTION   = false;

        FIVE_PERCENT_PENNY = true;
        PRE_PRE_TIMESTAMP_VALID = false;
        PRE_TIMESTAMP_VALID = false;
        TIMESTAMP_VALID = false;

        // Initialize the containers needed to store our stock data
                    momentumStocks_TickerList = new ArrayList<String>();

                    momentumStocks_List = new ArrayList<MomentumStock>();
                    momentumStocks_List_Temp = new ArrayList<MomentumStock>();

                    momentumStocks_List_1 = new ArrayList<MomentumStock>();
                    momentumStocks_List_2 = new ArrayList<MomentumStock>();
                    momentumStocks_List_3 = new ArrayList<MomentumStock>();

                    ML_5A = new ArrayList<MomentumStock>();
                    ML_5B = new ArrayList<MomentumStock>();
                    ML_5C = new ArrayList<MomentumStock>();
                    ML_5D = new ArrayList<MomentumStock>();
                    ML_5E = new ArrayList<MomentumStock>();

                    momentumStocks_List_Counter = 0;
                    

        /* INITIALIZE THE LAYOUT */

                    // Setup Table Layout View
                    stockListLayout = (LinearLayout) findViewById(R.id.StockListLinearLayout);
                    stockListTable = (TableLayout) findViewById(R.id.main_table);
                    stockListTable.setStretchAllColumns(true);



        CashBalanceET = (EditText)findViewById(R.id.CashBalanceET);
        StockExtras = (EditText)findViewById(R.id.StockSearchExtras);



        // Initialize the Buttons
        TrackingButton = (Button)findViewById(R.id.TrackingButton);
        TrackingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(!ONLINE) {

                    ONLINE = true;
                    TrackingButton.setBackgroundColor(Color.GREEN);
                    TrackingButton.setText("ONLINE");
                    momentumStocks_List.clear();
                    AutomatedStockTracker(100);
                    //EnvironmentDiagnostics("AutoStockTrader[100]");
                    return;

                }

                if(ONLINE) {

                    ONLINE = false;
                    PROCESS_STAGE = 0;
                    TrackingButton.setBackgroundColor(Color.RED);
                    TrackingButton.setText("OFFLINE");
                    AutomatedStockTracker(-100);
                    //EnvironmentDiagnostics("AutoStockTrader[-100]");
                    return;

                }


            }

        });

        AccountButton = (EditText) findViewById(R.id.AccountButton);
        AccountButton.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(final Editable s) {

            }
        });

        // create an object textToSpeech and adding features into it
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }

                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        // loop_function()
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }

                });

            }
        });


        // GET CURRENT DATE INFO
        int timeYear = Calendar.getInstance().get(Calendar.YEAR);
        int timeMonth = Calendar.getInstance().get(Calendar.MONTH);
        int timeDayDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        String  zeroYear, zeroMonth, zeroDayDate;

        timeMonth += 1;         // for some reason.. Month starts at 0 - 11
        if(timeMonth < 10)
            zeroMonth = String.format("%02d", timeMonth);
        else
            zeroMonth = Integer.toString(timeMonth);

        if(timeDayDate < 10)
            zeroDayDate = String.format("%02d", timeDayDate);
        else
            zeroDayDate = Integer.toString(timeDayDate);

        zeroYear = Integer.toString(timeYear);



        // INITIALIZE FOLDER FOR APP
                    File FIVEFIFTEEN = new File(Environment.getExternalStorageDirectory() + "/FIVEFIFTEEN");
                    if(!FIVEFIFTEEN.exists())
                        FIVEFIFTEEN.mkdir(); //directory is created;

                    File FIVEFIFTEEN_DAILY_LOGS = new File(Environment.getExternalStorageDirectory() + "/FIVEFIFTEEN/LOGS_" + zeroMonth + "-" + zeroDayDate + "-" + zeroYear);
                    if(!FIVEFIFTEEN_DAILY_LOGS.exists())
                        FIVEFIFTEEN_DAILY_LOGS.mkdir();

                    File FIVEFIFTEEN_AFTER_HOURS_LOGS = new File(Environment.getExternalStorageDirectory() + "/FIVEFIFTEEN/FIVEFIFTEEN_AFTER_HOURS_LOGS");
                    if(!FIVEFIFTEEN_AFTER_HOURS_LOGS.exists())
                        FIVEFIFTEEN_AFTER_HOURS_LOGS.mkdir();

                    File openLOGFile = new File(Environment.getExternalStorageDirectory() + "/FIVEFIFTEEN/FiveFifteen_Log_File.csv");

                    // Intialize our Log_File in case it hasn't been created yet with dummy data starting at $3000.00
                    if(!openLOGFile.exists())
                        writeCSVFileData("TCKR", "-404.00", "0", "0.00", "3000.00", false, 1);





    }   // end of onCreate() method


    void ZacksCSVTickerList() throws FileNotFoundException {

        // GET CURRENT DATE INFO
        int timeYear = Calendar.getInstance().get(Calendar.YEAR);
        int timeMonth = Calendar.getInstance().get(Calendar.MONTH);
        int timeDayDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        String  zeroYear, zeroMonth, zeroDayDate;

        timeMonth += 1;         // for some reason.. Month starts at 0 - 11
        if(timeMonth < 10)
            zeroMonth = String.format("%02d", timeMonth);
        else
            zeroMonth = Integer.toString(timeMonth);

        if(timeDayDate < 10)
            zeroDayDate = String.format("%02d", timeDayDate);
        else
            zeroDayDate = Integer.toString(timeDayDate);

        zeroYear = Integer.toString(timeYear);

        // LOAD THE CSV DATA OR REDIRECT TO WEBSITE TO DOWNLOAD IT

        File openCSVFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/zacks_custom_screen_"
                + zeroYear + "-"
                + zeroMonth + "-"
                + zeroDayDate + ".csv");


        if(openCSVFile.exists()){

            BufferedReader br = new BufferedReader(new FileReader(openCSVFile));

            try{
                // Get Header to Determine Ticker or Any Other Value of Importance
                String csvLine;
                csvLine = br.readLine();
                csvHeaderInfo = csvLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                for(int i = 0; i < csvHeaderInfo.length; i++){
                    if(csvHeaderInfo[i].contains("Ticker") || csvHeaderInfo[i].contains("Symbol")){
                        tickerColumn = i;
                    }
                }
                // Get the remaining values from the CSV
                // Use the headerCount to grab all values from the appropriate column
                while((csvLine = br.readLine()) != null){

                    //should handle comma inside quotes
                    csvValuesInfo = csvLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    //Splitter.on(Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
                    momentumStocks_TickerList.add(csvValuesInfo[tickerColumn].replace("\"", ""));      // remove the " " attached

                    try{
                        tickerCount++;
                    } catch (Exception e) {
                        Log.e("Problem: ", e.toString());
                    }
                }

            } catch (IOException ex){
                throw new RuntimeException("Error in reading CSV file: " + ex);
            }

        }   // END OF READING ZACKS_DATE_.CSV FILE
        //EnvironmentDiagnostics("finished ZacksCSVTickerList()");

    }



    private class MarketWatchTickerList extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AsyncTaskOn = true;
            EnvironmentDiagnostics("MarketWatchTickerList onPreExecute");
        }

        @Override
        protected String doInBackground(Void... params){


            try {

                Document doc;
                String URL = "";

                // MarketWatch Version - deprecated since March 6, 2024
                

                boolean SubscriptionValid = FutureDateCancelSubscription();
                boolean PageContinue = true;
                int pageCount = 0;

                if(FIVE_PERCENT_PENNY) {
                    // April 2022 Version
                    URL = "https://www.marketwatch.com/tools/screener/stock?exchange=all&skip=" + pageCount
                            + "&orderbyfield=&direction=desc&"
                            + "visiblecolumns=Symbol&"
                            + "pricemin=0.30&pricemax=10.00&changepercentmin=30&volumemin=250000";          // formerly minimum 20% and volume minimum of 8M
                }



                if(!SubscriptionValid)
                    URL = "";

                // S&P 500 List:    https://www.slickcharts.com/sp500
                // Nasdaq List:     http://eoddata.com/stocklist/NASDAQ.htm

                momentumStocks_TickerList.clear();
                pageCount = 0;  // re-initialize to zero after every minute

                while (PageContinue){

                    doc = Jsoup.connect(URL).get();

                    Elements PageNext = doc.getElementsByTag("i");
                    for(Element PageN : PageNext){
                        if(PageN.className().contains("icon icon--angle-right next j-pagination")) {
                            PageContinue = true;    // be able to continue loop
                            pageCount += 25;        // prepare next page search query
                            break;
                        }
                        else {
                            PageContinue = false;
                        }
                    }

                    List<String> TableRowContents = new ArrayList<String>();
                    Elements tables = doc.getElementsByTag("td");

                    for (Element td : tables) {

                        // Get All Table Values
                        if (td.className().contains("j-Symbol")) {   // if(td.className().contains("aleft") && !td.className().contains("company-col"))
                            momentumStocks_TickerList.add(td.text());
                            EnvironmentDiagnostics("Ticker: " + td.text());
                        }
                    }

                    TableRowContents.clear();
                    if(FIVE_PERCENT_PENNY) {

                        URL = "https://www.marketwatch.com/tools/screener/stock?exchange=all&skip=" + pageCount
                                + "&orderbyfield=&direction=desc&"
                                + "visiblecolumns=Symbol,CompanyName,Price,NetChange,ChangePercent,Volume&"
                                + "pricemin=0.30&pricemax=10.00&changepercentmin=30&volumemin=250000";
                    }

                }
    

                momentumStocks_TickerList.clear();

                

            } catch (IOException e/*Throwable t*/) {
                e.printStackTrace();
                momentumStocks_TickerList.add("AAPL");
                EnvironmentDiagnostics("Could not access MarketWatch website");
            }

            EnvironmentDiagnostics("MarketWatchTickerList doInBackground completed");
            return "";
        }

        @Override
        protected void onPostExecute(String result){        // can result be a String[] ??
            super.onPostExecute(result);
            AsyncTaskOn = false;
            EnvironmentDiagnostics("MarketWatchTickerList onPostExecute");

            String extras = StockExtras.getText().toString().toUpperCase();

            String[] tickers = extras.split(",");
            for(int j = 0; j < tickers.length; j++){
                momentumStocks_TickerList.add(tickers[j]);
            }


            if(AutomatedTrackerMode == 100) {
                FiveMinuteInterval();
                MARKETWATCHSEARCH = new MomentumStockObjectCreator().execute();
            }
        }

    }   // end of MarketWatchTickerList
  

    // THE BIG FIND-ALL DATA PARSER
    private class MomentumStockObjectCreator extends AsyncTask<Void, Integer, String> {
        // <Params == INPUT, Progress == UPDATES PASSED TO onProgressUpdate(), Result == WHAT doInBackground RETURNS>

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            AsyncTaskOn = true;


            pd = new ProgressDialog(MainActivity.this);
            pd.setMax(momentumStocks_TickerList.size()); // Progress Dialog Max Value
            //pd.getProgress();
            pd.setMessage("Fetching Stock Stats"); // Setting Message
            pd.setTitle("TICKER TRACKER"); // Setting Title
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Horizontal
            pd.getWindow().setGravity(Gravity.BOTTOM);  // https://stackoverflow.com/questions/3392256/how-to-change-the-position-of-a-progress-dialog
            pd.show(); // Display Progress Dialog
            pd.setCancelable(true);
            //EnvironmentDiagnostics("MomentumStockObjectCreator onPreExecute");

        }

        @Override
        protected String doInBackground(Void... params){

            List<String> momentumStocks_TableValueList = new ArrayList<String>();
            momentumStocks_List.clear();

            List<String> SpanHolder = new ArrayList<String>();

            String PreMarketPercentumStr;
            double PreMarketPercentumDouble;

            String percentumStr;
            double percentumDouble;

            String currentPriceStr;
            double priceDouble;

            String volumeStr;
            int volumeInt;

            // https://www.nasdaq.com/market-activity/stocks/kala/latest-real-time-trades
            List<String> Last20TradePrices = new ArrayList<String>();
            List<Integer> Last20TradeVolume = new ArrayList<Integer>();

            String companyNameStr;

            String previousCloseStr;
            double previousCloseDouble;

            String openPriceStr;
            double openPriceDouble;

            String dayRangeStr;
            double dayRangeDoubleLow;
            double dayRangeDoubleHigh;

            Image stockChart;

            String timeStamp;


            for(int i = 0; i < momentumStocks_TickerList.size(); i++) /*main loop */ {

                // Initialize search returns to NOT FOUND
                percentumStr = "-404 (-404.00%)";
                percentumDouble = -404;

                PreMarketPercentumStr = "-404 (-404.00%)";
                PreMarketPercentumDouble = -404;

                currentPriceStr = "-404 (-404.00%)";
                priceDouble = -404;

                volumeStr = "-404 (-404.00%)";
                volumeInt = -404;
                
                companyNameStr = "";

                previousCloseStr = "-404";
                previousCloseDouble = -404;

                openPriceStr = "-404";
                openPriceDouble = -404;

                dayRangeStr = "-404";
                dayRangeDoubleLow = -404;
                dayRangeDoubleHigh = -404;


                timeStamp = "";


                String TK = momentumStocks_TickerList.get(i);
                EnvironmentDiagnostics("MSOC-ticker: " + TK);
                momentumStocks_List_Counter = i;

                String siteURL = "https://finance.yahoo.com/quote/" + TK;       // March 6, 2024 edit
                String profileURL = "https://finance.yahoo.com/quote/" + TK + "/profile";



                try {

                    // Grab Profile Details

                    Document doc = Jsoup.connect(siteURL).get();
                    Elements fin_streams = doc.getElementsByTag("fin-streamer");


                    //EnvironmentDiagnostics("about to get premarket percentage");
                    for (Element fin_stream : fin_streams) {

                        EnvironmentDiagnostics(fin_stream.text());

                        if (fin_stream.className().contains("Mstart(4px)") && fin_stream.className().contains("Fz(24px)") && fin_stream.text().contains("%")) {
                            PreMarketPercentumDouble = Double.parseDouble(fin_stream.text());
                            EnvironmentDiagnostics("Premarket Percentage: " + PreMarketPercentumDouble);
                        }


                    }

                    /* Grab Main Page Information */
                    for (Element fin_stream : fin_streams) {

                        if(fin_stream.className().contains("Fw(b)") && fin_stream.className().contains("Fz(36px)")) {
                            priceDouble = Double.parseDouble(fin_stream.text());
                            EnvironmentDiagnostics("price is: " + priceDouble); //
                        }


                        if(fin_stream.className().contains("Mstart(4px)")
                                && fin_stream.className().contains("Fz(24px)")
                                && fin_stream.text().contains("%")) {

                                int sign = 1;
                                PreMarketPercentumStr = fin_stream.text();
                                if (PreMarketPercentumStr.contains("-")) {
                                    sign = -1;
                                }
                                Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
                                Matcher m = p.matcher(PreMarketPercentumStr);
                                while (m.find()) {
                                    PreMarketPercentumDouble = Double.parseDouble(m.group(1));
                                }
                                PreMarketPercentumDouble *= (double) sign;

                                EnvironmentDiagnostics("Pre|Post-market Percentum: " + PreMarketPercentumDouble);
                        }


                        if(fin_stream.className().contains("Pstart(8px)") && fin_stream.text().contains("%")){

                            int sign = 1;
                            percentumStr = fin_stream.text();    //buffer.append(span.text());
                            if (percentumStr.contains("-")) {
                                sign = -1;
                            }
                            Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
                            Matcher m = p.matcher(percentumStr);
                            while (m.find()) {
                                percentumDouble = Double.parseDouble(m.group(1));
                            }
                            percentumDouble *= (double) sign;
                            EnvironmentDiagnostics("percentumDb: " + percentumDouble);

                        }

                    } // end of finding Percentum and Price

                    // Get all document td class names + text inside them
                    momentumStocks_TableValueList.clear();
                    Elements tables = doc.getElementsByTag("td");
                    for (Element td : tables) {
                        // Get All Table Values
                        momentumStocks_TableValueList.add(td.text());
                    }

                    // TABLED DATA FROM WEBSITE

                    // Get the volume specifically
                    for(int j = 0; j < momentumStocks_TableValueList.size(); j++){

                        if(momentumStocks_TableValueList.get(j).startsWith("Vol")) {
                            volumeStr = momentumStocks_TableValueList.get(j+1).replaceAll(",", "");    // remove commas from 1,234,567 ... else its not numerical
                            volumeInt = Integer.parseInt(volumeStr);
                            //EnvironmentDiagnostics("VolumeInt for " + TK + ": " + volumeInt);
                            break;
                        }
                    }
                    

                    // TODO: actually the Previous Close is more important for determining Percentum Change!!!
                    // Get the open price specifically
                    for(int j = 0; j < momentumStocks_TableValueList.size(); j++){

                        if(momentumStocks_TableValueList.get(j).startsWith("Open")) {
                            openPriceStr = momentumStocks_TableValueList.get(j+1).replaceAll(",", "");
                            if(!openPriceStr.contains("N/A"))
                                openPriceDouble = Double.parseDouble(openPriceStr);
                            //EnvironmentDiagnostics("openpricedb: " + openPriceDouble);
                            break;
                        }
                    }

                    // Get the previous close specifically
                    for(int j = 0; j < momentumStocks_TableValueList.size(); j++){

                        if(momentumStocks_TableValueList.get(j).startsWith("Previous")) {       // TODO: use this for V2
                            previousCloseStr = momentumStocks_TableValueList.get(j+1).replaceAll(",", "");
                            if(!previousCloseStr.contains("N/A"))
                                previousCloseDouble = Double.parseDouble(previousCloseStr);
                            //EnvironmentDiagnostics("previousclosedb: " + previousCloseDouble);
                            break;
                        }
                    }

                    // Get the day's range specifically
                    for(int j = 0; j < momentumStocks_TableValueList.size(); j++){

                        if(momentumStocks_TableValueList.get(j).startsWith("Day's")) {       // TODO: use this for V2
                            dayRangeStr = momentumStocks_TableValueList.get(j+1).replaceAll(",", "");
                            String[] Divide = dayRangeStr.split("-");
                            if(!dayRangeStr.contains("N/A")) {
                                dayRangeDoubleLow = Double.parseDouble(Divide[0]);
                                dayRangeDoubleHigh = Double.parseDouble(Divide[1]);
                                //EnvironmentDiagnostics("Highest Price for: " + TK + " is: " + dayRangeDoubleHigh);
                            }
                            break;
                        }
                    }


                } catch (IOException e/*Throwable t*/) {
                    e.printStackTrace();
                    //EnvironmentDiagnostics("error reading: " + TK);
                    return "";
                }


                MomentumStock momentumStock = new MomentumStock(momentumStocks_TickerList.get(i), priceDouble, percentumDouble, volumeInt);
                momentumStock.PreMarketPercentage = PreMarketPercentumDouble;
                momentumStock.DayRangeLow = dayRangeDoubleLow;
                momentumStock.DayRangeHigh = dayRangeDoubleHigh;
                momentumStock.PreviousClose = previousCloseDouble;
                momentumStock.Open = openPriceDouble;

                //EnvironmentDiagnostics("MomentumStock Object creation");
                momentumStocks_List.add(momentumStock);
                //EnvironmentDiagnostics("MomentumStock #" + momentumStocks_List.size() + " - added to _List");

                pd.incrementProgressBy(1); // Increment By Value Of 1
                //EnvironmentDiagnostics("Writing MomentumStock details to log file");
                writeCSVFileData(TK,
                        priceDouble + "",
                        Integer.toString(volumeInt),
                        Double.toString(percentumDouble),
                        /*momentumStock.Sector + " - Sector," + momentumStock.Industry + " - Industry" "BID: " + bidFull + " | " + "ASK: " + askFull*/"",
                        false,
                        2);

            }

            // CLEAN UP DATA LISTS WITH MOST RECENT DATA AND PREPARE momentumStocks_List_1 AND ML_5A TO HAVE FRESH DATA
            if(momentumStocks_List_3.size() != 0) {
                // at the moment this happens at 9:34, this data is from 9:31 (3 mins away)
                momentumStocks_List_3.clear();
            }


            if(momentumStocks_List_2.size() != 0){

                // at the moment this happens at 9:34, this data is from 9:32 (2 mins away)
                for(int j = 0; j < momentumStocks_List_2.size(); j++){      

                    MomentumStock MS = new MomentumStock("TICKER", 0.00, 0.00, 0);

                    MS.Ticker = momentumStocks_List_2.get(j).Ticker;
                    MS.Price = momentumStocks_List_2.get(j).Price;
                    MS.Percentage = momentumStocks_List_2.get(j).Percentage;
                    MS.Volume = momentumStocks_List_2.get(j).Volume;
                    momentumStocks_List_3.add(MS);

                }

                momentumStocks_List_2.clear();

            }

            if(momentumStocks_List_1.size() != 0){

                // at the moment this happens at 9:34, this data is from 9:33 (1 min away)
                for(int k = 0; k < momentumStocks_List_1.size(); k++){

                    MomentumStock MS = new MomentumStock("TICKER", 0.00, 0.00, 0);

                    MS.Ticker = momentumStocks_List_1.get(k).Ticker;
                    MS.Price = momentumStocks_List_1.get(k).Price;
                    MS.Percentage = momentumStocks_List_1.get(k).Percentage;
                    MS.Volume = momentumStocks_List_1.get(k).Volume;
                    momentumStocks_List_2.add(MS);

                }

                momentumStocks_List_1.clear();

            }

            if(TIMESTAMP_VALID) {

                if (ML_5E.size() != 0)
                    ML_5E.clear();

                if (ML_5D.size() != 0) {

                    // at the moment this happens at 9:34, this data is from 9:32 (2 mins away)
                    for (int j = 0; j < ML_5D.size(); j++) {      

                        MomentumStock MS = new MomentumStock("TICKER", 0.00, 0.00, 0);

                        MS.Ticker = ML_5D.get(j).Ticker;
                        MS.Price = ML_5D.get(j).Price;
                        MS.Percentage = ML_5D.get(j).Percentage;
                        MS.Volume = ML_5D.get(j).Volume;
                        ML_5E.add(MS);
                    }
                    ML_5D.clear();
                }

                if (ML_5C.size() != 0) {

                    
                    for (int j = 0; j < ML_5C.size(); j++) {      

                        MomentumStock MS = new MomentumStock("TICKER", 0.00, 0.00, 0);

                        MS.Ticker = ML_5C.get(j).Ticker;
                        MS.Price = ML_5C.get(j).Price;
                        MS.Percentage = ML_5C.get(j).Percentage;
                        MS.Volume = ML_5C.get(j).Volume;
                        ML_5D.add(MS);
                    }
                    ML_5C.clear();
                }

                if (ML_5B.size() != 0) {

                   
                    for (int j = 0; j < ML_5B.size(); j++) {      

                        MomentumStock MS = new MomentumStock("TICKER", 0.00, 0.00, 0);

                        MS.Ticker = ML_5B.get(j).Ticker;
                        MS.Price = ML_5B.get(j).Price;
                        MS.Percentage = ML_5B.get(j).Percentage;
                        MS.Volume = ML_5B.get(j).Volume;
                        ML_5C.add(MS);
                    }
                    ML_5B.clear();
                }

                if (ML_5A.size() != 0) {

                    
                    for (int j = 0; j < ML_5A.size(); j++) {     

                        MomentumStock MS = new MomentumStock("TICKER", 0.00, 0.00, 0);

                        MS.Ticker = ML_5A.get(j).Ticker;
                        MS.Price = ML_5A.get(j).Price;
                        MS.Percentage = ML_5A.get(j).Percentage;
                        MS.Volume = ML_5A.get(j).Volume;
                        ML_5B.add(MS);
                    }
                    ML_5A.clear();
                }
            }
           


            return "";
        }

        @Override
        protected void onPostExecute(String result){        // can result be a String[] ??
            super.onPostExecute(result);
            AsyncTaskOn = false;
            pd.dismiss();

            CallStockInfo();
            MomentumStockSortation();
        }

    }   // end of MomentumStockObjectCreator


    public void CallStockInfo(){

        ticker_updates = AccountButton.getText().toString().toUpperCase();

        // get quick ticker alert
        if(ticker_updates.length() > 0) {

            String speechify = "";
            for(int i = 0; i < ticker_updates.length(); i++)
                speechify += ticker_updates.charAt(i) + "-";

            for (int i = 0; i < momentumStocks_List.size(); i++) {
                if (momentumStocks_List.get(i).Ticker.equals(ticker_updates)) {

                    speechify += "priced at: ";
                    speechify += momentumStocks_List.get(i).Price + "";
                    break;
                }
            }
            textToSpeech.speak(speechify, TextToSpeech.QUEUE_FLUSH, null);



        }

    }


     @SuppressLint("SuspiciousIndentation")
     public void MomentumStockSortation(){

        //EnvironmentDiagnostics("Entered MomentumStockSortation");

        // continuation of ObjectCreator copying...
        if(momentumStocks_List_1.size() == 0){

            
            for(int i = 0; i < momentumStocks_List.size(); i++){

                MomentumStock MS = new MomentumStock("TICKER", 0.00, 0.00, 0);

                MS.Ticker = momentumStocks_List.get(i).Ticker;
                MS.Price = momentumStocks_List.get(i).Price;
                MS.Percentage = momentumStocks_List.get(i).Percentage;
                MS.Volume = momentumStocks_List.get(i).Volume;
                momentumStocks_List_1.add(MS);
                
            }
            EnvironmentDiagnostics("finished updating MSL1 with size: " + momentumStocks_List_1.size());
            EnvironmentDiagnostics("SortationMethod MSL2 size: " + momentumStocks_List_2.size());
            EnvironmentDiagnostics("SortationMethod MSL3 size: " + momentumStocks_List_3.size());


        }   // end of if(M_L_1.size() == 0)

         if(TIMESTAMP_VALID){

             if(ML_5A.size() == 0){

                 for(int i = 0; i < momentumStocks_List.size(); i++){

                     MomentumStock MS = new MomentumStock("TICKER", 0.00, 0.00, 0);

                     MS.Ticker = momentumStocks_List.get(i).Ticker;
                     MS.Price = momentumStocks_List.get(i).Price;
                     MS.Percentage = momentumStocks_List.get(i).Percentage;
                     MS.Volume = momentumStocks_List.get(i).Volume;
                     ML_5A.add(MS);
                 }
             }

             // let's eliminate all 5C-5E where 5B was missing to not skew the candle-reading
             for(int f = 0; f < ML_5A.size(); f++){

                 boolean isFound = false;

                 for(int ff = 0; ff < ML_5B.size(); ff++){
                    if(ML_5B.get(ff).Ticker.equals(ML_5A.get(f).Ticker)){
                        isFound = true;
                        break;
                    }
                 }

                 if(isFound == false){

                     // in case stock fell out of favor for 10 mins and now back on the list
                     for(int c = 0; c < ML_5C.size(); c++){
                         if(ML_5C.get(c).Ticker.equals(ML_5A.get(f).Ticker))
                             ML_5C.remove(c);
                     }

                     // in case stock fell out of favor for 15 mins and now back on the list
                     for(int d = 0; d < ML_5D.size(); d++){
                         if(ML_5D.get(d).Ticker.equals(ML_5A.get(f).Ticker))
                             ML_5D.remove(d);
                     }

                     // in case stock fell out of favor for 20 mins and now back on the list
                     for(int e = 0; e < ML_5E.size(); e++){
                         if(ML_5E.get(e).Ticker.equals(ML_5A.get(f).Ticker))
                             ML_5E.remove(e);
                     }

                 }

             }

         }  // end of TIMESTAMP_VALID


         // 100 - Percentage, 200 - Volume, 300 - Combo Sortation ... TODO: test all three
        if(DEMO_MODE)
            SortationMethodOverloading(100);    // 100 - Percentage, 200 - Volume, 300 - Combo Sortation

        if(LIMITED_SUBSCRIPTION)
            SortationMethodOverloading(200);

        if(FULL_SUBSCRIPTION)
            SortationMethodOverloading(300);

        if(RICH_MAN_SUBSCRIPTION)
            SortationMethodOverloading(400);


            stockListTable.removeAllViews();    // clear up the table data
            String finalSet = "";


            EnvironmentDiagnostics("size of list: " + momentumStocks_List.size());
            //EnvironmentDiagnostics("Inputting values for table view");
            for(int i = -1; i < momentumStocks_List.size(); i++){     // -1 is the header...

                String tckr = "", tckrlink = "", strVol = "";
                double price = 0.0, prcntm = 0.0;
                double DayHighPrice = 0.0;
                int vol = 0;

                double min3percentum = 0.00;
                double min2percentum = 0.00;
                int min3volume = 0;
                int min2volume = 0;
                int VolumeDifference = -404;
                double PriceDifference = -404;

                DecimalFormat volumeFormatter = new DecimalFormat("#,###,###");
                RowClickNumber = i;

                if(i > -1) {


                    tckr = momentumStocks_List.get(i).Ticker;
                    price = momentumStocks_List.get(i).Price;
                    prcntm = momentumStocks_List.get(i).Percentage;
                    vol = momentumStocks_List.get(i).Volume;

                    DayHighPrice = momentumStocks_List.get(i).DayRangeHigh;
                    
                    strVol = volumeFormatter.format(vol);

                    
                    tckrlink = "https://finance.yahoo.com/chart/" + tckr;

                }

                // Compare data set from previous minute with current data
                int p_ctr = -1;
                for(int p = 0; p < momentumStocks_List_2.size(); p++){

                    if(momentumStocks_List_2.get(p).Ticker.equals(tckr)) {      // https://www.geeksforgeeks.org/compare-two-strings-in-java/
                        p_ctr = p;
                        EnvironmentDiagnostics("msl2 value: " + p);
                    }

                }

                final TextView tv_ticker = new TextView(this);
                tv_ticker.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv_ticker.setGravity(Gravity.CENTER);
                tv_ticker.setPadding(5, 15, 0, 15);
                if (i == -1) {
                    tv_ticker.setText("TICKER(" + momentumStocks_TickerList.size() + ")");
                    //tv_ticker.setBackgroundColor(Color.parseColor("#f0f0f0"));
                    tv_ticker.setBackgroundColor(Color.parseColor("#545454"));
                    tv_ticker.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                    tv_ticker.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    finalSet = "<a href=" + tckrlink + ">" + tckr + "</a>";
                    tv_ticker.setBackgroundColor(Color.parseColor("#545454"));
                    if(vol != -404 && prcntm > -404.0) {
                        tv_ticker.setText(Html.fromHtml(finalSet));
                        tv_ticker.setMovementMethod(LinkMovementMethod.getInstance());
                    } else {
                        tv_ticker.setText("-----");
                    }
                    tv_ticker.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    tv_ticker.setTextColor(Color.parseColor("#FFFFFF"));
                }   // END OF TICKER COLUMN


                final TextView tv_percentum = new TextView(this);
                tv_percentum.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv_percentum.setGravity(Gravity.CENTER);
                tv_percentum.setPadding(5, 15, 0, 15);
                if (i == -1) {
                    tv_percentum.setText("PERCENTUM");
                    tv_percentum.setBackgroundColor(Color.parseColor("#545454"));
                    tv_percentum.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                    tv_percentum.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    tv_percentum.setBackgroundColor(Color.parseColor("#545454"));
                    if (vol != -404 && prcntm > -404.0) {
                        tv_percentum.setText(String.valueOf(prcntm) + "%");
                    } else {
                        tv_percentum.setText("-----");
                    }
                    tv_percentum.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    tv_percentum.setTextColor(Color.parseColor("#FFFFFF"));
                }   // END OF PERCENTUM COLUMN


                final TextView tv_price = new TextView(this);
                tv_price.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv_price.setGravity(Gravity.CENTER);
                tv_price.setPadding(5, 15, 0, 15);
                if (i == -1) {
                    tv_price.setText("PRICE");
                    tv_price.setBackgroundColor(Color.parseColor("#545454"));
                    tv_price.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                    tv_price.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    tv_price.setBackgroundColor(Color.parseColor("#545454"));
                    if(p_ctr != -1 && momentumStocks_List_2.size() > 0)
                        PriceDifference = momentumStocks_List.get(i).Price / momentumStocks_List_2.get(p_ctr).Price;
                    if (vol != -404 && prcntm > -404.0) {
                        if(PriceDifference < 1.00 && PriceDifference != -404)     // price difference not moving very bullishly
                            tv_price.setBackgroundColor(Color.parseColor("#FF3349"));
                        if(PriceDifference <= 1.02 && PriceDifference >= 1.00 && PriceDifference != -404)     // price difference not moving very bullishly
                            tv_price.setBackgroundColor(Color.parseColor("#FF6E33"));
                        tv_price.setText(String.valueOf(price));
                    } else {
                        tv_price.setText("-----");
                    }
                    tv_price.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                    tv_price.setTextColor(Color.parseColor("#FFFFFF"));
                }   // END OF PRICE COLUMN


                final TextView tv_volume = new TextView(this);
                tv_volume.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv_volume.setGravity(Gravity.CENTER);
                tv_volume.setPadding(5, 15, 0, 15);
                if (i == -1) {
                    tv_volume.setText("VOLUME");
                    tv_volume.setBackgroundColor(Color.parseColor("#545454"));
                    tv_volume.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
                    tv_volume.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    //EnvironmentDiagnostics("about to consider p_ctr: " + p_ctr);
                    if(p_ctr != -1 && momentumStocks_List_2.size() > 0) {
                        VolumeDifference = momentumStocks_List.get(i).Volume - momentumStocks_List_2.get(p_ctr).Volume;
                        //EnvironmentDiagnostics("VolumeDifference(" + tckr + "): 0-" + momentumStocks_List.get(i).Volume + " | 1-" + momentumStocks_List_2.get(p_ctr).Volume);
                    }
                    tv_volume.setBackgroundColor(Color.parseColor("#545454"));
                    if (vol != -404 && prcntm > -404.0) {
                        tv_volume.setText(String.valueOf(strVol));
                        // https://htmlcolorcodes.com/
                        if(VolumeDifference <= 275000 && VolumeDifference != -404)                 
                            tv_volume.setBackgroundColor(Color.parseColor("#FF3349"));
                        if(VolumeDifference >= 500000 && VolumeDifference < 800000 && VolumeDifference != -404)                 
                            tv_volume.setBackgroundColor(Color.parseColor("#F033FF"));
                        if(VolumeDifference >= 800000 && VolumeDifference < 1000000 && VolumeDifference != -404)                 
                            tv_volume.setBackgroundColor(Color.parseColor("#AF33FF"));
                        if(VolumeDifference >= 1000000 && VolumeDifference != -404)                                             
                            tv_volume.setBackgroundColor(Color.parseColor("#6833FF"));

                    } else {
                        tv_volume.setText("-----");
                    }
                    tv_volume.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    tv_volume.setTextColor(Color.parseColor("#FFFFFF"));
                }   // END OF VOLUME COLUMN

                

                /***********************************************************************************
                 ************************************************************************************
                 ************************************************************************************
                 ***********************************************************************************/

                final TextView tv_box_1 = new TextView(this);
                tv_box_1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv_box_1.setGravity(Gravity.CENTER);
                tv_box_1.setPadding(5, 15, 0, 15);

                tv_box_1.setBackgroundColor(Color.parseColor("#545454"));

                double Vol = VolumeDifference;
                String VolStr = "";

                if(Vol < 100000)
                    VolStr = "< 100K";
                if (Vol >= 100000 && Vol < 1000000){
                    Vol /= 1000;
                    VolStr = (int)Vol + "K";
                }
                if(Vol >= 1000000){
                    Vol /= 100000;
                    Vol = (int)Vol;
                    Vol /= 10;
                    VolStr = Vol + "M";
                }


                int five_min_diff_perc = 0;
                boolean tckr_price_found = false;

                if(TIMESTAMP_VALID && ML_5B.size() != 0){

                    for(int p = 0; p < ML_5B.size(); p++){

                        if(ML_5B.get(p).Ticker.equals(tckr)) {      
                            double five_min_diff = momentumStocks_List.get(i).Price / ML_5B.get(p).Price;
                            EnvironmentDiagnostics("5-min-diff: msl->" + momentumStocks_List.get(i).Price + " / ml5b->" + ML_5B.get(p).Price);
                            five_min_diff *= 100;
                            five_min_diff_perc = (int)five_min_diff;
                            five_min_diff_perc -= 100;
                            tckr_price_found = true;
                            break;
                        }

                    }
                }


                tckrlink = "https://www.nasdaq.com/market-activity/stocks/" + tckr + "/latest-real-time-trades";
                finalSet = "<a href=" + tckrlink + ">" + VolStr + "</a>";

                if(TIMESTAMP_VALID && ML_5B.size() != 0 && tckr_price_found) {
                    // https://www.w3schools.com/colors/colors_picker.asp
                    if(five_min_diff_perc >= 10)  // <---- THIS IS THE ONE WE WANT TO LOOK TO TRADE ON!!!
                        tv_box_1.setBackgroundColor(Color.parseColor("#009900"));
                    if(five_min_diff_perc >= 5 &&  five_min_diff_perc < 9)
                        tv_box_1.setBackgroundColor(Color.parseColor("#99cc00"));
                    if(five_min_diff_perc >= -5 &&  five_min_diff_perc < 5)
                        tv_box_1.setBackgroundColor(Color.parseColor("#cc9900"));
                    if(five_min_diff_perc > -10 &&  five_min_diff_perc < -5)
                        tv_box_1.setBackgroundColor(Color.parseColor("#ff9900"));
                    if(five_min_diff_perc <= -10)
                        tv_box_1.setBackgroundColor(Color.parseColor("#ff3300"));

                    if(five_min_diff_perc >= 8 /*|| five_min_diff_perc <= -8*/) {
                        HighYield = true;
                        TTS_Updates("HIGH_YIELD");
                    }


                    tv_box_1.setText(five_min_diff_perc + "%"/*Html.fromHtml(finalSet)*/);
                }
                else
                    tv_box_1.setText("");

                tv_box_1.setMovementMethod(LinkMovementMethod.getInstance());

                tv_box_1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv_box_1.setTextColor(Color.parseColor("#FFFFFF"));

                // END OF PERCENTAGE CHANGE (MINUTE - 2) COLUMN

                /***********************************************/


                final TextView tv_box_2 = new TextView(this);
                tv_box_2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv_box_2.setGravity(Gravity.CENTER);
                tv_box_2.setPadding(5, 15, 0, 15);

                tv_box_2.setBackgroundColor(Color.parseColor("#545454"));

                //December 2023 Edit
                EnvironmentDiagnostics("FIVE MINUTE TESTERS");

                double e_price, d_price, c_price, b_price, a_price;
                double d_e, c_d, b_c, a_b;
                boolean b_price_valid, c_price_valid, d_price_valid, e_price_valid;
                e_price = d_price = c_price = b_price = a_price = d_e = c_d = b_c = a_b = 0.00;
                b_price_valid = c_price_valid = d_price_valid = e_price_valid = false;

                if(ML_5E.size() != 0){
                    for(int e = 0; e < ML_5E.size(); e++){
                        if(ML_5E.get(e).Ticker.equals(tckr)) {
                            e_price = ML_5E.get(e).Price;
                            e_price_valid = true;
                            EnvironmentDiagnostics("e_price[" + ML_5E.get(e).Ticker + "]->[$" + ML_5E.get(e).Price + "]");
                        }
                    }
                }

                if(ML_5D.size() != 0){
                    for(int d = 0; d < ML_5D.size(); d++){
                        if(ML_5D.get(d).Ticker.equals(tckr)) {
                            d_price = ML_5D.get(d).Price;
                            d_price_valid = true;
                            EnvironmentDiagnostics("d_price[" + ML_5D.get(d).Ticker + "]->[$" + ML_5D.get(d).Price + "]");
                        }
                    }
                }

                if(ML_5C.size() != 0){
                    for(int c = 0; c < ML_5C.size(); c++){
                        if(ML_5C.get(c).Ticker.equals(tckr)) {
                            c_price = ML_5C.get(c).Price;
                            c_price_valid = true;
                            EnvironmentDiagnostics("c_price[" + ML_5C.get(c).Ticker + "]->[$" + ML_5C.get(c).Price + "]");
                        }
                    }
                }

                if(ML_5B.size() != 0){
                    for(int b = 0; b < ML_5B.size(); b++){
                        if(ML_5B.get(b).Ticker.equals(tckr)) {
                            b_price = ML_5B.get(b).Price;
                            b_price_valid = true;
                            EnvironmentDiagnostics("b_price[" + ML_5B.get(b).Ticker + "]->[$" + ML_5B.get(b).Price + "]");
                        }
                    }
                }

                if(ML_5A.size() != 0){
                    for(int a = 0; a < ML_5A.size(); a++){
                        if(ML_5A.get(a).Ticker.equals(tckr)) {
                            a_price = ML_5A.get(a).Price;
                            EnvironmentDiagnostics("a_price[" + ML_5A.get(a).Ticker + "]->[$" + ML_5A.get(a).Price + "]");
                        }
                    }
                }

                StringBuilder sb = new StringBuilder();

                if(ML_5E.size()!=0 && ML_5D.size()!=0 && e_price_valid && d_price_valid)
                    d_e = d_price / e_price;
                if(ML_5D.size()!=0 && ML_5C.size()!=0 && d_price_valid && c_price_valid)
                    c_d = c_price / d_price;
                if(ML_5C.size()!=0 && ML_5B.size()!=0 && c_price_valid && b_price_valid)
                    b_c = b_price / c_price;
                if(ML_5B.size()!=0 && ML_5A.size()!=0 && b_price_valid)
                    a_b = a_price / b_price;
                // checking only if ML_5B.size !=0 doesn't help us if a stock popped onto the list on the TIME_STAMP_VALID event
                // this would lead to ML_5A price being divided by b_price(0.00) which is INFINITY and a crash error!

                if(d_e > 1.06 && ML_5E.size()!=0 && e_price_valid)
                    sb.append("G");
                if(d_e > 1 && d_e < 1.06 && ML_5E.size()!=0 && e_price_valid)
                    sb.append("g");
                if(d_e <= 1 && d_e > 0.94 && ML_5E.size()!=0 && e_price_valid)
                    sb.append("r");
                if(d_e < 0.94 && ML_5E.size()!=0 && e_price_valid)
                    sb.append("R");

                if(c_d > 1.06 && ML_5D.size()!=0 && d_price_valid)
                    sb.append("G");
                if(c_d > 1 && c_d < 1.06 && ML_5D.size()!=0 && d_price_valid)
                    sb.append("g");
                if(c_d <= 1 && c_d > 0.94 && ML_5D.size()!=0 && d_price_valid)
                    sb.append("r");
                if(c_d < 0.94 && ML_5D.size()!=0 && d_price_valid)
                    sb.append("R");

                if(b_c > 1.06 && ML_5C.size()!=0 && c_price_valid)
                    sb.append("G");
                if(b_c > 1 && b_c < 1.06 && ML_5C.size()!=0 && c_price_valid)
                    sb.append("g");
                if(b_c <= 1 && b_c > 0.94 && ML_5C.size()!=0 && c_price_valid)
                    sb.append("r");
                if(b_c < 0.94 && ML_5C.size()!=0 && c_price_valid)
                    sb.append("R");

                if(a_b > 1.06 && ML_5B.size()!=0 && b_price_valid)
                    sb.append("G");
                if(a_b > 1 && a_b < 1.06 && ML_5B.size()!=0 && b_price_valid)
                    sb.append("g");
                if(a_b <= 1 && a_b > 0.94 && ML_5B.size()!=0 && b_price_valid)
                    sb.append("r");
                if(a_b < 0.94 && ML_5B.size()!=0 && b_price_valid)
                    sb.append("R");

                /* ADD THE 3RD ROW OF DETAILS BASED ON 5 MINUTE INFORMATION */

                                    final TextView tv_box_5 = new TextView(this);
                                    tv_box_5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                            TableRow.LayoutParams.WRAP_CONTENT));
                                    tv_box_5.setGravity(Gravity.CENTER);
                                    tv_box_5.setPadding(5, 15, 0, 15);
                                    tv_box_5.setBackgroundColor(Color.parseColor("#545454"));
                                    tv_box_5.setTextColor(Color.parseColor("#FFFFFF"));

                                                    if(d_e > 1.06 && ML_5E.size()!=0 && e_price_valid){
                                                        tv_box_5.setText(d_price + "");
                                                        tv_box_5.setBackgroundColor(Color.parseColor("#228B22"));
                                                    } else if(d_e > 1 && d_e < 1.06 && ML_5E.size()!=0 && e_price_valid){
                                                        tv_box_5.setText(d_price + "");
                                                        tv_box_5.setBackgroundColor(Color.parseColor("#32CD32"));
                                                    } else if(d_e <= 1 && d_e > 0.94 && ML_5E.size()!=0 && e_price_valid){
                                                        tv_box_5.setText(d_price + "");
                                                        tv_box_5.setBackgroundColor(Color.parseColor("#FF6347"));
                                                    } else if(d_e < 0.94 && ML_5E.size()!=0 && e_price_valid){
                                                        tv_box_5.setText(d_price + "");
                                                        tv_box_5.setBackgroundColor(Color.parseColor("#AE0C00"));
                                                    } else {
                                                        tv_box_5.setText("---");
                                                        tv_box_5.setBackgroundColor(Color.parseColor("#545454"));
                                                    }

                                    final TextView tv_box_6 = new TextView(this);
                                    tv_box_6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                            TableRow.LayoutParams.WRAP_CONTENT));
                                    tv_box_6.setGravity(Gravity.CENTER);
                                    tv_box_6.setPadding(5, 15, 0, 15);
                                    tv_box_6.setBackgroundColor(Color.parseColor("#545454"));
                                    tv_box_6.setTextColor(Color.parseColor("#FFFFFF"));

                                                    if(c_d > 1.06 && ML_5D.size()!=0 && d_price_valid){
                                                        tv_box_6.setText(c_price + "");
                                                        tv_box_6.setBackgroundColor(Color.parseColor("#228B22"));
                                                    } else if(c_d > 1 && c_d < 1.06 && ML_5D.size()!=0 && d_price_valid){
                                                        tv_box_6.setText(c_price + "");
                                                        tv_box_6.setBackgroundColor(Color.parseColor("#32CD32"));
                                                    } else if(c_d <= 1 && c_d > 0.94 && ML_5D.size()!=0 && d_price_valid){
                                                        tv_box_6.setText(c_price + "");
                                                        tv_box_6.setBackgroundColor(Color.parseColor("#FF6347"));
                                                    } else if(c_d < 0.94 && ML_5D.size()!=0 && d_price_valid){
                                                        tv_box_6.setText(c_price + "");
                                                        tv_box_6.setBackgroundColor(Color.parseColor("#AE0C00"));
                                                    } else {
                                                        tv_box_6.setText("---");
                                                        tv_box_6.setBackgroundColor(Color.parseColor("#545454"));
                                                    }

                                    final TextView tv_box_7 = new TextView(this);
                                    tv_box_7.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                            TableRow.LayoutParams.WRAP_CONTENT));
                                    tv_box_7.setGravity(Gravity.CENTER);
                                    tv_box_7.setPadding(5, 15, 0, 15);
                                    tv_box_7.setBackgroundColor(Color.parseColor("#545454"));
                                    tv_box_7.setTextColor(Color.parseColor("#FFFFFF"));

                                                    if(b_c > 1.06 && ML_5C.size()!=0 && c_price_valid){
                                                        tv_box_7.setText(b_price + "");
                                                        tv_box_7.setBackgroundColor(Color.parseColor("#228B22"));
                                                    } else if(b_c > 1 && b_c < 1.06 && ML_5C.size()!=0 && c_price_valid){
                                                        tv_box_7.setText(b_price + "");
                                                        tv_box_7.setBackgroundColor(Color.parseColor("#32CD32"));
                                                    } else if(b_c <= 1 && b_c > 0.94 && ML_5C.size()!=0 && c_price_valid){
                                                        tv_box_7.setText(b_price + "");
                                                        tv_box_7.setBackgroundColor(Color.parseColor("#FF6347"));
                                                    } else if(b_c < 0.94 && ML_5C.size()!=0 && c_price_valid){
                                                        tv_box_7.setText(b_price + "");
                                                        tv_box_7.setBackgroundColor(Color.parseColor("#AE0C00"));
                                                    } else {
                                                        tv_box_7.setText("---");
                                                        tv_box_7.setBackgroundColor(Color.parseColor("#545454"));
                                                    }

                                    final TextView tv_box_8 = new TextView(this);
                                    tv_box_8.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                            TableRow.LayoutParams.WRAP_CONTENT));
                                    tv_box_8.setGravity(Gravity.CENTER);
                                    tv_box_8.setPadding(5, 15, 0, 15);
                                    tv_box_8.setBackgroundColor(Color.parseColor("#545454"));
                                    tv_box_8.setTextColor(Color.parseColor("#FFFFFF"));

                                                    if(a_b > 1.06 && ML_5B.size()!=0 && b_price_valid){
                                                        tv_box_8.setText(a_price + "");
                                                        tv_box_8.setBackgroundColor(Color.parseColor("#228B22"));
                                                    } else if(a_b > 1 && a_b < 1.06 && ML_5B.size()!=0 && b_price_valid){
                                                        tv_box_8.setText(a_price + "");
                                                        tv_box_8.setBackgroundColor(Color.parseColor("#32CD32"));
                                                    } else if(a_b <= 1 && a_b > 0.94 && ML_5B.size()!=0 && b_price_valid){
                                                        tv_box_8.setText(a_price + "");
                                                        tv_box_8.setBackgroundColor(Color.parseColor("#FF6347"));
                                                    } else if(a_b < 0.94 && ML_5B.size()!=0 && b_price_valid){
                                                        tv_box_8.setText(a_price + "");
                                                        tv_box_8.setBackgroundColor(Color.parseColor("#AE0C00"));
                                                    } else {
                                                        tv_box_8.setText("---");
                                                        tv_box_8.setBackgroundColor(Color.parseColor("#545454"));
                                                    }

                EnvironmentDiagnostics("d_e: " + d_e);
                EnvironmentDiagnostics("c_d: " + c_d);
                EnvironmentDiagnostics("b_c: " + b_c);
                EnvironmentDiagnostics("a_b: " + a_b);

                if(PRE_PRE_TIMESTAMP_VALID) {

                    if (a_b > 1 && b_c > 1 && c_d > 1)                  // [?]ggg
                        if(price < a_price) {
                            GreenTrendInterruption3 = true;

                            // todo:  determine yield size
                            if((price / a_price) >= 0.94){

                            } else {

                            }

                            tv_ticker.setBackgroundColor(Color.parseColor("#EBEDEF")); // #475059
                        }

                }

                if(PRE_TIMESTAMP_VALID) {       // todo: need to test this still (Feb 10, 2024)

                    if (a_b > 1 && b_c > 1 && c_d > 1)                  // [?]ggg
                        if(price < a_price) {
                            GreenTrendInterruption4 = true;
                            // todo:  determine yield size
                            if((price / a_price) >= 0.94){

                            } else {

                            }

                            tv_ticker.setBackgroundColor(Color.parseColor("#EBEDEF")); // #475059
                        }

                }

                if(TIMESTAMP_VALID) {
                            // SharedPreferences Buy-ins [ Solitary Stout Reds / Green Runs / Positive Assumption Lines ]

                            if (a_b > 1 && b_c > 1 && c_d > 1)                  // rggg
                                GreenRunning = true;

                            if(a_b <= 1 && b_c <= 1 && c_d <= 1 && a_b > 0 && b_c > 0 && c_d > 0)     // grrr     all values initialized to 0.00 so this triggers on app open (if TIMESTAMP_VALID)
                                //RedRunning = true;

                            if (a_b < 1 && a_b > 0.94 && b_c > 1 && c_d > 1 && d_e > 1) {   // [G/g][G/g][G/g][r] for the solitary stout reds...

                                if (b_c > 1.06 || c_d > 1.06 && !TradeExecutionInProgress) {

                                    StockBuyInLineUp = tckr;
                                    //TradeExecutionInProgress = true;        // the first stock in list to activate will be the one paid attention to...

                                    double buy_price_calc = (b_price - a_price) + b_price;      // ($2.15 - $2.00 = $0.15) + $2.15 = $2.30
                                    StockBuyInPriceLineUp = buy_price_calc;

                                    EnvironmentDiagnostics("Solitary Stout Red - Purchase Decision: " + tckr + " at price of: " + buy_price_calc);

                                }

                            }

                }







                                            // Determine how far below the Day High the Current Price resides...
                                            DecimalFormat percentageFormat = new DecimalFormat("#.##");
                                            double PriceOverDayHigh = 0.00;

                                            PriceOverDayHigh = price / DayHighPrice;
                                            PriceOverDayHigh *= 100;
                                            percentageFormat.format(PriceOverDayHigh);

                                            int HundredPercentValue = 0;
                                            HundredPercentValue = (int)PriceOverDayHigh;

                                            tv_box_2.setText(HundredPercentValue + "%");
                                            //tv_box_2.setText(sb);

                                            if(HundredPercentValue >= 95 && VolumeDifference >= 700000 && VolumeDifference < 1000000 && prcntm >= 35.0)
                                                tv_box_2.setBackgroundColor(Color.parseColor("#AF33FF"));
                                            if(HundredPercentValue >= 95 && VolumeDifference >= 1000000 && prcntm >= 35.0)
                                                tv_box_2.setBackgroundColor(Color.parseColor("#6833FF"));

                int TimeOfDay = TimeOfDayRanger();

                tv_box_2.setTextSize(TypedValue.COMPLEX_UNIT_PX, mediumTextSize);
                tv_box_2.setTextColor(Color.parseColor("#FFFFFF"));
                // END OF PERCENTAGE CHANGE (MINUTE - 1) COLUMN

                /***********************************************/


                final TextView tv_box_3 = new TextView(this);
                tv_box_3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv_box_3.setGravity(Gravity.CENTER);
                tv_box_3.setPadding(5, 15, 0, 15);

                tv_box_3.setBackgroundColor(Color.parseColor("#545454"));

               
                int SharesBuyable = 0;
                double fivePercentAbove = 0.00;
                double cashBalanceDb = 0.00;
                double maxBuyableDb = 0.00;

                String cashBalanceStr = CashBalanceET.getText().toString();

                if(!cashBalanceStr.isEmpty()) {
                    try {
                        cashBalanceDb = Double.parseDouble(cashBalanceStr);
                    } catch (Throwable t) {
                        // this means it is not double
                        t.printStackTrace();
                    }
                }

                maxBuyableDb = cashBalanceDb * 0.93;
                //if(TIMESTAMP_VALID)
                //    price += fifty_perc_above;
                if(a_price != 0.00)
                    maxBuyableDb /= a_price;                // originally 'price' but we want to see if we can catch closing price before small red candle
                SharesBuyable = (int)maxBuyableDb;
                double SBD = SharesBuyable /= 5;
                SBD *= 5;
                SharesBuyable = (int)SBD;

                tv_box_3.setText(SharesBuyable + "");
                //tv_box_3.setText("---");

                tv_box_3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv_box_3.setTextColor(Color.parseColor("#FFFFFF"));
                // END OF VOLUME DIFF (MINUTE - 2) COLUMN

                /*************************************************/


                final TextView tv_box_4 = new TextView(this);
                tv_box_4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv_box_4.setGravity(Gravity.CENTER);
                tv_box_4.setPadding(5, 15, 0, 15);

                tv_box_4.setBackgroundColor(Color.parseColor("#545454"));

                tv_box_4.setText(VolStr);

                tv_box_4.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tv_box_4.setTextColor(Color.parseColor("#FFFFFF"));
                // END OF VOLUME DIFF (MINUTE - 1) COLUMN

                /***********************************************/

                // add table row 1 - Ticker, Percentage, Volume
                final TableRow tr1 = new TableRow(this);
                tr1.setId(i + 1);
                TableLayout.LayoutParams tr1_Params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                tr1_Params.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
                tr1.setPadding(0,0,0,0);
                tr1.setLayoutParams(tr1_Params);
                tr1.addView(tv_ticker);
                tr1.addView(tv_percentum);
                tr1.addView(tv_price);
                tr1.addView(tv_volume);


                // add table row 2 - Percentage Change 2 mins ago, Percentage Change 1 min ago, Volume Change Diff 2 mins ago, Volume Change Diff 1 mins ago
                final TableRow tr2 = new TableRow(this);
                tr2.setId(i + 1);
                TableLayout.LayoutParams tr2_Params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                tr2_Params.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
                tr2.setPadding(0,0,0,0);
                tr2.setLayoutParams(tr2_Params);
                tr2.addView(tv_box_1);
                tr2.addView(tv_box_2);
                tr2.addView(tv_box_3);
                tr2.addView(tv_box_4);


                // add table row 3 - Price End 20 mins ago, Price End 15 mins ago, Price End 10 mins ago, Price End 5 mins ago
                final TableRow tr3 = new TableRow(this);
                tr3.setId(i + 1);
                TableLayout.LayoutParams tr3_Params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                tr3_Params.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
                tr3.setPadding(0,0,0,0);
                tr3.setLayoutParams(tr3_Params);
                tr3.addView(tv_box_5);
                tr3.addView(tv_box_6);
                tr3.addView(tv_box_7);
                tr3.addView(tv_box_8);


                stockListTable.addView(tr1, tr1_Params);
                if(i > -1) {
                    stockListTable.addView(tr2, tr2_Params);
                    stockListTable.addView(tr3, tr3_Params);
                }


                if (i > -1) {
                    // add separator row
                    final TableRow trSep = new TableRow(this);
                    TableLayout.LayoutParams trParamsSep = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT);
                    trParamsSep.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
                    trSep.setLayoutParams(trParamsSep);
                    TextView tvSep = new TextView(this);
                    TableRow.LayoutParams tvSepLay = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT);
                    tvSepLay.span = 4;
                    tvSep.setLayoutParams(tvSepLay);
                    tvSep.setBackgroundColor(Color.parseColor("#d9d9d9"));
                    tvSep.setHeight(10);
                    trSep.addView(tvSep);
                    stockListTable.addView(trSep, trParamsSep);
                }

            } // end of SORTATION INTO TABLE


         if(GreenTrendInterruption3) {

             GreenTrendInterruption3 = false;
             ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(3000);
             TTS_Updates("min3");
         }

         if(GreenTrendInterruption4) {

             GreenTrendInterruption4 = false;
             ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(3000);
             TTS_Updates("min4");
         }


         if(GreenRunning) {
             HighYield = GreenRunning = false;
             ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(2500);
             TTS_Updates("GREEN_DEVELOPMENT");
         }
        /*
         if(RedRunning) {
             RedRunning = false;
             ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(2500);
             TTS_Updates("RED_DEVELOPMENT");
         }
         */

        PROCESS_STAGE = 1;

        // Get the minute

        if(CurrentMinute == 4)
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(2500);
            //TTS_Updates("FOURTH_MINUTE");

        if(CurrentMinute == 9)
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(2500);
            //TTS_Updates("NINTH_MINUTE");

        if(momentumStocks_List.size() > new_tick_count)
            //TTS_Updates("NEW_TICKER_AVAILABLE");



        new_tick_count = momentumStocks_List.size();



    }   // end of MomentumStockSortation() function


    public void SortationMethodOverloading(int mode){

        //EnvironmentDiagnostics("Inside SortationMethodOverloading...");


        if(mode >= 100){    // Demo Mode

            Collections.sort(momentumStocks_List, new Comparator<MomentumStock>() {

                public int compare(MomentumStock MS1, MomentumStock MS2) {

                    return Double.valueOf(MS2.Percentage).compareTo(MS1.Percentage);    // Sort by percentage (highest to lowest)

                }
            });     // all Lists have been sorted now...

            writeCSVFileData("Sorted By Percentage...", "", "", "", "", false, 5);

            for(int SBP = 0; SBP < momentumStocks_List.size(); SBP++)
                writeCSVFileData(momentumStocks_List.get(SBP).Ticker, momentumStocks_List.get(SBP).Percentage + "", "", "", "", false, 5);

        }

        if(mode >= 200){    // Limited Subscription

            Collections.sort(momentumStocks_List, new Comparator<MomentumStock>() {

                public int compare(MomentumStock MS1, MomentumStock MS2) {

                    return Integer.valueOf(MS2.Volume).compareTo(MS1.Volume);         // Sort by volume (highest to lowest)

                }
            });     // all Lists have been sorted now...

            writeCSVFileData("Sorted By Volume...", "", "", "", "", false, 5);

            for(int SBV = 0; SBV < momentumStocks_List.size(); SBV++)
                writeCSVFileData(momentumStocks_List.get(SBV).Ticker, momentumStocks_List.get(SBV).Volume + "", "", "", "", false, 5);

        }


        if(mode >= 300){    // Full Subscribers

            // Best formula so far: it prioritizes the stocks with the least amount of fallback from the Day's Highest Price and then the highest percentage increase secondly
            // This should work SUPERBLY in choosing the best stock at 9:46 AM...


            Collections.sort(momentumStocks_List, new Comparator<MomentumStock>() {

                public int compare(MomentumStock MS1, MomentumStock MS2) {

                    return Double.valueOf((MS2.Price / MS2.DayRangeHigh) * (MS2.DayRangeLow / MS2.Open)).compareTo((MS1.Price / MS1.DayRangeHigh) * (MS1.DayRangeLow / MS1.Open));

                }
            });     // all Lists have been sorted now...

            writeCSVFileData("Sorted By (Price/DayHigh) / (DayLow/Open)", "", "", "", "", false, 5);

            for(int SBPMDHTDLOO = 0; SBPMDHTDLOO < momentumStocks_List.size(); SBPMDHTDLOO++)
                writeCSVFileData(momentumStocks_List.get(SBPMDHTDLOO).Ticker,
                        ((momentumStocks_List.get(SBPMDHTDLOO).Price / momentumStocks_List.get(SBPMDHTDLOO).DayRangeHigh) * (momentumStocks_List.get(SBPMDHTDLOO).DayRangeLow / momentumStocks_List.get(SBPMDHTDLOO).Open)) + "", "Price: " + momentumStocks_List.get(SBPMDHTDLOO).Price, "DayHigh: " + momentumStocks_List.get(SBPMDHTDLOO).DayRangeHigh + " | DayLow: " + momentumStocks_List.get(SBPMDHTDLOO).DayRangeLow, "Previous Close: " + momentumStocks_List.get(SBPMDHTDLOO).PreviousClose + " | Open: " + momentumStocks_List.get(SBPMDHTDLOO).Open, false, 5);




            Collections.sort(momentumStocks_List, new Comparator<MomentumStock>() {

                public int compare(MomentumStock MS1, MomentumStock MS2) {

                    // Sorted By Price Minus Day High Over Current Percentage Factor...
                    //return Double.valueOf(((MS2.Price - MS2.DayRangeHigh) - 0.00001) / (MS2.Price / MS2.PreviousClose)).compareTo((MS1.Price - MS1.DayRangeHigh) / (MS1.Price / MS1.PreviousClose));    // Minus 0.00001 because a perfect numerator is ZERO... ZERO DIVIDED BY ANYTHIGN IS STILL ZERO AND THE PERCENTAGE DENOMINATOR HAS NO EFFECT ON PLACEMENT
                    double one_percentage_MS2 = 0.00;
                    double one_percentage_MS1 = 0.00;

                    if(MS2.Price/MS2.DayRangeHigh == 1)
                        one_percentage_MS2 += MS2.Percentage/100;

                    if(MS1.Price/MS1.DayRangeHigh == 1)
                        one_percentage_MS1 += MS1.Percentage/100;

                    // NOTE: if several stocks are at their max price of the day, they will all be valued at 1.
                    // I am simply tacking on their current percentage level as the next qualifier to sort them...


                    return Double.valueOf((MS2.Price / MS2.DayRangeHigh) + one_percentage_MS2).compareTo((MS1.Price / MS1.DayRangeHigh) + one_percentage_MS1);

                }
            });     // all Lists have been sorted now...

            writeCSVFileData("Sorted By Price / DayRangeHigh + %", "", "", "", "", false, 5);

            for(int SBPMDHOP = 0; SBPMDHOP < momentumStocks_List.size(); SBPMDHOP++)
                writeCSVFileData(momentumStocks_List.get(SBPMDHOP).Ticker,
                        (momentumStocks_List.get(SBPMDHOP).Price / momentumStocks_List.get(SBPMDHOP).DayRangeHigh) + "", "Price: " + momentumStocks_List.get(SBPMDHOP).Price, "DayHigh: " + momentumStocks_List.get(SBPMDHOP).DayRangeHigh, "Percentage: " + momentumStocks_List.get(SBPMDHOP).Percentage, false, 5);





/*
            Collections.sort(momentumStocks_List, new Comparator<MomentumStock>() {

                public int compare(MomentumStock MS1, MomentumStock MS2) {

                    return Double.valueOf((MS2.Price / MS2.DayRangeHigh) * (MS2.Open / MS2.DayRangeLow)).compareTo((MS1.Price / MS1.DayRangeHigh) * (MS1.Open / MS1.DayRangeLow));

                }
            });     // all Lists have been sorted now...

            writeCSVFileData("Sorted By Price Over DayHigh Over Open Over DayLow", "", "", "", "", false, 5);

            for(int SBPMDHTOODL = 0; SBPMDHTOODL < momentumStocks_List.size(); SBPMDHTOODL++)
                writeCSVFileData(momentumStocks_List.get(SBPMDHTOODL).Ticker,
                        ((momentumStocks_List.get(SBPMDHTOODL).Price / momentumStocks_List.get(SBPMDHTOODL).DayRangeHigh) * (momentumStocks_List.get(SBPMDHTOODL).Open / momentumStocks_List.get(SBPMDHTOODL).DayRangeLow)) + "", "Price: " + momentumStocks_List.get(SBPMDHTOODL).Price, "DayHigh: " + momentumStocks_List.get(SBPMDHTOODL).DayRangeHigh + " | DayLow: " + momentumStocks_List.get(SBPMDHTOODL).DayRangeLow, "Previous Close: " + momentumStocks_List.get(SBPMDHTOODL).PreviousClose + " | Open: " + momentumStocks_List.get(SBPMDHTOODL).Open, false, 5);

*/
        }



        if(mode >= 400) {    // Full Subscribers



        }







    }

    void TTS_Updates(String Speech) {

        // https://ttsmp3.com/ --> Castilian Spanish (LUCIA)

        if(Speech.contains("min3")){
            MediaPlayer song = MediaPlayer.create(this, R.raw.g_trend_down_min3);
            song.start();
        }
        if(Speech.contains("min4")){
            MediaPlayer song = MediaPlayer.create(this, R.raw.g_trend_down_min4);
            song.start();
        }

        if(Speech.contains("NEW_TICKER_AVAILABLE")){
            MediaPlayer song = MediaPlayer.create(this, R.raw.new_ticker);
            song.start();
        }
        if(Speech.contains("FOURTH_MINUTE")){
            MediaPlayer song = MediaPlayer.create(this, R.raw.now_is_fourth_minute);
            song.start();
        }
        if(Speech.contains("NINTH_MINUTE")){
            MediaPlayer song = MediaPlayer.create(this, R.raw.now_is_ninth_minute);
            song.start();
        }
        if(Speech.contains("HIGH_YIELD")){
            MediaPlayer song = MediaPlayer.create(this, R.raw.tts_high_yield);
            song.start();
        }
        if(Speech.contains("GREEN_DEVELOPMENT")){
            // https://www.bing.com/search?q=translate+%27green+trend%27+into+korean
            MediaPlayer song = MediaPlayer.create(this, R.raw.tts_green_trend);
            song.start();
        }
        if(Speech.contains("RED_DEVELOPMENT")){
            // https://www.bing.com/search?q=translate+%27green+trend%27+into+korean
            MediaPlayer song = MediaPlayer.create(this, R.raw.tts_red_trend);
            song.start();
        }



    }


    static void writeCSVFileData(String Ticker, String Price, String Shares, String AssetBalance, String CashBalance, boolean Active, int LogType) {

        /*
        CSV Set Up:

        Purchase:
            Ticker, Purchase Price, Shares, Date Bought, Asset Balance, Cash Balance, StillinPlay? (Y/N)
            AKER, 0.35, 2300, 2018-11-21, 805.00, 21.00, true
            While in play, no other transactions will be made

        Sale:
            Ticker, Sold Price, Shares, Date Sold, Asset Balance, Cash Balance, StillinPlay? (Y/N)
            AKER, 0.38, 0, 2018-11-21, 0.00, 895.00, false
            While out of play, new transactions can be made

            writeCSVFileData("AKER", 0.35, 2300, 805.00, 21.00, true, 1);
        */

        // GET CURRENT DATE INFO

        int timeYear = Calendar.getInstance().get(Calendar.YEAR);
        int timeMonth = Calendar.getInstance().get(Calendar.MONTH);
        int timeDayDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        String  zeroYear, zeroMonth, zeroDayDate;

        timeMonth += 1;         // for some reason.. Month starts at 0 - 11
        if(timeMonth < 10)
            zeroMonth = String.format("%02d", timeMonth);
        else
            zeroMonth = Integer.toString(timeMonth);

        if(timeDayDate < 10)
            zeroDayDate = String.format("%02d", timeDayDate);
        else
            zeroDayDate = Integer.toString(timeDayDate);

        zeroYear = Integer.toString(timeYear);

        if(timeMonth == 0)
            zeroMonth = "January";
        if(timeMonth == 1)
            zeroMonth = "February";
        if(timeMonth == 2)
            zeroMonth = "March";
        if(timeMonth == 3)
            zeroMonth = "April";
        if(timeMonth == 4)
            zeroMonth = "May";
        if(timeMonth == 5)
            zeroMonth = "June";
        if(timeMonth == 6)
            zeroMonth = "July";
        if(timeMonth == 7)
            zeroMonth = "August";
        if(timeMonth == 8)
            zeroMonth = "September";
        if(timeMonth == 9)
            zeroMonth = "October";
        if(timeMonth == 10)
            zeroMonth = "November";
        if(timeMonth == 11)
            zeroMonth = "December";

        /******************************************************************************************/

        String DAILY_GRAPHING_FILENAME = zeroMonth + "-" + zeroDayDate + "-" + zeroYear + ".csv";
        String DAILY_GRAPHING_FILENAME_MW = "" + Ticker + "-" + zeroMonth + "-" + zeroDayDate + "-" + zeroYear + "_MW.csv";
        String DAILY_GRAPHING_FILENAME_YF = "" + Ticker + "-" + zeroMonth + "-" + zeroDayDate + "-" + zeroYear + "_YF.csv";
        String LOG_FILENAME = "FiveFifteen_Log_File.csv";
        String ENVIRONMENT_DIAGNOSTICS_FILENAME = "FiveFifteen_Environment_Diagnostics.csv";
        String TEMP_LOG_FILENAME = "FiveFifteen_Temp_Log_File.csv";

        String TOP_TEN_FILENAME = "FiveFifteen_TopTenByMinute.csv";

        String IsActive = "---";
        if(Active == true)
            IsActive = "YES";
        else
            IsActive = "NO";

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date DateStamp = new Date();

        String LOG_ENTRY =      Ticker
                + ","
                + DateStamp
                + ","
                + Price
                + ","
                + Shares
                /*+ ","
                + AssetBalance
                + ","
                + CashBalance
                + ","
                + IsActive*/
                + "\r\n";

        try{

            File ROOT = new File(Environment.getExternalStorageDirectory() + "/FIVEFIFTEEN");
            File ROOT_DAILY = new File(Environment.getExternalStorageDirectory() + "/FIVEFIFTEEN/LOGS_" + zeroMonth + "-" + zeroDayDate + "-" + zeroYear);

            // LOGTYPES:    1 = Buy/Sell Transactions Log       2 = Minutely Data Record     3 = Environment Diagnostics


            if(LogType == 1) {
                File LOG_FILE = new File(ROOT, LOG_FILENAME);
                FileWriter writer = new FileWriter(LOG_FILE, true);
                writer.append(LOG_ENTRY);
                writer.flush();
                writer.close();
            }


            if(LogType == 2) {
                File LOG_FILE = new File(ROOT_DAILY, DAILY_GRAPHING_FILENAME_YF);
                FileWriter writer = new FileWriter(LOG_FILE, true);
                writer.append(LOG_ENTRY);
                writer.flush();
                writer.close();

                LOG_FILE = new File(ROOT_DAILY, DAILY_GRAPHING_FILENAME);
                writer = new FileWriter(LOG_FILE, true);
                writer.append(LOG_ENTRY);
                writer.flush();
                writer.close();
            }


            if(LogType == 3) {
                File LOG_FILE = new File(ROOT, ENVIRONMENT_DIAGNOSTICS_FILENAME);
                FileWriter writer = new FileWriter(LOG_FILE, true);
                writer.append(LOG_ENTRY);
                writer.flush();
                writer.close();
            }


            if(LogType == 4) {
                File LOG_FILE = new File(ROOT, TEMP_LOG_FILENAME);
                FileWriter writer = new FileWriter(LOG_FILE, true);
                writer.append(LOG_ENTRY);
                writer.flush();
                writer.close();
            }

            if(LogType == 5) {
                File LOG_FILE = new File(ROOT, TOP_TEN_FILENAME);
                FileWriter writer = new FileWriter(LOG_FILE, true);
                writer.append(LOG_ENTRY);
                writer.flush();
                writer.close();
            }

            if(LogType == 8) {
                File LOG_FILE = new File(ROOT_DAILY, DAILY_GRAPHING_FILENAME_MW);
                FileWriter writer = new FileWriter(LOG_FILE, true);
                writer.append(LOG_ENTRY);
                writer.flush();
                writer.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    } // END OF WRITEFILEDATA



    public void AutomatedStockTracker(int mode){

        AutomatedTrackerMode = mode;

        IntentFilter intentFilter = new IntentFilter();
        if(BEFORE_MARKET_OPEN)
            intentFilter.addAction(Intent.ACTION_TIME_TICK);        // every minute
        if(FIVE_PERCENT_PENNY)
            intentFilter.addAction(Intent.ACTION_TIME_TICK);        // every minute
        if(FIFTEEN_PERCENT_MANY)
            intentFilter.addAction(Intent.ACTION_TIME_TICK);        // every minute
        if(N_FIFTEEN_RED)
            intentFilter.addAction(Intent.ACTION_TIME_TICK);        // every minute
        HTMLReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent){

                if(AutomatedTrackerMode == 100){

                    if(PROCESS_STAGE == 0) {                                                          // app off and just turned on

                        //EnvironmentDiagnostics("AutoTracker: Changing PROCESS_STAGE to 1");
                        //if(subscriptionValid)
                        PROCESS_STAGE = 1;

                    }

                    if(PROCESS_STAGE == 1) {                                                          // grab tickers

                        //EnvironmentDiagnostics("AutoTracker: MarketWatchTickerList about to be executed");
                        MARKETWATCHSEARCH = new MarketWatchTickerList().execute();

                    }

                    // PERHAPS THESE BOTTOMS ONES SHOULD BE DONE OUTSIDE THE MINUTELY

                    if(PROCESS_STAGE == 3) {                                                          // analyze them

                        //EnvironmentDiagnostics("AutoTracker: MomentumStockObjectCreator about to be executed");
                        if(AsyncTaskOn == false)
                            MARKETWATCHSEARCH = new MomentumStockObjectCreator().execute();

                    }

                    if(PROCESS_STAGE == 4){                                                         // get to work

                        //MomentumStockSortation();
                        //EnvironmentDiagnostics("AutoTracker: MomentumStockSortation executed");

                    }


                }

                if(AutomatedTrackerMode == 200){}

            }   // end of onReceive method
        };  // end of HTMLReceiver instantiation



        if(mode == 100) {
            registerReceiver(HTMLReceiver, intentFilter);
        }
        else if(mode == 200) {  // ANALYSIS ONLY MODE
            registerReceiver(HTMLReceiver, intentFilter);
        }
        else if(mode == -100){
            // to prevent crashing
            registerReceiver(HTMLReceiver, intentFilter);
            unregisterReceiver(HTMLReceiver);
            AsyncTaskOn = false;

        }

    }   // end of AutomatedStockTracker()


    public void EnvironmentDiagnostics(String StepByStep){

        String DiagnosticsLog = StepByStep;
        writeCSVFileData("MA: " + DiagnosticsLog, "0.00", "0", "0.00", "0.00",true, 3);
    }



    public boolean FutureDateCancelSubscription(){

        // https://stackoverflow.com/questions/36257085/set-date-and-desired-time-in-android

        Calendar calendar = Calendar.getInstance();
        Date SubscriptionEnding = new Date();
        Date today = calendar.getTime();

        calendar.setTime(SubscriptionEnding);
        calendar.set(Calendar.YEAR, 2024);          // Year 2020
        calendar.set(Calendar.MONTH, 8);            // September: counter starts from 0
        calendar.set(Calendar.DAY_OF_MONTH, 30);    // 30th day of the month

        //calendar.add(Calendar.MONTH, 1);          // add 1 month to 'today'
        //calendar.add(Calendar.DATE, 2);           // add 2 days to 'today'
        //calendar.add(Calendar.YEAR, 1);           // add 1 year to 'today'


        SubscriptionEnding = calendar.getTime();
        /*
        if(today.before(SubscriptionEnding))
            return true;
        else
            return false;

        */
        return true;
    }


    public int TimeOfDayRanger(){

        // https://stackoverflow.com/questions/40167572/how-to-get-current-hour-in-android

        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int hour12hrs = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        //System.out.println("Current hour 24hrs format:  " + hour24hrs + ":" + minutes +":"+ seconds);
        //System.out.println("Current hour 12hrs format:  " + hour12hrs + ":" + minutes +":"+ seconds);

        return hour24hrs;

    }


    public void FiveMinuteInterval(){

        // Get the time to determine every 5 minute interval for proto-candlestick data
        int timeStamp;

        Calendar calendar = Calendar.getInstance();
        timeStamp = calendar.get(Calendar.MINUTE);

        if( (timeStamp + 2) % 5 == 5 || (timeStamp + 2) % 5 == 0 )
            PRE_PRE_TIMESTAMP_VALID = true;
        else
            PRE_PRE_TIMESTAMP_VALID = false;

        if( (timeStamp + 1) % 5 == 5 || (timeStamp + 1) % 5 == 0 )
            PRE_TIMESTAMP_VALID = true;
        else
            PRE_TIMESTAMP_VALID = false;

        if((timeStamp+5) % 5 == 0)  // for the case of :00
            TIMESTAMP_VALID = true;
        else
            TIMESTAMP_VALID = false;
    }


   








    // Create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.main_menu:
                Intent menuIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(menuIntent);
                return true;
            case R.id.about_menu:
                menuIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(menuIntent);
                return true;
            case R.id.calculator_menu:
                menuIntent = new Intent(MainActivity.this, Calculator.class);
                startActivity(menuIntent);
                return true;
            case R.id.after_hours_analysis_menu:
                menuIntent = new Intent(MainActivity.this, AfterHoursAnalysisActivity.class);
                startActivity(menuIntent);
                return true;
            case R.id.fda_approval_menu:
                menuIntent = new Intent(MainActivity.this, FDAApproval.class);
                startActivity(menuIntent);
                return true;
            case R.id.market_breakout_menu:
                menuIntent = new Intent(MainActivity.this, MarketBreakout.class);
                startActivity(menuIntent);
                return true;

            /*
            case R.id.another_menu:
                menuIntent = new Intent(MainActivity.this, AnotherActivity.class);
                startActivity(menuIntent);
                return true;
            */
            default:
                return super.onOptionsItemSelected(item);
        }
    } // end of onOptionsItemSelected MENU



    



    @Override
    protected void onResume(){

        super.onResume();

    }   // end of onResume()


    @Override
    protected void onPause(){

        super.onPause();

    }   // end of onPause()

    // TEXT TO SPEECH ASSOCIATED FUNCTION
    @Override
    protected void onDestroy(){

        super.onDestroy();

    }



    @Override
    protected void onStart(){

        super.onStart();

    }   // end of onStart()



}   // end of MainActivity class


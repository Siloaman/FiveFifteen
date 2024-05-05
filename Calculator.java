import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calculator extends AppCompatActivity {

    EditText StockTicker, StockPrice, CashBalance;
    Button CalculatorButton;


    // TABLELAYOUT + SIZES
    int textSize = 0, smallTextSize = 0, mediumTextSize = 0;
    int leftRowMargin = 0, topRowMargin = 0, rightRowMargin = 0, bottomRowMargin = 0;
    TableLayout PercentListTable;

    List<Double> PercentumList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        setTitle("Stock Shorting Levels");

        StockPrice = (EditText)findViewById(R.id.editText);
        StockPrice.setGravity(Gravity.CENTER);

        CashBalance = (EditText)findViewById(R.id.editText2);
        CashBalance.setGravity(Gravity.CENTER);

        CalculatorButton = (Button)findViewById(R.id.calculateButton);

        PercentListTable = (TableLayout)findViewById(R.id.calculate_table);
        PercentListTable.setStretchAllColumns(true);    // *** NECESSARY!!!

        textSize = smallTextSize = mediumTextSize = 30;

        PercentumList = new ArrayList<Double>();

        PercentumList.add(1.15);    PercentumList.add(1.10);    PercentumList.add(1.05);
        PercentumList.add(1.035);    PercentumList.add(1.015);    PercentumList.add(1.00);
        PercentumList.add(0.95);    PercentumList.add(0.90);    PercentumList.add(0.85);




        CalculatorButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String currentPrice = StockPrice.getText().toString();
                String myBalance = CashBalance.getText().toString();
                double currentPriceDb = 0.0;
                double myBalanceDb = 0.0;
                int BuyIn = 0;

                if(!currentPrice.equals("")){          
                    try {
                        currentPriceDb = Double.parseDouble(currentPrice);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
                if(!myBalance.equals("")){
                    try {
                        myBalanceDb = Double.parseDouble(myBalance);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }


                }

                DecimalFormat priceFormat = new DecimalFormat("0.0000");
                DecimalFormat percentageFormat = new DecimalFormat("0.0000");

                if(myBalanceDb > 0.0){
                    if(currentPriceDb <= 0.0){
                        BuyIn = 0;
                    } else {
                        double stockPurchasePower = (myBalanceDb - 10.00) / currentPriceDb;
                        BuyIn = (int) stockPurchasePower;
                    }
                }

                calculate(BuyIn, currentPriceDb);

            }

        });     // end of onClickListener



    }   // end of onCreate()

    public void calculate(int buy, double price) {

        PercentListTable.removeAllViews();    // clear up the table data

        // Populate Screen with Ticker Symbols Gathered...
        for (int i = -2; i < PercentumList.size(); i++) {       // -2 is for header, -1 is CashBalance... going from -8% to 21%

            
            // data columns
            final TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.LEFT);
            tv.setPadding(5, 15, 0, 15);
            if (i == -2) {
                //tv.setText("Max Shares Buyable");
                //tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                //tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            } else if (i == -1) {
                tv.setText("PERCENTAGE");
                tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv.setText(PercentumList.get(i).toString());    // tv.setText(String.valueOf(row.invoiceNumber));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }   // END OF COLUMN 1


            final TextView tv2 = new TextView(this);
            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv2.setGravity(Gravity.LEFT);
            tv2.setPadding(5, 15, 0, 15);
            if (i == -2) {
                // do nothing
            } else if (i == -1){
                tv2.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv2.setText("BUY-IN PRICE");
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv2.setBackgroundColor(Color.parseColor("#f8f8f8"));

                double shortedPrice;

                shortedPrice = price * PercentumList.get(i);

                DecimalFormat percentageFormat = new DecimalFormat("0.0000");
                tv2.setText(percentageFormat.format(shortedPrice));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }   // END OF COLUMN 2


            final TextView tv3 = new TextView(this);
            tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv3.setGravity(Gravity.LEFT);
            tv3.setPadding(5, 15, 0, 15);
            if (i == -2) {
                tv3.setText("");
                tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            else if (i == -1) {
                tv3.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv3.setText("BUY-IN AMT");
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {


                double buy_in_amount = 0.0;

                buy_in_amount = (Double.parseDouble(CashBalance.getText().toString()) - 10.00) / (price * PercentumList.get(i));

                tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));
                tv3.setText("" + (int)buy_in_amount);
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }   // END OF COLUMN 3

            if(i == 1 || i == 3) {
                tv.setBackgroundColor(Color.parseColor("#5ff0c7"));
                tv2.setBackgroundColor(Color.parseColor("#5ff0c7"));
                tv3.setBackgroundColor(Color.parseColor("#5ff0c7"));
            }


            // add table row
            final TableRow tr = new TableRow(this);
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(0, 0, 0, 0);
            tr.setLayoutParams(trParams);
            tr.addView(tv);
            tr.addView(tv2);
            tr.addView(tv3);            

            PercentListTable.addView(tr, trParams);

            if (i > -2) {
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
                if(i == 2 || i == 5)
                    tvSep.setHeight(5);
                else
                    tvSep.setHeight(1);
                trSep.addView(tvSep);
                PercentListTable.addView(trSep, trParamsSep);
            }
        }   // end of for loop
    }   // end of calculate()

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
                Intent menuIntent = new Intent(Calculator.this, MainActivity.class);
                startActivity(menuIntent);
                return true;
            /*
            case R.id.about_menu:
                menuIntent = new Intent(CalculatorActivity.this, AboutActivity.class);
                //startActivity(menuIntent);
                return true;
            case R.id.ticker_pacer_menu:
                menuIntent = new Intent(CalculatorActivity.this, TickerPacerActivity.class);
                //startActivity(menuIntent);
                return true;
            case R.id.learning_menu:
                menuIntent = new Intent(MainActivity.this, LearningActivity.class);
                startActivity(menuIntent);
                return true;
            */
            default:
                return super.onOptionsItemSelected(item);
        }
    } // end of onOptionsItemSelected MENU

}

package com.holdings.siloaman.krenda;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BarReader extends AppCompatActivity {

    EditText StockTicker, StartPrice, EndPrice;
    Button CalculatorButton;


    // TABLELAYOUT + SIZES
    int textSize = 0, smallTextSize = 0, mediumTextSize = 0;
    int leftRowMargin = 0, topRowMargin = 0, rightRowMargin = 0, bottomRowMargin = 0;
    TableLayout PercentListTable;

    List<Double> PercentumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_reader);
        setTitle("Bar Percentage Levels");


        StartPrice = (EditText)findViewById(R.id.editText);
        StartPrice.setGravity(Gravity.CENTER);

        EndPrice = (EditText)findViewById(R.id.editText2);
        EndPrice.setGravity(Gravity.CENTER);

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

                String start_price = StartPrice.getText().toString();
                String end_price = EndPrice.getText().toString();
                double start_price_db = 0.0;
                double end_price_db = 0.0;


                if(!start_price.equals("")){          // https://stackoverflow.com/questions/3405928/how-to-check-if-edittext-has-a-value-in-android-java
                    try {
                        start_price_db = Double.parseDouble(start_price);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
                if(!end_price.equals("")){
                    try {
                        end_price_db = Double.parseDouble(end_price);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }


                }

                // https://stackoverflow.com/questions/9366280/android-round-to-2-decimal-places
                DecimalFormat priceFormat = new DecimalFormat("0.0000");
                DecimalFormat percentageFormat = new DecimalFormat("0.0000");

                calculate(end_price_db, start_price_db);

            }

        });     // end of onClickListener
    }

    public void calculate(double end, double start) {

        PercentListTable.removeAllViews();    // clear up the table data

        // Populate Screen with Ticker Symbols Gathered...
        for (int i = -2; i < 1; i++) {       // -2 is for header, -1 is CashBalance... going from -8% to 21%

            final TextView tv1 = new TextView(this);
            tv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv1.setGravity(Gravity.LEFT);
            tv1.setPadding(5, 15, 0, 15);
            if (i == -2) {
                // nothing
            } else if (i == -1){
                tv1.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv1.setText("SIXTY FIVE");
                tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv1.setBackgroundColor(Color.parseColor("#f8f8f8"));

                double interim_price;

                interim_price = end - start;
                interim_price *= 0.65;

                if(interim_price < 0.0)     // if its a red bar
                    interim_price += start;
                else
                    interim_price += start;

                DecimalFormat percentageFormat = new DecimalFormat("0.00");
                tv1.setText(percentageFormat.format(interim_price));
                tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }   // END OF COLUMN 1


            final TextView tv2 = new TextView(this);
            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv2.setGravity(Gravity.LEFT);
            tv2.setPadding(5, 15, 0, 15);
            if (i == -2) {
                // nothing
            } else if (i == -1){
                tv2.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv2.setText("THIRTY FIVE");
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv2.setBackgroundColor(Color.parseColor("#f8f8f8"));

                double interim_price;

                interim_price = end - start;
                interim_price *= 0.35;

                if(interim_price < 0.0)     // if its a red bar
                    interim_price += start;
                else
                    interim_price += start;

                DecimalFormat percentageFormat = new DecimalFormat("0.00");
                tv2.setText(percentageFormat.format(interim_price));
                tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }   // END OF COLUMN 2


            final TextView tv3 = new TextView(this);
            tv3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv3.setGravity(Gravity.LEFT);
            tv3.setPadding(5, 15, 0, 15);
            if (i == -2) {
                // nothing
            } else if (i == -1){
                tv3.setBackgroundColor(Color.parseColor("#f0f0f0"));
                tv3.setText("THIRTY FIVE ABOVE");
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, smallTextSize);
            } else {
                tv3.setBackgroundColor(Color.parseColor("#f8f8f8"));

                double interim_price;

                interim_price = end - start;
                interim_price *= 0.35;

                if(interim_price < 0.0)     // if its a red bar
                    interim_price += end;
                else
                    interim_price += end;

                DecimalFormat percentageFormat = new DecimalFormat("0.00");
                tv3.setText(percentageFormat.format(interim_price));
                tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }   // END OF COLUMN 3



            // add table row
            final TableRow tr = new TableRow(this);
            tr.setId(i + 1);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(0, 0, 0, 0);
            tr.setLayoutParams(trParams);
            tr.addView(tv1);
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
                Intent menuIntent = new Intent(BarReader.this, MainActivity.class);
                startActivity(menuIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    } // end of onOptionsItemSelected MENU

}

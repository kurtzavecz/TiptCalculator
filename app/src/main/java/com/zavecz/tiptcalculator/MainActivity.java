package com.zavecz.tiptcalculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //define widgets
    private EditText mSubtotalET;
    private TextView mPercentTV;
    private TextView mTipAmountTV;
    private TextView mTotalTV;
    private Button mPercentUp;
    private Button mPercentDown;
    private Button mReset;
    private Spinner mSplitSpinner;
    private TextView mPerPersonLabel;
    private TextView mPerPersonTV;
    private int split = 1;

    //define SharedPrefferences object
    private SharedPreferences mSavedValues;

    //define instance variables that should be saved
    private String mBillAmountString = "";
    private float mTipPercent = .15f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //get refference to the widgets
        mSubtotalET = (EditText) findViewById(R.id.mSubtotalET);
        mPercentTV = (TextView) findViewById(R.id.mPercentTV);
        mTipAmountTV = (TextView) findViewById(R.id.mTipAmountTV);
        mTotalTV = (TextView) findViewById(R.id.mTotalTV);
        mPercentUp = (Button) findViewById(R.id.mPercentUp);
        mPercentDown = (Button) findViewById(R.id.mPercentDown);
        mReset = (Button) findViewById(R.id.mReset);
        mSplitSpinner = (Spinner) findViewById(R.id.splitSpinner);
        mPerPersonLabel = (TextView) findViewById(R.id.spinnerLabel);
        mPerPersonTV = (TextView) findViewById(R.id.perPersonLabel);

        //set arrayadapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.split_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSplitSpinner.setAdapter(adapter);


        //set the listeners
        //set currentclass as the listener
        ButtonListener buttonListener = new ButtonListener();
        mSubtotalET.setOnEditorActionListener(editorActionListener);
        mPercentUp.setOnClickListener(buttonListener);
        mPercentDown.setOnClickListener(buttonListener);
        mReset.setOnClickListener(buttonListener);

        //Anonymous inner class as the listener
        mSplitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                        split = position + 1;
                                                        calculateAndDisplay();
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                    }
                                                });


        //get SharedPreferences object
        mSavedValues = getSharedPreferences("savedValues", MODE_PRIVATE);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.mPercentUp:
                    mTipPercent = mTipPercent + .01f;
                    calculateAndDisplay();
                    break;
                case R.id.mPercentDown:
                    mTipPercent = mTipPercent - .01f;
                    calculateAndDisplay();
                    break;
                case R.id.mReset:
                    mSubtotalET.setText("");
                    mTipPercent = .15f;
                    calculateAndDisplay();
                    break;
            }
        }
    }


    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){
                calculateAndDisplay();
            }

            Toast.makeText(getApplicationContext(), "ActionID" + actionId, Toast.LENGTH_LONG).show();
            return false;
        }
    };


    private void calculateAndDisplay() {

        Log.i(TAG, "calculateAndDisplay method called. ");

        //get bill amount from user
        mBillAmountString = mSubtotalET.getText().toString();
        float billAmount;
        if(mBillAmountString.equals("")){
            billAmount = 0;
        } else{
            billAmount = Float.parseFloat(mBillAmountString);
        }

        //get discount percent
        float discountPercent = 0;
        if(billAmount >= 200) {
            discountPercent = .2f;
        }else if(billAmount >= 100){
            discountPercent = .1f;
        }else {
            discountPercent = 0;
        }

        //calculate discount
        float tipAmount = billAmount * mTipPercent;
        float total = billAmount + tipAmount;

        //calculate split amount and show/hide split amount widgets
        float splitAmount = 0;
        if(split == 1) {
            //no split, hide perperson widget
            mPerPersonLabel.setVisibility(View.GONE);
            mPerPersonTV.setVisibility(View.GONE);
        }else{
            //Split, calculate amount and show per person widget
            splitAmount = total / split;
            mPerPersonLabel.setVisibility(View.VISIBLE);
            mPerPersonTV.setVisibility(View.VISIBLE);

        }

        Log.i(TAG, "Total: " + total);
        Toast toast = Toast.makeText(getApplicationContext(), "Tip Amount: " + tipAmount, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        //dispaly data on layout
        NumberFormat percent = NumberFormat.getPercentInstance();
        mPercentTV.setText(percent.format(mTipPercent));

        NumberFormat curency = NumberFormat.getCurrencyInstance();
        mTipAmountTV.setText(curency.format(tipAmount));
        mTotalTV.setText(curency.format(total));
        mPerPersonTV.setText(curency.format(splitAmount));
    }

    @Override
    protected void onPause() {

        //save the instance variables
        SharedPreferences.Editor editor = mSavedValues.edit();
        editor.putString("billAmountString", mBillAmountString);
        editor.putFloat("tipPercent", mTipPercent);
        editor.putInt("split", split);
        editor.apply();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //get instance variables
        mBillAmountString = mSavedValues.getString("billAmountString", "");
        mTipPercent = mSavedValues.getFloat("tipPercent", .15f);
        split = mSavedValues.getInt("split", 1);

        //set the bill amount on its widget
        mSubtotalET.setText(mBillAmountString);

        //call the calculate and display method
        calculateAndDisplay();
    }
}

package com.example.lenovo.converter

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.net.ConnectivityManager




class MainActivity : AppCompatActivity() {

    var data = arrayOf("EUR", "USD", "GBP", "RUB", "ALL", "XCD", "BBD", "BTN", "BND", "XAF", "CUP")

    companion object {
        const val COUNT_OF_CURRENCIES = 11
    }

    private var receiver: ServiceReceiver = ServiceReceiver(Handler())
    private var editing = false
    var editTextToSpinner: MutableMap<EditText, Spinner> = mutableMapOf()
    var spinnerToEditText: MutableMap<Spinner, EditText> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinnerFrom = findViewById<Spinner>(R.id.spinner_convert_from)
        val spinnerTo = findViewById<Spinner>(R.id.spinner_convert_to)
        val valueToConvertFrom: EditText = findViewById(R.id.value_from)
        val valueToConvertTo: EditText = findViewById(R.id.value_to)
        setupAdapterForSpinners(spinnerFrom, spinnerTo)

        editTextToSpinner[valueToConvertFrom] = spinnerFrom
        editTextToSpinner[valueToConvertTo] = spinnerTo
        spinnerToEditText[spinnerFrom] = valueToConvertFrom
        spinnerToEditText[spinnerTo] = valueToConvertTo
        receiver.setReceiver(object : ServiceReceiver.Receiver {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
                if (resultCode == Activity.RESULT_OK) {
                    val value = Converter.parseResult(resultData)
                    editing = true
                    if (resultData.getString("VIEW_NUMBER") == "first") {
                        findViewById<EditText>(R.id.value_to).setText(value)
                    } else {
                        findViewById<EditText>(R.id.value_from).setText(value)
                    }
                    editing = false
                }

            }
        })
        addTextChangedListener(valueToConvertFrom, valueToConvertTo)
        addTextChangedListener(valueToConvertTo, valueToConvertFrom)

        setSpinnerItemSelectedListeners(spinnerFrom, spinnerTo)
        setSpinnerItemSelectedListeners(spinnerTo, spinnerFrom)
    }

    private fun setSpinnerItemSelectedListeners(spinner: Spinner, secondSpinner: Spinner) {
        for (i in 0 until COUNT_OF_CURRENCIES) {
            spinner.setSelection(i)
            secondSpinner.setSelection(i)
            spinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val viewNumber: String = if (spinner.id == R.id.spinner_convert_from) {
                        "first"
                    } else {
                        "second"
                    }
                    Converter.convert(
                        applicationContext,
                        spinnerToEditText[spinner]?.text.toString(),
                        spinner.selectedItem.toString(),
                        secondSpinner.selectedItem.toString(),
                        viewNumber,
                        receiver
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            })
        }
    }

    private fun addTextChangedListener(editText: EditText, changeable: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (editing) {
                    return
                }
                if (editText.text.toString() == ".") {
                    editText.setText("")
                    return
                }
                if(!isNetworkConnected()) {
                    Toast.makeText(this@MainActivity, "Can't connect to the Internet", Toast.LENGTH_SHORT).show()
                    editing = true
                    changeable.text.clear()
                    editing = false
                    return
                }

                val viewNumber: String = if (editText.id == R.id.value_from) {
                    "first"
                } else {
                    "second"
                }
                Converter.convert(
                    applicationContext,
                    editText.text.toString(),
                    editTextToSpinner[editText]?.selectedItem.toString(),
                    editTextToSpinner[changeable]?.selectedItem.toString(),
                    viewNumber,
                    receiver
                )
            }
        });
    }

    private fun setupAdapterForSpinners(spinnerFrom: Spinner, spinnerTo: Spinner) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }
}

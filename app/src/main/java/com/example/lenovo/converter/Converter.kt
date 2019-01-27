package com.example.lenovo.converter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import java.io.InputStreamReader

object Converter {
    fun convert(
        context: Context,
        valueFrom: String,
        currencyFrom: String,
        currencyTo: String,
        viewNumber: String,
        receiver: ServiceReceiver
    ) {
        val request = "${currencyFrom}_$currencyTo"
        val url = "https://free.currencyconverterapi.com/api/v6/convert?q=$request&compact=ultra"
        val intent = Intent(context, Loader::class.java).apply {
            putExtra("EXTRA_URL", url)
            putExtra("RECEIVER", receiver)
            putExtra("REQUEST", request)
            putExtra("VIEW_NUMBER", viewNumber)
            putExtra("VALUE", valueFrom)
        }
        context.startService(intent)
    }

    fun parseResult(data: Bundle): String {
        val resultValue = data.getByteArray("RESULT_VALUE")
        val iS = resultValue?.inputStream()
        val jsn = JsonParser().parse(JsonReader(InputStreamReader(iS))) as JsonObject
        val value = jsn.get(data.getString("REQUEST")).asBigDecimal
        val valueFrom = data.getString("VALUE")
        if (valueFrom == "") {
            return valueFrom
        }
        val x = (valueFrom?.toBigDecimal()?.times(value))
        return x.toString()
    }
}
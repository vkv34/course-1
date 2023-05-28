package com.example.products.utils.retrofit

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date> {
                override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Date {
                    val dateAsString = json.asString
                    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    try {
                        return inputDateFormat.parse(dateAsString)
                    } catch (e: ParseException) {
                        throw JsonParseException(e)
                    }
                }
            })
            .create()

        val adapter = gson.getAdapter(TypeToken.get(type))
        return Converter<ResponseBody, Any> { body -> adapter.fromJson(body.string()) }
    }
}


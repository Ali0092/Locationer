package com.example.locationer

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.MalformedInputException

class DownloadURL {

    fun readUrl(placeURL: String): String {
        var data: String = " "
        var inputStream: InputStream? = null
        var httpURLConnection: HttpURLConnection? = null

        try {
            val url = URL(placeURL)
            httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.connect()

            inputStream = httpURLConnection.inputStream
            val bufferReader = BufferedReader(InputStreamReader(inputStream))
            val stringBufer = StringBuffer()
            var line = ""

            line = bufferReader.readLine()
            while ((line != null)) {
                stringBufer.append(line)
            }


            data = stringBufer.toString()
            bufferReader.close()

        } catch (e: MalformedInputException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            httpURLConnection?.disconnect()
        }
        return data
    }
}
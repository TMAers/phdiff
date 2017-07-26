package io.nthienan.phdiff.conduit

import org.apache.commons.io.IOUtils
import org.apache.http.HttpStatus
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.json.JSONObject
import org.sonar.api.utils.log.Loggers
import java.net.URL
import java.util.ArrayList

/**
 * Created on 17-Jul-17.
 * @author nthienan
 */
val LOG = Loggers.get(ConduitClient::class.java)

class ConduitClient(var url: String, var token: String) {

    fun perform(action: String, params: JSONObject): JSONObject {
        val httpClient = HttpClientBuilder.create().build()

        val response = httpClient.execute(makeRequest(action, params))
        val responseBody = IOUtils.toString(response.entity.content, Charsets.UTF_8)

        LOG.debug("$url responses: $responseBody")

        if (response.statusLine.statusCode != HttpStatus.SC_OK) {
            throw ConduitException(responseBody, response.statusLine.statusCode)
        }

        val result = JSONObject(responseBody)
        val errorInfo = result.get("error_info")
        if (result.get("error_code") != null || errorInfo != null) {
            throw ConduitException(errorInfo.toString(), response.statusLine.statusCode)
        }
        return result
    }

    private fun makeRequest(action: String, params: JSONObject): HttpPost {
        val postRequest = HttpPost(URL(URL(URL(url), "/api/"), action).toURI())

        val conduitMetadata = JSONObject()
        conduitMetadata.put("token", token)
        params.put("__conduit__", conduitMetadata)

        val formData = ArrayList<NameValuePair>()
        formData.add(BasicNameValuePair("params", params.toString()))

        postRequest.entity = UrlEncodedFormEntity(formData, "UTF-8")

        return postRequest
    }
}

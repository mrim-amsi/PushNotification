package com.example.cloudass3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException

class MainActivity : AppCompatActivity() {
    var requestQueue: RequestQueue? = null
    var firstName: EditText? = null
    var secondName: EditText? = null
    var email: EditText? = null
    var password: EditText? = null
    var signupButton: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firstName = findViewById<View>(R.id.firstName) as EditText
        secondName = findViewById<View>(R.id.secondName) as EditText
        email = findViewById<View>(R.id.email) as EditText
        password = findViewById<View>(R.id.password) as EditText
        signupButton = findViewById<View>(R.id.signup) as Button

        signupButton!!.setOnClickListener {
            val data = "{" +
                    "\"firstName\"" + ":" + "\"" + firstName!!.text.toString() + "\"," +
                    "\"secondName\"" + ":" + "\"" + secondName!!.text.toString() + "\"," +
                    "\"email\"" + ":" + "\"" + email!!.text.toString() + "\"," +
                    "\"password\"" + ":" + "\"" + password!!.text.toString() + "\"" +
                    "}"
            Submit(data)
            getRegToken()
            var i = Intent(this,Login:: class.java)
            i.putExtra("email",email!!.text.toString())
            i.putExtra("password",password!!.text.toString())
            startActivity(i)
        }

    }


    fun getRegToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful){
                Log.e("dna","Failed to get token ${task.exception}")

                return@addOnCompleteListener
            }
            var token = task.result
            val data = "{" +

                    "\"email\"" + ":" + "\"" + email!!.text.toString() + "\"," +
                    "\"password\"" + ":" + "\"" + password!!.text.toString() + "\"" +
                    "\"reg_token\"" + ":" + "\"" + token!! + "\"," +
                    "}"
            val URL = "https://mcc-users-api.herokuapp.com/add_reg_token"
            requestQueue = Volley.newRequestQueue(applicationContext)
            Log.d("TAG", "requestQueue: $requestQueue")
            val stringRequest: StringRequest = object : StringRequest(Method.PUT, URL, Response.Listener<String?> { response ->
                try {
                    val objres = JSONObject(response)
                    Log.d("TAG", "onResponse: $objres")
                } catch (e: JSONException) {
                    Log.d("TAG", "Server Error ")
                }
            }, Response.ErrorListener { error -> Log.d("TAG", "onErrorResponse: $error") }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    return try {
                        Log.d("TAG", "savedata: $data")
                        if (data == null) null else data.toByteArray(charset("utf-8"))
                    } catch (uee: UnsupportedEncodingException) {
                        null
                    }!!
                }
            }
            requestQueue!!.add(stringRequest)
            Log.e("dnaTok",token!!)

        }
    }
    private fun Submit(data: String) {
        val URL = "https://mcc-users-api.herokuapp.com/add_new_user"
        requestQueue = Volley.newRequestQueue(applicationContext)
        Log.d("TAG", "requestQueue: $requestQueue")
        val stringRequest: StringRequest = object : StringRequest(Request.Method.POST, URL, Response.Listener<String?> { response ->
            try {
                val objres = JSONObject(response)
                Log.d("TAG", "onResponse: $objres")
            } catch (e: JSONException) {
                Log.d("TAG", "Server Error ")
            }
        }, Response.ErrorListener { error -> Log.d("TAG", "onErrorResponse: $error") }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                return try {
                    Log.d("TAG", "savedata: $data")
                    if (data == null) null else data.toByteArray(charset("utf-8"))
                } catch (uee: UnsupportedEncodingException) {
                    null
                }!!
            }
        }
        requestQueue!!.add(stringRequest)
    }
}
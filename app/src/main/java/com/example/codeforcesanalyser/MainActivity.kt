package com.example.codeforcesanalyser

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.codeforcesanalyser.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFind.setOnClickListener {
            val handle = binding.etHandle.text.toString()
            if (handle.isNotEmpty()) {
                getData(handle)
            } else {
                Toast.makeText(this, "Please enter a Codeforces handle", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getData(handle: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.show()

        Log.d(TAG, "Fetching data for handle: $handle")

        RetrofitInstance.apiInterface.getData(handle).enqueue(object : Callback<responseDataClass?> {
            override fun onResponse(
                call: Call<responseDataClass?>,
                response: Response<responseDataClass?>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    Log.d(TAG, "API call successful")
                    val result = response.body()?.result
                    if (!result.isNullOrEmpty()) {
                        val latestRating = result.last().newRating
                        Log.d(TAG, "Latest rating: $latestRating")
                        binding.tvRating.text = "Current Rating: $latestRating"
                    } else {
                        Log.d(TAG, "No rating data available")
                        binding.tvRating.text = "No rating data available"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "API call failed: ${response.code()}, Error body: $errorBody")
                    Toast.makeText(this@MainActivity, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<responseDataClass?>, t: Throwable) {
                progressDialog.dismiss()
                Log.e(TAG, "API call failed", t)
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }
        })
    }
}